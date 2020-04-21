package com.elder.logrecodemodule.ViewModel

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.databinding.ObservableField
import android.graphics.BitmapFactory
import android.graphics.Point
import android.media.MediaPlayer
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.Projection
import com.amap.api.maps.model.*
import com.amap.api.maps.utils.SpatialRelationUtil
import com.autonavi.ae.route.model.GeoPoint
import com.autonavi.amap.mapcore.IPoint
import com.autonavi.amap.mapcore.MapProjection
import com.elder.logrecodemodule.Activity.PlayerActivity
import com.elder.logrecodemodule.R
import com.elder.zcommonmodule.DataBases.queryUserInfo
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.UIdeviceInfo
import com.elder.zcommonmodule.Entity.UpDataDriverEntitiy
import com.elder.zcommonmodule.Even.NomalPostStickyEven
import com.elder.zcommonmodule.Service.Screen.ScreenRecordService
import com.elder.zcommonmodule.Service.Screen.ScreenUtil
import com.elder.zcommonmodule.SmoothOverLay
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.FileUtils
import com.elder.zcommonmodule.Widget.SportTrailView
import com.elder.zcommonmodule.getImageUrl
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXVideoObject
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import java.text.DecimalFormat


class PlayerViewModel : BaseViewModel(), MediaPlayer.OnCompletionListener, SportTrailView.OnTrailChangeListener {


    var REQUEST_SCREEN = 10000
    var cservice: ScreenRecordService? = null
    var connect = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e("result", "onServiceConnected")
            cservice = (service as ScreenRecordService.RecordBinder).recordService
            var file = cservice!!.queryPath(uiDevice!!.id.get()!!)
            if (!file) {
                cservice!!.setFilePath(uiDevice!!.id.get()!!)
                ScreenUtil.setScreenService(cservice!!)
                var mediaProjectionManager = playerActivity.activity!!.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                if (mediaProjectionManager != null) {
                    val intent = mediaProjectionManager.createScreenCaptureIntent()
                    playerActivity.startActivityForResult(intent, REQUEST_SCREEN)
                }
            } else {
                cservice!!.setFilePath(uiDevice!!.id.get()!!)
                start()
            }
        }
    }

    override fun onDrawing(mCurrentPosition: FloatArray?) {
        var pro = playerActivity.player_mapview.map.projection
        var m = pro.fromScreenLocation(Point(mCurrentPosition!![0].toInt(), mCurrentPosition[1].toInt()))
        playerActivity.player_mapview.map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(m, zoomLevel, 50F, 0F)))
    }

    override fun onFinish() {
        Log.e("result", "onFinish")
        ScreenUtil.clearRecordElement()
    }


    var moving: SmoothOverLay? = null
    var upload = ObservableField<Boolean>(false)
    var name = ObservableField<String>()
    var time = ObservableField<String>()
    var player_avatar = ObservableField<String>()
    var distance = ObservableField<String>("0.0 KM")
    var onTime = ObservableField<String>("00:00:00")
    var onSpeed = ObservableField<String>("0.0 KM/H")
    var startTimer = 0L
    var isPlay = ObservableField<Boolean>(true)
    lateinit var datas: ArrayList<Location>
    var visibleField = ObservableField<Boolean>(false)
    lateinit var point: ArrayList<LatLng>
    override fun onCompletion(mp: MediaPlayer?) {

    }

    lateinit var playerActivity: PlayerActivity
    lateinit var data: UpDataDriverEntitiy
    var uiDevice: UIdeviceInfo? = null
    var d: Disposable? = null
    fun inject(playerActivity: PlayerActivity) {
        this.playerActivity = playerActivity
        var id = PreferenceUtils.getString(context, USERID)
        var info = queryUserInfo(id)[0]
        name.set(info.data!!.name)
        player_avatar.set(getImageUrl(info.data?.headImgFile))
//        d = RxBus.default?.toObservableSticky(NomalPostStickyEven::class.java)?.subscribe {
//            if (it.type == 106) {
        Observable.just("").subscribeOn(Schedulers.io()).map(Function<String, UpDataDriverEntitiy> {
            point = ArrayList()
            uiDevice = playerActivity.imgs
            var localFile = FileUtils.getStorageFile(uiDevice!!.id.get()!!)
            data = Gson().fromJson<UpDataDriverEntitiy>(localFile, UpDataDriverEntitiy::class.java)
            datas = data.locationArray!!
            datas?.forEach {
                point.add(LatLng(it.latitude, it.longitude))
            }
            return@Function data
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            onTime.set(ConvertUtils.formatTimeS(data.totalTime))
            onSpeed.set(DecimalFormat("0.0").format((data.mileage!! * 3.6 / data.totalTime)) + " KM/H")
            pre()
        }
//            }
//        }
//        RxSubscriptions.add(d)
        playerActivity.activity!!.bindService(Intent(playerActivity.activity, ScreenRecordService::class.java), connect, Service.BIND_AUTO_CREATE)
// videoView.post(new Runnable() {
//            @Override
//            public void run() {
//                videoView.setFixedSize(videoView.getWidth(), videoView.getHeight());
//                videoView.invalidate();
//
//                videoView.setVideoPath(proxyUrl);
//                videoView.start();
//            }
//        });
//        playerActivity.videoView0.setZOrderMediaOverlay(true)
//        playerActivity.videoView0.setOnCompletionListener(this)
//        playerActivity.videoView0.setVideoURI(Uri.parse("android.resource://" + context.packageName + "/raw/start_movie"))
//        playerActivity.videoView0.start()
    }

    fun start() {
        playerActivity.videoView.post {
            playerActivity.videoView.setFixedSize(playerActivity.videoView.width, playerActivity.videoView.height)
            playerActivity.videoView.invalidate()
            playerActivity.videoView.setVideoPath(Uri.parse("android.resource://" + context.packageName + "/raw/start_movie").toString())
            playerActivity.videoView.start()
            playerActivity.videoView.setOnCompletionListener {
                playerActivity.videoView.visibility = View.GONE
                startTimer = System.currentTimeMillis()
//                var list = ArrayList<Point>()
//                point.forEach {
//                    var pro = playerActivity.player_mapview.map.projection
//                    list.add(pro.toScreenLocation(it))
//                }
//                playerActivity.sportView.drawSportLine(list, R.drawable.start_point, R.drawable.navi_map_gps_locked, point, this)

                if (moving != null) {
                    CoroutineScope(uiContext).launch {
                        delay(500)
                        moving!!.startSmoothMove()
                    }
                }
            }
        }
    }


    var zoomLevel = 17F
    fun pre() {
        var marker = playerActivity.player_mapview.map.addMarker(MarkerOptions().position(LatLng(point[0].latitude, point[0].longitude)).zIndex(2f)
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked)))
        moving = SmoothOverLay(playerActivity.player_mapview.map, marker)
        var start = point[0]
        playerActivity.player_mapview.map.moveCamera(CameraUpdateFactory.changeLatLng(start))
        val pair = SpatialRelationUtil.calShortestDistancePoint(point, start)
        point.set(pair.first, start)
        moving!!.setVisible(true)
        moving!!.setPoints(point)
        var dur = 15
        if (data.mileage!! < 5000) {
        } else {
            if (data.mileage!! < 10000) {
                zoomLevel = 16F
            } else if (data.mileage!! > 10000 && data.mileage!! < 20000) {
                zoomLevel = 15F
            } else if (data.mileage!! > 20000 && data.mileage!! < 30000) {
                zoomLevel = 14F
            } else if (data.mileage!! > 30000 && data.mileage!! < 50000) {
                zoomLevel = 13F
            } else if (data.mileage!! > 50000 && data.mileage!! < 100000) {
                zoomLevel = 12F
            } else if (data.mileage!! > 100000 && data.mileage!! < 200000) {
                zoomLevel = 11F
            } else if (data.mileage!! > 200000 && data.mileage!! < 500000) {
                zoomLevel = 10F
            } else if (data.mileage!! > 500000) {
                zoomLevel = 9F
            }
        }
        moving!!.setTotalDuration(dur)
        var routeAngel = 0F
        var listp = ArrayList<LatLng>()
        var opition = PolylineOptions()
        opition.color(getColor(R.color.line_color)).width(14F)
        var poi = playerActivity.player_mapview.map.addPolyline(opition)
        var startTime = System.currentTimeMillis()
        var dis = 0.0
        var last = 0
        playerActivity.player_mapview.map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(point[0], zoomLevel, 50F, 0F)))
        moving!!.setAllTimeListener {
            //            Log.e("result", "index" + index + "it" + it + "total" + point.size)
            if (it != 0) {
                dis += AMapUtils.calculateLineDistance(LatLng(datas!![last]?.latitude!!, datas!![last]?.longitude!!), LatLng(datas!![it].latitude!!, datas!![it]?.longitude!!))
                last = it
            }
            distance.set(DecimalFormat("0.0").format(dis / 1000) + " KM")
            var angel = moving!!.`object`.rotateAngle
            routeAngel = getDirectionArea(datas!![it].bearing)
            if (System.currentTimeMillis() - startTime > 400) {
                startTime = System.currentTimeMillis()
                playerActivity.player_mapview.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(moving!!.position, zoomLevel, 50F, routeAngel)), 500, object : AMap.CancelableCallback {
                    override fun onFinish() {
                    }

                    override fun onCancel() {
                    }
                })
            }
            listp.add(moving!!.position)
            poi.points = listp
        }
        moving!!.setEndListener {
            CoroutineScope(uiContext).launch {
                delay(500)
                var b = LatLngBounds.builder()
                var biglatitude = 0.0  //最大纬度
                var littlatitude = 180.0 //最小纬度
                var biglon = 0.0  //最大经度
                var littlelon = 180.0 //最小经度
                isPlay.set(false)
                point.forEach {
                    if (it.latitude > biglatitude) {
                        biglatitude = it.latitude
                    }
                    if (it.latitude < littlatitude) {
                        littlatitude = it.latitude
                    }
                    if (it.longitude > biglon) {
                        biglon = it.longitude
                    }
                    if (it.longitude < littlelon) {
                        littlelon = it.longitude
                    }
                }
                b.include(LatLng(biglatitude, biglon))    //东北角
                b.include(LatLng(littlatitude, littlelon)) //西南角
                playerActivity.player_mapview.map.moveCamera(CameraUpdateFactory
                        .newLatLngBounds(b.build(), getWindowWidth()!!, getWindowWidth()!!, 50))
                visibleField.set(true)
                createAnimationMarker(start)
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.select_point))
                ObjectAnimator.ofFloat(playerActivity.scroll_view, "translationY", 0F, playerActivity.acse.y - playerActivity.scroll_view.y + ConvertUtils.dp2px(300f)).setDuration(1000).start()
                delay(1000)
                ScreenUtil.stopScreenRecord(playerActivity.activity)
//                upload.set(true)
            }
        }
    }

    fun getDirectionArea(route: Float): Float {
        if (route > 337.5 || route < 22.5) {
            return 0F
        } else if (22.5 <= route && route < 67.5) {
            //东北
            return 45F
        } else if (67.5 <= route && route < 112.5) {
            //东
            return 90F
        } else if (112.5 <= route && route < 157.5) {
            //东南
            return 135F
        } else if (157.5 < route && route < 202.5) {
            return 180F
        } else if (202.5 < route && route < 247.5) {
            return 225F
        } else if (247.5 < route && route < 292.5) {
            return 270F
        } else if (292.5 < route && route < 337.5) {
            return 315F
        } else {
            return 0F
        }
    }

    fun createAnimationMarker(startpoints: LatLng): Marker? {
        // 中心的marker
        var breatheMarker_center = playerActivity.player_mapview.map.addMarker(MarkerOptions().position(startpoints).zIndex(2f)
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.myself_all)))
        return breatheMarker_center
    }

    var shareDialog: AlertDialog? = null

    fun onClick(view: View) {
        when (view.id) {
            R.id.upload -> {
                if (shareDialog == null) {
                    shareDialog = DialogUtils.createShareDialog(playerActivity.activity!!, "", "")
                }
                shareDialog!!.show()
                shareDialog?.findViewById<TextView>(R.id.share_frend)?.setOnClickListener {
                    if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                        Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                    } else {
                        var wx = WXVideoObject()
                        wx.videoUrl = cservice?.recordFilePath
                        var msg = WXMediaMessage(wx)
                        var req = SendMessageToWX.Req()
                        req.transaction = "video" + System.currentTimeMillis()
                        req.message = msg
                        req.scene = SendMessageToWX.Req.WXSceneSession
                        BaseApplication.getInstance().mWxApi.sendReq(req)
                        shareDialog!!.dismiss()
                    }
                }

                shareDialog?.findViewById<TextView>(R.id.share_frendQ)?.setOnClickListener {
                    if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                        Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                    } else {
                        var wx = WXVideoObject()
                        wx.videoUrl = cservice?.recordFilePath
                        var msg = WXMediaMessage(wx)
                        var req = SendMessageToWX.Req()
                        req.transaction = "video" + System.currentTimeMillis()
                        req.message = msg
                        req.scene = SendMessageToWX.Req.WXSceneTimeline
                        BaseApplication.getInstance().mWxApi.sendReq(req)
                        shareDialog!!.dismiss()
                    }
                }
            }
            R.id.player_back -> {
//                finish()
                playerActivity._mActivity!!.onBackPressedSupport()
            }
            R.id.player_start -> {
                playerActivity.player_mapview.map.clear()
                isPlay.set(true)
                pre()
                ObjectAnimator.ofFloat(playerActivity.scroll_view, "translationY", 0F, 200f).setDuration(1000).start()
                visibleField.set(false)
                CoroutineScope(uiContext).launch {
                    delay(500)
                    moving!!.startSmoothMove()
                    upload.set(false)
                }
            }
        }

    }
}
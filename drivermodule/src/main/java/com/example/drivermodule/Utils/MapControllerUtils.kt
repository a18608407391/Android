package com.example.drivermodule.Utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.amap.api.maps.AMap
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.AlphaAnimation
import com.amap.api.maps.model.animation.Animation
import com.amap.api.maps.model.animation.AnimationSet
import com.amap.api.maps.model.animation.ScaleAnimation
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.enums.NaviType
import com.amap.api.navi.model.AMapCalcRouteResult
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.route.DistanceResult
import com.amap.api.services.route.DistanceSearch
import com.amap.api.services.route.RouteSearch
import com.amap.api.services.route.RouteSearch.*
import com.amap.api.trace.LBSTraceClient
import com.amap.api.trace.TraceListener
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.queryUserInfo
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.FileUtils
import com.example.drivermodule.CalculateRouteListener
import com.example.drivermodule.Controller.DriverItemModel
import com.example.drivermodule.CustomNaviCallback
import com.example.drivermodule.Entity.BitMapWithPath
import com.example.drivermodule.R
import com.example.drivermodule.Fragment.MapFragment
import com.google.gson.Gson
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.cs.tec.library.Base.Utils.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MapControllerUtils : GeocodeSearch.OnGeocodeSearchListener, DistanceSearch.OnDistanceSearchListener, TraceListener, AMap.OnMapScreenShotListener, AMap.OnCameraChangeListener, CustomNaviCallback, HttpInteface.IUploadDriverFiles, HttpInteface.IUploadDriverImages, HttpInteface.IRelogin {
    override fun IReloginSuccess(t: String) {
        showUpLoadDialog(parameter1!!, parameter2!!, parameter3!!, parameter4!!)
    }

    override fun IReloginError() {

    }

    override fun UploadDriverImagesSuccess(response: String) {
        Log.e("result", "UploadDriverImagesSuccess" + response)
    }

    override fun UploadDriverImagesError(ex: Throwable) {
        Log.e("result", "UploadDriverImagesError错误" + ex.message)
    }

    override fun UploadDriverSuccess(response: String) {
        Log.e("result", "UploadDriverSuccess" + response)

        var mult = MultipartBody.Builder().setType(MultipartBody.FORM)
        mult.addFormDataPart("files", File(parameter1).name, RequestBody.create(MediaType.parse("image/jpg"), File(parameter1)))
        mult.addFormDataPart("files", File(parameter2).name, RequestBody.create(MediaType.parse("image/jpg"), File(parameter2)))
//        var body = RequestBody.create(MediaType.parse("text/plain"), file)
//        var part = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.name, body).build()
        HttpRequest.instance.uploadDriverImages = this
        var t = MultipartBody.Part.createFormData("rid", fr.viewModel?.status?.driverNetRecord?.id.toString())
        HttpRequest.instance.postDriverImage(mult.build(), t)
    }

    override fun UploadDriverError(ex: Throwable) {
        Log.e("result", "UploadDriverError错误" + ex.message)
    }

    var CurState = 0

    companion object {
        var OnShareState = 19900325
    }

    override fun onCameraChangeFinish(p0: CameraPosition?) {
        when (CurState) {
            OnShareState -> {
                activity.mAmap.getMapScreenShot(this@MapControllerUtils)
                activity.fr_map_view.invalidate()
                CurState = 0
            }
        }
    }

    override fun onCameraChange(p0: CameraPosition?) {

    }

    override fun onMapScreenShot(p0: Bitmap?) {
        var k = Observable.just(p0!!).subscribeOn(Schedulers.io()).map(Function<Bitmap, String> {
            val s = SimpleDateFormat("yyyyMMddHHmmss")
            var p = Environment.getExternalStorageDirectory().path + "/big_" + s.format(Date()) + ".png"
            var fs = FileOutputStream(p)
            var t = it?.compress(Bitmap.CompressFormat.PNG, 100, fs)
            fs.flush()
            fs.close()
            return@Function p
        })
        var m = Observable.just(p0!!).subscribeOn(Schedulers.io()).map(Function<Bitmap, BitMapWithPath> {
            var map = BitMapWithPath()
            var width = it?.width
            var hight = it?.height
            var newhight = (hight!! / 2) - (ConvertUtils.dp2px(231F) / 2)
            var y = newhight
            var x = 0
            var newBitmap = Bitmap.createBitmap(it, x, y, width!!, ConvertUtils.dp2px(231F))
            val sdf = SimpleDateFormat("yyyyMMddHHmmss")
            var path = Environment.getExternalStorageDirectory().path + "/little_" + sdf.format(Date()) + ".png"
            var fos = FileOutputStream(path)
            var b = newBitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            map.path = path
            map.newBit = newBitmap
            return@Function map
        })
        Observable.zip(k, m, object : BiFunction<String, BitMapWithPath, BitMapWithPath> {
            override fun apply(t1: String, t2: BitMapWithPath): BitMapWithPath {
                var file = FileUtils.writeTxtToFile(Gson().toJson(fr?.shareUpLoad), Environment.getExternalStorageDirectory().path + "/Amoski", "/UpLoad_location_File" + fr.viewModel?.status?.driverNetRecord?.id + ".txt")
                t2.file = file
                t2.BigPath = t1
                return t2
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            Upload(it.BigPath!!, it.path!!, it.newBit!!, it.file!!)
        }
    }


    var parameter1: String? = null
    var parameter2: String? = null
    var parameter3: Bitmap? = null
    var parameter4: File? = null
    fun Upload(p: String, path: String, newBitmap: Bitmap, file: File) {
        this.parameter1 = p
        this.parameter2 = path
        this.parameter3 = newBitmap
        this.parameter4 = file
//        var body = RequestBody.create(MediaType.parse("text/plain"), file)
//        var part = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.name, body).build()
//       var t =  MultipartBody.Part.createFormData("rid", fr.viewModel?.status?.driverNetRecord?.id.toString())
//        HttpRequest.instance.uploadDriverFiles = this
//        HttpRequest.instance.postDriverFile(part,t)
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        var id = PreferenceUtils.getString(context, USERID)
        var user = queryUserInfo(id)[0]

        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build()
            var body = RequestBody.create(MediaType.parse("text/plain"), file)
            var part = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.name, body).build()
            var request = Request.Builder().addHeader("appToken", token).url(Base_URL + "AmoskiRiding/ridingManage/ridingFileUpd" + "?rid=" + fr.viewModel?.status?.driverNetRecord?.id).post(part).build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).doOnError {
            activity._mActivity?.dismissProgressDialog()
            showUpLoadDialog(p, path, newBitmap, file)
        }.observeOn(AndroidSchedulers.mainThread()).subscribe {
            Log.e("result","当前啊啊啊请求" + it)
            var m = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
            if (m.code == 0) {
                Observable.create(ObservableOnSubscribe<Response> {
                    var client = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build()
                    var mult = MultipartBody.Builder().setType(MultipartBody.FORM)
                    mult.addFormDataPart("files", File(p).name, RequestBody.create(MediaType.parse("image/jpg"), File(p)))
                    mult.addFormDataPart("files", File(path).name, RequestBody.create(MediaType.parse("image/jpg"), File(path)))
                    var request = Request.Builder().addHeader("appToken", token).url(Base_URL + "AmoskiRiding/ridingManage/uploadRidingPic" + "?rid=" + fr.viewModel?.status?.driverNetRecord?.id).post(mult.build()).build()
                    var call = client.newCall(request)
                    var response = call.execute()
                    it.onNext(response)
                }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                    return@Function it.body()?.string()
                }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    Log.e("result","当前请求" + it)
                    var res = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                    activity._mActivity?.dismissProgressDialog()

                    if (res.code == 0) {
                        fr?.share.apply {
                            this?.userInfo?.set(user)
                            this?.shareIcon = parameter1
                        }
                        fr?.driverAvatar?.set(getImageUrl(user.data?.headImgFile))
                        fr?.userName?.set(user.data?.name)
                        var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        var d = Date(System.currentTimeMillis())
                        fr?.finishTime?.set(simple.format(d))
                        fr?.deivceInfo?.id?.set(fr.viewModel?.status?.driverNetRecord?.id.toString())
                        fr?.deivceInfo?.distance?.set(DecimalFormat("0.0").format(fr.viewModel?.status?.distance!! / 1000))
                        fr?.deivceInfo?.time?.set(ConvertUtils.formatTimeS(fr.viewModel?.status?.second!!))
                        fr?.deivceInfo?.date?.set(simple.format(d))
                        fr?.cancleDriver(true)
                        File(path).delete()
                    } else {
                        if (res.code == 10009) {
                            var map = HashMap<String, String>()
                            var str = PreferenceUtils.getString(context, USER_PHONE)
//                          var str =   Base64.encodeToString(PreferenceUtils.getString(context,USER_PHONE), Base64.DEFAULT))
                            map.put("phoneNumber", Base64.encodeToString(str.toByteArray(Charset.forName("UTF-8")), Base64.DEFAULT))
                            HttpRequest.instance.ReLoginImpl = this
                            HttpRequest.instance.relogin(map)
                        } else {
                            Toast.makeText(context, res.msg, Toast.LENGTH_SHORT).show()
                            showUpLoadDialog(p, path, newBitmap, file)
                        }
                    }
                }
            } else {
                if (m.code == 10009) {
                    var map = HashMap<String, String>()
                    var str = PreferenceUtils.getString(context, USER_PHONE)
//                          var str =   Base64.encodeToString(PreferenceUtils.getString(context,USER_PHONE), Base64.DEFAULT))
                    map.put("phoneNumber", Base64.encodeToString(str.toByteArray(Charset.forName("UTF-8")), Base64.DEFAULT))
                    HttpRequest.instance.ReLoginImpl = this
                    HttpRequest.instance.relogin(map)
                } else {
                    Toast.makeText(context, m.msg, Toast.LENGTH_SHORT).show()
                    showUpLoadDialog(p, path, newBitmap, file)
                }
//                Toast.makeText(context, m.msg, Toast.LENGTH_SHORT).show()
//                showUpLoadDialog(p, path, newBitmap, file)
            }
        }
    }


    fun showUpLoadDialog(p: String, path: String, newBitmap: Bitmap, file: File) {
        var d = DialogUtils.createNomalDialog(fr.mapFr.activity!!, getString(R.string.upload_fail), getString(R.string.upload_cancle), getString(R.string.upload_again))
        d.show()
        d.setOnBtnClickL(OnBtnClickL {
            activity._mActivity?.finish()
            d.dismiss()
        }, OnBtnClickL {
            Upload(p, path, newBitmap, file)
            d.dismiss()
        })
    }


    override fun onMapScreenShot(p0: Bitmap?, p1: Int) {}
    override fun onRequestFailed(p0: Int, p1: String?) {
    }

    override fun onTraceProcessing(p0: Int, p1: Int, p2: MutableList<LatLng>?) {
    }

    override fun onFinished(p0: Int, p1: MutableList<LatLng>?, p2: Int, p3: Int) {
    }

    override fun onDistanceSearched(p0: DistanceResult?, p1: Int) {
    }

    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {

        if (p1 == 1000) {
            var even = RxBusEven()
            even.type = RxBusEven.DriverMapPointRegeocodeSearched
            even.value = p0
            RxBus.default?.post(even)
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
    }

    var geocoderSearch: GeocodeSearch
    var fr: DriverItemModel
    var mRoutePath: RouteSearch
    var disSearch: DistanceSearch
    var disQuery: DistanceSearch.DistanceQuery
    var mTraceClient: LBSTraceClient
    var activity: MapFragment
    lateinit var navi: AMapNavi

    constructor(activty: MapFragment) {
        this.activity = activty
        geocoderSearch = GeocodeSearch(activty._mActivity)
        geocoderSearch.setOnGeocodeSearchListener(this)
        mRoutePath = RouteSearch(activty._mActivity)
        disSearch = DistanceSearch(context)
        disSearch.setDistanceSearchListener(this)
        disQuery = DistanceSearch.DistanceQuery()
        mTraceClient = LBSTraceClient(context)
        fr = activty.viewModel?.items!![0] as DriverItemModel
    }


    fun queryDistance(vararg points: LatLonPoint) {
        disQuery.addOrigins(*points)
    }

    fun queryGeocoder(latLonPoint: LatLonPoint) {
        var query = RegeocodeQuery(latLonPoint, 200F,
                GeocodeSearch.AMAP)// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query)// 设置异步逆地理编码请求
        activity.getMapPointController()?.screenMaker?.hideInfoWindow()
    }


    var breatheMarker: Marker? = null
    var breatheMarker_center: Marker? = null

    fun setLocation(location: Location) {
        breatheMarker?.period = 1
        breatheMarker_center?.period = 1
        breatheMarker?.position = LatLng(location.latitude, location.longitude)
        breatheMarker_center?.position = LatLng(location.latitude, location.longitude)
    }

    fun clearMarker() {
        if (breatheMarker != null) {
            breatheMarker?.remove()
            breatheMarker_center?.remove()
            breatheMarker = null
            breatheMarker_center = null
        }
    }

    fun createAnimationMarker(flag: Boolean, end: LatLonPoint): Marker? {
        if (!flag) {
//            Log.e("result", "关闭定位")
            activity.mAmap.isMyLocationEnabled = false
        }
        // 中心的marker
        var bit = BitmapFactory.decodeResource(activity.resources, R.drawable.green_circle)
        breatheMarker = activity.mAmap.addMarker(MarkerOptions().position(LatLng(end.latitude, end.longitude)).zIndex(1f)
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.myself_inner)))
        // 中心的marker
        breatheMarker_center = activity.mAmap.addMarker(MarkerOptions().position(LatLng(end.latitude, end.longitude)).zIndex(2f)
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked)))

        // 动画执行完成后，默认会保持到最后一帧的状态
        var animationSet = AnimationSet(true)
        var alphaAnimation = AlphaAnimation(0.5f, 0f)
        alphaAnimation.setDuration(2000)
        // 设置不断重复
        alphaAnimation.repeatCount = Animation.INFINITE
        var scaleAnimation = ScaleAnimation(1f, 2.5f, 1f, 2.5f)
        scaleAnimation.setDuration(2000)
        // 设置不断重复
        scaleAnimation.repeatCount = Animation.INFINITE
        animationSet.addAnimation(alphaAnimation)
        animationSet.addAnimation(scaleAnimation)
        animationSet.setInterpolator(LinearInterpolator())
        breatheMarker!!.setAnimation(animationSet)
        breatheMarker!!.startAnimation()
        return breatheMarker_center
    }

    fun createMaker(end: Location): Marker? {
        return activity.mAmap.addMarker(MarkerOptions().position(LatLng(end.latitude, end.longitude)).zIndex(2f)
                .anchor(0.1f, 0.1f).icon(BitmapDescriptorFactory.fromResource(R.drawable.myself_all)))
    }


    fun createScreenMarker(): Marker? {
        var screenMarker = activity.mAmap.addMarker(MarkerOptions().title("确定选点").snippet(getString(R.string.finaly_point))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.select_point)))
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(getWindowWidth()!! / 2, getWindowHight()!! / 2)
        return screenMarker
    }

    fun setDriverRoute(startPoint: LatLonPoint, endPoint: LatLonPoint, passPointDatas: ArrayList<Location>) {
        CoroutineScope(uiContext).launch {
            activity._mActivity?.showProgressDialog("正在规划路径中......")
        }
        navi = AMapNavi.getInstance(context)
        navi.addAMapNaviListener(this)

        caculateRouteListener = activity
        var sList: MutableList<NaviLatLng> = ArrayList()
        var eList: MutableList<NaviLatLng> = ArrayList()
        var mStartLatlng = converNaviLatLngPoint(startPoint)
        var mEndLatlng = converNaviLatLngPoint(endPoint)
        sList.add(mStartLatlng!!)
        eList.add(mEndLatlng!!)
        var wayPoint = ArrayList<NaviLatLng>()
        passPointDatas.forEach {
            wayPoint.add(NaviLatLng(it.latitude, it.longitude))
        }
        var strategy = DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION
        try {
            strategy = navi.strategyConvert(true, false, false, false, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        navi.calculateDriveRoute(sList, eList, wayPoint, strategy)
    }

    var caculateRouteListener: CalculateRouteListener? = null

    override fun onCalculateRouteSuccess(p0: AMapCalcRouteResult?) {
        super.onCalculateRouteSuccess(p0)
        Log.e("result", "算路成功！！！！！")
        activity?._mActivity!!.dismissProgressDialog()
        if (caculateRouteListener != null) {
            caculateRouteListener?.CalculateCallBack(p0!!)
            navi.startNavi(NaviType.GPS)
        }
    }

    override fun onCalculateRouteFailure(p0: AMapCalcRouteResult?) {
        super.onCalculateRouteFailure(p0)
        activity?._mActivity!!.dismissProgressDialog()
    }

}
package com.example.private_module.ViewModel.Picture

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.amap.api.services.weather.LocalWeatherForecastResult
import com.amap.api.services.weather.LocalWeatherLiveResult
import com.amap.api.services.weather.WeatherSearch
import com.amap.api.services.weather.WeatherSearchQuery
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.MakerEntity
import com.elder.zcommonmodule.Entity.WeatherAndLocation
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.USER_TOKEN
import com.elder.zcommonmodule.getImageUrl
import com.example.private_module.Activity.Picture.ShareEditPictureActivity
import com.example.private_module.BR
import com.example.private_module.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.GsonUtil
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_share_edit_pic.*
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.Utils.ConvertUtils.Companion.bmpToByteArray
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ShareEditPictureViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack, HttpInteface.getMakerList, View.OnClickListener, WeatherSearch.OnWeatherSearchListener {
    override fun onWeatherLiveSearched(weatherLiveResult: LocalWeatherLiveResult?, p1: Int) {
        weather.weatherResult = weatherLiveResult
    }

    override fun onWeatherForecastSearched(p0: LocalWeatherForecastResult?, p1: Int) {

    }

    override fun onClick(v: View?) {
        if (isWhite.get()!!) {
            isWhite.set(false)
        } else {
            isWhite.set(true)
        }
    }

    override fun getMakerListSuccess(it: String) {
        Log.e("result", it)
        var entitiy = GsonUtil.fromJson<ArrayList<MakerEntity>>(it, object : TypeToken<ArrayList<MakerEntity>>() {}.type)
        items.clear()
        var entity = MakerEntity()
        entity.isChecked.set(true)
        items.add(entity)
        entitiy.forEach {
            items.add(it)
        }
        initLinear()
    }

    override fun getMakerListError(ex: Throwable) {
        Log.e("result", ex.message + "异常")
    }

    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {

    }

    var currentMoveMaker = ObservableField<String>("")
    var locationTitle = ObservableField<Boolean>(false)
    var rightIcon = ObservableField<Boolean>(false)
    var path = ObservableField<String>()
    var leftImageVisible = ObservableField<Boolean>(true)
    var bottomtitle = ObservableField<String>("长沙·未来漫城")
    var items = ObservableArrayList<MakerEntity>()
    var data = arrayOf(R.drawable.warter_marker_non, R.drawable.warter_marker, R.drawable.warter_marker_q, R.drawable.warter_marker_z, R.drawable.warter_marker_m)
    var component = TitleComponent()
    var isWhite = ObservableField<Boolean>(true)
    lateinit var shareEditPictureActivity: ShareEditPictureActivity
    fun inject(shareEditPictureActivity: ShareEditPictureActivity) {
        this.shareEditPictureActivity = shareEditPictureActivity
//        shareEditPictureActivity.move_img.setOnClickListener(this)
        HttpRequest.instance.getMakerListResult(this)
        var map = HashMap<String, String>()
        HttpRequest.instance.getMakerList(map)
        shareEditPictureActivity.move_tv.setOnClickListener(this)
        shareEditPictureActivity.move_right.setOnClickListener(this)
        component.title.set(getString(R.string.share))
        component.arrowVisible.set(false)
        component.rightText.set("")
        path.set(shareEditPictureActivity.url)
        component.setCallBack(this)
        RxSubscriptions.add(RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
            invoke(it)
        })
        var pos = ServiceEven()
        pos.type = "HomeStart"
        RxBus.default?.post(pos)
//        context.startService(Intent(context, LowLocationService::class.java).setAction("start"))
    }


    fun invoke(location: AMapLocation) {
        if (location!!.aoiName.isNullOrEmpty()) {
            weather.location = location!!.poiName
        } else {
            weather.location = location!!.aoiName
        }
        var mquery = WeatherSearchQuery(location.city, WeatherSearchQuery.WEATHER_TYPE_LIVE)
        var mweathersearch = WeatherSearch(context)
        mweathersearch.setOnWeatherSearchListener(this)
        mweathersearch.query = mquery
        mweathersearch.searchWeatherAsyn()
    }

    var weather: WeatherAndLocation = WeatherAndLocation()
    var lastView: MakerEntity? = null
    fun initLinear() {
        var linear = shareEditPictureActivity.findViewById<LinearLayout>(R.id.share_horizontallayout)
        items.forEachIndexed { index, makerEntity ->
            var t = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var binding = DataBindingUtil.inflate<ViewDataBinding>(t, R.layout.add_warter_mark_type_layout, linear, false)
            var view = binding.root
            var img = view.findViewById<ImageView>(R.id.acse)
            if (index == 0) {
                lastView = makerEntity
                img.setImageResource(R.drawable.warter_marker_non)
            } else {
                var options = RequestOptions().timeout(3000).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true)
                Glide.with(img.context).asBitmap().load(getImageUrl(makerEntity?.smallImgUrl)).apply(options).into(img)
            }
            img.setOnClickListener {
                //                itemClick.HorizontalScrollViewItemClick(it, m)
                if (!makerEntity.isChecked.get()!!) {
                    lastView?.isChecked?.set(false)
                    makerEntity.isChecked.set(true)
                    lastView = makerEntity
                    Log.e("result", makerEntity.imgUrl + "")
                    if (makerEntity.imgUrl == null) {
                        currentMoveMaker.set("")
                    } else {
                        currentMoveMaker.set(getImageUrl(makerEntity.imgUrl))
                    }
                    if (makerEntity.type == 0) {
                        locationTitle.set(false)
                        rightIcon.set(true)
                    } else if (makerEntity.type == 2) {
                        leftImageVisible.set(true)
//                        shareEditPictureActivity.move_tv.resetWidth()
                        locationTitle.set(true)
                        rightIcon.set(true)
                        leftImageVisible.set(true)
                        if (weather != null) {
                            bottomtitle.set(weather?.location)
                        }
                    } else if (makerEntity.type == 1) {
//                        shareEditPictureActivity.move_tv.addWidth(ConvertUtils.dp2px(30F))
                        locationTitle.set(true)
                        leftImageVisible.set(false)
                        var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        var d = Date(System.currentTimeMillis())
                        var sss = simple.format(d)
                        if (weather != null) {
                            if (!weather?.weatherResult?.liveResult?.weather.isNullOrEmpty()) {
                                bottomtitle.set(sss + " " + weather?.weatherResult?.liveResult?.weather)
                            } else {
                                bottomtitle.set("$sss ")
                            }
                        }
                        rightIcon.set(true)
                    }

//                    else if (makerEntity.type == 3) {
//                        leftImageVisible.set(false)
////                        shareEditPictureActivity.move_tv.addWidth(ConvertUtils.dp2px(15F))
//                        locationTitle.set(true)
//                        bottomtitle.set("2019-03-09 14:45 晴")
//                        rightIcon.set(true)
//                    }
                }
            }
            binding.setVariable(BR.makerEntity, makerEntity)
            linear.addView(view)
        }
        linear.invalidate()
    }

    fun onBottomClick(view: View) {
        when (view.id) {
            R.id.share_frend -> {
                if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                    Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                } else {
                    var layout = shareEditPictureActivity.findViewById<RelativeLayout>(R.id.save_layout)
                    var bitmap = ConvertUtils.view2Bitmap(layout)
                    var m = bitmap?.height!!.toDouble() / ConvertUtils.dp2px(60F)
                    var newBitmap = Bitmap.createBitmap(bitmap, 0, 0, (bitmap.width / m).toInt(), ConvertUtils.dp2px(60F))
                    var imgobj = WXImageObject(bitmap)
                    var msg = WXMediaMessage()
                    msg.mediaObject = imgobj
                    msg.title = getString(R.string.share_title)
                    msg.thumbData = bmpToByteArray(newBitmap!!, true)
                    var req = SendMessageToWX.Req()
                    req.transaction = System.currentTimeMillis().toString() + "img"
                    req.message = msg
                    req.scene = SendMessageToWX.Req.WXSceneSession
                    BaseApplication.getInstance().mWxApi.sendReq(req)
                }
            }
            R.id.share_frendQ -> {
                if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                    Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                } else {
                    var layout = shareEditPictureActivity.findViewById<RelativeLayout>(R.id.save_layout)
                    var bitmap = ConvertUtils.view2Bitmap(layout)
                    var m = bitmap?.height!!.toDouble() / ConvertUtils.dp2px(60F)
                    var newBitmap = Bitmap.createBitmap(bitmap, 0, 0, (bitmap.width / m).toInt(), ConvertUtils.dp2px(60F))
                    var imgobj = WXImageObject(bitmap)
                    var msg = WXMediaMessage()
                    msg.mediaObject = imgobj
                    msg.title = getString(R.string.share_title)
                    msg.thumbData = bmpToByteArray(newBitmap!!, true)
                    var req = SendMessageToWX.Req()
                    req.transaction = System.currentTimeMillis().toString() + "img"
                    req.message = msg
                    req.scene = SendMessageToWX.Req.WXSceneTimeline
                    BaseApplication.getInstance().mWxApi.sendReq(req)
                }
            }
            R.id.share_download -> {
                var bitmap = ConvertUtils.view2Bitmap(shareEditPictureActivity.save_layout)
                val sdf = SimpleDateFormat("yyyyMMddHHmmss")
                var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + File.separator + sdf.format(Date()) + ".jpg")
                var fos = FileOutputStream(file.path)
                var b = bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                if (b!!) {
                    Toast.makeText(context, getString(R.string.save_local_success), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, getString(R.string.save_local_fail), Toast.LENGTH_SHORT).show()
                }
                MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, file.name, null)
                finish()
            }
            R.id.restore_net_phone -> {
                var bitmap = ConvertUtils.view2Bitmap(shareEditPictureActivity.save_layout)
                val sdf = SimpleDateFormat("yyyyMMddHHmmss")
                var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + File.separator + sdf.format(Date()) + ".jpg")
                var fos = FileOutputStream(file.path)
                var b = bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                if (b!!) {
                    var token = PreferenceUtils.getString(context, USER_TOKEN)
                    Observable.create(ObservableOnSubscribe<Response> {
                        var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                        var mult = MultipartBody.Builder().setType(MultipartBody.FORM)
                        mult.addFormDataPart("files", file.name, RequestBody.create(MediaType.parse("image/jpg"), file))
                        var request = Request.Builder().addHeader("appToken", token).url(Base_URL + "AmoskiActivity/userCenterManage/uploadFile").post(mult.build()).build()
                        var call = client.newCall(request)
                        var response = call.execute()
                        it.onNext(response)
                    }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                        return@Function it.body()!!.string()
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                        Log.e("result", it)
                        var response = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                        if (response.code == 0) {
                            Toast.makeText(context, getString(R.string.save_net_success), Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }
//                    pictureSelectorActivity.dismissProgressDialog()
//                    if (deletFile.size != 0) {
//                        deletFile.forEach {
//                            it.delete()
//                        }
//                    }
                    }

                } else {
                    Toast.makeText(context, getString(R.string.save_net_fail), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
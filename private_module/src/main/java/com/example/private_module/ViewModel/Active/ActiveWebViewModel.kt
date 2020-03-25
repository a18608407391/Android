package com.example.private_module.ViewModel.Active

import android.content.Intent
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.USER_TOKEN
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.private_module.Activity.Active.ActiveWebActivity
import com.example.private_module.R
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.modelpay.PayReq
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.active_web_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.WX_APP_ID
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.lang.Exception
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ActiveWebViewModel : BaseViewModel() {


    var startWx = false


    lateinit var ac: ActiveWebActivity
    fun inject(activeWebActivity: ActiveWebActivity) {
        this.ac = activeWebActivity
        if (activeWebActivity.type == 0) {
            webUrl.set(Base_URL + "/AmoskiWebActivity/personalcenter/roadbookActivitype/order/orderList.html?appToken=" + PreferenceUtils.getString(context, USER_TOKEN) + "&type=app")
        } else if (activeWebActivity.type == 1) {
            webUrl.set(Base_URL + "/AmoskiWebActivity/personalcenter/roadbookActivitype/eticket/eticketList.html?appToken=" + PreferenceUtils.getString(context, USER_TOKEN) + "&type=app")
        } else if (activeWebActivity.type == 2) {
            webUrl.set(Base_URL + "/AmoskiWebActivity/personalcenter/roadbookActivitype/activity/detail.html?id=" + activeWebActivity.id + "&type=app")
        } else if (activeWebActivity.type == 3) {
            Log.e("result", "加载")
            webUrl.set("$Base_URL/AmoskiWebActivity/personalcenter/roadbookActivitype/activity/activityList.html?appToken=" + PreferenceUtils.getString(context, USER_TOKEN) + "&type=app")
        } else if (activeWebActivity.type == 4) {
            Log.e("result", "$Base_URL/AmoskiWebActivity/personalcenter/roadbookActivitype/activity/detail.html?appToken=" + PreferenceUtils.getString(context, USER_TOKEN) + "id=" + activeWebActivity.id + "&type=app")
            webUrl.set("$Base_URL/AmoskiWebActivity/personalcenter/roadbookActivitype/activity/detail.html?appToken=" + PreferenceUtils.getString(context, USER_TOKEN) + "&id=" + activeWebActivity.id + "&type=app")
        } else if (activeWebActivity.type == 5) {
            webUrl.set("$Base_URL/AmoskiWebActivity/personalcenter/roadbookActivitype/order/index.html?appToken=" + PreferenceUtils.getString(context, USER_TOKEN) + "&id=" + activeWebActivity.id + "&code=" + activeWebActivity.code + "&type=app")
        }
    }


    var webUrl = ObservableField<String>("")
    var roadCommand = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            Log.e("result", t + "当前链接")
            if (t.endsWith("gotoApp")) {
                if (ac.type < 5) {
                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
                    finish()
                } else {
                    finish()
                }
            } else if (t.contains("gotoTicketList")) {
                ac.type = 1
                webUrl.set(Base_URL + "/AmoskiWebActivity/personalcenter/roadbookActivitype/eticket/eticketList.html?appToken=" + PreferenceUtils.getString(context, USER_TOKEN) + "&type=app")
                RxBus.default!!.post("ActiveWebGotoApp")
            } else if (t.startsWith("alipays://platformapi")) {
                if (t.contains("end")) {
                    var m = t.split("end")
                    var ur = m[0] + "end"
                    var k = ur.replace("com.eg.android.AlipayGphone", ac.packageName)
                    try {
                        var uri = Uri.parse(k)
                        var intent = Intent(Intent.ACTION_VIEW, uri)
                        ac.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "支付错误！请检查是否安装支付宝", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (t.startsWith("weixin://wap/pay")) {
//                var m = t.split("&")
//                var sign = m[3].split("=")[1]
//                var noncestr = m[2].split("=")[1]
//                var packages = m[1].split("=")[1]
//
//                var pay = PayReq()
//                pay.appId = WX_APP_ID
//                pay.sign = sign
//                pay.partnerId = "1542700051"
//                pay.packageValue = packages
//                pay.prepayId = "wx" + m[0].split("wx")[1]
//                Log.e("result", "sign" + sign + "nocestr" + noncestr + "prepay" + pay.prepayId + "partnerId" + pay.partnerId)
//                pay.nonceStr = noncestr
////                pay.partnerId
//                pay.timeStamp = System.currentTimeMillis().toString()
//                pay.extData = "app data"
//                BaseApplication.getInstance().mWxApi.sendReq(pay)

                var m = t + "&redirect_url=" + ac.packageName
                try {
                    var uri = Uri.parse(m)
                    var intent = Intent(Intent.ACTION_VIEW, uri)
                    ac.startActivity(intent)
                    startWx = true
                } catch (e: Exception) {
                    Toast.makeText(context, "支付错误！请检查是否安装微信", Toast.LENGTH_SHORT).show()
                }
            } else if (t == "com.elder.amoski://") {
                ac.web_active.loadUrl("http://yomoy.com.cn/AmoskiWebActivity/personalcenter/roadbookActivitype/order/orderPayment.html?isWeiXin=true")
                return
            } else if (t.startsWith("https://mclient.alipay.com/h5Continue.htm")) {
                ac.web_active.loadUrl(Base_URL + "/AmoskiWebActivity/personalcenter/roadbookActivitype/order/orderList.html?appToken=" + PreferenceUtils.getString(context, USER_TOKEN) + "&type=app")
            } else if (t.contains("/AmoskiWebActivity/personalcenter/album/shopalbum.html") || t.contains("/AmoskiWebActivity/personalcenter/roadbookActivitype/activity/detail.html")) {
                if (t.contains("code=")) {
                    var msg = t.split("&")
// http://yomoy.com.cn/AmoskiWebActivity/personalcenter/roadbookActivitype/activity/detail.html?id=10&endTime=1572404400000&ImgUrl=AmoskiActivity/appRidingGuideManage/getActivityImages?code=10&platform=wx&TitleTxt=%E9%95%BF%E6%B2%99%EF%BC%9A%E7%BB%85%E5%A3%AB%E9%AA%91%E8%A1%8C%E6%B4%BB%E5%8A%A8DGR

                    var html = t
                    var endtime = ""
                    var title = ""
                    var imgUrl = ""
                    var time = ""
                    var flag = false
                    if (t.contains("shopalbum")) {
                        title = msg[1]
                        imgUrl = msg[2]
                        flag = true
                    } else if (t.contains("activity/detail")) {
                        endtime = msg[1]
                        title = msg[4]
                        imgUrl = msg[2]
                        time = endtime.split("=")[1]
                        flag = false
                    }
                    imgUrl = imgUrl.substring(7)
                    var content = title.split("=")[1]
                    Log.e("result", "content" + URLDecoder.decode(content) + "imgUrl" + imgUrl)
                    var shareDialog = DialogUtils.createShareDialog(ac, "", "")
                    shareDialog?.findViewById<TextView>(R.id.share_frend)?.setOnClickListener {
                        if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                            CoroutineScope(uiContext).launch {
                                Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            shareWx(SendMessageToWX.Req.WXSceneSession, imgUrl, URLDecoder.decode(content), html, time, flag)
                        }
                        shareDialog!!.dismiss()
                    }
                    shareDialog?.findViewById<TextView>(R.id.share_frendQ)?.setOnClickListener {
                        if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                            CoroutineScope(uiContext).launch {
                                Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            shareWx(SendMessageToWX.Req.WXSceneTimeline, imgUrl, URLDecoder.decode(content), html, time, flag)
                        }
                        shareDialog!!.dismiss()
                    }
                } else {
                    ac.web_active.loadUrl(t)
                }
            } else {
                var url = t
                if (t.startsWith("http://yomoy.com.cn//AmoskiWebActivity/personalcenter/roadbookActivitype/order/index.html")) {
                    url = url + "&appToken=" + PreferenceUtils.getString(context, USER_TOKEN)
                }
                ac.web_active.loadUrl(url)
            }
        }
    })
    var LoadingFinishCommand = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            ac.dismissProgressDialog()
        }
    })

    fun shareWx(type: Int, url: String, title: String, html: String, time: String, flag: Boolean) {

        Log.e("result", "图片地址" + Base_URL + url)

        ac.showProgressDialog(getString(R.string.http_loading))
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            var request = Request.Builder().url(Base_URL + url).get().build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, Bitmap> {
            Log.e("result", "图片转213")
            return@Function BitmapFactory.decodeStream(it.body()?.byteStream())
        }).subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<Bitmap> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(it: Bitmap) {
                Log.e("result", "图片转" + it.height + it.width)
                var newBitmap = ConvertUtils.compressByQuality(it, 32000, true)
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                var wx = WXWebpageObject()
                wx.webpageUrl = html
                var msg = WXMediaMessage()
                msg.mediaObject = wx
                msg.title = title
                if (!flag) {
                    msg.description = Base_URL
//                    msg.description = "开始时间：" + sdf.format(Date(time.toLong()))
                } else {
                    msg.description = Base_URL
                }
//                Log.e("result", html + "html" + title + "time" + sdf.format(Date(time.toLong())))
                msg.thumbData = newBitmap
                var req = SendMessageToWX.Req()
                req.transaction = System.currentTimeMillis().toString() + "img"
                req.message = msg
                req.scene = type
                BaseApplication.getInstance().mWxApi.sendReq(req)
                ac.dismissProgressDialog()
            }

            override fun onError(e: Throwable) {
                Toast.makeText(ac, "网络错误！", Toast.LENGTH_SHORT).show()
                ac.dismissProgressDialog()
            }
        })

//        CoroutineScope(ioContext).async {
//            var file = Glide.with(ac)
//                    .load(Base_URL + url)
//                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//            var files = file.get()
//
//            if (files != null) {
//
////                var arr = Base64.decode(files.readBytes(), Base64.DEFAULT)
//                var bitmap = BitmapFactory.decodeByteArray(files.readBytes(),0,files.readBytes().size)
//
//
//
//
//
//            } else {
//                Toast.makeText(context, "图片获取失败", Toast.LENGTH_SHORT).show()
//            }
    }
}
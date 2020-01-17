package com.elder.blogin.ViewModel

import android.databinding.ObservableField
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.blogin.Activity.WebActivity
import com.elder.blogin.R
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.SHARE_PICTURE
import com.elder.zcommonmodule.USER_TOKEN
import com.elder.zcommonmodule.Utils.FileSystem
import com.elder.zcommonmodule.Utils.FileUtils
import com.elder.zcommonmodule.getImageUrl
import com.tencent.smtt.export.external.interfaces.JsPromptResult
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.ioContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import org.cs.tec.library.http.DownLoadManager
import org.cs.tec.library.http.download.ProgressCallBack
import java.io.File


class WebViewModel : BaseViewModel() {


    var titleVisible = ObservableField<Boolean>(true)
    var loadWeb = ObservableField<String>()
    var title = ObservableField<String>()
    lateinit var webActivity: WebActivity
    fun inject(webActivity: WebActivity) {
        this.webActivity = webActivity
        if (this.webActivity?.type == 0) {
            loadWeb.set("http://122.114.91.150:8181/index.html")
            titleVisible.set(true)
        } else if (this.webActivity?.type == 1) {
            title.set(getString(R.string.log_fr_bottom_title))
            titleVisible.set(true)
            loadWeb.set("https://mp.weixin.qq.com/s/x7k6xJM_grpqgIkNdL4Dyw")
        } else if (this.webActivity?.type == 2) {
            title.set(getString(R.string.active_log_fr_bottom_title))
            titleVisible.set(true)
            var token = PreferenceUtils.getString(context, USER_TOKEN)
            loadWeb.set("$Base_URL/AmoskiWebActivity/personalcenter/album/shopalbum.html?appToken=$token")
        }

        webActivity?.web.addJavascriptInterface(this, "wx")
        webActivity?.web.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                Log.e("result", "onJsAlert")

                return super.onJsAlert(p0, p1, p2, p3)
            }

            override fun onJsConfirm(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                Log.e("result", "onJsConfirm")
                return super.onJsConfirm(p0, p1, p2, p3)
            }

            override fun onJsBeforeUnload(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                Log.e("result", "onJsBeforeUnload")
                return super.onJsBeforeUnload(p0, p1, p2, p3)
            }

            override fun onJsPrompt(p0: WebView?, p1: String?, p2: String?, p3: String?, p4: JsPromptResult?): Boolean {
                Log.e("result", "onJsPrompt")
                return super.onJsPrompt(p0, p1, p2, p3, p4)
            }

            override fun onJsTimeout(): Boolean {
                Log.e("result", "onJsTimeout")
                return super.onJsTimeout()
            }
        }
    }

    fun onClick(view: View) {
        if(webActivity.type==0){
            ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation()
        }else{
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
        }
        webActivity.finish()
    }
    var time = 0L
    var command = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            if (System.currentTimeMillis() - time > 500) {
                time = System.currentTimeMillis()
            } else {
                return
            }
            if (t.contains("activityImgDown")) {
                webActivity.showProgressDialog("下载中......")
                DownLoadManager.getInstance().load(t, object : Observer<File> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(file: File) {
                        webActivity.dismissProgressDialog()
                        CoroutineScope(ioContext).async {
                            FileSystem.unzipFile(file.path, Environment.getExternalStorageDirectory().getPath() + "/Amoski/Pic")
                        }

                        Toast.makeText(context, "下载成功",Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                    }
                })

//                webActivity.showProgressDialog("开始下载中0%")
//                var time = System.currentTimeMillis()
//                DownLoadManager.getInstance().load(t, object : ProgressCallBack<File>(Environment.getExternalStorageDirectory().getPath() + "/Amoski", time.toString() + "img" + ".zip") {
//                    override fun onSuccess(file: File) {
//                        Log.e("result", "成功")
//                        webActivity.dismissProgressDialog()
//                        FileSystem.unzipFile(file.path, file.path + "/Pic")
//                        webActivity.web.reload()
//                    }
//
//                    override fun progress(progress: Long, total: Long) {
//                        var persent = (progress / total) * 100
//                        webActivity.progressDialog?.setTitle("正在下载中$persent%")
//                    }
//
//                    override fun onError(e: Throwable) {
//                        Log.e("result", e.toString())
//                    }
//                })
            } else {
                var s = t.split("imgUrl=")
                var m = s[1].split("&")
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.ADD_MARKER_ACTIVITY).withString(RouterUtils.PrivateModuleConfig.ADD_MARKER_URL, getImageUrl(m[0])).navigation()
            }
        }
    })
}
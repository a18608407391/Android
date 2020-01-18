package com.zk.library.Base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.zk.library.BuildConfig
import com.zk.library.Bus.ServiceEven
import com.zk.library.Bus.event.ActivityDestroyEven
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.WX_APP_ID
import org.cs.tec.library.http.NetworkUtil
import java.util.concurrent.TimeUnit
import android.content.Intent
import cn.jpush.im.android.api.model.Message


open class BaseApplication : Application() {
    var getWidthPixels = 0
    var getHightPixels = 0
    var curActivity = 0


    companion object {
        private lateinit var instanc: BaseApplication
        var MinaConnected = false
        var isClose = false

        @Synchronized
        fun setApplication(baseApplication: BaseApplication) {
            instanc = baseApplication
            baseApplication.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                override fun onActivityPaused(activity: Activity?) {
                }

                override fun onActivityResumed(activity: Activity?) {
                }

                override fun onActivityStarted(activity: Activity?) {
                }

                override fun onActivityDestroyed(activity: Activity?) {
                    Log.e("result", "destroy" + activity?.javaClass!!.simpleName!!)
                    RxBus.default?.post(ActivityDestroyEven(activity?.javaClass!!.simpleName!!))
                    AppManager.get()!!.removeActivity(activity)
                }

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                    Log.e("result", "onActivitySaveInstanceState" + activity!!.componentName)
                }

                override fun onActivityStopped(activity: Activity?) {
                }

                override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                    AppManager.get()!!.addActivity(activity!!)
                    Log.e("result", "onActivityCreated" + activity!!.componentName)
                }
            })
        }

        fun getInstance(): BaseApplication {
            return instanc
        }

        val IMAGE_MESSAGE = 1
        val TAKE_PHOTO_MESSAGE = 2
        val TAKE_LOCATION = 3
        val FILE_MESSAGE = 4
        val TACK_VIDEO = 5
        val TACK_VOICE = 6
        val BUSINESS_CARD = 7
        val REQUEST_CODE_SEND_FILE = 26
        var registerOrLogin: Long = 1
        val REQUEST_CODE_TAKE_PHOTO = 4
        val REQUEST_CODE_SELECT_PICTURE = 6
        val REQUEST_CODE_CROP_PICTURE = 18
        val REQUEST_CODE_CHAT_DETAIL = 14
        val RESULT_CODE_FRIEND_INFO = 17
        val REQUEST_CODE_ALL_MEMBER = 21
        val RESULT_CODE_EDIT_NOTENAME = 29
        val NOTENAME = "notename"
        val REQUEST_CODE_AT_MEMBER = 30
        val RESULT_CODE_AT_MEMBER = 31
        val RESULT_CODE_AT_ALL = 32
        val SEARCH_AT_MEMBER_CODE = 33

        val RESULT_BUTTON = 2
        val START_YEAR = 1900
        val END_YEAR = 2050
        val RESULT_CODE_SELECT_FRIEND = 23

        val REQUEST_CODE_SELECT_ALBUM = 10
        val RESULT_CODE_SELECT_ALBUM = 11
        val RESULT_CODE_SELECT_PICTURE = 8
        val REQUEST_CODE_BROWSER_PICTURE = 12
        val RESULT_CODE_BROWSER_PICTURE = 13
        val RESULT_CODE_SEND_LOCATION = 25
        val RESULT_CODE_SEND_FILE = 27
        val REQUEST_CODE_SEND_LOCATION = 24
        val REQUEST_CODE_FRIEND_INFO = 16
        val RESULT_CODE_CHAT_DETAIL = 15
        val REQUEST_CODE_FRIEND_LIST = 17
        val ON_GROUP_EVENT = 3004
        val DELETE_MODE = "deleteMode"
        val RESULT_CODE_ME_INFO = 20
        var forwardMsg: List<Message> = ArrayList()
//        val CONV_TYPE = "conversationType" //value使用 ConversationType
        val ROOM_ID = "roomId"
        val POSITION = "position"


        val MSG_JSON = "msg_json"
        val MSG_LIST_JSON = "msg_list_json"
        val NAME = "name"
        val ATALL = "atall"
        val SEARCH_AT_MEMBER_NAME = "search_at_member_name"
        val SEARCH_AT_MEMBER_USERNAME = "search_at_member_username"
        val SEARCH_AT_APPKEY = "search_at_appkey"

        var isAtMe: Map<Long, Boolean> = HashMap()
        var isAtall: Map<Long, Boolean> = HashMap()
        //        var forwardMsg: List<Message> = ArrayList()
        var PICTURE_DIR = "sdcard/JChatDemo/pictures/"
        private val JCHAT_CONFIGS = "JChat_configs"
        var FILE_DIR = "sdcard/JChatDemo/recvFiles/"
        var VIDEO_DIR = "sdcarVIDEOd/JChatDemo/sendFiles/"
        var THUMP_PICTURE_DIR: String? = null
        val ATUSER = "atuser"
        var maxImgCount: Int = 0               //允许选择图片最大数
        val GROUP_NAME = "groupName"
    }

    fun getScreenWidths(): Int {
        return getWidthPixels
    }

    fun getScreenHights(): Int {
        return getHightPixels
    }

    var Reconnected: Disposable? = null
    override fun onCreate() {
        super.onCreate()
        setApplication(this)
        RxJavaPlugins.setErrorHandler {
            //            Log.e("result", it.message + "网络错误信息")
        }
        registerWx()
        RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "MINA_FORCE_CLOSE") {
                MinaConnected = false
                RxBus.default!!.post("AppMINA_FORCE_CLOSE")
                if (!isClose) {
                    var flag = NetworkUtil.isNetworkAvailable(context)
                    if (flag) {
                        if (!MinaConnected) {
                            var pos = ServiceEven()
                            pos.type = "splashContinue"
                            RxBus.default?.post(pos)
                        } else {
                            isClose = false
                            Reconnected?.dispose()
                        }
                    } else {
                        Reconnected = Observable.interval(0, 8, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
                            if (flag && !MinaConnected) {
                                var pos = ServiceEven()
                                pos.type = "splashContinue"
                                RxBus.default?.post(pos)
                            } else if (MinaConnected) {
                                isClose = false
                                Reconnected?.dispose()
                            }
                        }
                    }
                }
            } else if (it == "MinaConnected") {
                MinaConnected = true
                isClose = false
                RxBus.default!!.post("AppMinaConnected")
            }
        }
    }
    lateinit var mWxApi: IWXAPI
    private fun registerWx() {
        mWxApi = WXAPIFactory.createWXAPI(this, WX_APP_ID, false)
        // 将该app注册到微信
        mWxApi.registerApp(WX_APP_ID)
        var map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        QbSdk.initTbsSettings(map)
        var intent = Intent(this, PreLoadX5Service::class.java)
        startService(intent)
    }
}

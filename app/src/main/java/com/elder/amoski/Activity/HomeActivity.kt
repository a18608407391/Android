package com.elder.amoski.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.work.WorkManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.amoski.BR
import com.elder.amoski.R
import com.elder.amoski.ViewModel.HomeViewModel
import com.elder.amoski.databinding.ActivityHomeBinding
import com.elder.zcommonmodule.Entity.UserInfo
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Bus.RxBus
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Entity.HotData
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.LoginStateChangeEvent
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.amap.api.location.AMapLocation
import com.cstec.administrator.chart_module.Fragment.MessageFragment
import com.cstec.administrator.chart_module.Utils.Extras
import com.cstec.administrator.chart_module.Utils.RequestCode
import com.elder.amoski.Fragment.HomeFragment
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Even.RequestErrorEven
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.FileSystem
import com.elder.zcommonmodule.Utils.Utils
import com.example.private_module.UI.UserInfoFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zk.library.Base.Transaction.anim.DefaultHorizontalAnimator
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Bus.event.RxBusEven.Companion.CHAT_ROOM_ACTIVITY_RETURN
import com.zk.library.Worker.WorkRequest
import org.cs.tec.library.Bus.RxSubscriptions
import org.json.JSONObject
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


@Route(path = RouterUtils.ActivityPath.HOME)
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(), HttpInteface.ExitLogin, HttpInteface.IRelogin {
    override fun IReloginSuccess(t: String) {
        var resp = Gson().fromJson<BaseResponse>(t, BaseResponse::class.java)
        PreferenceUtils.putString(context, USER_TOKEN, resp.data.toString())
        PreferenceUtils.putLong(context, TOKEN_LIMIT, System.currentTimeMillis())
        BaseApplication.getInstance().lastTokenTime = System.currentTimeMillis()
        BaseApplication.getInstance().TokenTimeOutCheck = false
    }

    override fun doTouchEven(ev: MotionEvent?): Boolean {
        super.doTouchEven(ev)
        if (!BaseApplication.getInstance().TokenTimeOutCheck) {
            if (System.currentTimeMillis() - BaseApplication.getInstance().lastTokenTime >= BaseApplication.getInstance().TokenTimeOutLimit) {
                Log.e("result", "登录过期")
                BaseApplication.getInstance().TokenTimeOutCheck = true
                var phone = PreferenceUtils.getString(context, USER_PHONE)
                var map = HashMap<String, String>()
                map["phoneNumber"] = FileSystem.getPhoneBase64(phone!!)
                map["type"] = "app"
                HttpRequest.instance.ReLoginImpl = this
                HttpRequest.instance.relogin(map)
                return false
            }
        }
        return true
    }

    override fun IReloginError() {
    }

    override fun ExitLoginSuccess(it: String) {
        exitForce()
    }

    override fun ExitLoginError(ex: Throwable) {
//        Toast.makeText(context, "退出登录错误", Toast.LENGTH_SHORT).show()
    }

    override fun initVariableId(): Int {
        return BR.HomeViewModel
    }

    @Autowired(name = RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY)
    @JvmField
    var resume: String? = null

    @Autowired(name = RouterUtils.ActivityPath.AMAP_LOCATION)
    @JvmField
    var location: AMapLocation? = null


    @Autowired(name = RouterUtils.MapModuleConfig.RESUME_MAP_TEAMCODE)
    @JvmField
    var code: String? = null

    override fun initContentView(savedInstanceState: Bundle?): Int {
        Utils.setStatusBar(this, false, false)
        return R.layout.activity_home
    }

    override fun initViewModel(): HomeViewModel? {
        return ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        BaseApplication.getInstance().curActivity = 1
        var key = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clip = key.primaryClip
        if (clip != null && clip.itemCount > 0 && clip.getItemAt(0).text != null) {
            var tv = clip.getItemAt(0).text.toString()
            if (!tv.isNullOrEmpty() && tv.contains("Amoski:HDID=")) {
                var number = tv.split("=")[1]
                var dialog = DialogUtils.createNomalDialog(this@HomeActivity, getString(R.string.active_notify), getString(R.string.cancle), getString(R.string.confirm))
                dialog.show()
                dialog.setOnBtnClickL(OnBtnClickL {
                    key.clearPrimaryClip()
                    dialog.dismiss()
                }, OnBtnClickL {
                    dialog.dismiss()
                    clip = ClipData.newPlainText("label", "")
                    key.primaryClip = clip
                    ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_AC).withInt(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_TYPE, 4).withString(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_ID, number).navigation()
                })
            }
        }

        if (homeFr!!.viewModel!!.returnPrivate) {
            homeFr!!.viewModel?.mRadioGroup!!.check(R.id.main_right)
            homeFr!!.viewModel?.returnPrivate = false
        }
    }

    var homeFr: HomeFragment? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData() {
        super.initData()
        //这里处理所有Fragment跳转动画
        fragmentAnimator = DefaultHorizontalAnimator()
        JMessageClient.registerEventReceiver(this)
        homeFr = HomeFragment.newInstance()
        loadRootFragment(R.id.home_main_layout, homeFr!!)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        mViewModel?.inject(this)
        RxSubscriptions.add(RxBus.default?.toObservable(RequestErrorEven::class.java)?.subscribe {
            Log.e(this.javaClass.name, "${it.errorCode}")
            if (it.errorCode == 10009) {
//                exitForce()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("result", requestCode.toString() + "requestCode" + resultCode)
        when (requestCode) {
            GET_USERINFO -> {
                if (data != null) {
                    var info = data?.extras!!["userInfo"] as UserInfo
                    var fr = homeFr!!.viewModel?.myself as UserInfoFragment
                    fr.callback(info)
//                    var even = ActivityResultEven(requestCode, )
//                    RxBus.default?.post(even)
                }
            }
            999 -> {
                if (resultCode == 0) {
//                    Toast.makeText(context, "后台數據權限开启未启动", Toast.LENGTH_SHORT).show()
                } else if (resultCode == Activity.RESULT_OK) {
                    PreferenceUtils.putBoolean(context, "OPEN_GOD_MODEL", true)
                }
            }

            REQUEST_LOAD_ROADBOOK -> {
                if (data != null) {
                    var date = data.getSerializableExtra("hotdata") as HotData
                    RxBus.default?.post(date)
                    ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY)
                            .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                            .withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "myroad")
                            .withSerializable(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY_ROAD, date)
                            .navigation()
                    finish()
                }
            }

            REQUEST_DISCOVER_LOAD_ROADBOOK -> {//路书页-》发现页路书
                if (data != null) {
                    var data = data!!.getSerializableExtra("hotdata") as HotData
                    var pos = ServiceEven()
                    pos.type = "HomeDriver"
                    RxBus.default?.post(pos)
                    ARouter.getInstance()
                            .build(RouterUtils.MapModuleConfig.MAP_ACTIVITY)
                            .withString("type", "discover2RoodBook")
                            .withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal")
                            .withSerializable(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY_ROAD, data)
                            .navigation()
                }
            }

            SOCIAL_DETAIL_RETURN -> {
                if (data != null) {
                    homeFr!!.viewModel?.social!!.initResult(data)
                }
            }
            PRIVATE_DATA_RETURN -> {
                var fr = homeFr!!.viewModel?.myself as UserInfoFragment
                fr.getUserInfo(false)
            }
            MSG_RETURN_REQUEST -> {//从消息界面返回
//                if (resultCode == MSG_RETURN_REQUEST) {
//                    main_bottom_bg.check(R.id.main_left)
//                } else {
//                    var fr = mViewModel?.myself as UserInfoFragment
//                    fr.getUserInfo(false)
//                }

            }
            MSG_RETURN_REFRESH_REQUEST -> {//@我的界面返回
                var fr = homeFr!!.viewModel?.messageFragment as MessageFragment
                fr.viewModel!!.initNet()
            }
            RequestCode.PICK_IMAGE -> {
                if (data == null) {
                    return
                }
                var flag = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false)
                if (flag) {
                    RxBus.default!!.post(RxBusEven.getInstance(CHAT_ROOM_ACTIVITY_RETURN, data))
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    var time: Long = 0
    override fun doPressBack() {
        super.doPressBack()
        if (System.currentTimeMillis() - time > 3000) {
            Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_SHORT).show()
            time = System.currentTimeMillis()
        } else {
            System.exit(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun exit() {
        HttpRequest.instance.exit = this
        HttpRequest.instance.exitLogin(HashMap())
    }

    fun exitForce() {
        RxBus.default?.post("ExiLogin")
        PreferenceUtils.putBoolean(context, RE_LOGIN, true)
        var pos = ServiceEven()
        pos.type = "HomeStop"
        RxBus.default?.post(pos)
        ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation(this, object : NavCallback() {
            override fun onArrival(postcard: Postcard?) {
                PreferenceUtils.putBoolean(context, RE_LOGIN, true)
                PreferenceUtils.putString(context, USER_TOKEN, null)
                finish()
            }
        })
    }

    fun onEventMainThread(event: LoginStateChangeEvent) {
        val reason = event.reason
        when (reason) {
            LoginStateChangeEvent.Reason.user_logout -> {
                exit()
            }
        }
    }

    override fun onBackPressedSupport() {
        Log.e("result", "onBackPressedSupport")
        super.onBackPressedSupport()
    }
}
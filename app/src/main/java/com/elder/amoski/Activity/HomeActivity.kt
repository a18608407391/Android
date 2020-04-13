package com.elder.amoski.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
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
import android.content.ClipData
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.LoginStateChangeEvent
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.cstec.administrator.chart_module.Fragment.MessageFragment
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Even.RequestErrorEven
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Utils
import com.example.private_module.UI.UserInfoFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import org.cs.tec.library.Bus.RxSubscriptions
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


@Route(path = RouterUtils.ActivityPath.HOME)
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(), HttpInteface.ExitLogin {
    override fun ExitLoginSuccess(it: String) {
        exitForce()
    }

    override fun ExitLoginError(ex: Throwable) {
        Toast.makeText(context, "退出登录错误", Toast.LENGTH_SHORT).show()
    }

    override fun initVariableId(): Int {
        return BR.HomeViewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        Log.e("home", "HomeCreated")
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
        Log.e("home", "$clip")
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData() {
        super.initData()
        Log.e("home", "initData")
        JMessageClient.registerEventReceiver(this)
        var pos = ServiceEven()
        pos.type = "HomeStart"
        RxBus.default?.post(pos)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        mViewModel?.inject(this)
        main_bottom_bg.setOnCheckedChangeListener(mViewModel)
        RxSubscriptions.add(RxBus.default?.toObservable(RequestErrorEven::class.java)?.subscribe {
            Log.e(this.javaClass.name, "${it.errorCode}")
            if (it.errorCode == 10009) {
                exitForce()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("result", requestCode.toString() + "requestCode" + resultCode)
        when (requestCode) {
            GET_USERINFO -> {
                if (data != null) {
                    var info = data?.extras!!["userInfo"] as UserInfo
                    var fr = mViewModel?.myself as UserInfoFragment
                    fr.callback(info)
//                    var even = ActivityResultEven(requestCode, )
//                    RxBus.default?.post(even)
                }
            }
            999 -> {
                if (resultCode == 0) {
                    Toast.makeText(context, "后台活动开启未启动", Toast.LENGTH_SHORT).show()
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
                    Log.e("debug", "${this.localClassName};${data.title}")
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
                    mViewModel?.social!!.initResult(data)
                }
            }
            PRIVATE_DATA_RETURN -> {
                var fr = mViewModel?.myself as UserInfoFragment
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
                var fr = mViewModel?.messageFragment as MessageFragment
                fr.viewModel!!.initNet()
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
}
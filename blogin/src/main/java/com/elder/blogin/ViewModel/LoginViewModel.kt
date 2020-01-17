package com.elder.blogin.ViewModel

import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.blogin.Activity.LoginActivity
import com.elder.blogin.R
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import com.tencent.mm.opensdk.modelmsg.SendAuth
import android.widget.Toast
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.CameraPosition
import com.zk.library.Base.BaseApplication
import org.cs.tec.library.Base.Utils.context
import kotlinx.android.synthetic.main.activity_login.*
import org.cs.tec.library.WX_APP_ID


class LoginViewModel : BaseViewModel(){

    lateinit var loginActivity: LoginActivity

    fun onClick(view: View) {
        when (view.id) {
            R.id.login_toRegister -> {
                //前往注册界面
                ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 1).navigation()
            }
            R.id.login_toInfo -> {
                //前往登录界面
                ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_PASSWORD).navigation()
                finish()
            }
            R.id.login_toWx -> {
                //微信认证登录
//                val compat = ActivityOptionsCompat.makeScaleUpAnimation(loginActivity.root_layout, loginActivity.root_layout.getWidth() / 2, loginActivity.root_layout.getHeight() / 2, 0, 0)
//                ARouter.getInstance().build( RouterUtils.ActivityPath.HOME).withOptionsCompat(compat).navigation()
//                RouterUtils.MapModuleConfig.MAP_ACTIVITY
//                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
//                finish()
//                AppManager.get()?.finishAllActivity()
//                var dialog = DialogUtils.creteDialog(loginActivity, 0)
//                dialog.show()
                if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled()) {
                    Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                    return
                }
                var req = SendAuth.Req()
                req.transaction = "login"
                req.scope = "snsapi_userinfo"
                req.state = "wechat_sdk_xb_live_state"//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
                BaseApplication.getInstance().mWxApi.sendReq(req)
            }
            R.id.login_protocal4 -> {
                ARouter.getInstance().build(RouterUtils.LoginModuleKey.WEB_VIEW).navigation()
            }
        }
    }
    fun inject(loginActivity: LoginActivity) {
        this.loginActivity = loginActivity

//        loginActivity.login_video.post {
//            loginActivity.login_video.setFixedSize(loginActivity.login_video.width, loginActivity.login_video.height)
//            loginActivity.login_video.invalidate()
//            loginActivity.login_video.setVideoURI(Uri.parse("android.resource://" + context.packageName + "/raw/android_start"))
//            loginActivity.login_video.start()
//            loginActivity.login_video.setOnCompletionListener {
//                loginActivity.login_video.start()
//            }
//        }
    }


}
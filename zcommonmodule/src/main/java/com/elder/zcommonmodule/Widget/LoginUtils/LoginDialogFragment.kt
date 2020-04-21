package com.elder.zcommonmodule.Widget.LoginUtils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Http.NetWorkManager
import com.elder.zcommonmodule.Service.Login.LoginService
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Widget.CityPicker.CityPickerDialogFragment
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.cs.tec.library.USERID


class LoginDialogFragment : BaseDialogFragment, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fragment_login_towx -> {
                if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled()) {
                    Toast.makeText(org.cs.tec.library.Base.Utils.context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                    return
                }
                var req = SendAuth.Req()
                req.transaction = "relogin"
                req.scope = "snsapi_userinfo"
                req.state = "wechat_sdk_xb_live_state"//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
                BaseApplication.getInstance().mWxApi.sendReq(req)
            }
            R.id.fragment_passwordLogin -> {
                var name = loginName.text.toString()
                var pass = Password.text.toString()
                if (name.isNullOrEmpty() || pass.isNullOrEmpty()) {
                    Toast.makeText(context, "账号或密码为空!", Toast.LENGTH_SHORT)
                    return
                }
                if (name.length != 11) {
                    Toast.makeText(context, "手机号输入错误！", Toast.LENGTH_SHORT)
                    return
                }
                if (pass.length < 6) {
                    Toast.makeText(context, "密码不能小于6位！", Toast.LENGTH_SHORT)
                    return
                }
                var dialog = DialogUtils.showProgress(this.activity!!, getString(R.string.http_loading))
                var map = HashMap<String, String>()
                map["mobile"] = name
                map["pwd"] = pass
                NetWorkManager.instance.getOkHttpRetrofit()?.create(LoginService::class.java)?.login(NetWorkManager.instance.getBaseRequestBody(map)!!)?.map {
                    return@map it
                }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe {
                    if (it.code == 0) {
                        PreferenceUtils.putString(org.cs.tec.library.Base.Utils.context, USER_PHONE, name)
                        PreferenceUtils.putString(org.cs.tec.library.Base.Utils.context, USER_PASS, pass)
                        PreferenceUtils.putString(org.cs.tec.library.Base.Utils.context, USER_TOKEN, it.data as String)
                        PreferenceUtils.putBoolean(org.cs.tec.library.Base.Utils.context, RE_LOGIN, false)
                        PreferenceUtils.putString(org.cs.tec.library.Base.Utils.context, USERID, it.msg)
                    } else {
                        Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                    }
                    dismissValue = true
                    dialog.dismiss()
                }
            }
        }
    }

    companion object {
        fun newInstance(enable: Boolean): LoginDialogFragment {
            val fragment = LoginDialogFragment()
            val args = Bundle()
            args.putBoolean("cp_enable_anim", enable)
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var loginName: EditText

    lateinit var Password: EditText

    lateinit var visibleIcon: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentView = inflater.inflate(R.layout.login_fragment_dialog, container, false)
        loginName = mContentView?.findViewById(R.id.fragment_phone_number)!!
        Password = mContentView?.findViewById(R.id.fragment_visible_pass)!!
        visibleIcon = mContentView?.findViewById(R.id.fragment_visible_icon_change)!!
        mContentView?.findViewById<TextView>(R.id.fragment_passwordLogin)!!.setOnClickListener(this)
        mContentView?.findViewById<LinearLayout>(R.id.fragment_login_towx)!!.setOnClickListener(this)
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    constructor()

}
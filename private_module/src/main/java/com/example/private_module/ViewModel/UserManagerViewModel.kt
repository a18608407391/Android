package com.example.private_module.ViewModel

import android.databinding.ObservableField
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.USER_PHONE
import com.example.private_module.Activity.UserManagerActivity
import com.example.private_module.R
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_setting.view.*
import kotlinx.android.synthetic.main.activity_usermaneger.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString


class UserManagerViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {

    var phone = ObservableField<String>()


    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {
    }

    fun inject(userManagerActivity: UserManagerActivity) {
        component.callback = this
        component.title.set(getString(R.string.user_manager_and_bond))
        component.arrowVisible.set(false)
        component.rightText.set("")
        var ph = PreferenceUtils.getString(context, USER_PHONE)
        if (ph.isNullOrEmpty()) {
            userManagerActivity.change_phone.text = "绑定手机号"
            phone.set("暂无绑定手机号")
            userManagerActivity.phone_state.text = "当前为微信登录"
        } else {
            userManagerActivity.phone_state.text = ""
            userManagerActivity.change_phone.text = "更换手机号"
            phone.set(ph.replace(ph.substring(3, 7), "****"))
        }
    }

    var component = TitleComponent()

    fun onClick(view: View) {
        when (view.id) {
            R.id.manager_changepass -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.CHANGEPASS).navigation()
            }
            R.id.change_phone -> {
                var mobile = PreferenceUtils.getString(context, USER_PHONE)
                ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 5).navigation()
            }
        }
    }
}

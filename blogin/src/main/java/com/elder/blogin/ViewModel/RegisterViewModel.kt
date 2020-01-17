package com.elder.blogin.ViewModel

import android.databinding.ObservableField
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.blogin.Activity.RegisterActivity
import com.elder.blogin.R
import com.elder.blogin.Utils.getCode
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.RxBusSubscriber
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.RouterUtils.LoginModuleKey.Companion.PHONE_NUMBER
import com.zk.library.Utils.RouterUtils.LoginModuleKey.Companion.TYPE_CLASS
import kotlinx.android.synthetic.main.activity_register.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class RegisterViewModel : BaseViewModel() {
    lateinit var registerActivity: RegisterActivity
    var phone: String = ""
    var isClick = ObservableField<Boolean>(true)
    var warmText = ObservableField<String>()
    fun onClick(view: View) {
        when (view.id) {
            R.id.register_next -> {
                //处理不同界面跳轉
                if (TextUtils.isEmpty(phone)) {
                    warmText.set(getString(R.string.phonenotEmpty))
                    return
                }
                if (phone.length != 11) {
                    warmText.set(getString(R.string.phoneError))
                    return
                }
                if (registerActivity.type == 5) {
                    registerActivity.type = 4
                }
                getCode(registerActivity, phone, registerActivity.type)
            }
        }
    }

    var phoneChangeCommand = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            phone = t
            if (registerActivity.type == 2) {
                if (phone.length != 11) {
                    isClick.set(false)
                } else {
                    isClick.set(true)
                }
            }
        }
    })

    fun inject(registerActivity: RegisterActivity) {
        this.registerActivity = registerActivity
    }
}
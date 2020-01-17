package com.elder.blogin.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.blogin.BR
import com.elder.blogin.R
import com.elder.blogin.ViewModel.RegisterViewModel
import com.elder.blogin.databinding.ActivityRegisterBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.base_top_arrow.*

@Route(path = RouterUtils.ActivityPath.REGISTER)
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>() {

    @Autowired(name = RouterUtils.LoginModuleKey.TYPE_CLASS)
    @JvmField
    var type: Int = 0

    //注册和免密码登录和忘记密码
    override fun initVariableId(): Int {
        return BR.register
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.fullScreen(this)
        return R.layout.activity_register
    }

    override fun initViewModel(): RegisterViewModel? {
        return ViewModelProviders.of(this)[RegisterViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
        main_back.setOnClickListener {
            finish()
        }
        return_login.setOnClickListener {
            finish()
        }
        if (type == 1) {
            noregister_warm.visibility = View.GONE
            register_warm.visibility = View.VISIBLE
            login_lable.text = getString(R.string.new_user_register)
            register_next.text = getString(R.string.next)
        } else if (type == 2) {
            noregister_warm.visibility = View.VISIBLE
            register_warm.visibility = View.GONE
            login_lable.text = getString(R.string.noPassLogin)
            register_next.text = getString(R.string.next)
        } else if (type == 3) {
            noregister_warm.visibility = View.GONE
            register_warm.visibility = View.GONE
            login_lable.text = getString(R.string.forgetPassword)
            register_next.text = getString(R.string.next)
        } else if (type == 4) {
            noregister_warm.visibility = View.GONE
            register_warm.visibility = View.GONE
            login_lable.text = getString(R.string.setting_account)
            register_next.text = getString(R.string.next)
        } else if (type == 5) {
            noregister_warm.visibility = View.GONE
            register_warm.visibility = View.GONE
            login_lable.text = getString(R.string.setting_new_phone)
            register_next.text = getString(R.string.next)
        }
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }
}
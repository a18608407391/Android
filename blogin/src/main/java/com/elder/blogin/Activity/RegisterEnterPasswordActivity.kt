package com.elder.blogin.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.blogin.BR
import com.elder.blogin.R
import com.elder.blogin.Utils.getCode
import com.elder.blogin.ViewModel.RegisterEnterPassViewModel
import com.elder.blogin.databinding.ActivityRegisterEnterpassBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_register_enterpass.*

@Route(path = RouterUtils.ActivityPath.REGISTER_SETTINGPASS)
class RegisterEnterPasswordActivity : BaseActivity<ActivityRegisterEnterpassBinding, RegisterEnterPassViewModel>() {
    //短信验证接受设置密码界面
    @Autowired(name = RouterUtils.LoginModuleKey.PHONE_NUMBER)
    @JvmField
    var phone: String? = null


    @Autowired(name = RouterUtils.LoginModuleKey.COUNT_DOWN_TIMER)
    @JvmField
    var second: Int? = 0


    @Autowired(name = RouterUtils.LoginModuleKey.TYPE_CLASS)
    @JvmField
    var type: Int = 0


    override fun initVariableId(): Int {
        return BR.register_enterpassModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.fullScreen(this)
        return R.layout.activity_register_enterpass
    }

    override fun initViewModel(): RegisterEnterPassViewModel? {
        return ViewModelProviders.of(this).get(RegisterEnterPassViewModel::class.java)
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
        main_back_pass.setOnClickListener {
            finish()
        }
        if (type == 1) {
            warm_layout.visibility = View.VISIBLE
            bottom_line.visibility = View.VISIBLE
            enterpass_layout.visibility = View.VISIBLE
            enter_verifyCode_layout.visibility = View.VISIBLE
            enter_verifyCode_layout_line.visibility = View.VISIBLE
        } else if (type == 2) {
            warm_layout.visibility = View.GONE
            bottom_line.visibility = View.GONE
            enterpass_layout.visibility = View.GONE
            enter_verifyCode_layout.visibility = View.VISIBLE
            enter_verifyCode_layout_line.visibility = View.VISIBLE
        } else if (type == 3) {
            warm_layout.visibility = View.VISIBLE
            bottom_line.visibility = View.VISIBLE
            enterpass_layout.visibility = View.VISIBLE
            enter_verifyCode_layout.visibility = View.VISIBLE
            enter_verifyCode_layout_line.visibility = View.VISIBLE
        } else if (type == 4) {
            warm_layout.visibility = View.VISIBLE
            bottom_line.visibility = View.VISIBLE
            enterpass_layout.visibility = View.VISIBLE
            enter_verifyCode_layout.visibility = View.VISIBLE
            enter_verifyCode_layout_line.visibility = View.VISIBLE
        } else if (type == 5) {
            getCode(this, phone!!, 5)
            warm_layout.visibility = View.GONE
            bottom_line.visibility = View.GONE
            enterpass_layout.visibility = View.GONE
            enter_verifyCode_layout.visibility = View.VISIBLE
            enter_verifyCode_layout_line.visibility = View.VISIBLE
        } else if (type == 6) {
            warm_layout.visibility = View.GONE
            bottom_line.visibility = View.GONE
            enterpass_layout.visibility = View.GONE
            enter_verifyCode_layout.visibility = View.VISIBLE
            enter_verifyCode_layout_line.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}
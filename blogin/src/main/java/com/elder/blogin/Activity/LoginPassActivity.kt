package com.elder.blogin.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.blogin.BR
import com.elder.blogin.R
import com.elder.blogin.ViewModel.LoginPassViewModel
import com.elder.blogin.databinding.ActivityLoginpassBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.base_top_arrow.*


@Route(path = RouterUtils.ActivityPath.LOGIN_PASSWORD)
class LoginPassActivity : BaseActivity<ActivityLoginpassBinding, LoginPassViewModel>() {
    // 密码登录

    @Autowired(name = RouterUtils.LoginModuleKey.TYPE_CLASS)
    @JvmField
    var type: Int = 0


    override fun initVariableId(): Int {
        return BR.loginpass_ViewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.fullScreen(this)
        return R.layout.activity_loginpass
    }

    override fun initViewModel(): LoginPassViewModel? {
        return ViewModelProviders.of(this)[LoginPassViewModel::class.java]
    }
    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
        login_lable.text = getString(R.string.app_name)
        main_back.setOnClickListener {
            ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation()
            finish()
        }
    }

    override fun doPressBack() {
        super.doPressBack()
        ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation()
        finish()
    }
}
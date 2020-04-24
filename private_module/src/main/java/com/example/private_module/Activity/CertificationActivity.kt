package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.CertificationViewModel
import com.example.private_module.databinding.ActivityCertficationBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.PrivateModuleConfig.CERTIFICATION)
class CertificationActivity : BaseFragment<ActivityCertficationBinding, CertificationViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_certfication
    }

    @Autowired(name = RouterUtils.PrivateModuleConfig.USER_AUTH)
    @JvmField
    var auth: Boolean = false

    override fun initVariableId(): Int {
        return BR.certification_ViewModel
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_certfication
//    }
//
//    override fun initViewModel(): CertificationViewModel? {
//        return ViewModelProviders.of(this)[CertificationViewModel::class.java]
//    }
//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }
    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }
}
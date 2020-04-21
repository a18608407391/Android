package com.cstec.administrator.social.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewModel.AiteViewModel
import com.cstec.administrator.social.databinding.ActivityAiteBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.SocialConfig.SOCIAL_AITE)
class AiteActivity : BaseFragment<ActivityAiteBinding, AiteViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_aite
    }

    override fun initVariableId(): Int {
        return BR.aite_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
//        return R.layout.activity_aite
//    }

//
//    override fun initViewModel(): AiteViewModel? {
//        return ViewModelProviders.of(this)[AiteViewModel::class.java]
//    }


//    override fun doPressBack() {
//        super.doPressBack()
//                finish()
//    }
    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }
}
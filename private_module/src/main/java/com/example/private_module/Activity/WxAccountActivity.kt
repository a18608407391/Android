package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.WxAccountViewModel
import com.example.private_module.databinding.ActivityWxaccoutBinding
import com.zk.library.Base.BaseActivity


class WxAccountActivity : BaseActivity<ActivityWxaccoutBinding, WxAccountViewModel>() {
    override fun initVariableId(): Int {
        return BR.wx_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_wxaccout
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

    override fun initViewModel(): WxAccountViewModel? {
        return ViewModelProviders.of(this)[WxAccountViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
}
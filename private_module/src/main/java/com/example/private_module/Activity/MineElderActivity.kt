package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.R
import com.example.private_module.ViewModel.MineElderViewModel
import com.example.private_module.databinding.ActivityMineElderBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.example.private_module.BR

@Route(path = RouterUtils.PrivateModuleConfig.MINE_ELDER)
class MineElderActivity : BaseActivity<ActivityMineElderBinding, MineElderViewModel>() {

    override fun initVariableId(): Int {
        return BR.mineElderViewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_mine_elder
    }

    override fun initViewModel(): MineElderViewModel? {
        return ViewModelProviders.of(this)[MineElderViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel!!.inject(this)
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }


}
package com.example.drivermodule.Activity.Team

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.REQUEST_CREATE_JOIN
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.CreateTeamViewModel
import com.example.drivermodule.databinding.ActivityCreateTeamBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.TeamModule.TEAM_CREATE)
class CreateTeamActivity : BaseFragment<ActivityCreateTeamBinding, CreateTeamViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_create_team
    }

    override fun initVariableId(): Int {
        return BR.create_team
    }
//    override fun initContentView(savedInstanceState: Bundle?): Int {
////        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
////        StatusbarUtils.setTranslucentStatus(this)
////        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//
//    }
//
//    override fun initViewModel(): CreateTeamViewModel? {
//        return ViewModelProviders.of(this)[CreateTeamViewModel::class.java]
//    }

    override fun initData() {
        super.initData()
        viewModel?.location = arguments!!.getSerializable(RouterUtils.MapModuleConfig.START_LOCATION) as Location?
        viewModel?.inject(this)
    }

//
//    override fun doPressBack() {
//        super.doPressBack()
//        var intent = Intent()
//        intent.putExtra("type", "cancle")
//        setResult(REQUEST_CREATE_JOIN, intent)
//        finish()
//    }

}
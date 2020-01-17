package com.example.drivermodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.DriverSettingViewModel
import com.example.drivermodule.databinding.ActivityDrivingSettingBinding
import com.zk.library.BR
import com.zk.library.Base.BaseActivity

class DriverSettingActivity : BaseActivity<ActivityDrivingSettingBinding, DriverSettingViewModel>() {
    override fun initVariableId(): Int {
        return BR.driving_setting_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_driving_setting
    }

    override fun initViewModel(): DriverSettingViewModel? {
        return ViewModelProviders.of(this)[DriverSettingViewModel::class.java]
    }

}
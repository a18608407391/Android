package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.EDIT_CARS
import com.elder.zcommonmodule.INSERT_CARS
import com.example.private_module.BR
import com.example.private_module.Bean.CarsEntity
import com.example.private_module.R
import com.example.private_module.ViewModel.AddCarViewModel
import com.example.private_module.databinding.ActivityAddcarBinding
import com.google.gson.Gson
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils

@Route(path = RouterUtils.PrivateModuleConfig.ADD_CARS)
class AddCarActivity : BaseActivity<ActivityAddcarBinding, AddCarViewModel>() {
    override fun initVariableId(): Int {
        return BR.add_car_ViewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_addcar
    }

    override fun initViewModel(): AddCarViewModel? {
        return ViewModelProviders.of(this)[AddCarViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == INSERT_CARS) {
            if (data != null) {
                mViewModel?.cars?.clear()
                var entitiy = CarsEntity()
                mViewModel?.cars?.add(entitiy)
                mViewModel?.initDatas()
            }
        } else if (requestCode == EDIT_CARS) {
            Log.e("result", "走这里2")
            if (data != null) {
                var entitiy = data!!.getSerializableExtra("carsEntitiy") as CarsEntity
                mViewModel?.cars?.forEachIndexed { index, carsEntity ->
                    if (carsEntity.id.get()!! == entitiy.id.get()) {
                        Log.e("result", Gson().toJson(carsEntity) + "当前车信息")
                        mViewModel?.cars?.set(index, entitiy)
                    }
                }
                mViewModel!!.adapter.setCarDatas(mViewModel!!.cars)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.EDIT_CARS
import com.elder.zcommonmodule.INSERT_CARS
import com.elder.zcommonmodule.Widget.RecyclerCoverFlow
import com.example.private_module.BR
import com.example.private_module.Bean.CarsEntity
import com.example.private_module.R
import com.example.private_module.ViewModel.AddCarViewModel
import com.example.private_module.databinding.ActivityAddcarBinding
import com.google.gson.Gson
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils

@Route(path = RouterUtils.PrivateModuleConfig.ADD_CARS)
class AddCarActivity : BaseFragment<ActivityAddcarBinding, AddCarViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_addcar
    }

    override fun initVariableId(): Int {
        return BR.add_car_ViewModel
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_addcar
//    }
//
//    override fun initViewModel(): AddCarViewModel? {
//        return ViewModelProviders.of(this)[AddCarViewModel::class.java]
//    }


    lateinit var add_car_recycle: RecyclerCoverFlow
    override fun initData() {
        super.initData()
        add_car_recycle = binding!!.root!!.findViewById(R.id.add_car_recycle)
        viewModel?.inject(this)
    }

//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == INSERT_CARS) {
            if (data != null) {
                viewModel?.cars?.clear()
                var entitiy = CarsEntity()
                viewModel?.cars?.add(entitiy)
                viewModel?.initDatas()
            }
        } else if (requestCode == EDIT_CARS) {
            Log.e("result", "走这里2")
            if (data != null) {
                var entitiy = data!!.getSerializableExtra("carsEntitiy") as CarsEntity
                viewModel?.cars?.forEachIndexed { index, carsEntity ->
                    if (carsEntity.id.get()!! == entitiy.id.get()) {
                        Log.e("result", Gson().toJson(carsEntity) + "当前车信息")
                        viewModel?.cars?.set(index, entitiy)
                    }
                }
                viewModel!!.adapter.setCarDatas(viewModel!!.cars)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        when (requestCode) {
            INSERT_CARS -> {
                if (data != null) {
                    viewModel?.cars?.clear()
                    var entitiy = CarsEntity()
                    viewModel?.cars?.add(entitiy)
                    viewModel?.initDatas()
                }
            }
            EDIT_CARS -> {
                if (data != null) {
                    var entitiy = data!!.getSerializable("carsEntitiy") as CarsEntity
                    viewModel?.cars?.forEachIndexed { index, carsEntity ->
                        if (carsEntity.id.get()!! == entitiy.id.get()) {
                            Log.e("result", Gson().toJson(carsEntity) + "当前车信息")
                            viewModel?.cars?.set(index, entitiy)
                        }
                    }
                    viewModel!!.adapter.setCarDatas(viewModel!!.cars)
                }
            }
        }
    }

}
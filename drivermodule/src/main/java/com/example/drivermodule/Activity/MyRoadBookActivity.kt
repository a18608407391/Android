package com.example.drivermodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.example.drivermodule.BR
import com.elder.zcommonmodule.Entity.HotData
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBook.MyRoadViewModel
import com.example.drivermodule.databinding.ActivityMyRoadbookBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import org.cs.tec.library.Bus.RxBus


@Route(path = RouterUtils.MapModuleConfig.MY_ROAD_BOOK_AC)
class MyRoadBookActivity : BaseActivity<ActivityMyRoadbookBinding, MyRoadViewModel>() {
    override fun initVariableId(): Int {
        return BR.my_road_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_my_roadbook
    }


    override fun initViewModel(): MyRoadViewModel? {
        return ViewModelProviders.of(this)[MyRoadViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOAD_ROADBOOK) {
            if (data != null) {
                var data = data.getSerializableExtra("hotdata") as HotData
                RxBus.default?.post(data)
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "myroad").withSerializable(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY_ROAD, data).navigation()
            }
        }
    }

    override fun doPressBack() {
        super.doPressBack()
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
        finish()
    }
}
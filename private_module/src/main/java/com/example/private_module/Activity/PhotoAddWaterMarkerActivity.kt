package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.BR
import com.example.private_module.Bean.PhotoEntitiy
import com.example.private_module.R
import com.example.private_module.ViewModel.PhotoAddWaterMarkerViewModel
import com.example.private_module.databinding.ActivityPhotoAddWaterBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.PrivateModuleConfig.ADD_MARKER_ACTIVITY)
class PhotoAddWaterMarkerActivity : BaseActivity<ActivityPhotoAddWaterBinding, PhotoAddWaterMarkerViewModel>() {

    @Autowired(name = RouterUtils.PrivateModuleConfig.ADD_MARKER_URL)
    @JvmField
    var url: String? = null


    override fun initVariableId(): Int {
        return BR.marker_add_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, false, 0x00000000)
        return R.layout.activity_photo_add_water
    }

    override fun initViewModel(): PhotoAddWaterMarkerViewModel? {
        return ViewModelProviders.of(this)[PhotoAddWaterMarkerViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
}
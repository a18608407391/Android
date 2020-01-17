package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.PictureSelectorViewModel
import com.example.private_module.databinding.ActivityPictureSelectorBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.GridDividerItemDecoration
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_picture_selector.*


@Route(path = RouterUtils.PrivateModuleConfig.PICTURE_SELECTOR)
class PictureSelectorActivity : BaseActivity<ActivityPictureSelectorBinding, PictureSelectorViewModel>() {
    override fun initVariableId(): Int {
        return BR.picture_selector_ViewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_picture_selector
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

    override fun initViewModel(): PictureSelectorViewModel? {
        return ViewModelProviders.of(this)[PictureSelectorViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        recy.addItemDecoration(GridDividerItemDecoration(this,1,1,false))

//        val offsetsItemDecoration = GridOffsetsItemDecoration(
//                GridOffsetsItemDecoration.GRID_OFFSETS_VERTICAL)
//        offsetsItemDecoration.setVerticalItemOffsets(ConvertUtils.dp2px(8F))
//        offsetsItemDecoration.setHorizontalItemOffsets(ConvertUtils.dp2px(8F))
//        recy.addItemDecoration(offsetsItemDecoration)
        mViewModel!!.inject(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mViewModel?.onResult(requestCode, resultCode, data)
    }
}
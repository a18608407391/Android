package com.elder.amoski.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.amoski.BR
import com.elder.amoski.R
import com.elder.amoski.ViewModel.GuideViewModel
import com.elder.amoski.databinding.ActivityGuideBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.ActivityPath.GUIDE)
class GuideActivity  :BaseActivity<ActivityGuideBinding,GuideViewModel>(){
    override fun initVariableId(): Int {
        return BR.guide_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.fullScreen(this)
        return R.layout.activity_guide
    }


    override fun initViewModel(): GuideViewModel? {
        return ViewModelProviders.of(this)[GuideViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
}
package com.example.private_module.Activity.NewVession

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Entity.Location
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.NewVession.MyLikeViewModel
import com.example.private_module.databinding.ActivityMylikeBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_mylike.*

@Route(path = RouterUtils.PrivateModuleConfig.MY_LIKE_AC)
class MyLikeActivity  :BaseActivity<ActivityMylikeBinding,MyLikeViewModel>(){


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null



    override fun initVariableId(): Int {
        return BR.my_like_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_mylike
    }

    override fun initViewModel(): MyLikeViewModel? {
        return ViewModelProviders.of(this)[MyLikeViewModel::class.java]
    }
    override fun initData() {
        mViewModel?.inject(this)
        like_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        like_swipe.setOnRefreshListener(mViewModel!!)
    }

    override fun doPressBack() {
        super.doPressBack()
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
            override fun onArrival(postcard: Postcard?) {
                finish()
            }
        })
    }
}
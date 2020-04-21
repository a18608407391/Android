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
import com.elder.zcommonmodule.Widget.PoketSwipeRefreshLayout
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.NewVession.MyLikeViewModel
import com.example.private_module.databinding.ActivityMylikeBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_mylike.*

@Route(path = RouterUtils.PrivateModuleConfig.MY_LIKE_AC)
class MyLikeActivity : BaseFragment<ActivityMylikeBinding, MyLikeViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_mylike
    }


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null


    override fun initVariableId(): Int {
        return BR.my_like_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_mylike
//    }

    //    override fun initViewModel(): MyLikeViewModel? {
//        return ViewModelProviders.of(this)[MyLikeViewModel::class.java]
//    }
    var swp: PoketSwipeRefreshLayout? = null
    override fun initData() {
        swp = binding!!.root!!.findViewById(R.id.like_swipe)
        location = arguments?.getSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION) as Location?
        viewModel ?. inject (this)
        swp!!.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swp!!.setOnRefreshListener(viewModel!!)
    }

//    override fun doPressBack() {
//        super.doPressBack()
//        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
//            override fun onArrival(postcard: Postcard?) {
//                finish()
//            }
//        })
//    }
}
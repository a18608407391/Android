package com.cstec.administrator.social.Activity

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewModel.MyDynamicsViewModel
import com.cstec.administrator.social.databinding.ActivityMyDynamicsBinding
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_my_dynamics.*
import kotlinx.android.synthetic.main.fragment_social.*


@Route(path = RouterUtils.SocialConfig.MY_DYNAMIC_AC)
class MyDynamicsActivity : BaseActivity<ActivityMyDynamicsBinding, MyDynamicsViewModel>() {


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_TYPE)
    @JvmField
    var type: Int = 0


    override fun initVariableId(): Int {
        return BR.my_dynamics_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_my_dynamics
    }

    override fun initViewModel(): MyDynamicsViewModel? {
        return ViewModelProviders.of(this)[MyDynamicsViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        mysocial_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        mysocial_swipe.setOnRefreshListener(mViewModel!!)
        mViewModel?.inject(this)
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
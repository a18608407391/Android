package com.example.private_module.Activity.NewVession

import android.arch.lifecycle.ViewModelProviders
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
import com.example.private_module.ViewModel.NewVession.MyFansViewModel
import com.example.private_module.databinding.ActivityMyfansBinding
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_myfans.*


@Route(path = RouterUtils.PrivateModuleConfig.MY_FANS_AC)
class MyFansActivity : BaseActivity<ActivityMyfansBinding, MyFansViewModel>() {




    override fun initVariableId(): Int {
        return BR.my_fans_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_myfans
    }

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_MEMBER_ID)
    @JvmField
    var id: String? = null


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID)
    @JvmField
    var type: Int = 0


    override fun initViewModel(): MyFansViewModel? {
        return ViewModelProviders.of(this)[MyFansViewModel::class.java]
    }

    override fun initData() {
        fans_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        fans_swipe.setOnRefreshListener(mViewModel!!)
        mViewModel?.inject(this)
    }

    override fun doPressBack() {
        super.doPressBack()
        if (type == 1) {
            if (mViewModel!!.destroyList!!.contains("DriverHomeActivity")) {
                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, id)
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location)
                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0)
                        .navigation(this, object : NavCallback() {
                            override fun onArrival(postcard: Postcard?) {
                                finish()
                            }
                        })
            } else {
                finish()
            }
        }
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
            override fun onArrival(postcard: Postcard?) {
                finish()
            }
        })
    }
}
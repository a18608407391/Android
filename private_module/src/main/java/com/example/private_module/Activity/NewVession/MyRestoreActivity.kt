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
import com.example.private_module.ViewModel.NewVession.MyRestoreViewModel
import com.example.private_module.databinding.ActivityMyRestoreBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_my_restore.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions


@Route(path = RouterUtils.PrivateModuleConfig.MY_RESTORE_AC)
class MyRestoreActivity  :BaseActivity<ActivityMyRestoreBinding,MyRestoreViewModel>(){
    override fun initVariableId(): Int {
        return BR.my_restore_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_my_restore
    }

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null

    override fun initViewModel(): MyRestoreViewModel? {
        return ViewModelProviders.of(this)[MyRestoreViewModel::class.java]
    }

    override fun initData() {
        restore_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        restore_swipe.setOnRefreshListener(mViewModel!!)
        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "ActiveWebGotoApp") {
                finish()
            }
        }
        RxSubscriptions.add(s)
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
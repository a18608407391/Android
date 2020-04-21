package com.example.private_module.Activity.NewVession

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Widget.PoketSwipeRefreshLayout
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.NewVession.CommandViewModel
import com.example.private_module.databinding.CommandActivityBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.command_activity.*


@Route(path = RouterUtils.PrivateModuleConfig.COMMAND_AC)
class CommandActivity : BaseFragment<CommandActivityBinding, CommandViewModel>() {
    override fun initContentView(): Int {
        return R.layout.command_activity
    }


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null

    override fun initVariableId(): Int {
        return BR.command_model
    }


//    override fun doPressBack() {
//        super.doPressBack()
//        mViewModel?.returnBack()
//    }
//
//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.command_activity
//    }
//
//    override fun initViewModel(): CommandViewModel? {
//        return ViewModelProviders.of(this)[CommandViewModel::class.java]
//    }


    var swp: PoketSwipeRefreshLayout? = null
    override fun initData() {
        super.initData()
        location = arguments?.getSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION) as Location?
        swp = binding!!.root!!.findViewById(R.id.command_swipe)
        command_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        command_swipe.setOnRefreshListener(viewModel!!)
        viewModel?.inject(this)
    }
}
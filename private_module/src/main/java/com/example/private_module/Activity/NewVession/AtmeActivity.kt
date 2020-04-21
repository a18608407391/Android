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
import com.example.private_module.ViewModel.NewVession.AtmeViewModel
import com.example.private_module.databinding.AtmeActivityBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_myfans.*
import kotlinx.android.synthetic.main.atme_activity.*


@Route(path = RouterUtils.PrivateModuleConfig.Atme_AC)
class AtmeActivity : BaseFragment<AtmeActivityBinding, AtmeViewModel>() {
    override fun initContentView(): Int {
        return R.layout.atme_activity
    }


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null

    override fun initVariableId(): Int {
        return BR.atme_viewmodel
    }

//    override fun doPressBack() {
//        super.doPressBack()
//        mViewModel?.returnBack()
//    }
//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.atme_activity
//    }
//
//    override fun initViewModel(): AtmeViewModel? {
//        return ViewModelProviders.of(this)[AtmeViewModel::class.java]
//
//    }

    var swp: PoketSwipeRefreshLayout? = null
    override fun initData() {
        super.initData()
        location = arguments?.getSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION) as Location?
        swp = binding!!.root!!.findViewById(R.id.at_swipe)
        swp!!.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swp!!.setOnRefreshListener(viewModel!!)
        viewModel?.inject(this)
    }
}
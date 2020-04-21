package com.cstec.administrator.chart_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.SystemNotifyViewModel
import com.cstec.administrator.chart_module.databinding.ActivitySysNotifyBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.MSG_RETURN_REQUEST
import com.elder.zcommonmodule.Widget.PoketSwipeRefreshLayout
import com.elder.zcommonmodule.getImageUrl
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_sys_notify.*


@Route(path = RouterUtils.Chat_Module.SysNotify_AC)
class SystemNotifyListActivity : BaseFragment<ActivitySysNotifyBinding, SystemNotifyViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_sys_notify
    }

    override fun initVariableId(): Int {
        return BR.system_notify_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_sys_notify
//    }


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null

//    override fun initViewModel(): SystemNotifyViewModel? {
//        return ViewModelProviders.of(this)[SystemNotifyViewModel::class.java]
//    }


    lateinit var swip: PoketSwipeRefreshLayout
    override fun initData() {
        super.initData()
        location = arguments!!.getSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION) as Location?
        swip = binding!!.root!!.findViewById(R.id.sys_swipe)
        swip.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swip.setOnRefreshListener(viewModel!!)
        viewModel?.inject(this)
    }


//    override fun doPressBack() {
//        super.doPressBack()
//        mViewModel?.returnBack()
//    }
}
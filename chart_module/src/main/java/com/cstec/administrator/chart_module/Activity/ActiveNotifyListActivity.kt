package com.cstec.administrator.chart_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.ActiveNotifyViewModel
import com.cstec.administrator.chart_module.databinding.ActivityActiveNotifyBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Widget.PoketSwipeRefreshLayout
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_active_notify.*
import kotlinx.android.synthetic.main.activity_sys_notify.*
import org.cs.tec.library.Bus.RxBus

@Route(path = RouterUtils.Chat_Module.ActiveNotify_AC)
class ActiveNotifyListActivity : BaseFragment<ActivityActiveNotifyBinding, ActiveNotifyViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_active_notify
    }


    override fun initVariableId(): Int {
        return BR.active_notify_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_active_notify
//    }

    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null

//    override fun initViewModel(): ActiveNotifyViewModel? {
//        return ViewModelProviders.of(this)[ActiveNotifyViewModel::class.java]
//    }

    lateinit var swip: PoketSwipeRefreshLayout
    override fun initData() {
        super.initData()
        location = arguments!!.getSerializable(RouterUtils.PartyConfig.PARTY_LOCATION) as Location?
        swip = binding!!.root.findViewById(R.id.active_swipe)
        active_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        active_swipe.setOnRefreshListener(viewModel!!)
        viewModel?.inject(this)
    }
}
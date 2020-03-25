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
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_active_notify.*
import kotlinx.android.synthetic.main.activity_sys_notify.*

@Route(path = RouterUtils.Chat_Module.ActiveNotify_AC)
class ActiveNotifyListActivity : BaseActivity<ActivityActiveNotifyBinding, ActiveNotifyViewModel>() {


    override fun initVariableId(): Int {
        return BR.active_notify_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_active_notify
    }

    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null

    override fun initViewModel(): ActiveNotifyViewModel? {
        return ViewModelProviders.of(this)[ActiveNotifyViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        active_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        active_swipe.setOnRefreshListener(mViewModel!!)
        mViewModel?.inject(this)
    }
}
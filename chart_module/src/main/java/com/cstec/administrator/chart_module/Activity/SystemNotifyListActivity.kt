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
import com.elder.zcommonmodule.getImageUrl
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_sys_notify.*


@Route(path = RouterUtils.Chat_Module.SysNotify_AC)
class SystemNotifyListActivity : BaseActivity<ActivitySysNotifyBinding, SystemNotifyViewModel>() {
    override fun initVariableId(): Int {
        return BR.system_notify_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_sys_notify
    }


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null

    override fun initViewModel(): SystemNotifyViewModel? {
        return ViewModelProviders.of(this)[SystemNotifyViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        sys_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        sys_swipe.setOnRefreshListener(mViewModel!!)
        mViewModel?.inject(this)
    }


    override fun doPressBack() {
        super.doPressBack()
        mViewModel?.returnBack()
    }
}
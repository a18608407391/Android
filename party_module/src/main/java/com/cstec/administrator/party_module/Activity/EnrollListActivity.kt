package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.EnrollViewModel
import com.cstec.administrator.party_module.databinding.ActivityEnrollBinding
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.PartyConfig.ENROLL)
class EnrollListActivity : BaseActivity<ActivityEnrollBinding, EnrollViewModel>() {

    @Autowired(name = RouterUtils.PartyConfig.PARTY_ID)
    @JvmField
    var id: Int = 0


    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null

    override fun initVariableId(): Int {
        return BR.enroll_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_enroll
    }

    fun returnBack() {
        finish()
    }

    override fun initViewModel(): EnrollViewModel? {
        return ViewModelProviders.of(this)[EnrollViewModel::class.java]
    }

    override fun doPressBack() {
        super.doPressBack()
        returnBack()
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
}
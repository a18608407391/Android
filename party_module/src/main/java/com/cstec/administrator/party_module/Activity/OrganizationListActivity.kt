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
import com.cstec.administrator.party_module.ViewModel.OrganizationListViewModel
import com.cstec.administrator.party_module.databinding.ActivityOrganizationBinding
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.PartyConfig.ORGANIZATION)
class OrganizationListActivity : BaseFragment<ActivityOrganizationBinding, OrganizationListViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_organization
    }

    @Autowired(name = RouterUtils.PartyConfig.PARTY_ID)
    @JvmField
    var ids: Int = 0

    override fun initVariableId(): Int {
        return BR.organization_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_organization
//    }
//
//    override fun initViewModel(): OrganizationListViewModel? {
//        return ViewModelProviders.of(this)[OrganizationListViewModel::class.java]
//    }
//
//    override fun doPressBack() {
//        super.doPressBack()
//        returnBack()
//    }
//
//    fun returnBack() {
//        finish()
//    }

    override fun initData() {
        super.initData()
        ids = arguments!!.getInt(RouterUtils.PartyConfig.PARTY_ID)
        viewModel?.inject(this)
    }
}
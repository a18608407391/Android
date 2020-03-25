package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.OrganizationListViewModel
import com.cstec.administrator.party_module.databinding.ActivityOrganizationBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.PartyConfig.ORGANIZATION)
class OrganizationListActivity : BaseActivity<ActivityOrganizationBinding, OrganizationListViewModel>() {

    @Autowired(name = RouterUtils.PartyConfig.PARTY_ID)
    @JvmField
    var id: Int  =  0

    override fun initVariableId(): Int {
        return BR.organization_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_organization
    }

    override fun initViewModel(): OrganizationListViewModel? {
        return ViewModelProviders.of(this)[OrganizationListViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
}
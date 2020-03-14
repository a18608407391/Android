package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyDetailBinding
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_party_detail.*


@Route(path = RouterUtils.PartyConfig.PARTY_DETAIL)
class PartyDetailActivty : BaseActivity<ActivityPartyDetailBinding, PartyDetailViewModel>() {


    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.PartyConfig.PARTY_ID)
    @JvmField
    var party_id: String? = null

    override fun initVariableId(): Int {
        return BR.party_detail_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_party_detail
    }

    override fun initViewModel(): PartyDetailViewModel? {
        return ViewModelProviders.of(this)[PartyDetailViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mPartyDetailViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mPartyDetailTabLayout))
        mPartyDetailTabLayout.setupWithViewPager(mPartyDetailViewPager)
        mViewModel?.inject(this)
    }
}
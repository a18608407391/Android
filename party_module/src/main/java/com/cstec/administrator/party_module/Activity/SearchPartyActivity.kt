package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.SearchPartyViewModel
import com.cstec.administrator.party_module.databinding.ActivitySearchPartyBinding
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_search_party.*


class SearchPartyActivity : BaseActivity<ActivitySearchPartyBinding, SearchPartyViewModel>() {


    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.PartyConfig.PARTY_CITY)
    @JvmField
    var party_city: String? = null

    override fun initVariableId(): Int {
        return BR.search_party_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_search_party
    }

    override fun initViewModel(): SearchPartyViewModel? {
        return ViewModelProviders.of(this)[SearchPartyViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        search_et.setOnEditorActionListener(mViewModel)
        mViewModel?.inject(this)
    }
}
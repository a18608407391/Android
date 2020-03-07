package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyDetailBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseViewModel


class PartyDetailActivty : BaseActivity<ActivityPartyDetailBinding, PartyDetailViewModel>() {
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
        mViewModel?.inject(this)
    }
}
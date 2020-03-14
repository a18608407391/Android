package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyClockDetailViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyClockDetailBinding
import com.zk.library.Base.BaseActivity


class PartyClockDetailActivity : BaseActivity<ActivityPartyClockDetailBinding, PartyClockDetailViewModel>() {
    override fun initVariableId(): Int {
        return BR.party_clock_detail_model
    }
    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_party_clock_detail
    }
    override fun initViewModel(): PartyClockDetailViewModel? {
        return ViewModelProviders.of(this)[PartyClockDetailViewModel::class.java]
    }
}
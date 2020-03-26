package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyCheckTicketViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyCheckTicketBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils


class PartyCheckTicketActivity : BaseActivity<ActivityPartyCheckTicketBinding, PartyCheckTicketViewModel>() {





    override fun initVariableId(): Int {
        return BR.party_check_ticket_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_party_check_ticket
    }

    override fun initViewModel(): PartyCheckTicketViewModel? {
        return ViewModelProviders.of(this)[PartyCheckTicketViewModel::class.java]
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }
    override fun initData() {
        super.initData()
        mViewModel?.inejct(this)
    }
}
package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.BondAccountViewModel
import com.example.private_module.databinding.ActivityBondAccountBinding
import com.zk.library.Base.BaseActivity

class AccountBondActivity : BaseActivity<ActivityBondAccountBinding, BondAccountViewModel>() {
    override fun initVariableId(): Int {
        return BR.bond_account_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_bond_account
    }

    override fun initViewModel(): BondAccountViewModel? {
        return ViewModelProviders.of(this)[BondAccountViewModel::class.java]
    }
    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

}
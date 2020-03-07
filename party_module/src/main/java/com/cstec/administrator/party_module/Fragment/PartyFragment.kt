package com.cstec.administrator.party_module.Fragment

import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyViewModel
import com.cstec.administrator.party_module.databinding.PartyFragmentBinding
import com.elder.zcommonmodule.Utils.DialogUtils
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.PartyConfig.PARTY_MAIN)
class PartyFragment : BaseFragment<PartyFragmentBinding, PartyViewModel>() {
    override fun initContentView(): Int {

        return R.layout.party_fragment
    }

    override fun initVariableId(): Int {
        return BR.party_main_model
    }

    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }

    override fun onResume() {
        super.onResume()
    }
}
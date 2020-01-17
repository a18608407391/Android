package com.cstec.administrator.chart_module.Fragment

import android.databinding.ViewDataBinding
import com.cstec.administrator.chart_module.Base.CharBaseFragment
import com.cstec.administrator.chart_module.R
import com.zk.library.Base.BaseViewModel
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.ViewModel.ConversationViewModel
import com.cstec.administrator.chart_module.databinding.FragmentConversationBinding

class ConversationListFragment : CharBaseFragment<FragmentConversationBinding, ConversationViewModel>() {
    override fun initContentView(): Int {
        return R.layout.fragment_conversation
    }

    override fun initVariableId(): Int {
        return BR.conversation_viewmodel
    }


    override fun initData() {
        super.initData()
        viewModel?.inject(this)

    }


}
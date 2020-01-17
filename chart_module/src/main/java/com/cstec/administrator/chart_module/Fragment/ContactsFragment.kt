package com.cstec.administrator.chart_module.Fragment

import android.databinding.ViewDataBinding
import com.cstec.administrator.chart_module.Base.CharBaseFragment
import com.cstec.administrator.chart_module.R
import com.zk.library.Base.BaseViewModel
import com.cstec.administrator.chart_module.BR

class ContactsFragment  :CharBaseFragment<ViewDataBinding,BaseViewModel>(){
    override fun initContentView(): Int {

        return R.layout.fragment_contacts
    }

    override fun initVariableId(): Int {
        return BR.contacts_viewmodel
    }

}
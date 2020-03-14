package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.view.View
import com.cstec.administrator.party_module.Activity.SubjectPartyActivity
import com.cstec.administrator.party_module.R
import com.zk.library.Base.BaseViewModel
import com.zk.library.Base.ItemViewModel
import com.cstec.administrator.party_module.BR
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding


class SubjectPartyViewModel : BaseViewModel() {

    lateinit var subject: SubjectPartyActivity
    fun inject(subjectPartyActivity: SubjectPartyActivity) {
        this.subject = subjectPartyActivity
    }

    var city = ObservableField<String>("湖南省")

    var items = ObservableArrayList<ItemViewModel<BaseViewModel>>()

    var adapter = BindingViewPagerAdapter<ItemViewModel<BaseViewModel>>()

    var itemBinding = ItemBinding.of<ItemViewModel<BaseViewModel>> { itemBinding, position, item ->
        when (position) {
            0 -> {
                itemBinding.set(BR.subject_item_model, R.layout.subject_item_model_layout)
            }
            1 -> {
                itemBinding.set(BR.mo_brigade_item_model, R.layout.mo_brigade_item_model_layout)
            }
            2 -> {
                itemBinding.set(BR.clock_item_model, R.layout.clock_item_model_layout)
            }
        }
    }

    fun onClick(view: View) {

    }
}
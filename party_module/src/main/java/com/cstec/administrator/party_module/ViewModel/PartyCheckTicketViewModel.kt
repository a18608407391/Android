package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import com.cstec.administrator.party_module.Activity.PartyCheckTicketActivity
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.io.Serializable


class PartyCheckTicketViewModel : BaseViewModel(){


    lateinit var activity: PartyCheckTicketActivity
    fun inejct(partyCheckTicketActivity: PartyCheckTicketActivity) {
        this.activity = partyCheckTicketActivity
    }

    var adapter = BindingRecyclerViewAdapter<CheckOneEntity>()
    var items = ObservableArrayList<CheckOneEntity>()
    var itemBinding = ItemBinding.of<CheckOneEntity> { itemBinding, position, item ->
        itemBinding.set(BR.check_one, R.layout.ticked_first_item)
    }
    var SecondAdapter = BindingRecyclerViewAdapter<CheckTwoEntity>()
    var SecondItems = ObservableArrayList<CheckTwoEntity>()
    var SecondItemBinding = ItemBinding.of<CheckTwoEntity> { itemBinding, position, item ->
        if (position == 0) {
            itemBinding.set(BR.check_two, R.layout.ticked_second_item_one)
        } else {
            itemBinding.set(BR.check_two, R.layout.ticked_second_item_two)
        }
    }

    fun initData() {

    }



    class CheckOneEntity : Serializable {
        var check = false
    }

    class CheckTwoEntity : Serializable {

    }
}
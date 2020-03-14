package com.cstec.administrator.party_module.ItemModel

import android.databinding.ObservableArrayList
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.SubjectPartyViewModel
import com.zk.library.Base.ItemViewModel
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.HoriTitleEntity
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer

class SubjectItemModel : ItemViewModel<SubjectPartyViewModel>() {

    var hori = ObservableArrayList<HoriTitleEntity>()

    var command = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            var item = hori.get(t)
            item.check = true
            if (!item.check) {
                hori.forEachIndexed { index, horiTitleEntity ->
                    if (index == t) {
                        hori[index] = item
                    } else {
                        horiTitleEntity.check = false
                        hori[index] = horiTitleEntity
                    }
                }
            }
            load()
        }
    })

    private fun load() {

    }

    var adapter = BindingRecyclerViewAdapter<Any>()

    var items = ObservableArrayList<Any>()

    var itemBinding = ItemBinding.of<Any>() { itemBinding, position, item ->
        itemBinding.set(BR.subject_data, R.layout.subject_child_item_layout)
    }

}
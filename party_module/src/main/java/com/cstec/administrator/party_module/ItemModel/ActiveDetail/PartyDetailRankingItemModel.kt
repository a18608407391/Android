package com.cstec.administrator.party_module.ItemModel.ActiveDetail

import android.databinding.ObservableArrayList
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel
import com.elder.zcommonmodule.Entity.PhotoEntitiy
import com.zk.library.Base.ItemViewModel
import com.cstec.administrator.party_module.BR
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

class PartyDetailRankingItemModel : BasePartyItemModel() {

    var adapter = BindingRecyclerViewAdapter<PhotoEntitiy>()

    var items = ObservableArrayList<PhotoEntitiy>()

    var itemBinding = ItemBinding.of<PhotoEntitiy>() { itemBinding, position, item ->
        if (item.getItemType() == 0) {
            itemBinding.set(BR.title, R.layout.party_detail_title_item)
        } else if (item.getItemType() == 1) {
            itemBinding.set(BR.img_item, R.layout.party_detail_img_item)
        }
    }
}
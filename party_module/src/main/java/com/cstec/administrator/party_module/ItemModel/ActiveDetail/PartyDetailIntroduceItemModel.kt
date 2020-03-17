package com.cstec.administrator.party_module.ItemModel.ActiveDetail

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.util.Log
import com.cstec.administrator.party_module.PartyDetailEntity
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass


class PartyDetailIntroduceItemModel : BasePartyItemModel() {

    var adapter = BindingRecyclerViewAdapter<Any>()

    var items = MergeObservableList<Any>()

    var itemBinding = OnItemBindClass<Any>().map(PartyDetailEntity::class.java) { itemBinding, position, item ->
        itemBinding.set(BR.party_detail_item_html, R.layout.party_detail_item_html)
    }
            .map(PartyDetailEntity.PartyDetailRoadListItem::class.java) { itemBinding, position, item ->
                when (item.type) {
                    0 -> {
                        itemBinding.set(BR.party_detail_item_list, R.layout.party_detail_item_listtop)
                    }
                    1 -> {
                        itemBinding.set(BR.party_detail_item_list, R.layout.party_detail_item_listcontenttop)
                    }
                    2 -> {
                        itemBinding.set(BR.party_detail_item_list, R.layout.party_detail_item_listcontentbottom)
                    }
                }
            }
            .map(PartyDetailEntity.Cost::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.item_cost, R.layout.party_detail_item_cost)
            }.map(String::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.title, R.layout.base_item_title1)
            }

    var list = ObservableArrayList<PartyDetailEntity.PartyDetailRoadListItem>()


    fun initData() {
        var model = viewModel as PartyDetailViewModel
        Log.e("result", "加载数据1")
        var j = PartyDetailEntity.PartyDetailRoadListItem()
        j.type = 0
        var i = PartyDetailEntity.PartyDetailRoadListItem()
        i.type = 1
        var k = PartyDetailEntity.PartyDetailRoadListItem()
        k.type = 1
        k.itemtype = 1
        var G = PartyDetailEntity.PartyDetailRoadListItem()
        G.type = 1
        G.itemtype = 2
        list.add(j)

        list.add(i)
        list.add(k)
        list.add(G)
        list.add(G)
        list.add(G)
        list.add(G)
        var cost = PartyDetailEntity.Cost()
        items.insertItem("活动详情")
        if(model.data.get()==null){
        }else{
            items.insertItem(model.data.get()!!)
        }

        items.insertItem("行程路线")
        items.insertList(list)
        items.insertItem("费用说明")
        items.insertItem(cost)
    }
}
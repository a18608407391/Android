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


    var scrollEnable = ObservableField<Boolean>(true)

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
        //数据是个坑
        model.data.get()?.SCHEDULE!!.forEachIndexed { index, schedules ->
            //添加起点
            var j = PartyDetailEntity.PartyDetailRoadListItem()
            j.type = 0
            if (index < 10) {
                j.DAY = "0" + (index + 1)
            }else{
                j.DAY = (index+1).toString()
            }
            j.PATH_POINT_NAME = schedules.PLACE_DEPARTURE
            j.ADDRESS = schedules.DESTINATION

            list.add(j)
            schedules.HISTORY?.forEachIndexed { index1, partyDetailRoadListItem ->
                var k = PartyDetailEntity.PartyDetailRoadListItem()
                k.type = 1
                if (index1 == schedules.HISTORY!!.size - 1) {
                    k.itemtype = 2
                } else {
                    k.itemtype = 1
                }
                k.PATH_POINT_NAME = partyDetailRoadListItem.INTRODUCE
                k.ADDRESS = partyDetailRoadListItem.INTRODUCE_TYPE
                k.TIME_REQUIRED = partyDetailRoadListItem.ABOUT_TIME
                k.IMAGE1 = partyDetailRoadListItem.IMAGES
                k.START_TIME = partyDetailRoadListItem.START_TIME!!.split(":")[0] + ":" + partyDetailRoadListItem.START_TIME!!.split(":")[1]
                list.add(k)
            }
        }
        var cost = PartyDetailEntity.Cost()
        items.insertItem("活动详情")
        items.insertItem(model.data.get()!!)
        items.insertItem("行程路线")
        items.insertList(list)
        items.insertItem("费用说明")
        items.insertItem(cost)
    }
}
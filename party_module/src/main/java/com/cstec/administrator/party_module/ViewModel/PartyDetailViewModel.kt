package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import com.cstec.administrator.party_module.Activity.PartyDetailActivty
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.PartyDetailAdapter
import com.cstec.administrator.party_module.PartyDetailEntity
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Inteface.TitleClickListener
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.OnItemBind
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass


class PartyDetailViewModel : BaseViewModel(), TitleClickListener {
    override fun onTitleArrowClick(entity: Any) {

    }

    fun inject(partyDetailActivty: PartyDetailActivty) {
        var j = PartyDetailEntity.PartyDetailRoadListItem()
        j.type = 0

        var i = PartyDetailEntity.PartyDetailRoadListItem()
        i.type = 1

        var k = PartyDetailEntity.PartyDetailRoadListItem()
        k.type = 1
        k.itemtype = 1

        list.add(j)
        list.add(i)
        list.add(k)
        items.insertList(list)

    }

    var list = ObservableArrayList<PartyDetailEntity.PartyDetailRoadListItem>()

    var part_One = ObservableField<PartyDetailEntity.PartyDetailPartOne>()        //第一部分数据

    var selectTab = ObservableField<Int>()

    var adapter = PartyDetailAdapter()

    var items = MergeObservableList<Any>()

    var titlelistener: TitleClickListener = this
    var itemBinding = OnItemBindClass<Any>()
            .map(String::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.title, R.layout.base_item_title1).bindExtra(BR.title_listener, titlelistener).bindExtra(BR.position, position)
            }
            .map(PartyDetailEntity.PartyDetailPartOne::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.party_detail_item_top, R.layout.party_detail_item_top)
            }
            .map(Int::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.party_detail_item_tab, R.layout.party_detail_item_tab)
            }
            .map(String::class.java) { itemBinding, position, item ->
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

//            .map(PartyHomeEntity.ClockActive::class.java) { itemBinding, position, item ->
//            }
//            .map(PartyHomeEntity.HotDistination::class.java) { itemBinding, position, item -> }
//
//            .map(PartyHomeEntity.MBRecommend::class.java) { itemBinding, position, item ->
//
//            }.map(PartyHomeEntity.WonderfulActive::class.java) { itemBinding, position, item ->
//
//            }

}
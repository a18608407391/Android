package com.cstec.administrator.party_module.ItemModel.ActiveDetail

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.util.Log
import com.cstec.administrator.party_module.PartyDetailEntity
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ViewModel.PartyClockDetailViewModel
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel
import com.cstec.administrator.party_module.ViewModel.PartyMoboDetailViewModel
import com.elder.zcommonmodule.Base_URL
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.cs.tec.library.Base.Utils.getString


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
            }.map(PartyDetailEntity.ActiveNotice::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.notice, R.layout.active_notice_layout)
            }

    var list = ObservableArrayList<PartyDetailEntity.PartyDetailRoadListItem>()
    var lists = ObservableArrayList<PartyDetailEntity.Cost>()

    fun initData() {
        var model = viewModel as PartyDetailViewModel
        if (!model.data.get()!!.SCHEDULE.isNullOrEmpty()) {
            model.data.get()?.SCHEDULE!!.forEachIndexed { index, schedules ->
                //添加起点
                var j = PartyDetailEntity.PartyDetailRoadListItem()
                j.type = 0
                if (index < 10) {
                    j.DAY = "0" + (index + 1)
                } else {
                    j.DAY = (index + 1).toString()
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
                    k.PATH_POINT_NAME = partyDetailRoadListItem.INTRODUCE_TYPE
                    k.ADDRESS = partyDetailRoadListItem.INTRODUCE
                    k.TIME_REQUIRED = partyDetailRoadListItem.ABOUT_TIME
                    k.IMAGE1 = ArrayList()
                    partyDetailRoadListItem.IMAGES!!.forEach {
                        k.IMAGE1!!.add(Base_URL + it.PROJECT_URL + it.METHOD_PATH_URL + it.FILE_PATH_URL)
                    }
                    if (partyDetailRoadListItem.START_TIME != null) {
                        k.START_TIME = partyDetailRoadListItem.START_TIME!!.split(":")[0] + ":" + partyDetailRoadListItem.START_TIME!!.split(":")[1]
                        list.add(k)
                    }
                }
                if (!model.data.get()!!.TICKET_PRICE_DESCRIBE.isNullOrEmpty()) {
                    model.data.get()!!.TICKET_PRICE_DESCRIBE!!.forEachIndexed { index, describe ->
                        if (!describe.DESCRIBE.isNullOrEmpty() && !describe.NAME_INVOICE.isNullOrEmpty()) {
                            var cost = PartyDetailEntity.Cost()
                            cost.describe = describe.DESCRIBE
                            cost.title = describe.NAME_INVOICE!!
                            if (describe.TICKET_PRICE.isNullOrEmpty()) {
                                cost.price = "免费"
                            } else {
                                cost.price = getString(R.string.rmb) + describe.TICKET_PRICE
                            }
                            lists.add(cost)
                        }
                    }
                }
            }
        }

        if (!model.data.get()!!.DETAILS_ACTIVITIES.isNullOrEmpty()) {
            items.insertItem("活动详情")
            items.insertItem(model.data.get()!!)
        }
        if (!list.isEmpty()) {
            items.insertItem("行程路线")
            items.insertList(list)
        }
        if (!lists.isEmpty()) {
            items.insertItem("费用说明")
            items.insertList(lists)
        }

        if (!model.data.get()!!.ACTIVITY_NOTICE.isNullOrEmpty()) {
            items.insertItem("活动须知")
            var notice = PartyDetailEntity.ActiveNotice()
            notice.notice = model.data.get()!!.ACTIVITY_NOTICE
            items.insertItem(notice)
        }
    }


    fun initDataClock() {
        var model = viewModel as PartyClockDetailViewModel
        if (!model.data.get()!!.SCHEDULE.isNullOrEmpty()) {
            model.data.get()?.SCHEDULE!!.forEachIndexed { index, schedules ->
                //添加起点
                var j = PartyDetailEntity.PartyDetailRoadListItem()
                j.type = 0
                if (index < 10) {
                    j.DAY = "0" + (index + 1)
                } else {
                    j.DAY = (index + 1).toString()
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
                    k.PATH_POINT_NAME = partyDetailRoadListItem.INTRODUCE_TYPE
                    k.ADDRESS = partyDetailRoadListItem.INTRODUCE
                    k.TIME_REQUIRED = partyDetailRoadListItem.ABOUT_TIME
                    k.IMAGE1 = ArrayList()
                    partyDetailRoadListItem.IMAGES!!.forEach {
                        k.IMAGE1!!.add(Base_URL + it.PROJECT_URL + it.METHOD_PATH_URL + it.FILE_PATH_URL)
                    }
                    if (partyDetailRoadListItem.START_TIME != null) {
                        Log.e("result", "开始时间" + partyDetailRoadListItem.START_TIME)
                        k.START_TIME = partyDetailRoadListItem.START_TIME!!.split(":")[0] + ":" + partyDetailRoadListItem.START_TIME!!.split(":")[1]
                        list.add(k)
                    }
                }
                if (!model.data.get()!!.TICKET_PRICE_DESCRIBE.isNullOrEmpty()) {
                    model.data.get()!!.TICKET_PRICE_DESCRIBE!!.forEachIndexed { index, describe ->
                        if (!describe.DESCRIBE.isNullOrEmpty() && !describe.NAME_INVOICE.isNullOrEmpty()) {
                            var cost = PartyDetailEntity.Cost()
                            cost.describe = describe.DESCRIBE
                            cost.title = describe.NAME_INVOICE!!
                            if (describe.TICKET_PRICE.isNullOrEmpty()) {
                                cost.price = "免费"
                            } else {
                                cost.price = getString(R.string.rmb) + describe.TICKET_PRICE
                            }
                            lists.add(cost)
                        }
                    }
                }
            }
        }

        if (!model.data.get()!!.DETAILS_ACTIVITIES.isNullOrEmpty()) {
            items.insertItem("活动详情")
            items.insertItem(model.data.get()!!)
        }
        if (!list.isEmpty()) {
            items.insertItem("行程路线")
            items.insertList(list)
        }
        if (!lists.isEmpty()) {
            items.insertItem("费用说明")
            items.insertList(lists)
        }

        if (!model.data.get()!!.ACTIVITY_NOTICE.isNullOrEmpty()) {
            items.insertItem("活动须知")
            var notice = PartyDetailEntity.ActiveNotice()
            notice.notice = model.data.get()!!.ACTIVITY_NOTICE
            items.insertItem(notice)
        }
    }

    fun initDataSubject() {
        var model = viewModel as PartyMoboDetailViewModel
        if (!model.data.get()!!.SCHEDULE.isNullOrEmpty()) {
            model.data.get()?.SCHEDULE!!.forEachIndexed { index, schedules ->
                //添加起点
                var j = PartyDetailEntity.PartyDetailRoadListItem()
                j.type = 0
                if (index < 10) {
                    j.DAY = "0" + (index + 1)
                } else {
                    j.DAY = (index + 1).toString()
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
                    k.PATH_POINT_NAME = partyDetailRoadListItem.INTRODUCE_TYPE
                    k.ADDRESS = partyDetailRoadListItem.INTRODUCE
                    k.TIME_REQUIRED = partyDetailRoadListItem.ABOUT_TIME
                    k.IMAGE1 = ArrayList()
                    partyDetailRoadListItem.IMAGES!!.forEach {
                        k.IMAGE1!!.add(Base_URL + it.PROJECT_URL + it.METHOD_PATH_URL + it.FILE_PATH_URL)
                    }
                    if (partyDetailRoadListItem.START_TIME != null) {
                        k.START_TIME = partyDetailRoadListItem.START_TIME!!.split(":")[0] + ":" + partyDetailRoadListItem.START_TIME!!.split(":")[1]
                        list.add(k)
                    }
                }
                if (!model.data.get()!!.TICKET_PRICE_DESCRIBE.isNullOrEmpty()) {
                    model.data.get()!!.TICKET_PRICE_DESCRIBE!!.forEachIndexed { index, describe ->
                        if (!describe.DESCRIBE.isNullOrEmpty() && !describe.NAME_INVOICE.isNullOrEmpty()) {
                            var cost = PartyDetailEntity.Cost()
                            cost.describe = describe.DESCRIBE
                            cost.title = describe.NAME_INVOICE!!
                            if (describe.TICKET_PRICE.isNullOrEmpty()) {
                                cost.price = "免费"
                            } else {
                                cost.price = getString(R.string.rmb) + describe.TICKET_PRICE
                            }
                            lists.add(cost)
                        }
                    }
                }
            }
        }

        if (!model.data.get()!!.DETAILS_ACTIVITIES.isNullOrEmpty()) {
            items.insertItem("活动详情")
            items.insertItem(model.data.get()!!)
        }
        if (!list.isEmpty()) {
            items.insertItem("行程路线")
            items.insertList(list)
        }
        if (!lists.isEmpty()) {
            items.insertItem("费用说明")
            items.insertList(lists)
        }

        if (!model.data.get()!!.ACTIVITY_NOTICE.isNullOrEmpty()) {
            items.insertItem("活动须知")
            var notice = PartyDetailEntity.ActiveNotice()
            notice.notice = model.data.get()!!.ACTIVITY_NOTICE
            items.insertItem(notice)
        }
    }
}
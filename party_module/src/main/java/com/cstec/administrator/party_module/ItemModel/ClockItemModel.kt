package com.cstec.administrator.party_module.ItemModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.*
import com.cstec.administrator.party_module.ViewModel.SubjectPartyViewModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer

class ClockItemModel : BasePartyItemModel(), HttpInteface.PartyClock_inf, SubjectClick, HttpInteface.PartyUnReadNotify_inf {
    override fun PartyUnReadNotifySucccess(it: String) {
        var model = viewModel as SubjectPartyViewModel
        model.msgCount.set(Integer.valueOf(it))
        model.onCreate = true
    }

    override fun PartyUnReadNotifyError(it: Throwable) {
    }

    override fun onSubjectItemClick(entity: SubjectEntity) {
        RxBus.default!!.post(RxBusEven.getInstance(RxBusEven.StatusBar,0))
        var model = viewModel as SubjectPartyViewModel
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                model.subject.location)
        bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, model.city.get())
        bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
        bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE)
        model.startFragment(model.subject, RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL, bundle)
//
//        ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
//                .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                        Location(model.subject.location!!.latitude, model.subject.location!!.longitude)).withInt(RouterUtils.PartyConfig.NavigationType, 1).withInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, model.subject.city).navigation()
    }

    override fun PartyClockSucccess(it: String) {
        var model = viewModel as SubjectPartyViewModel
        refreshStatus.set(2)
        var entity = Gson().fromJson<SubjectItemModelEntity>(it, SubjectItemModelEntity::class.java)
        if (!entity.data.isNullOrEmpty()) {
            entity.data!!.forEach {
                var item = it
                item.DISTANCE = "全长" + item.DISTANCE + "km" + " 距离" + it.SQRTVALUE + "km"
                var start = item.ACTIVITY_START!!.split(" ")[0]
                var stop = item.ACTIVITY_STOP!!.split(" ")[0]
                item.ACTIVITY_START = start + "至" + stop
                items.add(item)
            }
        }
        if (!model.onCreate) {
            getUnRead()
        }
    }

    override fun PartyClockError(it: Throwable) {
        var model = viewModel as SubjectPartyViewModel
        model.subject.dismissProgressDialog()
    }

    fun getUnRead() {
        HttpRequest.instance.partyUnRead = this
        HttpRequest.instance.getPartyActiveUnRead(java.util.HashMap())
    }

    override fun refresh() {
        super.refresh()
        load(true)
        refreshStatus.set(1)
    }

    var hori = ObservableArrayList<HoriTitleEntity>().apply {
        this.add(HoriTitleEntity("推荐", true))
        this.add(HoriTitleEntity("热门", false))
        this.add(HoriTitleEntity("离我最近", false))
        this.add(HoriTitleEntity("活动里程", false))
    }


    var command = BindingCommand(object : BindingConsumer<HoriTitleEntity> {
        override fun call(t: HoriTitleEntity) {
            var i = hori.indexOf(t)
            if (!t.check) {
                hori.forEachIndexed { index, horiTitleEntity ->
                    horiTitleEntity.check = index == i
                    hori.set(index, horiTitleEntity)
                }
            }
            type.set(i + 1)
            load(true)
        }
    })

    override fun load(flag: Boolean) {
        super.load(flag)
        if (flag) {
            items.clear()
            start = 1
            pageSize = 10
        }
        var model = viewModel as SubjectPartyViewModel
        HttpRequest.instance.partyClock = this
        var map = HashMap<String, String>()
        map["x"] = model.subject?.location!!.longitude.toString()
        map["y"] = model.subject?.location!!.latitude.toString()
        map["type"] = type.get().toString()
        map["city"] = model.city.get()!!
        map["start"] = start.toString()
        map["pageSize"] = pageSize.toString()
        HttpRequest.instance.getPartyClock(map)
    }

    var type = ObservableField(1)
    var start = 1
    var pageSize = 10


    var adapter = BindingRecyclerViewAdapter<SubjectEntity>()

    var items = ObservableArrayList<SubjectEntity>()
    var listener: SubjectClick = this
    var itemBinding = ItemBinding.of<SubjectEntity> { itemBinding, position, item ->
        itemBinding.set(BR.subject_data, R.layout.clock_child_item_layout).bindExtra(BR.listener, listener)
    }

    var cur = 0L
    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            Log.e("result", "加载更多" + t)
            var model = viewModel as SubjectPartyViewModel
            if (!model.onCreate) {
                return
            }
            if (System.currentTimeMillis() - cur > 1000) {
                if (t < start * pageSize) {
                    return
                } else {
                    start++
                    load(false)
                }
            } else {
                cur = System.currentTimeMillis()
            }
        }
    })
}
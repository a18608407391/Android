package com.cstec.administrator.party_module.ItemModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.*
import com.cstec.administrator.party_module.ViewModel.SubjectPartyViewModel
import com.zk.library.Base.ItemViewModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer

class MoBrigadeItemModel : BasePartyItemModel(), HttpInteface.PartyMoto_inf, SubjectClick, HttpInteface.PartyUnReadNotify_inf {
    override fun PartyUnReadNotifySucccess(it: String) {
        var model = viewModel as SubjectPartyViewModel
        model.msgCount.set(Integer.valueOf(it))
        model.onCreate = true
    }

    override fun PartyUnReadNotifyError(it: Throwable) {
    }

    override fun onSubjectItemClick(entity: SubjectEntity) {

        var model = viewModel as SubjectPartyViewModel
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                model.subject.location)
        bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, model.city.get())
        bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
        bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE)
        model.startFragment(model.subject, RouterUtils.PartyConfig.PARTY_DETAIL, bundle)
//        ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
//                .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                        Location(model.subject.location!!.latitude, model.subject.location!!.longitude)).withInt(RouterUtils.PartyConfig.NavigationType, 1).withInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, model.subject.city).navigation()
    }

    override fun PartyMotoSucccess(it: String) {
        var model = viewModel as SubjectPartyViewModel
        refreshStatus.set(2)
        var entity = Gson().fromJson<SubjectItemModelEntity>(it, SubjectItemModelEntity::class.java)
        if (!entity.data.isNullOrEmpty()) {
            entity.data!!.forEach {
                var model = it
                if (model.DISTANCE == null) {
                    model.DISTANCE = "0"
                }
                model.DISTANCE = "时长" + model.DAY + "天" + " " + "里程" + model.DISTANCE + "km"
                if (model.TICKET_PRICE.isNullOrEmpty() || model.TICKET_PRICE!!.toDouble() <= 0) {
                    model.TICKET_PRICE = "免费"
                } else {
                    model.TICKET_PRICE = getString(R.string.rmb) + model.TICKET_PRICE
                }
                items.add(model)
            }
        }

        if (!model.onCreate) {
            getUnRead()
        }
    }

    override fun refresh() {
        super.refresh()
        load(true)
        refreshStatus.set(1)
    }

    fun getUnRead() {
        HttpRequest.instance.partyUnRead = this
        HttpRequest.instance.getPartyActiveUnRead(HashMap())
    }

    override fun PartyMotoError(it: Throwable) {
        var model = viewModel as SubjectPartyViewModel
        model.subject.dismissProgressDialog()
    }

    var hori = ObservableArrayList<HoriTitleEntity>().apply {
        this.add(HoriTitleEntity("推荐", true))
        this.add(HoriTitleEntity("热门", false))
        this.add(HoriTitleEntity("活动里程", false))
        this.add(HoriTitleEntity("最新发布", false))
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


    var listener: SubjectClick = this


    override fun load(flag: Boolean) {
        super.load(flag)
        Log.e("result", "加载Mobo")
        if (flag) {
            items.clear()
            start = 1
            pageSize = 10
        }
        var model = viewModel as SubjectPartyViewModel
        HttpRequest.instance.partyMobo = this
        var map = HashMap<String, String>()
        map["x"] = model.subject?.location!!.longitude.toString()
        map["y"] = model.subject?.location!!.latitude.toString()
        map["type"] = type.get().toString()
        map["city"] = model.city.get()!!
        map["start"] = start.toString()
        map["pageSize"] = pageSize.toString()
        HttpRequest.instance.getPartyMobo(map)
    }

    var type = ObservableField(1)
    var start = 1
    var pageSize = 10
    var adapter = BindingRecyclerViewAdapter<SubjectEntity>()
    var items = ObservableArrayList<SubjectEntity>()
    var itemBinding = ItemBinding.of<SubjectEntity>() { itemBinding, position, item ->
        itemBinding.set(BR.subject_data, R.layout.mobrigage_child_item_layout).bindExtra(BR.listener, listener)
    }


    var cur = 0L


    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {

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
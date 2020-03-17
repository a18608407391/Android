package com.cstec.administrator.party_module.ItemModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.util.Log
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.SubjectPartyViewModel
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.HoriTitleEntity
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.cstec.administrator.party_module.SubjectItemModelEntity
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer

class ClockItemModel : BasePartyItemModel(), HttpInteface.PartyClock_inf {
    override fun PartyClockSucccess(it: String) {
        var model = viewModel as SubjectPartyViewModel
        model.subject.dismissProgressDialog()
        var entity = Gson().fromJson<SubjectItemModelEntity>(it, SubjectItemModelEntity::class.java)
        if (!entity.data.isNullOrEmpty()) {
            entity.data!!.forEach {
                items.add(it)
            }
        }

        Log.e("result", "itemSize" + items.size)
    }

    override fun PartyClockError(it: Throwable) {
        var model = viewModel as SubjectPartyViewModel
        model.subject.dismissProgressDialog()
    }

    var hori = ObservableArrayList<HoriTitleEntity>().apply {
        this.add(HoriTitleEntity("推荐",true))
        this.add(HoriTitleEntity("热门",false))
        this.add(HoriTitleEntity("离我最近",false))
        this.add(HoriTitleEntity("活动里程",false))
    }


    var command = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            var item = hori.get(t)
            if (!item.check) {
                hori.forEachIndexed { index, horiTitleEntity ->
                    if (index == t) {
                        item.check = true
                        hori[index] = item
                    } else {
                        horiTitleEntity.check = false
                        hori[index] = horiTitleEntity
                    }
                }
            }
            type.set(t + 1)
            load(true)
        }
    })

    override fun load(flag: Boolean) {
        super.load(flag)
        if (flag) {
            items.clear()
        }
        var model = viewModel as SubjectPartyViewModel
        model.subject.showProgressDialog("正在获取打卡列表数据.....")
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


    var adapter = BindingRecyclerViewAdapter<Any>()

    var items = ObservableArrayList<Any>()

    var itemBinding = ItemBinding.of<Any>() { itemBinding, position, item ->
        itemBinding.set(BR.subject_data, R.layout.clock_child_item_layout)
    }
    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            Log.e("result", "加载更多" + t)
            if (t <start*pageSize) {
                return
            }else{
                start++
                load(false)
            }
        }
    })
}
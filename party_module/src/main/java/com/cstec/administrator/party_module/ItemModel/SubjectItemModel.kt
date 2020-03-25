package com.cstec.administrator.party_module.ItemModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.*
import com.cstec.administrator.party_module.ViewModel.SubjectPartyViewModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.util.*

class SubjectItemModel : BasePartyItemModel(), HttpInteface.PartySuject_inf, SubjectClick, HttpInteface.PartyUnReadNotify_inf {
    override fun PartyUnReadNotifySucccess(it: String) {
        var model = viewModel as SubjectPartyViewModel
        model.msgCount.set(Integer.valueOf(it))
        model.onCreate = true
    }

    override fun PartyUnReadNotifyError(it: Throwable) {
    }

    override fun onSubjectItemClick(entity: SubjectEntity) {

        var model = viewModel as SubjectPartyViewModel
        ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
                .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        Location(model.subject.location!!.latitude, model.subject.location!!.longitude)).withInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, model.subject.city).navigation()

    }

    override fun PartySubjectSucccess(it: String) {
        var model = viewModel as SubjectPartyViewModel
        model.subject.dismissProgressDialog()
        model.subject.dismissProgressDialog()
        var entity = Gson().fromJson<SubjectItemModelEntity>(it, SubjectItemModelEntity::class.java)
        if (!entity.data.isNullOrEmpty()) {
            entity.data!!.forEach {
                items.add(it)
            }
        }
        if (!model.onCreate) {
            getUnRead()
        }
    }

    override fun PartySubjectError(it: Throwable) {
        var model = viewModel as SubjectPartyViewModel
        model.subject.dismissProgressDialog()
    }

    var hori = ObservableArrayList<HoriTitleEntity>().apply {
        this.add(HoriTitleEntity("推荐", true))
        this.add(HoriTitleEntity("热门", false))
        this.add(HoriTitleEntity("离我最近", false))
        this.add(HoriTitleEntity("活动里程", false))
    }

    var command = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            var item = hori.get(t)
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
            type.set(t + 1)
            load(true)
        }
    })
    var type = ObservableField(1)
    var start = 1
    var pageSize = 10
    fun getUnRead() {
        HttpRequest.instance.partyUnRead = this
        HttpRequest.instance.getPartyActiveUnRead(HashMap())
    }

    override fun load(flag: Boolean) {
        super.load(flag)
        if (flag) {
            items.clear()
            start = 1
            pageSize = 10
        }
        var model = viewModel as SubjectPartyViewModel
        model.subject.showProgressDialog("正在获取主题列表数据.....")
        HttpRequest.instance.partySubject = this
        var map = HashMap<String, String>()
        map["x"] = model.subject?.location!!.longitude.toString()
        map["y"] = model.subject?.location!!.latitude.toString()
        map["type"] = type.get().toString()
        map["city"] = model.city.get()!!
        map["start"] = start.toString()
        map["pageSize"] = pageSize.toString()
        HttpRequest.instance.getPartySubject(map)
    }

    var adapter = BindingRecyclerViewAdapter<SubjectEntity>()

    var items = ObservableArrayList<SubjectEntity>()

    var listener: SubjectClick = this
    var itemBinding = ItemBinding.of<SubjectEntity> { itemBinding, position, item ->
        itemBinding.set(BR.subject_data, R.layout.subject_child_item_layout).bindExtra(BR.listener, listener)
    }
    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            Log.e("result", "加载更多" + t)
            if (t < start * pageSize) {
                return
            } else {
                start++
                load(false)
            }
        }
    })
}
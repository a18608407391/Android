package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.support.design.widget.TabLayout
import android.view.View
import com.cstec.administrator.party_module.Activity.PartyClockDetailActivity
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailIntroduceItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailPhotoItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailRankingItemModel
import com.cstec.administrator.party_module.PartyDetailEntity
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class PartyClockDetailViewModel : BaseViewModel(), HttpInteface.PartyDetail, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>, HttpInteface.PartyRestore_inf {
    override fun PartyRestoreSucccess(it: String) {
        party.dismissProgressDialog()
        if (collection.get() == 0) {
            collection.set(1)
        } else {
            collection.set(0)
        }
    }

    override fun PartyRestoreError(it: Throwable) {

    }

    override fun onTabReselected(p0: TabLayout.Tab?) {
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        var t = p0?.position
        if (t == 0) {
            initData()
        } else if (t == 1) {
            var model = items[1] as PartyDetailPhotoItemModel
            model.initData(true)
        } else if (t == 2) {
            var model = items[2] as PartyDetailRankingItemModel
            model.initData()
        }
    }

    override fun getPartyDetailSuccess(it: String) {
        if (it.isNullOrEmpty()) {
            return
        }
        var entity = Gson().fromJson<PartyDetailEntity>(it, PartyDetailEntity::class.java)
        if (!entity.TYPE.isNullOrEmpty()) {
            entity.TYPE!!.split(",").forEach {
                typeData.add(it)
            }
        }
        if (!entity.SIGN_UP.isNullOrEmpty()) {
            entity.SIGN_UP!!.forEach {
                if (it.HEAD_IMG_FILE.isNullOrEmpty()) {
                    members.add("")
                } else {
                    members.add(it.HEAD_IMG_FILE)
                }
            }
        }
        restoreTime.set(entity.COLLECTION_TIME!!.split(" ")[0])
        data.set(entity)
        var t = items[0] as PartyDetailIntroduceItemModel
        t.initDataClock()
    }

    override fun getPartyDetailError(it: Throwable) {

    }


    lateinit var party: PartyClockDetailActivity
    fun inject(partyClockDetailActivity: PartyClockDetailActivity) {
        this.party = partyClockDetailActivity
        items.add(PartyDetailIntroduceItemModel().ItemViewModel(this@PartyClockDetailViewModel))
        items.add(PartyDetailPhotoItemModel().setActivity(partyClockDetailActivity).setPartyId(party.code).ItemViewModel(this@PartyClockDetailViewModel))
        items.add(PartyDetailRankingItemModel().setCode(party.code).ItemViewModel(this@PartyClockDetailViewModel))
    }

    var collection = ObservableField(0)

    var visible = ObservableField<Boolean>(false)
    var data = ObservableField<PartyDetailEntity>()

    var typeData = ObservableArrayList<String>()

    var members = ObservableArrayList<String>()

    var restoreTime = ObservableField<String>()

    var mTiltes = arrayOf("介绍", "相册", "排名")

    var pagerTitle = BindingViewPagerAdapter.PageTitles<BasePartyItemModel> { position, item ->
        mTiltes[position]
    }

    var tabCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {

            if (t == 0) {
                var model = items[0] as PartyDetailIntroduceItemModel
                model.initData()
            } else if (t == 1) {
                var model = items[1] as PartyDetailPhotoItemModel
                model.initData(true)
            } else if (t == 2) {
                var model = items[2] as PartyDetailRankingItemModel
                model.initData()
            }
        }
    })

    var adapter = BindingViewPagerAdapter<BasePartyItemModel>()

    var items = ObservableArrayList<BasePartyItemModel>().apply {

    }
    var itemBinding = ItemBinding.of<BasePartyItemModel> { itemBinding, position, item ->
        when (position) {
            0 -> {
                itemBinding.set(BR.detail_introduce, R.layout.party_detail_part_introduce)
            }
            1 -> {
                itemBinding.set(BR.detail_photo, R.layout.party_detail_part_photo)
            }
            2 -> {
                itemBinding.set(BR.detail_ranking, R.layout.party_detail_part_ranking)
            }
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.detail_arrow -> {
                finish()
            }
            R.id.arrow_party -> {
                finish()
            }
            R.id.tel_phone -> {

            }
            R.id.restore_party -> {
                party.showProgressDialog(getString(R.string.http_loading))
                HttpRequest.instance.partyRestore = this
                var map = HashMap<String, String>()
                map["targetId"] = data.get()!!.ID.toString()
                map["targetIdType"] = data.get()!!.TYPE.toString()
                HttpRequest.instance.getPartyRestore(map)
            }
            R.id.right_now -> {

            }
        }
    }

    private fun initData() {
        if (data.get() == null) {
            HttpRequest.instance.partyDetail = this
            var map = HashMap<String, String>()
            map["id"] = party.party_id!!.toString()
            map["x"] = party.location!!.longitude.toString()
            map["y"] = party.location!!.latitude.toString()
            HttpRequest.instance.getPartyDetail(map)
        }
    }
}
package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.util.Log
import android.view.View
import com.cstec.administrator.party_module.Activity.PartyDetailActivty
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailIntroduceItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailPhotoItemModel
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Inteface.TitleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class PartyDetailViewModel : BaseViewModel(), TitleClickListener, HttpInteface.PartyDetail {
    override fun getPartyDetailSuccess(it: String) {
        Log.e("result", "活动详情数据" + it)
    }

    override fun getPartyDetailError(it: Throwable) {

    }

    override fun onTitleArrowClick(entity: Any) {

    }

    lateinit var partyDetailActivty: PartyDetailActivty
    fun inject(partyDetailActivty: PartyDetailActivty) {
        this.partyDetailActivty = partyDetailActivty
        var model = items[0] as PartyDetailIntroduceItemModel
        model.initData()
        initData()
    }

    private fun initData() {
        HttpRequest.instance.partyDetail = this
        var map = HashMap<String, String>()
        map["id"] = partyDetailActivty.party_id!!
        map["x"] = partyDetailActivty.location!!.longitude.toString()
        map["y"] = partyDetailActivty.location!!.latitude.toString()
        HttpRequest.instance.getPartyDetail(map)
    }

    var tabCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            if (t == 0) {
                var model = items[0] as PartyDetailIntroduceItemModel
                model.initData()
            } else {

            }
        }
    })

    fun onClick(view:View){

    }

    var titlelistener: TitleClickListener = this


    var mTiltes = arrayOf("介绍", "相册")

    var pagerTitle = BindingViewPagerAdapter.PageTitles<BasePartyItemModel> { position, item ->
        mTiltes[position]
    }

    var adapter = BindingViewPagerAdapter<BasePartyItemModel>()

    var items = ObservableArrayList<BasePartyItemModel>().apply {
        this.add(PartyDetailIntroduceItemModel())
        this.add(PartyDetailPhotoItemModel())
    }
    var itemBinding = ItemBinding.of<BasePartyItemModel> { itemBinding, position, item ->
        when (position) {
            0 -> {
                itemBinding.set(BR.detail_introduce, R.layout.party_detail_part_introduce)
            }
            1 -> {
                itemBinding.set(BR.detail_photo, R.layout.party_detail_part_photo)
            }
        }
    }
}
package com.cstec.administrator.party_module.ItemModel.ActiveDetail

import android.databinding.ObservableArrayList
import android.util.Log
import com.amap.api.track.query.model.BaseResponse
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel
import com.elder.zcommonmodule.Entity.PhotoEntitiy
import com.zk.library.Base.ItemViewModel
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.PartyRankingEntity
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

class PartyDetailRankingItemModel : BasePartyItemModel(), HttpInteface.PartyRanking_inf {
    override fun PartyRankingSucccess(it: String) {
        Log.e("result", "PartyAlbumSucccess数据" + it)
        var entity = Gson().fromJson<PartyRankingEntity>(it, PartyRankingEntity::class.java)
        if (!entity.data.isNullOrEmpty()) {
            entity.data!!.forEach {
                items.add(it)
            }
        }
    }

    override fun PartyRankingError(it: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    var adapter = BindingRecyclerViewAdapter<PartyRankingEntity.PartyRanking>()

    var items = ObservableArrayList<PartyRankingEntity.PartyRanking>()

    var itemBinding = ItemBinding.of<PartyRankingEntity.PartyRanking>() { itemBinding, position, item ->
        itemBinding.set(BR.party_ranking, R.layout.party_ranking_item).bindExtra(BR.position, position)
    }


    var mCode = 0
    fun setCode(code: Int): PartyDetailRankingItemModel {
        this.mCode = code
        return this
    }

    var start = 1
    var pageSize = 100
    fun initData() {
        items.clear()
        HttpRequest.instance.partyRanking = this
        var map = HashMap<String, String>()
        map["code"] = mCode.toString()
        map["start"] = start.toString()
        map["pageSize"] = pageSize.toString()
        HttpRequest.instance.getPartyRanking(map)
    }


}
package com.cstec.administrator.party_module.ItemModel.ActiveDetail

import android.databinding.ObservableArrayList
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

class PartyDetailRankingItemModel : BasePartyItemModel(), HttpInteface.PartyAlbum_inf {
    override fun PartyAlbumSucccess(it: String) {
        var entity = Gson().fromJson<ArrayList<PartyRankingEntity>>(it, object : TypeToken<ArrayList<PartyRankingEntity>>() {}.type)
        entity.forEach {
            items.add(it)
        }
    }

    override fun PartyAlbumError(it: Throwable) {
    }

    var adapter = BindingRecyclerViewAdapter<PartyRankingEntity>()

    var items = ObservableArrayList<PartyRankingEntity>()

    var itemBinding = ItemBinding.of<PartyRankingEntity>() { itemBinding, position, item ->
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
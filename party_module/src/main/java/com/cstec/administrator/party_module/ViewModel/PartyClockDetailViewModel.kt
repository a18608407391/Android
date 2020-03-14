package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailIntroduceItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailPhotoItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailRankingItemModel
import com.cstec.administrator.party_module.R
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding


class PartyClockDetailViewModel : BaseViewModel() {
    var mTiltes = arrayOf("介绍", "相册", "排名")

    var pagerTitle = BindingViewPagerAdapter.PageTitles<BasePartyItemModel> { position, item ->
        mTiltes[position]
    }

    var adapter = BindingViewPagerAdapter<BasePartyItemModel>()

    var items = ObservableArrayList<BasePartyItemModel>().apply {
        this.add(PartyDetailIntroduceItemModel())
        this.add(PartyDetailPhotoItemModel())
        this.add(PartyDetailRankingItemModel())
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
}
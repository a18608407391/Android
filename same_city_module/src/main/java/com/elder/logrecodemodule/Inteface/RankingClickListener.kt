package com.elder.logrecodemodule.Inteface

import com.elder.zcommonmodule.Entity.CountryMemberEntity


interface RankingClickListener {

    fun RankingItemClick(position: Int, item: CountryMemberEntity)
}
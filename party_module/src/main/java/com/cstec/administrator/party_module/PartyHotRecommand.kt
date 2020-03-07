package com.cstec.administrator.party_module

import android.databinding.ObservableArrayList
import java.io.Serializable


class PartyHotRecommand  :Serializable{

    var list = ObservableArrayList<PartyHomeEntity.HotRecommend>()

}
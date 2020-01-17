package com.elder.zcommonmodule.Entity

import android.databinding.ObservableField
import java.io.Serializable


class SpecActiveData  :Serializable{



    var url  = ObservableField<String>()

    var title = ObservableField<String>("骑视:英国一地8天轻奢深度游")

    var time =  ObservableField<String>("2019-8-24至9-15·爱摩生活馆")

    var type =  0



}
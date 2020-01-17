package com.elder.zcommonmodule.Entity

import java.io.Serializable


class SearchEntity : Serializable {


    var data: ArrayList<CountryMemberEntity>? = null
    var draw = 0
    var recordsFiltered = 0

    var recordsTotal = 0
}
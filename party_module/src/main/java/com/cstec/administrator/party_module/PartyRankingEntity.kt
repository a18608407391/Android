package com.cstec.administrator.party_module

import java.io.Serializable


class PartyRankingEntity : Serializable {


    var data: ArrayList<PartyRanking>? = null


    class PartyRanking : Serializable {
        var ID = 0
        var NAME: String? = null
        var TEL: String? = null
        var HEAD_IMG_FILE: String? = null
        var DISTANCE_SUM: String? = null
        var CREATE_DATE: String? = null
    }


}
package com.elder.zcommonmodule.Entity

import java.io.Serializable


class HomeEntitiy : Serializable {

    var code = 0

    var data: HomeBean? = null

    var msg: String? = null


    class HomeBean : Serializable {
        var findMemberView  :UserInfo.Result ? = null
        var wholeCountryMember :ArrayList<CountryMemberEntity> ? = null
        var queryGuideList  :ArrayList<HotData> ? = null
        var sameCityMember  :ArrayList<CountryMemberEntity> ? = null
    }
}
package com.cstec.administrator.social.Entity

import java.io.Serializable


class FocusMemberListEntity : Serializable {

    var data: ArrayList<FocusMemberEntity>? = null

    class FocusMemberEntity : Serializable {
        var createDate: String? = null
        var fansMemberId :String ? = null
        var memberName :String ? = null
        var memberImage :String ? = null
    }

}
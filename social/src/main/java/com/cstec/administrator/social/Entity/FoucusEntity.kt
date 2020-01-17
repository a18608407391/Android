package com.cstec.administrator.social.Entity

import com.elder.zcommonmodule.Entity.SocialHoriEntity
import java.io.Serializable


class FoucusEntity : Serializable {

    var data: ArrayList<SocialHoriEntity>? = null

    var draw = 0

    var recordsFiltered: Double = 0.0

    var recordsTotal: Double = 0.0


}
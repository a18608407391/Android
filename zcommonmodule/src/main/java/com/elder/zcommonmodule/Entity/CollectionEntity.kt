package com.elder.zcommonmodule.Entity

import java.io.Serializable


class CollectionEntity : Serializable {


    var data: ArrayList<Collection>? = null


    class Collection : Serializable {

        var createDate: String? = null

        var dynamicId: String? = null

        var id: String? = null

        var isRead: String? = null

        var memberId: String? = null

        var memberImages: String? = null
        var memberName: String? = null

        var releaseDynamicParent:DynamicsCategoryEntity.Dynamics? = null

    }


}
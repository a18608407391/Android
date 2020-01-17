package com.elder.zcommonmodule.Entity

import java.io.Serializable


class DynamicsSimple  :Serializable{

    var collectionCount: String? = null
    var commentCount: String? = null
    var createDate: String? = null
    var distance: String? = null
    var dynamicImageList: ArrayList<SocialPhotoEntity>? = null
    var dynamicSpotFabulousList: ArrayList<SocialHoriEntity>? = null
    var fabulousCount: String? = null
    var id: String? = null
    var length: String? = null
    var memberId: String? = null
    var memberImageUrl: String? = null
    var memberName: String? = null
    //        var mentionListId: ArrayList<String>? = null
    var pageSize: String? = null
    var parentDynaminId: String? = null
    var parentDynamin: DynamicsCategoryEntity.Dynamics? = null
    var publishContent: String? = null
    var relationTypeId: String? = null
    var releaseAddress: String? = null
    var saveAlbum: String? = null
    var score: String? = null
    var state: String? = null
    var type: String? = null
    var typeDesc: String? = null
    var typeImage: String? = null
    var typeTitle: String? = null
    var updateDate: String? = null
    var xAxis: String? = null
    var yAxis: String? = null
}
package com.elder.zcommonmodule.Entity

import java.io.Serializable


class LikesEntity : Serializable {


    var data: ArrayList<LikeBean>? = null


    var img: String? = null

    var name: String? = null

    var time: String? = null

    var content: String? = null

    var likeImg: String? = null

    var isFrends = 0


    class LikeBean : Serializable {
        var commentId: String? = null

        var createDate: String? = null

        var dynamicCommentParent: CommentDetailBean? = null

        var releaseDynamicParent: DynamicsCategoryEntity.Dynamics? = null

        var dynamicId: String? = null

        var id: String? = null

        var isRead: String? = null

        var length: String? = null

        var memberId: String? = null

        var memberImages: String? = null

        var memberName: String? = null
    }

}
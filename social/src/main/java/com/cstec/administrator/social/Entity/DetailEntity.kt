package com.cstec.administrator.social.Entity

import android.location.Location
import java.io.Serializable


open class DetailEntity : Serializable {

    var detailId = 0

    var dynamicsType = 0     //  0 代表照片   1 代表视频  2 代表活动

    var avatar: String? = null   //头像

    var name: String? = null  //名字

    var publishTime: String? = null  //发布时间

    var journal: String? = null   //日志

    var videoUrl: String? = null

    var activeUrl: String? = null

    var publishPhotoUrls: ArrayList<String>? = null  //发布图片集

    var publishLocation: Location? = null    //发布动态地理位置

    var likerPhotosUrls: ArrayList<String>? = null //浏览人群头像

    var LikeNumber: Int = 0   //点赞数

    var comments: ArrayList<CommentDetail>? = null

    var starCount = 0         //点赞数

    var commentCount = 0        //评论数


    class CommentDetail : Serializable {
        var commentId = 0

        var avatar: String? = null     //评论人头像

        var starCount = 0

        var name: String? = null  //评论人名字

        var publishTime: String? = null

        var commentContent: String? = null

        var additionalComment: ArrayList<Additional>? = null
    }


    class Additional : Serializable {
        var additionalId = 0

        var commentType: Int = 0            //默认 0为普通评论   1 为回复评论

        var commentator: String? = null        //评论人

        var starCount = 0

        var argued: String? = null            //回复人        // 普通评论默认为null  回复评论时有值

        var commentContent: String? = null
    }
}
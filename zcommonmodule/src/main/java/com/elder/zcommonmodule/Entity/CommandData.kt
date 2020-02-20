package com.elder.zcommonmodule.Entity

import java.io.Serializable


class CommandData : Serializable {

    var ID = 0  //评论ID
    var DYNAMIC_ID = 0
    var REPLY_COMMENT_ID = 0 //上个评论的ID
    var COMMENT_CONTENT: String? = null //当前评论内容
    var MEMBER_ID = 0 //当前评论会员ID
    var MEMBER_ID_NAME: String? = null //当前评论会员昵称
    var MEMBER_ID_HEAD_IMG_FILE: String? = null//当前评论会员头像
    var IS_READ = 0
    var CREATE_DATE: String? = null
    var REPLY_MEMBER_ID = 0//上个评论会员ID
    var REPLY_MEMBER_ID_NAME: String? = null//上个评论会员昵称
    var REPLY_MEMBER_ID_HEAD_IMG_FILE: String? = null//上个评论会员头像
    var TYPE = 0
    var DYNAMIC_MEMBER_ID = 0//发布该动态的会员ID
    var DYNAMIC_MEMBER_NAME: String? = null //发布该动态的会员昵称
    var MEMBER_IMAGE_URL: String? = null//发布该动态的会员头像
    var RELEASE_ADDRESS: String? = null//发布动态的位置
    var PUBLISH_CONTENT: String? = null//动态内容
    var Y_AXIS = 0.0
    var X_AXIS = 0.0
    var STATE = 0
    var COMMENT_CONTENT1: String? = null
    var TIME: String? = null
    var IMAGES: ImageData? = null


    class ImageData : Serializable {
        var ID = 0
        var DYNAMIC_ID = 0
        var PROJECT_URL: String? = null
        var FILE_PATH_URL: String? = null
        var FILE_PATH: String? = null
        var IMG_COMPRESS: String? = null
        var FILE_NAME_URL: String? = null
        var WIDTH = 0
        var HEIGHT = 0
    }
}
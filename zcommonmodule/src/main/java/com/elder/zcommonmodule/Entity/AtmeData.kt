package com.elder.zcommonmodule.Entity

import java.io.Serializable


class AtmeData : Serializable {



// [{"ID":166,"DYNAMIC_ID":"513","IS_READ":"1","CREATE_DATE":"2020-02-19 17:21:15","MEMBER_ID":"119","MEMBER_NAME":"100001129"
// ,"PUBLISH_CONTENT":"@amoski10000117gjilil，likl","TYPE":"1","HEAD_IMG_FILE":"/Activity/userHeaderPicoriginalImg/2020Y/02M/19D/11920200219163031983.jpg",
// "TIME":"1分钟前"},{"ID":165,"DYNAMIC_ID":"512","IS_READ":"1","CREATE_DATE":"2020-02-19 17:20:04","MEMBER_ID":"119","MEMBER_NAME":
// "100001129","PUBLISH_CONTENT":"@amoski10000117lo hou z xing\u0027","TYPE":"1",
// "HEAD_IMG_FILE":"/Activity/userHeaderPicoriginalImg/2020Y/02M/19D/11920200219163031983.jpg","TIME":"2分钟前"},{"ID":164,"DYNAMIC_ID":"504","IS_READ":"1","CREATE_DATE":"2020-02-19 17:16:23","MEMBER_ID":"119","MEMBER_NAME":"100001129","PUBLISH_CONTENT":"@amoski10000117好的呀","TYPE":"1","HEAD_IMG_FILE":"/Activity/userHeaderPicoriginalImg/2020Y/02M/19D/11920200219163031983.jpg","TIME":"6分钟前"},{"ID":162,"DYNAMIC_ID":"503","IS_READ":"1","CREATE_DATE":"2020-02-19 17:14:52","MEMBER_ID":"119","MEMBER_NAME":"100001129","PUBLISH_CONTENT":"@amoski10000117@优摩游测试1toawpmm，mg","TYPE":"1","HEAD_IMG_FILE":"/Activity/userHeaderPicoriginalImg/2020Y/02M/19D/11920200219163031983.jpg","TIME":"8分钟前","IMAGES":{"ID":622,"DYNAMIC_ID":"503","PROJECT_URL":"AmoskiActivity","FILE_PATH_URL":"/DynamicManage/getFile?fileNameUrl\u003d","FILE_PATH":"/home/uploadFile/images/createActivity/2020/02/19/17/fileNameUrlc4b51f5f87d94a9a9677e75a13e4daff.jpg","IMG_COMPRESS":"/home/uploadFile/images/createActivity/2020/02/19/17/imgCompressc4b51f5f87d94a9a9677e75a13e4daff.jpg","FILE_NAME_URL":"file_1.jpg","WIDTH":"750","HEIGHT":"1334"}},{"ID":161,"DYNAMIC_ID":"501","IS_READ":"1","CREATE_DATE":"2020-02-19 17:08:03","MEMBER_ID":"119","MEMBER_NAME":"100001129","PUBLISH_CONTENT":"@amoski10000117天亏去啦","TYPE":"1","HEAD_IMG_FILE":"/Activity/userHeaderPicoriginalImg/2020Y/02M/19D/11920200219163031983.jpg","TIME":"14分钟前"},{"ID":159,"DYNAMIC_ID":"500","IS_READ":"1","CREATE_DATE":"2020-02-19 17:07:24","MEMBER_ID":"119","MEMBER_NAME":"100001129","PUBLISH_CONTENT":"合同多咯@amoski10000117@优摩游测试1","TYPE":"1","HEAD_IMG_FILE":"/Activity/userHeaderPicoriginalImg/2020Y/02M/19D/11920200219163031983.jpg","TIME":"15分钟前"},{"ID":155,"DYNAMIC_ID":"498","IS_READ":"1","CREATE_DATE":"2020-02-19 16:57:18","MEMBER_ID":"119","MEMBER_NAME":"100001129","PUBLISH_CONTENT":"Canon@amoski10000117@优摩游测试1","TYPE":"1","HEAD_IMG_FILE":"/Activity/userHeaderPicoriginalImg/2020Y/02M/19D/11920200219163031983.jpg","TIME":"25分钟前","IMAGES":{"ID":619,"DYNAMIC_ID":"498","PROJECT_URL":"AmoskiActivity","FILE_PATH_URL":"/DynamicManage/getFile?fileNameUrl\u003d","FILE_PATH":"/home/uploadFile/images/createActivity/2020/02/19/16/fileNameUrladf5c772ff154f4096a08e6d6fc5244b.jpg","IMG_COMPRESS":"/home/uploadFile/images/createActivity/2020/02/19/16/imgCompressadf5c772ff154f4096a08e6d6fc5244b.jpg","FILE_NAME_URL":"file_0.jpg","WIDTH":"750","HEIGHT":"1334"}},{"ID":153,"DYNAMIC_ID":"496","IS_READ":"1","CREATE_DATE":"2020-02-19 16:56:02","MEMBER_ID":"119","MEMBER_NAME":"100001129","PUBLISH_CONTENT":"来咯嗯呢@amoski10000117","TYPE":"1","HEAD_IMG_FILE":"/Activity/userHeaderPicoriginalImg/2020Y/02M/19D/11920200219163031983.jpg","TIME":"26分钟前","IMAGES":{"ID":616,"DYNAMIC_ID":"496","PROJECT_URL":"AmoskiActivity","FILE_PATH_URL":"/DynamicManage/getFile?fileNameUrl\u003d","FILE_PATH":"/home/uploadFile/images/createActivity/2020/02/19/16/fileNameUrlb9fd534f7246475799568c6ebf2f36aa.jpg","IMG_COMPRESS":"/home/uploadFile/images/createActivity/2020/02/19/16/imgCompressb9fd534f7246475799568c6ebf2f36aa.jpg","FILE_NAME_URL":"file_0.jpg","WIDTH":"750","HEIGHT":"750"}},{"ID":151,"DYNAMIC_ID":"495","IS_READ":"1","CREATE_DATE":"2020-02-19 16:54:43","MEMBER_ID":"119","MEMBER_NAME":"100001129","PUBLISH_CONTENT":"j\u0027ooooo@amoski10000117@优摩游测试1","TYPE":"1","HEAD_IMG_FILE":"/Activity/userHeaderPicoriginalImg/2020Y/02M/19D/11920200219163031983.jpg","TIME":"28分钟前"},{"ID":149,"DYNAMIC_ID":"494","IS_READ":"1","CREATE_DATE":"2020-0

        var ID: String? = null

    var DYNAMIC_ID: String? = null

    var IS_READ = 0

    var CREATE_DATE: String? = null

    var MEMBER_ID: String? = null

    var MEMBER_NAME: String? = null

    var PUBLISH_CONTENT: String? = null

    var TYPE = 0

    var TIME: String? = null


    var HEAD_IMG_FILE: String? = null

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
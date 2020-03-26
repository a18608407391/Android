package com.cstec.administrator.party_module

import java.io.Serializable


class PartyDetailEntity : Serializable {

    var IS_COLLECTION = 0
    var ID = 0
    var CODE = 0
    var TITLE: String? = null
    var NATURE = 0
    var STATE = 0
    var DESCRIBE: String? = null
    var NOTICE: String? = null
    var CREATE_DATA: String? = null
    var BIG_TYPE = 0
    var RECOMMEND_ORDER = 0
    var TYPE: String? = null
    var DESTINATION: String? = null
    var PATH_POINT: String? = null
    var ACTIVITY_INTENSITY = 0
    var EQUIPMENT_REQUIREMENTS = 0
    var DETAILS_ACTIVITIES: String? = null
    var ACTIVITY_NOTICE: String? = null
    var DACT_Y_AXIS: Double = 0.0
    var DACT_X_AXIS: Double = 0.0
    var SUBORDINATE_CITY: String? = null
    var MANDATORY_FIELD: String? = null
    var SERVICE_TEL :String ? = null
    var SCOPE_REGISTRATION: String? = null
    var COLLECTION_PLACE: String? = null

    var COLLECTION_TIME: String? = null

    var ACTIVITY_START: String? = null
    var ACTIVITY_STOP: String? = null
    var ACTIVITY_END: String? = null
    var DAY: String = ""
    var TICKET_PRICE: String? = null
    var TICKET_PRICE_DESCRIBE: ArrayList<Describe>? = null
    var NUMBER_LIMITATION: String? = null
    var SHOW_NUMBER: String? = null
    var SPONSOR_UNIT: String? = null
    var GUIDANCE_UNIT: String? = null
    var CO_ORGANIZER: String? = null
    var UNDERTAKING_UNIT: String? = null
    var RIDING_OFFICER_MEMBER_ID: String? = null
    var NAME: String? = null
    var HEAD_IMG_FILE: String? = null
    var FILE_NAME_URL: String? = null
    var LABEL: String? = null
    var X_AXIS: Double = 0.0
    var Y_AXIS: Double = 0.0
    var DISTANCE: String? = null
    var MAN_COUNT: Int = 0
    var SQRTVALUE = 0
    var SQRTVALUE1 = 0
    var SIGN_UP: ArrayList<SignUp>? = null
    var SCHEDULE: ArrayList<SCHEDULES>? = null
    var ACTIVITY_STATUS = 0

    class Describe {
        var NAME_INVOICE: String? = null
        var TICKET_PRICE: String? = null
        var DESCRIBE: String? = null
    }

    class SignUp {
        var HEAD_IMG_FILE: String? = null
    }

    class PartyDetailPartOne : Serializable {

    }


    class SCHEDULES : Serializable {
        var ID = 0
        var BASICS_ID = 0
        var ORG_CODE: String? = null
        var PLACE_DEPARTURE: String? = null
        var DESTINATION: String? = null
        var DAYS_STATISTICS = 0
        var CREATE_NAME: String? = null
        var CREATE_DATA: String? = null
        var UPDATE_NAME: String? = null
        var UPDATE_DATE: String? = null
        var INTRODUCE: String? = null
        var HISTORY: ArrayList<History>? = null
        var ROUTE: ArrayList<PartyDetailRoadListItem>? = null
    }


    class PartyDetailRoadListItem : Serializable {
        //type = 0  其实部分  type =1 内容头接口部分  type =2 内容尾接口部分
        var type = 0
        var itemtype = 0
        var DAY: String = ""
        var ORDER_ID = 0
        var Y_AXIS: Double = 0.0
        var X_AXIS: Double = 0.0
        var PATH_POINT_NAME: String? = null
        var DISTANCE: String? = null
        var TIME_REQUIRED: String? = null
        var ADDRESS: String? = null
        var DESCRIBE: String? = null
        var ROUTE_IMAGES: ArrayList<FilePath>? = null
        var START_TIME: String? = null
        var IMAGE1: ArrayList<String>? = null
    }

    class Cost {
        var title: String = "1、交通:全程进山往返费用,及子梅村到子梅垭口往返交通费用。\n2、住宿:按行程所列标准入住,已包含出发前当晚成都商务酒店住宿。\n3、餐饮:8正7早,包含徒步期间的营地早餐、营地晚餐。营地早餐为:粥、榨菜、大饼、清炒蔬菜或面条等;营地晚餐为:三荤两素一汤。\n4、门票:包含贡嗄寺进山门票。\n5、马匹:包含马匹驮装备费用。 \n6、装备:炊事帐篷、燃料、炉具、炊具、部分餐具、通讯工具等。 \n7、向导及协作:专业领队和当地藏民协作。 \n8、保险:赠送户外意外险; \n9、活动组织费等。"
        var noInclud: String = "1、交通:各地往返成都大交通费用。 \n2、个人骑马费用(全程骑马费用1200元/人,价格随季节浮动) \n3、徒步中的5顿路餐需自理。 \n4、用餐酒水费、其他娱乐费用。 \n5、报价未含发票,如需发票,税点自理。 \n6、单房差,本行程不收自然单房差,如果队员要求自己全程住宿一间,则需要补单房差。 \n7、个人装备驮运不能超过20公斤超重则需补超额费用。"
        var price: String? = null
        var describe: String? = null
    }

    class History : Serializable {

        var START_TIME: String? = null
        var STOP_TIME: String? = null
        var INTRODUCE: String? = null
        var INTRODUCE_TYPE: String? = null
        var ABOUT_TIME: String? = null
        var INSPECT_TICKET: String? = null
        var IMAGES: ArrayList<FileUrl>? = null
    }

    class FilePath : Serializable {

        var PROJECT_URL: String? = null
        var FILE_PATH_URL: String? = null
        var FILE_NAME_URL: String? = null
    }

    class FileUrl : Serializable {
        var PROJECT_URL: String? = null
        var METHOD_PATH_URL: String? = null
        var FILE_PATH_URL: String? = null
    }

    class ActiveNotice {
        var notice: String? = null
    }


}
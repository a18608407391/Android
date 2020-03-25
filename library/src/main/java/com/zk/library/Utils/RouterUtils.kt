package com.zk.library.Utils

class RouterUtils() {
    class ActivityPath() {
        companion object {
            const val HOME = "/app/home"
            const val GUIDE = "/app/guide"
            const val ORGAN = "/app/organ"
            const val LOGIN_CODE = "/login/logincode"
            const val LOGIN_PASSWORD = "/login/loginpassword"
            const val REGISTER = "/login/register"
            const val REGISTER_SETTINGPASS = "/login/registerSetting"
        }
    }

    class LoginModuleKey() {
        companion object {
            const val TYPE_CLASS = "/type/class"
            const val PHONE_NUMBER = "/phone/number"
            const val WEB_VIEW = "/web/activity"
            const val COUNT_DOWN_TIMER = "/count/down"
        }
    }

    class Chat_Module() {
        companion object {
            const val SysNotify_AC = "/chat/systemnotify"
            const val ActiveNotify_AC = "/chat/activenotify"
            const val Chat_AC = "/chat/room"
            const val MSG_AC = "/chat/msglist"
            const val Chat_TARGET_ID = "/chat/targetId"
            const val Chat_App_Key = "/chat/appkey"
            const val Chat_DRAFT = "/chat/draft"
            const val Chat_GROUP_ID = "/chat/groupId"
            const val Chat_AtAllId = "/chat/atallId"
            const val Chat_AtId = "/chat/atId"
            const val Chat_CONV_TITLE = "/chat/conv_title"
            const val Chat_FromGroup = "/chat/from_group"
            const val Chat_MEMBERS_COUNT = "/chat/member_count"
            const val Chat_MsgCount = "/chat/msg_count"
            const val Chat_MsgId = "/chat/msg_id"
            const val Chat_FromChatActivity = "/chat/fromchatactivity"
            const val Chat_CONV_TYPE = "/chat/conv_type"
            const val Chat_Room_ID = "/chat/roomId"
            const val Chat_Room_Name = "/chat/roomName"
            const val Chat_GROUP_NAME = "/chat/groupName"
        }
    }


    class PrivateModuleConfig() {
        companion object {
            const val PICTURE_WATERMARK = "/private/picture_water_mark"
            const val PICTURE_SELECTOR = "/private/picture_selector"
            const val USER_INFO_EDIT = "/user/info"
            const val CHANGENICKNAME = "/change/nickname"
            const val MY_PHOTO_ALBUM = "/photo/album"
            const val NICKNAME = "/change/nicknameValues"
            const val MemberAuth = "/member/auth"
            const val CERTIFICATION = "/certification/auth"
            const val ADD_CARS = "/add/cars"
            const val Edit_CARS = "/Edit/cars"
            const val BOND_CARS = "/bond/cars"
            const val Atme_AC = "/private/atme"
            const val COMMAND_AC = "/private/command"
            const val MY_ACTIVE_WEB_AC = "/private/my_active_web"
            const val MY_ACTIVE_WEB_TYPE = "/private/my_active_web_type"
            const val MY_ACTIVE_WEB_ID = "/private/my_active_web_id"

            const val USER_INFO = "/user/info"

            const val USER_SETTING = "/user/setting"
            const val USERREQUEST = "/user/request"
            const val USERMANAGER = "/user/manager"
            const val NOTIFYCATION = "/user/notify"
            const val USER_AUTH = "/user/auth"
            const val CHANGEPASS = "/user/pass"
            const val SETTING_CATEGORY = "/setting/category"


            //            const val
            const val ADD_MARKER_URL = "/private/addmakerurl"
            const val ADD_MARKER_ACTIVITY = "/private/addmarker"
            const val SHARE_ADD_MARKER_ACTIVITY = "/private/share_addmarker"


            // ------------------------------------

            const val MY_FOCUS_AC = "/private/myfocus"
            const val MY_LIKE_AC = "/private/mylike"
            const val MY_FANS_AC = "/private/myfans"
            const val MY_RESTORE_AC = "/private/myrestore"

        }
    }

    class MapModuleConfig {
        companion object {
            const val MAP_ACTIVITY = "/map/main"
            const val MAP_FR = "/map/fr"
            const val SEARCH_ACTIVITY = "/map/search"
            const val COUNT_DOWN_ACTIVITY = "/map/count"
            const val SEARCH_MODEL = "/map/search_model"
            const val RESULT_STR = "/str/result"
            const val NAVIGATION = "/map/navigation"
            const val NAVIGATION_DATA = "/map/navigationdata"
            const val NAVIGATION_TYPE = "/map/navigationtype"
            const val NAVIGATION_DISTANCE = "/activity/navigationdistance"
            const val NAVIGATION_TIME = "/map/navigationtime"
            const val START_LOCATION = "/data/start_location"
            const val END_LOCATION = "/data/end_location"
            const val DRIVER_FR = "/fr/driver"
            const val TEAM_FR = "/fr/team"
            const val MAP_POINT_FR = "/fr/mappoint"
            const val NAVIGATION_FR = "/fr/navigation"
            const val ROAD_DETAIL = "/road/detail"
            const val ROAD_DATA = "/road/data"
            const val ROAD_DISTANCE = "/road/distance"
            const val ROAD_TIME = "/road/time"
            const val SHARE_ACTIVITY = "/map/share"
            const val SHARE_TYPE = "/share/typeen"
            const val SMOOTH_ACTIVITY = "/map/smooth"
            const val RESUME_MAP_ACTIVITY = "/map/map_resume"
            const val RESUME_MAP_ACTIVITY_ROAD = "/activity/map_road"
            const val RESUME_MAP_FLOAT = "/map/map_floate"
            const val ROUTE_BOOK_FR = "/fr/routebooksss"
            const val ROAD_BOOK_ACTIVITY = "/map/routebook"
            const val ROAD_BOOK_WEB_ACTIVITY = "/map/roadbook_web"
            const val ROAD_BOOK_SEARCH_ACTIVITY = "/map/roadbook_search"
            const val ROAD_CURRENT_POINT = "/config/point"
            const val ROAD_CURRENT_TYPE = "/configroad/type"
            const val MY_ROAD_BOOK_AC = "/map/myroadbook"
            const val ROAD_BOOK_FIRST_ACTIVITY = "/map/firstbook"
            const val ROAD_BOOK_FIRST_ENTITY = "/activity/entityfirst"
            const val ROAD_WEB_ID = "/web/roadid"
            const val ROAD_WEB_TYPE = "/web/roadtype"
        }
    }

    class TeamModule {
        companion object {
            const val TEAM_CREATE = "/team/create_team"
            const val SETTING = "/team/setting_team"
            const val MANAGER = "/team/manager_team"
            const val DELETE = "/team/delete"
            const val TEAM_INFO = "/team/info"
            const val TEAM_NAME = "/team/name"
            const val CHANGE_NAME = "/team/changename"
            const val TEAMER_PASS = "/team/teamerpass"
        }
    }


    class LogRecodeConfig {
        companion object {
            const val LogRecodeFR = "/log/fragment"
            const val LogListActivity = "/log/loglist"
            const val SHARE = "/log/share"
            const val PLAYER = "/log/player"
            const val SHARE_ENTITY = "/activity/share"
            const val LOG_LIST_ENTITY = "/activity/entity"
            const val SAME_CITY_LOCATION_AC = "/activity/citylocation"
            const val SAME_CITY_RANKING = "/activity/ranking"
            const val SEARCH_MEMBER = "/activity/searchmember"
            const val LOCATION_SIDE = "/activity/locationside"
        }
    }

    //FragmentPath
    class FragmentPath() {
        companion object {
            const val FIND = "/bfind"
            const val FINDPAGE = "${FIND}/find"
            const val MANAGER = "/bmanager"
            const val MANAGERPAGE = "${MANAGER}/manager"
            const val MIDDLE = "/bmiddle"
            const val MIDDLEPAGE = "${MIDDLE}/middle"
            const val IM = "/bim"
            const val IMPAGE = "${IM}/im"
            const val MYSELF = "/bmyself"
            const val MYSELFPAGE = "${MYSELF}/Myself"
        }
    }

    class SocialConfig() {

        companion object {

            const val SOCIAL_PHOTO = "/social/photo"
            const val SOCIAL_DETAIL = "/social/detail"
            const val SOCIAL_DETAIL_ENTITY = "/social/detail_entity"
            const val SOCIAL_RELEASE = "/social/release"
            const val SOCIAL_MAIN = "/social/main"
            const val SOCIAL_AITE = "/social/aite"
            const val SOCIAL_FOCUS_LIST = "/social/focuslist"
            const val MY_DYNAMIC_AC = "/social/mydynamic"
            const val SOCIAL_LOCATION = "/social/location"
            const val SOCIAL_TYPE = "/social/navigationtype"
            const val SOCIAL_CAVALIER_HOME = "/social/cavalierhome"

            const val SOCIAL_MEMBER_ID = "/social/memberid"
            const val SOCIAL_NAVITATION_ID = "/social/navigationid"
            const val SOCIAL_GET_LIKE = "/social/getlike"
            const val SOCIAL_MAX_COUNT = "/social/maxcount"

        }
    }

    class PartyConfig {
        companion object {
            const val PARTY_MAIN = "/party/main"
            const val SUBJECT_PARTY = "/party/subject"
            const val PARTY_DETAIL = "/party/detail"
            const val PARTY_ID = "/party/id"
            const val PARTY_CODE = "/party/code"
            const val PARTY_CITY = "/party/city"
            const val PARTY_LOCATION = "/party/location"
            const val Party_SELECT_TYPE = "/party/select_type"
            const val PARTY_CLOCK_DETAIL = "/party/clock_detail"
            const val PARTY_SUBJECT_DETAIL = "/party/subject_detail"
            const val SEARCH_PARTY = "/party/search"
            const val ORGANIZATION = "/party/organization"
            const val ENROLL = "/party/enroll"
        }

    }

    class SameCityConfig {
        companion object {

            const val SAME_CITY_MAIN = "/samecity/main"
        }

    }
}
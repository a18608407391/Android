package org.cs.tec.library

import android.os.Environment
import java.io.File.separator
import android.os.Environment.getExternalStorageDirectory
import java.io.File


const val ACTION_CONNECT_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"
const val VIDEO_DURATION = "video_duration"
const val TIP = "tip"
const val DOWN = "down"
const val LOCAL_UPDATE_IMG = "dou_Img"
const val WX_APP_ID = "wx941dc5dccc68dee3"
const val WX_SERCRET_ID = "cecfaae035206e45a332f271a788650e"
val ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
var OTHER_IMG = ROOT_PATH + File.separator + "temporary_organ/other" + File.separator
var NOTE_IMG = ROOT_PATH + File.separator + "temporary_organ/note" + File.separator
var GRADE_IMG = ROOT_PATH + File.separator + "temporary_organ/grade" + File.separator


const val CODE_SUCCESS = 0
const val SPLIT = "/"
const val SYMBOL_SPLIT = ","

const val USER_TYPE = "USER_TYPE"//用户类型
const val USER_IMG = "USER_IMG"//用户头像
const val USER_NAME = "USER_NAME"//用户姓名
const val IS_SETTING_PASS = "isSettingPwd"//用户是否设置了密码
const val B = "B"//机构
const val C = "C"//个人
const val D = "D"//马甲
const val FORGET = "FORGET"
const val PHONE = "PHONE"
const val REGISTER = "register"
const val ATTENTION_CHILDREN = "attentionChildren"
const val RESETPWD = "resetPwd"
const val CHANGEPHONE = "changePhone"
const val SESSION_ID = "session_id"//用户登陆后唯一的标识

const val REQUEST_CODE = "requestCode" //请求码
const val RESULT_CODE = "resultCode"  //返回码
const val SUCCESS = "SUCCESS"
const val WAIT = "WAIT"
const val BUILD_SESSION = "build_session_id"

const val YES = "是"
const val NO = "否"
const val Y = "Y"
const val N = "N"

const val PROVINCE = "province"
const val CITY = "city"
const val DISTRICT = "district"
const val CROWD = "crowd"
const val PARENT = "parent"
const val CHILD = "child"
const val TIME = "time"
const val TIME_UPDATE_TIME = "TIME_UPDATE_TIME"
const val COMPANY_USER_ID = "commpany_user_id"


const val NAME = "name"
const val HEAD_URL = "head_url"
const val JOB = "job"
const val ENTITY = "Entity"
const val ENTITY_LIST = "Entity_list"
const val USER_ENTITY = "userEntity"
const val USERID = "UserID"
const val ROLE = "role"
const val COMPANY_STATUS = "company_status"
const val USER_STATUS = "user_status"
const val QUESTION_TYPE = "question_type"
const val TOKEN_DEVICE = "token_device"
const val INVITE_CODE = "inviteCode"
const val LOGIN_OUT = "loginOut"

const val RESULT_LOCATION = 3 //返回定位信息
const val REQUEST_LOCATION = 4 //请求定位信息
const val ADD_ENTITY = "ADD"


const val COURSECLASSIFYONE = "courseClassifyOne"
const val COURSECLASSIFYTWO = "courseClassifyTwo"
const val HOTBUSINESSDISTRICT = "hotBusinessDistrict"
const val HOTSEARCH = "hotSearch "

const val SEND_MAIN_FRAGMENT = 5 //主页发送数据
const val SEND_SECOND_SEARCH_ALL = 6 //二级搜索页面发送数据
const val SEND_FIRST_SEARCH = 7
const val SEND_LOCAL_SEARCH = 8

const val RECOMMEND = "recommend" //推荐排序
const val DISTANCE = "distance" //距离排序


const val LAT = "LAT"
const val LNG = "LNG"


const val LANDMARK = "engine"
const val ENGINENEXT = "engineNext"
const val SEARCHER_NAME = "engineName"
const val LOCAL_CITY = "engineCitys"
const val PARENT_TYPE_ID = "parent_id"//父id
const val CHILD_TYPE_ID = "child_id"  //子id
const val SOURCE_TYPE_ID = "source_id"
const val TYPE_ID_VALUE = "type_value"//const value
const val TYPE_NAME = "type_name"//分类name


const val READ_PHONE_STATE = 0x3001
const val BASIC_INFO_CODE = 0x5001
const val COMPANY_FULL_NAME_CODE = 0x5011
const val COMPANY_INFO_CODE = 0x5002
const val COMPANY_INFO_NEXT_CODE = 0x5003
const val USER_INFO_CODE = 0X5004
const val USER_INFO_NEXT_CODE = 0X5005
const val RESERVATION_LIST_CODE = 0X5006
const val RESERVATION_DETAILS_CODE = 0X5007
const val SELFFRAGMENT_CODE = 0X5008
const val PERSONINFO_CODE = 0X5009
const val OTHER_POSITION_CODE = 0X5010
const val ORGAN_MAIN_BUSINESS_CODE = 0X5011
const val MAIN_FRAGMENT_CODE = 0X5012
const val MY_ATTENTION_CODE = 0X5013
const val ORGAN_INFO_CODE = 0X5014
const val MY_COLLECTION_CODE = 0X5015
const val COURSE_INFO_CODE = 0X5016
const val ORGANER_IDENTITY_CODE = 0X5017
const val REPLACE_COMPANY_CODE = 0X5018
const val ORGAN_HOME_FRAGMENT_CODE = 0X5019
const val PAY_SUCCESS_CODE = 0X5020
const val ADD_CHILD_INFO_CODE = 0X5021
const val VERIFY_CHILD_INFO_CODE = 0X5022
const val ADD_CHILD_INFO_TWO_CODE = 0X5023
const val CHILD_INTRODUCE_CODE = 0X5024
const val EDIT_CHILDREN_CODE = 0X5025
const val LOOK_GRADE_CODE = 0X5026
const val CHILD_FOCUS_CODE = 0X5027
const val INSTALL_GRADE_CODE = 0X5028
const val GRADE_FRAGMENT_CODE = 0X5029
const val OTHER_RESULT_FRAGMENT_CODE = 0X5030
const val CLASS_NOTES_FRAGMENT_CODE = 0X5031
const val RESULT_DETAILS_CODE = 0X5032
const val EDIT_CHILD_RESULT_CODE = 0X5033
const val IMAGE_SELECTOR_CODE = 0X5034
const val PREVIEW_CODE = 0X5035
const val QUESTION_CODE = 0X5036
const val QUESTION_RESULT_CODE = 0X5037
const val ANSWER_DETAILS_CODE = 0X5038
const val COMMENT_DETAILS_CODE = 0X5039
const val CHECK_MY_ANSWER_CODE = 0X5040
const val NEARBY_CODE = 0X5041
const val PLAZA_CODE = 0X5042
const val QUESTION_USER_CODE = 0X5043
const val PROVE_DETAIL_CODE = 0X5044
const val PROVE_RESULT_CODE = 0X5045
const val PROVE_SEARCH_CODE = 0X5046
const val PROVE_EDIT_CODE = 0X5047
const val PROVE_EDIT_RESULT_CODE = 0X5048
const val ONLINE_DETAIL_CODE = 0X5049
const val EXCHANGE_COURSE_CODE = 0X5050
const val SETTING_CODE = 0X5051
const val HOME_CODE = 0X5052
const val ANSWER_CODE = 0X5053
const val DOU_CODE = 0X5054
const val FIND_CODE = 0X5055
const val TASK_CODE = 0X5056
const val ABOUT_OUR_CODE = 0X5057
const val SCORE_INFO_CODE = 0X5058
const val WX_WITHDRAW_CODE = 0X5059
const val WALLET_MANAGE_CODE = 0X5060
const val PROVE_FRAGMENT_CODE = 0X5061
const val CHOOSE_FRIEND_CODE = 0X5062
const val SCORE_PRESENTED_CODE = 0X5063
const val JOIN_TOPIC_CODE = 0X5064
const val CHOOSE_ORGAN_CODE = 0X5065
const val COMPANY_SHORT_NAME_CODE = 0X5066
const val ADD_LABEL_FOR_COMPANY = 0X5067
const val COMPANY_INTRODUCE_CODE = 0X5068
const val ORGAN_ME_FRAGMENT = 0X5069
const val ACTIVITY_REQUEST_CODE = 0X5070
const val ACTIVITY_RESULT_CODE = 0X5071
const val SPECIFIC_LOCATION_CODE = 0X5072
const val CHOOSE_CITY_CODE = 0X5073


const val CODE_SEARCH_CITY = "code_search"
const val CODE_SEARCH_VALUE = "07"
const val CODE = "code"

const val LOCATION = "location_place"
const val LOCATION_CITY = "location_city"

const val ACTIVITY_RESULT = 0x1001
const val ACTIVITY_RESULT_ORDER_LIST = 0x1002
const val THEME_ADD_RESULT_CODE = 0x1003
//    public static final int ACTIVITY_RESULT_final = 0x1003;
const val ACTIVITY_SELF_ORDER = 6001
const val ACTIVITY_SELF_LOCATION = 6002
const val ACTIVITY_MAIN_LOCATION_TYPE = 6003
const val ACTIVITY_SELF_ENGINE = 6004
const val ACTIVITY_ORGAN_TYPE = 6005
const val ACTIVITY_SEACHER_MAIN_LOCATION_TYPE = 6006
//    public static final String LOCATION_BEAN = "location_bean";
const val TIP_BEAN = "tip_bean"
const val AREA_TYPE = "area_type"
const val M = "m"
const val KM = "km"


const val TEACHER_USERID = "teacherID"
const val COMPANYID = "companyID"
const val COMPANY_NAME = "companyName"
const val COURSEID = "courseID"
const val BUSINESS_USER = "businessUser"

const val IMAGEURLS = "imageUrls"
const val IMAGEURLBUNDLE = "imageUrlBundle"

const val COURSEINFOENTITY = "CourseInfoEntity"
const val COUPONSUSEENTITY = "CouponsUseEntity"
const val USEENTITY = "useEntity"
const val USEENTITY_LIST = "useEntity_list"
const val ORDER = "order"
const val COURSENAME = "courseName"
const val ALLPRICE = "allPrice"


const val ORDERCODE = "orderCode"//预约订单号
const val COURSETYPE = "courseType"//课程类型
/**
 * 预约状态
 */
const val CANCEL = "cancel"//取消
const val SUBMITTED = "submitted"//完成
const val TOPAY = "toPay" //待支付
const val TOCONFIRM = "toConfirm"//待确认
const val DONE = "done"//完成
const val PAYEXPIRED = "payExpired"//订单超时

const val ORDER_TYPE = "order_Type"
const val SYMBOL = ";"

const val ENTRANCE = "entrance"//入口
const val SELF_COUPON = "self_coupon"
const val UPDATE_PASS = "update_pass"
const val ACCOUNT = "account" //用户登录账号

const val CLICK_POSITION = "click_position" //点击item的position
const val RESET_LOGIN = "reset_login" //标识重新登录
const val FIND_ID = "findId"
const val CHOOSE_INDENTITY = "choose_indentity"
const val PAY_SUCCES = "pay_succes"
const val BUSINESS_LICENSE = "business_license" //营业执照


const val WXPAY = "wxPay"
const val aliPay = "aliPay"
const val CHILD_ENTITY = "childEntity" //孩子对象集合
const val SINGLE_CHILD_ENTITY = "singleChildEntity" //孩子单个对象
const val GRADE = "grade"               //成绩
const val GRADE_ENTITY = "gradeEntity"
const val NOTECLASSIFY = "noteClassify"//笔记成果分类
const val OTHERCLASSIFY = "otherClassify"//其他成功分类
const val NOTE = "note"               //获取笔记成果
const val OTHER = "other"            //获取其他成果
const val SCORE = "score"            //成绩
const val VIDEO = "video"            //视频
const val PROVE = "prove"            //说说
const val QUESTION = "question"     //问题
const val QUESTIONQ = "questionQ"
const val ANSWER = "answer"     //问题
const val IMAGE = "image"            //图片
const val IMG = "img"            //图片
const val PREVIEW = "preview"            //预览
const val RESULT_PHOTO_ENTITY = "resultPhotoEntity" //成果相册
const val TAG = "tag"
const val SCREEN = "screen"
const val HEAD = "HEAD"
const val RESULT_ENTITY = "resultEntity"//成果对象
const val CHILD_ID = "childId"
const val ID = "Id"
const val SELECTED_IMGES = "selected_images"//已选择的图片/视频集合
const val IMAGE_ENTITYS = "image_entitiys"//手机中所有的图片/视频集合
const val TITLE_QUESTION = "question_title"

var HTTPS = "https://"
var HTTP = "http"
const val REQUEST_PIC = 0x2080115
const val REQUEST_CODE_LOCAL = 0x2080
const val REQUEST_CODE_CAMERA = 0x2081
const val REQUEST_SHOOT = 0x2082
const val REQUEST_CODE_ACT = 0x2083
const val RESULTCODE_CODE_ACT = 0x2084

const val REMARK = "remark"
const val WOMEN = "W"
const val MAN = "M"
const val SAVE = "save"//保存
const val DELETE = "delete"//删除
const val UPDATE = "update"//修改
const val STATUS = "update"//状态
const val MAX_CHOOSE_IMGS = "max_choose_imgs"//最大可选图片数
const val CAN_CHOOSE_VIDEO = "canChooseVideo"//是否可以选择视频
const val CAN_CHOOSE_SIZE = "canChooseIMG"//是否可以选择视频
const val NEW = "new" //最新
const val HOT = "hot" //热门
const val ATTENTION = "attention" //关注
const val DYNAMIC = "dynamic" //动态
const val FANS = "fans" //关注
const val REMOVE_ATTENTION = "remove_attention" //取消关注

const val QUESTION_ENTITY = "question_entity" //问题实体
const val ANSWER_ENTITY_LIST = "answer_entity_list" //回答实体集合
const val ANSWER_ENTITY = "answer_entity" //回答实体集合
const val COMMENT_COUNT = "comment_count"//评论数
const val COMMENT = "comment"//评论
const val REPLY = "reply"//回复
const val QUESTION_ID = "question_id"
const val ANSWER_ID = "answer_id"
const val COMMENT_ENTITY = "comment_entity"
const val ATTENTIONQ = "attentionQ"
const val APPLAUD = "applaud"
const val COLLECTION = "collection"
const val SINGLE = "single"
const val QUESTION_TITLE = "question_title"
const val TYPE = "type"
const val CHANGED = "changed"
const val RECOMMENT = "recomment"//推荐
const val NEARBY = "nearby"      //附近
const val SEARCH = "search"      //搜索
const val PLAZA = "plaza"         //广场
const val SHARE_ID = "share_id"         //分享ID
const val SHARE = "share"         //分享
const val REMOVE = "remove"         //分享
const val IS_LOAD_DATA = "isLoadData"         //是否加载数据

const val POSITION = "position"
const val VIP = "vip"             //会员福利
const val CHOICENESS = "choiceness"//精选
const val VIDEO_TYPE = 0//视频
const val AUDIO_TYPE = 1//音频
const val COURSE_TYPE = 0//课程精选
const val VIP_TYPE = 1//会员福利
const val NO_NAME = "noName"//无名字
const val HAS_NAME = "hasName"//有名字
const val CLASS_TYPE = "classType"//课程类型
const val ALL = "all"
const val SYSTEM = "system"
const val PRAISE = "praise"//点赞类型
const val LIKE = "like"//收藏
const val ANSWER_ANSWER = "answer_answer"//回答
const val ANSWER_REPLY = "answer_reply"//回答下评论
const val ANSWER_COMMENT = "answer_comment"//回答回复
const val SHARE_SHARE = "share_share"//心情
const val SHARE_REPLY = "share_reply"//心情下评论
const val SHARE_COMMENT = "share_comment"//心情下回复
const val REPLY_TYPE = "reply_type"//
const val PERFECT_INFO = "perfectInfo"//

const val TASK_GET_ANSWER = "getAnswer"//提问获得回答
const val TASK_GET_COMMENT = "getComment"//回答获得评论
const val TASK_SHARE_CONTENT = "shareContent"//分享内容
const val TASK_SHARE_INCOME = "shareIncome"//晒收入
const val TASK_SHARE = "share"    //发布动态
const val TASK_APPLAUD = "applaud"//给他人点赞
const val TASK_QUESTION = "question"//首次提出问题
const val TASK_ANSWER = "answer"//首次回答问题
const val TASK_PERSONAL_INFO = "personalInfo"//完善个人信息
const val TASK_ABOUT_US = "aboutUs"//了解兜去学起源
const val TASK_INVITE = "invite"//邀请好友
const val TASK_RESULT_ENTRY = "resultEntry"//使用管理工具
const val TASK_MANAGEMENT_TOOL = "managementTool"//使用一次管理工具
const val TASK_FREE_LISTEN = "freeListen"//点击任意课程免费试用
const val TASK_FIRST_COMMENT = "firstComment"//首次评论

const val SIGN = "sign"
const val OUTER = "outer"
const val INNER = "inner"
const val NOVICELIST = "novice"
const val EVERYDAYLIST = "everyDay"
const val OTHERLIST = "other"

const val PRICE = "price"
const val PAY_TYPE = "payType"//支付方式
const val CASH_PAY = "cash"//现金支付
const val INTEGRAL_PAY = "integral"//积分兑换
const val IS_PAY = "isPay"//是否已购买
const val ORDER_LIST = "order_list"
const val HOME = "home"
const val MESSAGE = "message"
const val PERSON = "person"
const val PRIMITIVE = "primitive"//系统标签
const val SIMILAR = "similar"//同一类标签
const val TOPIC_NAME = "topic_name"//话题名称
const val HOT_SEARCH_TERMS = "hotSearchTerms"//热门搜索词
const val WEB = "web"
const val WEB_ID = "id"
const val WEB_TYPE = "type"
const val ORGAN_SHORT_NAME = "organShortName"//机构简称
const val ORGAN_FULL_NAME = "organFullName"//机构全称

const val APP_CREATE = "create"//创建机构
const val CREATE = "create"//创建机构
const val JOIN = "join"//加入机构
/**
 * 用户申请状态
 */
const val STATUS_WAIT = "WAIT"      //待审核
const val STATUS_SUCCESS = "SUCCESS"//成功
const val STATUS_FAILED = "FAILED"//失败
/**
 * 机构申请状态
 */
const val ORGAN_STATUS = "organ_status" //机构审核状态
const val ORGAN_NO_CHECK = "noCheck" //机构未审核
const val ORGAN_CHECKING = "checking" //机构审核中
const val ORGAN_CHECK_FAILD = "checkFaild" //机构审核失败
const val ORGAN_CHECK_SUCCESS = "checkSuccess"  //机构审核通过


const val LABEL_LENGTH = "labelLength"  //标签总长度
const val LABEL = "label"  //标签
const val COMPANY_INTRODUCE = "companyIntroduce"  //公司介绍
const val MIN_WITHDRAW_AMOUNT = "minWithdrawAmount"  //最小提现金额

/**
 * 提现状态
 */
const val WITHDRAW_EXAMINE = "examine"  //审核中
const val WITHDRAW_DOWN = "down"  //提现完成
const val WITHDRAW_FAIL = "fail"  //提现失败
const val S = "S"  //S超级管理
const val A = "A"  //A普通管理


const val DAY = "day"  //-1昨天 0今天

/**
 * 订单状态
 */
const val ORDER_ALL = "全部"
const val ORDER_UNPAY = "待付款"
const val ORDER_SURE = "待确认"
const val ORDER_FINISH = "已完成"
const val ORDER_CANCEL = "已取消"

/** 订单状态--全部  */
const val ORDERSTATUS_ALL = ""
/** 订单状态--待支付  */
const val ORDERSTATUS_TOPAY = "toPay"
/** 订单状态--已取消  */
const val ORDERSTATUS_CANCEL = "cancel"
/** 订单状态--待确认  */
const val ORDERSTATUS_TOCONFIRM = "toConfirm"
/** 订单状态--已完结  */
const val ORDERSTATUS_DONE = "done"


const val CITY_NAME = "cityName"

/**
 * 搜索类型
 */
const val ORGAN = "机构"
const val ONLINE_COURSE = "线上课程"
const val OFFLINE_COURSE = "线下课程"
const val RONG_CLOUD_TOKEN = "Rong_Cloud_Token"//融云Token
const  val SP_WELCOME  = "sp_welcome"


//SPUtls
const val SP_LAT = "sp_location_lat"
const val SP_LNG = "sp_location_lng"
const val SP_ADDRESS = "sp_location_address"
const val SP_CITY = "sp_location_city"
const val SP_WELCOME_FIRST = "sp_welcome_first"
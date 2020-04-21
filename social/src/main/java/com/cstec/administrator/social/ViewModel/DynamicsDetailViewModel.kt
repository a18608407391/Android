package com.cstec.administrator.social.ViewModel

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.os.Bundle
import com.cstec.administrator.social.Activity.DynamicsDetailActivity
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.zk.library.Base.BaseViewModel
import org.cs.tec.library.Bus.RxSubscriptions
import com.cstec.administrator.social.Entity.CommentBean
import com.cstec.administrator.social.ViewAdapter.CommentExpandAdapter
import com.google.gson.Gson
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.support.design.widget.BottomSheetDialog
import android.text.TextUtils
import android.view.View
import com.cstec.administrator.social.R
import android.support.design.widget.BottomSheetBehavior
import android.util.Log
import android.widget.*
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.Activity.ReleaseDynamicsActivity
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.Entity.GridClickEntity
import com.elder.zcommonmodule.DETAIL_RESULT
import com.elder.zcommonmodule.DataBases.queryUserInfo
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.RELEASE_RESULT
import com.elder.zcommonmodule.Utils.DialogUtils
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.SimpleDateFormat
import java.util.*


class DynamicsDetailViewModel : BaseViewModel(), HttpInteface.SocialDynamicsCommentList, HttpInteface.SocialDynamicsComment, CommentExpandAdapter.ClickNetListener, CommentExpandAdapter.GroupClickNetListener, CommentExpandAdapter.GroupImgClickNetListener {
    override fun GroupImgNetWork(bean: CommentDetailBean?) {
//        ARouter.getInstance()
//                .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(dynamicsDetailActivity.location?.latitude!!, dynamicsDetailActivity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, dynamicsDetailActivity.location?.aoiName!!, dynamicsDetailActivity.location!!.poiName))
//                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, bean?.memberId)
//                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 3)
//                .navigation()

        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, bean?.memberId)
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        dynamicsDetailActivity.start(model)

    }


    override fun sendGroupNetWork(bean: CommentDetailBean?) {
        //一级评论点赞

        var map = HashMap<String, String>()
        Log.e("retrofitBack", "当前dynamicId" + detialBean.id!!)
        Log.e("retrofitBack", "当前commentId" + bean!!.id.toString())
        map["dynamicId"] = detialBean.id!!
        map["commentId"] = bean!!.id.toString()
        HttpRequest.instance.getCommentDynamicsLike(map)
    }

    override fun sendNetWork(bean: CommentDetailBean?) {
        // 二级评论点赞  不做區分
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        var map = HashMap<String, String>()
        map["dynamicId"] = detialBean.id!!
        map["commentId"] = bean!!.id.toString()
        HttpRequest.instance.getCommentDynamicsLike(map)
    }


    var field = ObservableField<DynamicsCategoryEntity.Dynamics>()


    override fun ResultCommentSuccess(it: String) {
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date = Date(System.currentTimeMillis())
        if (commontDialog != null && commontDialog!!.isShowing) {
            commontDialog!!.dismiss()
            var detailBean = CommentDetailBean(user?.data?.name, commentStr)
            detailBean.createDate = sdf.format(date)
            detailBean.memberId = user?.data?.id
            detailBean.memberName = user?.data?.name
            detailBean.memberImages = user?.data?.headImgFile
            adapterEx.addTheCommentData(detailBean)
            var d = field.get()
            var t = Integer.valueOf(d?.commentCount)
            d!!.commentCount = (t + 1).toString()
            commentCount.set(d!!.commentCount)
            commentText.set("全部评论(" + d!!.commentCount + ")")
            field.set(d)
            Toast.makeText(dynamicsDetailActivity.activity, "评论成功", Toast.LENGTH_SHORT).show()
        }
        if (replyCommonDialog != null && replyCommonDialog!!.isShowing) {
            replyCommonDialog!!.dismiss()
            var detailBean = CommentDetailBean(replyContent)
            if (curType == 0) {
                detailBean.createDate = sdf.format(date)
                detailBean.memberName = user?.data?.name
                detailBean.replyMemberName = data!![replyPosition].replyMemberName
                detailBean.memberId = user?.data?.id
                detailBean.memberImages = user?.data?.headImgFile
            } else {
                //回复人名字
                detailBean.createDate = sdf.format(date)
                detailBean.memberName = user?.data?.name
                detailBean.replyMemberName = data!![replyPosition].dynamicCommentList[replyChildPosition].memberName
                detailBean.replyMemberId = data!![replyPosition].dynamicCommentList[replyChildPosition].memberId
                detailBean.memberId = user?.data?.id

            }
            adapterEx.addTheReplyData(detailBean, replyPosition)
            dynamicsDetailActivity.mCommentList!!.expandGroup(replyPosition)
            Toast.makeText(dynamicsDetailActivity.activity, "回复成功", Toast.LENGTH_SHORT).show()
        }
    }

    override fun ResultCommentError(ex: Throwable) {

    }

    var spanclick = BindingCommand(object : BindingConsumer<DynamicsSimple> {
        override fun call(t: DynamicsSimple) {

            var bundle = Bundle()
            bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location)
            bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, t.memberId)
            var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
            model.arguments = bundle
            dynamicsDetailActivity.start(model)
//            ARouter.getInstance()
//                    .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                    .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(dynamicsDetailActivity.location?.latitude!!, dynamicsDetailActivity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, dynamicsDetailActivity.location?.aoiName!!, dynamicsDetailActivity.location!!.poiName))
//                    .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, t.memberId)
//                    .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 3)
//                    .navigation()

        }
    })

    override fun ResultCommentListSuccess(it: String) {
        if (getChildList) {
            var bean = Gson().fromJson<CommentBean>(it, CommentBean::class.java)
            adapterEx.addReplyList(bean.data, LoadMoreClickPosition)
        } else {
            var bean = Gson().fromJson<CommentBean>(it, CommentBean::class.java)
            data = bean.data
            adapterEx.setLoadData(data)
            for (i in 0 until data!!.size) {
                dynamicsDetailActivity.mCommentList!!.expandGroup(i)
            }
        }
        getChildList = false
    }

    override fun ResultCommentListError(ex: Throwable) {
    }

    var clickBinding = BindingCommand(object : BindingConsumer<GridClickEntity> {
        override fun call(t: GridClickEntity) {
            var list = ObservableArrayList<String>()
            t.urlList!!.forEach {
                list.add(it)
            }
            DialogUtils.createBigPicShow(dynamicsDetailActivity.activity!!, list, t.childPosition)
        }
    })

    var avatar = ObservableField<String>()
    var memberName = ObservableField<String>()
    var focus = ObservableField(0)
    var like = ObservableField(0)
    var likeCount = ObservableField<String>("0")
    var collection = ObservableField(0)
    var collectionCount = ObservableField<String>()
    var commentCount = ObservableField<String>("0")
    var CurrentClickTime = 0L


//    fun toNav() {
//        if (dynamicsDetailActivity.navigationType == 1) {
//            if (destroyList!!.contains("GetLikeActivity")) {
//                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_GET_LIKE).navigation(dynamicsDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        } else if (dynamicsDetailActivity.navigationType == 2) {
//            if (destroyList!!.contains("MyDynamicsActivity")) {
//                ARouter.getInstance().build(RouterUtils.SocialConfig.MY_DYNAMIC_AC).navigation(dynamicsDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        } else if (dynamicsDetailActivity.navigationType == 3) {
//            if (destroyList!!.contains("DriverHomeActivity")) {
//                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location).withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, dynamicsDetailActivity.detail!!.id).withSerializable(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0).navigation(dynamicsDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        } else if (dynamicsDetailActivity.navigationType == 4) {
//            if (destroyList!!.contains("MyLikeActivity")) {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_LIKE_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location).navigation(dynamicsDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        } else if (dynamicsDetailActivity.navigationType == 5) {
//            if (destroyList!!.contains("CommandActivity")) {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.COMMAND_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location).navigation(dynamicsDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        } else if (dynamicsDetailActivity.navigationType == 6) {
//            if (destroyList!!.contains("AtmeActivity")) {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.Atme_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location).navigation(dynamicsDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        } else if (dynamicsDetailActivity.navigationType == 7) {
//            if (destroyList!!.contains("SystemNotifyListActivity")) {
//                ARouter.getInstance().build(RouterUtils.Chat_Module.SysNotify_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location).navigation(dynamicsDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        } else if (dynamicsDetailActivity.navigationType == 8) {
//            if (destroyList!!.contains("MyRestoreActivity")) {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_RESTORE_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location).navigation(dynamicsDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }vn
//        } else {
//            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(dynamicsDetailActivity, object : NavCallback() {
//                override fun onArrival(postcard: Postcard?) {
//                    finish()
//                }
//            })
//        }
//    }

    fun onClick(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        when (view.id) {

            R.id.collection_click -> {
                var count = Integer.valueOf(detialBean.collectionCount)
                if (detialBean?.hasCollection == 0) {
                    detialBean?.hasCollection = 1
                    collection.set(1)
                    count++
                } else {
                    collection.set(0)
                    count--
                    detialBean?.hasCollection = 0
                }
                detialBean.collectionCount = count.toString()
                collectionCount.set(count.toString())
                field.set(detialBean)
                var map = java.util.HashMap<String, String>()
                map["dynamicId"] = field.get()!!.id!!
                HttpRequest.instance.getDynamicsCollection(map)
            }
            R.id.like_click -> {
                if (detialBean.dynamicSpotFabulousList == null) {
                    detialBean.dynamicSpotFabulousList = ArrayList()
                }
                var count = Integer.valueOf(detialBean.fabulousCount)
                if (detialBean.isLike == 0) {
                    detialBean.isLike = 1
                    count++
                    addBeanSpot()
                    like.set(1)
                } else {
                    detialBean.isLike = 0
                    count--
                    like.set(0)
                    removeBeanSpot()
                }
                initLinear(dynamicsDetailActivity.mLinear!!, detialBean)
                detialBean.fabulousCount = count.toString()
                field.set(detialBean)
                likeCount.set(detialBean.fabulousCount)
                var map = java.util.HashMap<String, String>()
                map["dynamicId"] = field.get()!!.id!!
                HttpRequest.instance.getDynamicsLike(map)
            }
            R.id.focus_click -> {
                if (detialBean.followed == 0) {
                    focus.set(1)
                }
                detialBean.followed = 1
                field.set(detialBean)
                var map = java.util.HashMap<String, String>()
                map["fansMemberId"] = detialBean.memberId.toString()

                HttpRequest.instance.getDynamicsFocus(map)
            }
            R.id.share_click -> {
                if (dynamicsDetailActivity.location == null) {
                    Toast.makeText(context, "获取定位信息异常，请重试!", Toast.LENGTH_SHORT).show()
                    return
                }
                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location)
                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, field.get())
                var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_RELEASE).navigation() as ReleaseDynamicsActivity
                model.arguments = bundle
                dynamicsDetailActivity.startForResult(model, RELEASE_RESULT)

//                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_RELEASE).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(dynamicsDetailActivity.location?.latitude!!, dynamicsDetailActivity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, dynamicsDetailActivity.location?.aoiName!!, dynamicsDetailActivity.location!!.poiName)).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, field.get()).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).navigation()
            }
            R.id.back_arrow -> {
//                toNav()
//                RxBus.default?.post(field.get()!!)
                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, field.get())
                dynamicsDetailActivity.setFragmentResult(DETAIL_RESULT, bundle)
                dynamicsDetailActivity!!._mActivity!!.onBackPressedSupport()
            }
            R.id.avatar_click -> {

                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location)
                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, dynamicsDetailActivity.detail?.memberId)
                var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
                model.arguments = bundle
                dynamicsDetailActivity.start(model)
//                ARouter.getInstance()
//                        .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(dynamicsDetailActivity.location?.latitude!!, dynamicsDetailActivity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, dynamicsDetailActivity.location?.aoiName!!, dynamicsDetailActivity.location!!.poiName))
//                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, dynamicsDetailActivity.detail?.memberId)
//                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 3)
//                        .navigation()
            }
        }
    }


    var commentText = ObservableField<String>("全部评论(0)")


    fun addBeanSpot() {
        var entity = SocialHoriEntity()
        entity.memberImages = user!!.data?.headImgFile
        entity.memberId = Integer.valueOf(user!!.data?.id)
        entity.memberName = user!!.data?.name
        entity.createDate = detialBean.createDate
        detialBean.dynamicSpotFabulousList!!.add(entity)
    }

    fun removeBeanSpot() {
        if (detialBean.dynamicSpotFabulousList.isNullOrEmpty()) {
            return
        }
        var member: SocialHoriEntity? = null

        detialBean.dynamicSpotFabulousList!!.forEach {
            if (it.memberId.toString() == user!!.data?.id) {
                member = it
            }
        }
        detialBean.dynamicSpotFabulousList!!.remove(member)
    }

    var page = 10
    var pageSize = 1
    lateinit var dynamicsDetailActivity: DynamicsDetailActivity
    var user: UserInfo? = null
    fun inject(dynamicsDetailActivity: DynamicsDetailActivity) {
        this.dynamicsDetailActivity = dynamicsDetailActivity
        HttpRequest.instance.DynamicCommentListResult = this
        HttpRequest.instance.DynamicCommentResult = this
        collection.set(dynamicsDetailActivity.detail?.hasCollection)
        like.set(dynamicsDetailActivity.detail?.isLike)
        focus.set(dynamicsDetailActivity.detail?.followed)
        commentCount.set(dynamicsDetailActivity.detail?.commentCount)
        collectionCount.set(dynamicsDetailActivity.detail?.collectionCount.toString())
        var id = PreferenceUtils.getString(context, USERID)
        user = queryUserInfo(id)[0]
        initEx()
        initLinear(dynamicsDetailActivity.mLinear!!, dynamicsDetailActivity.detail)
        likeCount.set(dynamicsDetailActivity.detail?.fabulousCount)
        field.set(dynamicsDetailActivity.detail)
        this.detialBean = dynamicsDetailActivity.detail!!
        if (detialBean.memberId == PreferenceUtils.getString(context, USERID)) {
            avatar.set(user?.data?.headImgFile)
            memberName.set(user!!.data?.name)
        } else {
            avatar.set(detialBean.memberImageUrl)
            memberName.set(detialBean.memberName)
        }
        commentText.set("全部评论(" + dynamicsDetailActivity.detail?.commentCount + ")")
        var map = HashMap<String, String>()
        map["length"] = page.toString()
        map["pageSize"] = pageSize.toString()
        map["dynamicId"] = dynamicsDetailActivity.detail?.id.toString()
        HttpRequest.instance.getDynamicsCommonList(map)

        RxSubscriptions.add(RxBus.default?.toObservable(CanalierHomeEntity::class.java)!!.subscribe {
            if (it.followed == 0) {
                detialBean.followed = 0
                focus.set(0)
            } else {
                detialBean.followed = 1
                focus.set(1)
            }
            field.set(detialBean)
        })
    }

    private fun initLinear(social_linear: LinearLayout?, detail: DynamicsCategoryEntity.Dynamics?) {
        social_linear!!.removeAllViews()
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        detail?.dynamicSpotFabulousList?.forEach {
            var binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.dynamics_detail_item_layout, social_linear, false)
            binding.setVariable(BR.dynamics_item, it.memberImages)
            social_linear?.addView(binding.root)
        }
        social_linear?.setOnClickListener {
            var bundle = Bundle()
            bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, dynamicsDetailActivity.location)
            bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, dynamicsDetailActivity.detail)
            var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_FOCUS_LIST).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
            model.arguments = bundle
            dynamicsDetailActivity.start(model)

//            ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_FOCUS_LIST)
//                    .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(dynamicsDetailActivity.location?.latitude!!, dynamicsDetailActivity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, dynamicsDetailActivity.location?.aoiName!!, dynamicsDetailActivity.location!!.poiName))
//                    .withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, dynamicsDetailActivity.detail).navigation()

        }
        social_linear?.invalidate()
    }

    var getChildList = false
    var LoadMoreClickPosition = 0
    lateinit var detialBean: DynamicsCategoryEntity.Dynamics
    var data: ArrayList<CommentDetailBean>? = null
    lateinit var adapterEx: CommentExpandAdapter
    private fun initEx() {
        adapterEx = CommentExpandAdapter(dynamicsDetailActivity.activity)
        dynamicsDetailActivity.mCommentList!!.setGroupIndicator(null)
        dynamicsDetailActivity.mCommentList!!.setAdapter(adapterEx)
        adapterEx.childNet = this
        adapterEx.groupNet = this
        adapterEx.groupImgNet = this
        dynamicsDetailActivity.mCommentList!!.setOnGroupClickListener { parent, v, groupPosition, id ->
            showReplyDialog(groupPosition, 0, 0)
            return@setOnGroupClickListener true
        }
        dynamicsDetailActivity.mCommentList!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            if (data!!.get(groupPosition).dynamicCommentList.get(childPosition).id == -1) {
                LoadMoreClickPosition = groupPosition
                var map = HashMap<String, String>()
                map["length"] = page.toString()
                map["pageSize"] = pageSize.toString()
                map["dynamicId"] = detialBean.id.toString()
                map["id"] = data!![groupPosition].id.toString()
                HttpRequest.instance.getDynamicsCommonList(map)
                getChildList = true
            } else {
                showReplyDialog(groupPosition, childPosition, 1)
            }
            return@setOnChildClickListener true
        }
        dynamicsDetailActivity.mCommentList!!.setOnGroupExpandListener {

        }

    }

    fun onCommentClick(view: View) {
        showCommentDialog()
    }

    var replyCommonDialog: BottomSheetDialog? = null
    var replyPosition = 0
    var replyContent = ""
    var replyChildPosition = 0
    var curType = 0
    private fun showReplyDialog(position: Int, type: Int, i: Int) {
        this.replyPosition = position
        this.curType = i
        replyChildPosition = type
        replyCommonDialog = BottomSheetDialog(dynamicsDetailActivity.activity!!)
        val commentView = LayoutInflater.from(dynamicsDetailActivity.activity).inflate(R.layout.comment_dialog_layout, null)
        val commentText = commentView.findViewById(R.id.dialog_comment_et) as EditText
        val bt_comment = commentView.findViewById(R.id.dialog_comment_bt) as Button
        replyChangeText = commentView.findViewById(R.id.change_word)
//        commentText.filters = arrayOf(NameLengthFilter(140))
        if (i == 0) {
            commentText.hint = "回复 " + data!!.get(position).memberName + " 的评论:"
        } else {
            commentText.hint = "回复 " + data!!.get(position).dynamicCommentList[type].memberName + " 的评论:"
        }
        replyCommonDialog!!.setContentView(commentView)
        bt_comment.setOnClickListener {
            replyContent = commentText.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(replyContent)) {
                var map = HashMap<String, String>()
                map["commentContent"] = replyContent
                map["dynamicId"] = detialBean.id.toString()
                if (i == 0) {
                    ///
                    map["replyCommentId"] = data!![position].id.toString()   //代表第几条一级评论的ID
//                    map["replyMemberId"] = data!![position].memberId.toString()   //代表第几条一级评论的 memberID
                } else {
                    map["replyCommentId"] = data!![position].id.toString() //代表第几条一级评论的ID
                    map["replyMemberId"] = data!![position].dynamicCommentList[type].memberId.toString()  // 代表第几条一级评论下的第几条二级评论的id
                }
                HttpRequest.instance.getDynamicsCommon(map)

            } else {
                Toast.makeText(dynamicsDetailActivity.activity, "回复内容不能为空", Toast.LENGTH_SHORT).show()
            }
        }
        commentText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length > 140) {
                    Toast.makeText(context, getString(R.string.single_commen_stop_warm), Toast.LENGTH_SHORT).show()
                    return
                }
                replyChangeText!!.text = charSequence.length.toString()
                if (!TextUtils.isEmpty(charSequence) && charSequence.length > 2) {
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"))
                } else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"))
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        replyCommonDialog!!.show()
    }

//    private fun generateTestData(): List<CommentDetailBean> {
//        val gson = Gson()
//        var commentBean = gson.fromJson(testJson, CommentBean::class.java)
//        return commentBean.data
//    }


    var commontDialog: BottomSheetDialog? = null
    var commentStr: String = ""


    var replyChangeText: TextView? = null
    var commentChangeText: TextView? = null

    private fun showCommentDialog() {
        commontDialog = BottomSheetDialog(dynamicsDetailActivity.activity!!)
        val commentView = LayoutInflater.from(dynamicsDetailActivity.activity!!).inflate(R.layout.comment_dialog_layout, null)
        val commentText = commentView.findViewById<View>(R.id.dialog_comment_et) as EditText
        val bt_comment = commentView.findViewById<View>(R.id.dialog_comment_bt) as Button
//        commentText.filters = arrayOf(NameLengthFilter(140))
        commentChangeText = commentView.findViewById(R.id.change_word)
        commontDialog!!.setContentView(commentView)


        /**
         * 解决bsd显示不全的情况
         */
        val parent = commentView.parent as View
        val behavior = BottomSheetBehavior.from(parent)
        commentView.measure(0, 0)
        behavior.peekHeight = commentView.measuredHeight

        bt_comment.setOnClickListener {
            commentStr = commentText.text.toString().trim { it <= ' ' }

            if (!TextUtils.isEmpty(commentStr)) {
                var map = HashMap<String, String>()
                map["commentContent"] = commentStr
                map["dynamicId"] = detialBean.id.toString()

                HttpRequest.instance.getDynamicsCommon(map)
            } else {
                Toast.makeText(dynamicsDetailActivity.activity, "评论内容不能为空", Toast.LENGTH_SHORT).show()
            }
        }
        commentText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length > 140) {
                    Toast.makeText(context, getString(R.string.single_commen_stop_warm), Toast.LENGTH_SHORT).show()
                    return
                }
                commentChangeText!!.text = charSequence.length.toString()
                if (!TextUtils.isEmpty(charSequence) && charSequence.length > 2) {
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"))
                } else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"))
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        commontDialog!!.show()
    }

    fun String_length(value: String): Int {
        var valueLength = 0
        val chinese = "[\u4e00-\u9fa5]"
        for (i in 0 until value.length) {
            val temp = value.substring(i, i + 1)
            if (temp.matches(chinese.toRegex())) {
                valueLength += 1
            } else {
                valueLength += 1
            }
        }
        return valueLength
    }

    var scrollToPosition = 0
}
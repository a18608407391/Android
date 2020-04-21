package com.cstec.administrator.chart_module.ViewModel

import android.app.Dialog
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import com.cstec.administrator.chart_module.Activity.MsgActivity
import com.elder.zcommonmodule.Component.TitleComponent
import com.zk.library.Base.BaseViewModel
import cn.jpush.im.android.api.model.Conversation
import com.cstec.administrator.chart_module.Adapter.ConversationListAdapter
import com.cstec.administrator.chart_module.Adapter.SortTopConvList
import cn.jpush.im.android.api.enums.ConversationType
import com.cstec.administrator.chart_module.Adapter.SortConvList
import cn.jpush.im.android.api.JMessageClient
import java.util.*
import cn.jpush.im.android.api.model.GroupInfo
import android.content.Intent
import cn.jpush.im.android.api.model.UserInfo
import android.view.WindowManager
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.View.ChatUtils.DialogCreator
import com.zk.library.Base.BaseApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.zk.library.Utils.RouterUtils
import android.net.ConnectivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.databinding.ObservableField
import android.util.Log
import cn.jpush.im.android.api.event.*
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.elder.zcommonmodule.Entity.MsgCountData
import com.cstec.administrator.chart_module.Even.Event
import com.cstec.administrator.chart_module.Even.EventType
import com.elder.zcommonmodule.MSG_RETURN_REFRESH_REQUEST
import com.elder.zcommonmodule.MSG_RETURN_REQUEST
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Utils.PreferenceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.USERID
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MsgViewModel : BaseViewModel(), View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, TitleComponent.titleComponentCallBack, HttpInteface.getMsgNotifyCount {
    override fun getNotifyCountSuccess(it: String) {
        if (it.isNullOrEmpty()) {
            return
        }
        var data = Gson().fromJson<MsgCountData>(it, MsgCountData::class.java)
        likeCount.set(data.fabulousCount)
        commandCount.set(data.commentCount)
        atMeCount.set(data.callMeCount)
        SystemCount.set(data.lastSystemCount)
        SystemStr.set(data.lastSystem?.msg_title)
        SystemTime.set(data.lastSystem?.time)
    }


    var likeCount = ObservableField(0)

    var commandCount = ObservableField(0)

    var atMeCount = ObservableField(0)

    var SystemCount = ObservableField(0)

    var ActiveCount = ObservableField(0)

    var SystemStr = ObservableField("")

    var SystemTime = ObservableField("")


    override fun getNotifyCountError(ex: Throwable) {
        Log.e("result", "getNotifyCountError" + ex.message)
    }

    override fun onComponentClick(view: View) {
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(activity, object : NavCallback() {
            override fun onArrival(postcard: Postcard?) {
                finish()
            }
        })
    }

    override fun onComponentFinish(view: View) {
    }

    private var mDialog: Dialog? = null
    private var mWidth = 0
    var mListAdapter: ConversationListAdapter? = null
    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        val conv = mDatas[position]
        if (conv != null) {
            val listener = View.OnClickListener { v ->
                when (v.id) {
                    //会话置顶
                    R.id.jmui_top_conv_ll -> {
                        //已经置顶,去取消
                        if (!TextUtils.isEmpty(conv.extra)) {
                            mListAdapter?.setCancelConvTop(conv)
                            //没有置顶,去置顶
                        } else {
                            mListAdapter?.setConvTop(conv)
                        }
                        mDialog?.dismiss()
                    }
                    //删除会话
                    R.id.jmui_delete_conv_ll -> {
                        if (conv.type == ConversationType.group) {
                            JMessageClient.deleteGroupConversation((conv.targetInfo as GroupInfo).groupID)
                        } else {
                            JMessageClient.deleteSingleConversation((conv.targetInfo as UserInfo).userName)
                        }
                        mDatas.removeAt(position)
                        if (mDatas.size > 0) {
                            activity.setNullConversation(true)
                        } else {
                            activity.setNullConversation(false)
                        }
                        mListAdapter?.notifyDataSetChanged()
                        mDialog?.dismiss()
                    }
                    else -> {
                    }
                }
            }
            mDialog = DialogCreator.createDelConversationDialog(activity, listener, TextUtils.isEmpty(conv.extra))
            mDialog?.show()
            mDialog?.getWindow()?.setLayout((0.8 * mWidth).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        }
        return true
    }
//15111323908

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position >= 0) {
            //这里-3是减掉添加的三个headView
            var conv = mDatas[position]
//            intent.putExtra(JGApplication.CONV_TITLE, conv.title)
            //群聊
            if (conv.type == ConversationType.group) {
                val groupId = (conv.targetInfo as GroupInfo).groupID
                if (mListAdapter?.includeAtMsg(conv)!!) {
//                    intent.putExtra("atMsgId", mListAdapter.getAtMsgId(conv))
                    ARouter.getInstance().build(RouterUtils.Chat_Module.Chat_AC)
                            .withString(RouterUtils.Chat_Module.Chat_CONV_TITLE, conv.title)
                            .withLong(RouterUtils.Chat_Module.Chat_GROUP_ID, groupId)
                            .withInt(RouterUtils.Chat_Module.Chat_MsgId, mListAdapter!!.getAtMsgId(conv)!!)
                            .withString(RouterUtils.Chat_Module.Chat_DRAFT, mListAdapter!!.getDraft(conv.id))
                            .navigation()
                }

                if (mListAdapter?.includeAtAllMsg(conv)!!) {
//                    intent.putExtra("atAllMsgId", mListAdapter.getatAllMsgId(conv))
                    ARouter.getInstance().build(RouterUtils.Chat_Module.Chat_AC)
                            .withString(RouterUtils.Chat_Module.Chat_CONV_TITLE, conv.title)
                            .withLong(RouterUtils.Chat_Module.Chat_GROUP_ID, groupId)
                            .withInt(RouterUtils.Chat_Module.Chat_AtAllId, mListAdapter!!.getatAllMsgId(conv)!!)
                            .withString(RouterUtils.Chat_Module.Chat_DRAFT, mListAdapter!!.getDraft(conv.id))
                            .navigation()
                }

//                intent.putExtra(JGApplication.GROUP_ID, groupId)
//                intent.putExtra(JGApplication.DRAFT, getAdapter().getDraft(conv.id))
//                intent.setClass(mContext.getActivity(), ChatActivity::class.java)
//                mContext.getActivity().startActivity(intent)

                return
                //单聊
            } else if (conv.type == ConversationType.single) {
                var targetId = (conv.targetInfo as UserInfo).getUserName()
//                intent.putExtra(JGApplication.TARGET_ID, targetId)
//                intent.putExtra(JGApplication.TARGET_APP_KEY, conv.targetAppKey)
//                intent.putExtra(JGApplication.DRAFT, getAdapter().getDraft(conv.id))


                ARouter.getInstance().build(RouterUtils.Chat_Module.Chat_AC)
                        .withString(RouterUtils.Chat_Module.Chat_CONV_TITLE, conv.title)
                        .withString(RouterUtils.Chat_Module.Chat_TARGET_ID, targetId)
                        .withString(RouterUtils.Chat_Module.Chat_App_Key, conv.targetAppKey)
                        .withString(RouterUtils.Chat_Module.Chat_DRAFT, mListAdapter!!.getDraft(conv.id))
                        .navigation()
            }


//            intent.setClass(mContext.getActivity(), ChatActivity::class.java)
//            mContext.getContext().startActivity(intent)

        }
    }

    override fun onClick(v: View?) {
        //跳转搜索界面
//        val intent = Intent()
//        intent.setClass(mContext.getActivity(), SearchContactsActivity::class.java!!)
//        mContext.startActivity(intent)
        when (v?.id) {
            R.id.sys_click -> {
                //跳转到系统通知界面
                ARouter.getInstance()
                        .build(RouterUtils.Chat_Module.SysNotify_AC)
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
                        .navigation(activity, MSG_RETURN_REQUEST)
            }
            R.id.active_click -> {
                //跳转到活动列表界面
                ARouter.getInstance().build(RouterUtils.Chat_Module.ActiveNotify_AC).navigation()
            }
            R.id.at_click -> {
                ARouter.getInstance()
                        .build(RouterUtils.PrivateModuleConfig.Atme_AC)
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
                        .navigation(activity, MSG_RETURN_REFRESH_REQUEST)
            }
            R.id.command_click -> {
                ARouter.getInstance()
                        .build(RouterUtils.PrivateModuleConfig.COMMAND_AC)
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
                        .navigation(activity, MSG_RETURN_REFRESH_REQUEST)
            }
            R.id.get_like_click -> {
                var id = PreferenceUtils.getString(context, USERID)
                ARouter.getInstance()
                        .build(RouterUtils.SocialConfig.SOCIAL_GET_LIKE)
                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 8)
                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, id)
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
                        .navigation(activity, MSG_RETURN_REFRESH_REQUEST)
            }
        }
    }


    fun initNet() {
        HttpRequest.instance.getMsgCount = this
        HttpRequest.instance.getMsgNotifyCount(HashMap())
    }


    var topConv: ArrayList<Conversation> = ArrayList()
    var forCurrent: ArrayList<Conversation> = ArrayList()
    var delFeedBack: ArrayList<Conversation> = ArrayList()
    var mDatas = ArrayList<Conversation>()
    lateinit var activity: MsgActivity
    fun inject(msgActivity: MsgActivity) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        this.activity = msgActivity
        JMessageClient.registerEventReceiver(this)
        mWidth = BaseApplication.getInstance().getWidthPixels
        activity.setListener(this)
        activity.setItemListeners(this)
        activity.setLongClickListener(this)
        component.title.set("消息")
        component.arrowVisible.set(false)
        component.rightText.set("")
        component.callback = this
        initConvListAdapter()
        initNet()
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEvent(event: Event) {
        when (event.type) {
            EventType.createConversation -> {
                var conv = event.getConversation();
                if (conv != null) {
                    mListAdapter?.addNewConversation(conv);
                }
            }
            EventType.deleteConversation -> {
                var conv = event.getConversation();
                if (conv != null) {
                    mListAdapter?.deleteConversation(conv);
                }
            }
            EventType.draft -> {
                var conv = event.getConversation();
                var draft = event.draft
                if (!TextUtils.isEmpty(draft)) {
                    mListAdapter?.putDraftToMap(conv, draft);
                    mListAdapter?.setToTop(conv);
                    //否则删除
                } else {
                    mListAdapter?.delDraftFromMap(conv);
                }
            }
            EventType.addFriend -> {

            }
        }

    }

    var component = TitleComponent()
    private fun initConvListAdapter() {
        forCurrent.clear()
        topConv.clear()
        delFeedBack.clear()
        var i = 0
        var groupConv = ArrayList<Conversation>()
        if (JMessageClient.getConversationList() == null) {
            mDatas = ArrayList()
        } else {
            mDatas = JMessageClient.getConversationList() as ArrayList<Conversation>
        }
        mDatas.forEach {
            if (it.type == ConversationType.group) {
                groupConv.add(it)
            }
        }
        if (mDatas != null && mDatas.size > 0) {
            activity.setNullConversation(true)
            val sortConvList = SortConvList()
            Collections.sort(mDatas, sortConvList)
            for (con in mDatas) {
                //如果会话有聊天室会话就把这会话删除
                if (con.getTargetId() == "feedback_Android" || con.getType() == ConversationType.chatroom) {
                    delFeedBack.add(con)
                }
                if (!TextUtils.isEmpty(con.getExtra())) {
                    forCurrent.add(con)
                }
            }
            topConv.addAll(forCurrent)
            mDatas.removeAll(forCurrent)
            mDatas.removeAll(delFeedBack)
            mDatas.removeAll(groupConv)
        } else {
            activity.setNullConversation(false)
        }
        if (topConv != null && topConv.size > 0) {
            val top = SortTopConvList()
            Collections.sort(topConv, top)
            for (conv in topConv) {
                if (conv.type == ConversationType.single) {
                    mDatas.add(i, conv)
                    i++
                }
            }
        }
        mListAdapter = ConversationListAdapter(activity, mDatas, activity)
        activity.setConvListAdapter(mListAdapter!!)
    }


    var mReceiver: NetworkReceiver? = null
    fun initReceiver() {
        mReceiver = NetworkReceiver()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        activity.registerReceiver(mReceiver, filter)
    }

    //监听网络状态的广播
    inner class NetworkReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null && intent.action == "android.net.conn.CONNECTIVITY_CHANGE") {
                val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeInfo = manager.activeNetworkInfo
                if (null == activeInfo) {
                    activity.showHeaderView()
                } else {
                    activity.dismissHeaderView()
                }
            }
        }
    }


    fun refresh(conv: Conversation) {
        CoroutineScope(uiContext).launch {
            if (conv.type != ConversationType.chatroom) {
                mListAdapter?.setToTop(conv)
            }
        }
    }

    fun onEventMainThread(event: MessageReceiptStatusChangeEvent) {
        mListAdapter?.notifyDataSetChanged()
    }

    fun sortConvList() {
        mListAdapter?.sortConvList()
    }
}
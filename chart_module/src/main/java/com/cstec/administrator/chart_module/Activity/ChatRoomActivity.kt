package com.cstec.administrator.chart_module.Activity

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.content.EventNotificationContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.event.*
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.chart_module.Activity.pickImage.PickImageActivity
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.Base.ChartBaseActivity
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.ChatRoomViewModel
import com.cstec.administrator.chart_module.databinding.ActivityChartRoomBinding
import com.zk.library.Utils.RouterUtils
import com.cstec.administrator.chart_module.View.ChatUtils.EmoticonsKeyboardUtils
import com.cstec.administrator.chart_module.Adapter.ChattingListAdapter
import com.cstec.administrator.chart_module.Even.ImageEvent
import com.cstec.administrator.chart_module.Utils.RequestCode
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.StatusbarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.uiContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


@Route(path = RouterUtils.Chat_Module.Chat_AC)
class ChatRoomActivity : ChartBaseActivity<ActivityChartRoomBinding, ChatRoomViewModel>(), View.OnClickListener {


    @Autowired(name = RouterUtils.Chat_Module.Chat_App_Key)
    @JvmField
    var mTargetAppKey: String? = null


    @Autowired(name = RouterUtils.Chat_Module.Chat_DRAFT)
    @JvmField
    var draft: String? = null

    @Autowired(name = RouterUtils.Chat_Module.Chat_TARGET_ID)
    @JvmField
    var mTargetId: String? = null


    @Autowired(name = RouterUtils.Chat_Module.Chat_GROUP_ID)
    @JvmField
    var mGroupId: Long = 0L

    @Autowired(name = RouterUtils.Chat_Module.Chat_CONV_TITLE)
    @JvmField
    var mTitle: String? = null


    @Autowired(name = RouterUtils.Chat_Module.Chat_AtId)
    @JvmField
    var mAtMsgId: Int = 0


    @Autowired(name = RouterUtils.Chat_Module.Chat_AtAllId)
    @JvmField
    var mAtAllMsgId: Int = 0

    @Autowired(name = RouterUtils.Chat_Module.Chat_AtAllId)
    @JvmField
    var fromGroup: Boolean = false


    @Autowired(name = RouterUtils.Chat_Module.Chat_MEMBERS_COUNT)
    @JvmField
    var MEMBERS_COUNT: Int = 0

    @Autowired(name = RouterUtils.Chat_Module.Chat_Room_ID)
    @JvmField
    var chatRoomId: Long = 0

    override fun onClick(v: View?) {
        when (v?.getId()) {
            R.id.jmui_return_btn -> mViewModel?.returnBtn()
            R.id.jmui_right_btn ->
                //如果是聊天室
                if (mViewModel?.isChatRoom!!) {
                    mViewModel?.startChatRoomActivity(chatRoomId)
                } else {
                    mViewModel?.startChatDetailActivity(mTargetId, mTargetAppKey, mGroupId)
                }
            R.id.jmui_at_me_btn -> {
                if (mViewModel?.mUnreadMsgCnt!! < ChattingListAdapter.PAGE_MESSAGE_COUNT) {
                    var position = ChattingListAdapter.PAGE_MESSAGE_COUNT + mAtMsgId - mViewModel?.mConv?.latestMessage?.id!!
                    mViewModel?.mChatView!!.setToPosition(position)
                } else {
                    mViewModel?.mChatView!!.setToPosition(mAtMsgId + mViewModel?.mUnreadMsgCnt!! - mViewModel?.mConv!!.latestMessage.id)
                }
            }
        }
    }

    override fun initVariableId(): Int {
        return BR.char_room_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_chart_room
    }


    override fun initViewModel(): ChatRoomViewModel? {
        return ViewModelProviders.of(this)[ChatRoomViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            var isConsum = mViewModel?.ekBar!!.dispatchKeyEventInFullScreen(event)
            return if (isConsum) isConsum else super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mViewModel?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    open fun onEvent(event: MessageEvent) {
        var message = event.message
        if (message.contentType == ContentType.eventNotification) {
            var groupInfo = message.targetInfo as GroupInfo
            var groupId = groupInfo.groupID
            var type = (message
                    .content as EventNotificationContent).eventNotificationType
            if (groupId == mGroupId) {
                when (type) {
                    EventNotificationContent.EventNotificationType.group_member_added -> {
                        var userNames = (message.content as EventNotificationContent).userNames;
                        mViewModel?.refreshGroupNum()
                        if (userNames.contains(mViewModel?.mMyInfo?.nickname) || userNames.contains(mViewModel?.mMyInfo?.userName)) {
                            CoroutineScope(uiContext).launch {
                                mViewModel?.mChatView?.showRightBtn()
                            }
                        }

                    }
                    EventNotificationContent.EventNotificationType.group_member_removed -> {
                        var userNames = (message.getContent() as EventNotificationContent).userNames;
                        var operator = (message.getContent() as EventNotificationContent).operatorUserInfo;
                        //群主删除了当前用户，则隐藏聊天详情按钮
                        if ((userNames.contains(mViewModel?.mMyInfo!!.nickname) || userNames.contains(mViewModel?.mMyInfo?.userName)) && operator.userID != mViewModel?.mMyInfo?.userID) {
                            CoroutineScope(uiContext).launch {
                                mViewModel?.mChatView?.dismissRightBtn();
                                var groupInfo = mViewModel?.mConv?.getTargetInfo() as GroupInfo
                                if (TextUtils.isEmpty(groupInfo.groupName)) {
                                    mViewModel?.mChatView?.setChatTitle(R.string.group)
                                } else {
                                    mViewModel?.mChatView?.setChatTitle(groupInfo.groupName)
                                }
                                mViewModel?.mChatView?.dismissGroupNum()
                            }
                        } else {
                            mViewModel?.refreshGroupNum()
                        }
                    }
                    EventNotificationContent.EventNotificationType.group_member_exit -> {
                        var content = message.content as EventNotificationContent;
                        if (content.userNames.contains(JMessageClient.getMyInfo().userName)) {
                            mViewModel?.mChatAdapter?.notifyDataSetChanged()
                        } else {
                            mViewModel?.refreshGroupNum()
                        }
                    }
                }
            }
        }
        CoroutineScope(uiContext).launch {
            if (message.targetType === ConversationType.single) {
                var userInfo = message.targetInfo as UserInfo
                var targetId = userInfo.userName
                var appKey = userInfo.appKey
                if (mViewModel?.mIsSingle!! && targetId == mTargetId && appKey == mTargetAppKey) {
                    val lastMsg = mViewModel?.mChatAdapter?.lastMsg
                    if (lastMsg == null || message.id !== lastMsg.id) {
                        mViewModel?.mChatAdapter?.addMsgToList(message)
                    } else {
                        mViewModel?.mChatAdapter?.notifyDataSetChanged()
                    }
                }
            } else {
                val groupId = (message.targetInfo as GroupInfo).groupID
                if (groupId == mGroupId) {
                    val lastMsg = mViewModel?.mChatAdapter?.lastMsg
                    if (lastMsg == null || message.id !== lastMsg.id) {
                        mViewModel?.mChatAdapter?.addMsgToList(message)
                    } else {
                        mViewModel?.mChatAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    open fun onEventMainThread(event: MessageRetractEvent) {
        val retractedMessage = event.retractedMessage
        mViewModel?.mChatAdapter?.delMsgRetract(retractedMessage)
    }


    open fun onEvent(event: OfflineMessageEvent) {
        var conv = event.conversation;
        if (conv.type == ConversationType.single) {
            var userInfo = conv.targetInfo as UserInfo
            var targetId = userInfo?.userName
            var appKey = userInfo?.appKey
            if (mViewModel?.mIsSingle!! && targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
                var singleOfflineMsgList = event.offlineMessageList;
                if (singleOfflineMsgList != null && singleOfflineMsgList.size > 0) {
                    mViewModel?.mChatView?.setToBottom()
                    mViewModel?.mChatAdapter?.addMsgListToList(singleOfflineMsgList)
                }
            }
        } else {
            var groupId = (conv.targetInfo as GroupInfo).groupID;
            if (groupId == mGroupId) {
                var offlineMessageList = event.offlineMessageList;
                if (offlineMessageList != null && offlineMessageList.size > 0) {
                    mViewModel?.mChatView?.setToBottom();
                    mViewModel?.mChatAdapter!!.addMsgListToList(offlineMessageList);
                }
            }
        }
    }

    open fun onEvent(event: CommandNotificationEvent) {
        if (event.type == CommandNotificationEvent.Type.single) {
            var msg = event.msg
            CoroutineScope(uiContext).launch {
                var obj = JSONObject(msg)
                var jsonContent = obj.getJSONObject("content");
                var messageString = jsonContent.getString("message")
                if (TextUtils.isEmpty(messageString)) {
                    mViewModel?.mChatView?.setTitle(mViewModel?.mConv?.title)
                } else {
                    mViewModel?.mChatView?.setTitle(messageString)
                }
            }
        }
    }


    open fun onEventMainThread(event: ChatRoomMessageEvent) {
        var messages = event.messages
        mViewModel?.mChatAdapter?.addMsgListToList(messages)
    }

    open fun onEventMainThread(event: MessageReceiptStatusChangeEvent) {
        var messageReceiptMetas = event.messageReceiptMetas
        for (meta in messageReceiptMetas) {
            var serverMsgId = meta.serverMsgId
            var unReceiptCnt = meta.unReceiptCnt
            mViewModel?.mChatAdapter?.setUpdateReceiptCount(serverMsgId, unReceiptCnt)
        }
    }




}
package com.cstec.administrator.chart_module.ViewModel

import android.os.Handler
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.cstec.administrator.chart_module.Activity.ChatRoomActivity
import com.cstec.administrator.chart_module.View.ChatUtils.DropDownListView
import com.cstec.administrator.chart_module.View.ChatUtils.SimpleCommonUtils
import com.cstec.administrator.chart_module.View.XhsEmoticonsKeyBoard
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.activity_chart_room.*
import com.cstec.administrator.chart_module.Adapter.ChattingListAdapter
import cn.jpush.im.android.api.callback.GetGroupInfoCallback
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.Conversation
import android.text.TextUtils
import android.view.View
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.View.ChatView
import java.lang.ref.WeakReference
import cn.jpush.im.api.BasicCallback
import cn.jpush.im.android.api.enums.ConversationType
import android.text.Editable
import android.text.TextWatcher
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.options.MessageSendingOptions
import com.cstec.administrator.chart_module.Activity.ChooseAtMemberActivity
import com.cstec.administrator.chart_module.Data.EmoticonEntity
import com.cstec.administrator.chart_module.Inteface.EmoticonClickListener
import com.cstec.administrator.chart_module.Model.Constants
import com.cstec.administrator.chart_module.Utils.CommonUtils
import com.cstec.administrator.chart_module.Utils.ToastUtil
import com.cstec.administrator.chart_module.View.ChatUtils.FuncLayout
import com.cstec.administrator.chart_module.View.Emoji.EmojiBean
import com.cstec.administrator.chart_module.View.SimpleAppsGridView
import com.zk.library.binding.command.ViewAdapter.edittext.ViewAdapter.addTextChangedListener
import java.io.File


class ChatRoomViewModel : BaseViewModel(), FuncLayout.OnFuncKeyBoardListener {
    override fun OnFuncPop(height: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnFuncClose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var mMyInfo: UserInfo? = null
    //    var mUIHandler = UIHandler(this)
    var mGroupInfo: GroupInfo? = null

    var mAtMsgId: Int = 0
    var mAtAllMsgId: Int = 0
    var mUnreadMsgCnt: Int = 0
    var mShowSoftInput = false
    var forDel = ArrayList<UserInfo>()
    var mLongClick = false
    var mAtAll = false
    var isChatRoom = false
    var mIsSingle = true
    var mAtList: ArrayList<UserInfo>? = null
    var mChatAdapter: ChattingListAdapter? = null
    var mConv: Conversation? = null
    lateinit var ekBar: XhsEmoticonsKeyBoard
    lateinit var lvChat: DropDownListView
    lateinit var mChatView: ChatView
    lateinit var activity: ChatRoomActivity
    lateinit var mUIHandler: UIHandler
    fun inject(chatRoomActivity: ChatRoomActivity) {
        this.activity = chatRoomActivity
        this.ekBar = chatRoomActivity.ek_bar
        this.lvChat = chatRoomActivity.lv_chat
        this.mChatView = chatRoomActivity.chat_view
        this.mChatView.setListeners(chatRoomActivity)
        mUIHandler = UIHandler(chatRoomActivity)
        initView()
        initData()
    }

    private fun initData() {
        SimpleCommonUtils.initEmoticonsEditText(ekBar.etChat)
        initEmoticonsKeyBoardBar()
        if (!TextUtils.isEmpty(activity.mTargetId)) {
            //单聊
            mIsSingle = true
            mChatView.setChatTitle(activity.mTitle)
            mConv = JMessageClient.getSingleConversation(activity.mTargetId, activity.mTargetAppKey)
            if (mConv == null) {
                mConv = Conversation.createSingleConversation(activity.mTargetId, activity.mTargetAppKey)
            }
            mChatAdapter = ChattingListAdapter(activity, mConv, longClickListener)
        } else {
            //群聊
            mIsSingle = false
            val fromGroup = activity.fromGroup
            if (fromGroup) {
                mChatView.setChatTitle(activity.mTitle, activity.MEMBERS_COUNT)
                mConv = JMessageClient.getGroupConversation(activity.mGroupId)
                mChatAdapter = ChattingListAdapter(activity, mConv, longClickListener)//长按聊天内容监听
            } else {
                mAtMsgId = activity.mAtMsgId
                mAtAllMsgId = activity.mAtAllMsgId
                mConv = JMessageClient.getGroupConversation(activity.mGroupId)
                if (mConv != null) {
                    val groupInfo = mConv?.targetInfo as GroupInfo
                    val userInfo = groupInfo.getGroupMemberInfo(mMyInfo?.userName, mMyInfo?.appKey)
                    //如果自己在群聊中，聊天标题显示群人数
                    if (userInfo != null) {
                        if (!TextUtils.isEmpty(groupInfo.groupName)) {
                            mChatView.setChatTitle(activity.mTitle, groupInfo.groupMembers.size)
                        } else {
                            mChatView.setChatTitle(activity.mTitle, groupInfo.groupMembers.size)
                        }
                        mChatView.showRightBtn()
                    } else {
                        if (!TextUtils.isEmpty(activity.mTitle)) {
                            mChatView.setChatTitle(activity.mTitle)
                        } else {
                            mChatView.setChatTitle(R.string.group)
                        }
                        mChatView.dismissRightBtn()
                    }
                } else {
                    mConv = Conversation.createGroupConversation(activity.mGroupId)
                }
                //更新群名
                JMessageClient.getGroupInfo(activity.mGroupId, object : GetGroupInfoCallback(false) {
                    override fun gotResult(status: Int, desc: String, groupInfo: GroupInfo) {
                        if (status == 0) {
                            mGroupInfo = groupInfo
                            mUIHandler.sendEmptyMessage(REFRESH_CHAT_TITLE)
                        }
                    }
                })
                if (mAtMsgId !== -1) {
                    mUnreadMsgCnt = mConv?.unReadMsgCnt!!
                    // 如果 @我 的消息位于屏幕显示的消息之上，显示 有人@我 的按钮
                    if (mAtMsgId + 8 <= mConv?.latestMessage?.getId()!!) {
                        mChatView.showAtMeButton()
                    }
                    mChatAdapter = ChattingListAdapter(activity, mConv, longClickListener, mAtMsgId)
                } else {
                    mChatAdapter = ChattingListAdapter(activity, mConv, longClickListener)
                }

            }
            //聊天信息标志改变
            mChatView.setGroupIcon()
        }

        if (activity.draft != null && !TextUtils.isEmpty(activity.draft)) {
            ekBar.etChat.setText(activity.draft)
        }

        mChatView.setChatListAdapter(mChatAdapter)
//        mChatAdapter.initMediaPlayer();
        mChatView.listView.setOnDropDownListener {
            mUIHandler.sendEmptyMessageDelayed(REFRESH_LAST_PAGE, 1000)
        }
        mChatView.setToBottom()
        mChatView.setConversation(mConv)
    }

    private fun initEmoticonsKeyBoardBar() {
        ekBar.setAdapter(SimpleCommonUtils.getCommonAdapter(activity, emoticonClickListener))
        ekBar.addOnFuncKeyBoardListener(this)
        var gridView = SimpleAppsGridView(activity)
        ekBar.addFuncView(gridView)
        ekBar.etChat.setOnSizeChangedListener { w, h, oldw, oldh -> scrollToBottom() }
        ekBar.btnSend.setOnClickListener {
            var mcgContent = ekBar.getEtChat().getText().toString();
            scrollToBottom()
            if (mcgContent.equals("")) {
                return@setOnClickListener
            }
            var msg: Message
            var content = TextContent(mcgContent)
            if (mAtAll) {
                msg = mConv!!.createSendMessageAtAllMember(content, null)
                mAtAll = false;
            } else if (null != mAtList) {
                msg = mConv!!.createSendMessage(content, mAtList, null)
            } else {
//                LogUtils.d("ChatActivity", "create send message conversation = " + mConv + "==content==" + content.toString());
                msg = mConv!!.createSendMessage(content);
            }

            if (!isChatRoom) {
                //设置需要已读回执
                var options = MessageSendingOptions()
                options.isNeedReadReceipt = true
                JMessageClient.sendMessage(msg, options)
                mChatAdapter!!.addMsgFromReceiptToList(msg)
                ekBar.getEtChat().setText("")
                if (mAtList != null) {
                    mAtList?.clear()
                }
                if (forDel != null) {
                    forDel.clear()
                }
            } else {
                JMessageClient.sendMessage(msg)
                mChatAdapter?.addMsgToList(msg)
                ekBar.getEtChat().setText("")
            }
        }

        ekBar.voiceOrText.setOnClickListener {
            val i = it.id
            if (i == R.id.btn_voice_or_text) {
                ekBar.setVideoText()
                ekBar.btnVoice.initConv(mConv, mChatAdapter, mChatView)
            }
        }
    }

    var emoticonClickListener = object : EmoticonClickListener<Any> {
        override fun onEmoticonClick(o: Any?, actionType: Int, isDelBtn: Boolean) {
            if (isDelBtn) {
                SimpleCommonUtils.delClick(ekBar.getEtChat());
            } else {
                if (o == null) {
                    return
                }
                if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                    if (o is EmoticonEntity) {
                        OnSendImage(o.iconUri)
                    }
                } else {
                    var content: String? = null
                    if (o is EmojiBean) {
                        content = o.emoji;
                    } else if (o is EmoticonEntity) {
                        content = o.content;
                    }

                    if (TextUtils.isEmpty(content)) {
                        return
                    }
                    var index = ekBar.getEtChat().selectionStart
                    var editable = ekBar.etChat.text
                    editable?.insert(index, content)
                }
            }
        }
    }

    fun OnSendImage(iconUri: String) {
        var substring = iconUri?.substring(7)
        var file = File(substring)
        ImageContent.createImageContentAsync(file, object : ImageContent.CreateImageContentCallback() {
            override fun gotResult(responseCode: Int, responseMessage: String?, imageContent: ImageContent?) {
                if (responseCode == 0) {
                    imageContent?.setStringExtra("jiguang", "xiong")
                    var msg = mConv?.createSendMessage(imageContent)
                    handleSendMsg(msg!!)
                } else {
                    ToastUtil.shortToast(activity, responseMessage)
                }
            }
        });
    }

    private fun handleSendMsg(msg: Message) {
        mChatAdapter?.setSendMsgs(msg)
        mChatView?.setToBottom()
    }
    fun scrollToBottom() {
        lvChat.requestLayout()
        lvChat.post { lvChat.setSelection(lvChat.bottom) }
    }


    fun initView() {
        initListView()
        ekBar.etChat.addTextChangedListener(object : TextWatcher {
            private var temp: CharSequence = ""

            override fun afterTextChanged(arg0: Editable) {
                if (temp.length > 0) {
                    mLongClick = false
                }

                if (mAtList != null && mAtList!!.size > 0) {
                    for (info in mAtList!!) {
                        val name = info.getDisplayName()

                        if (!arg0.toString().contains("@$name ")) {
                            forDel.add(info)
                        }
                    }
                    mAtList!!.removeAll(forDel)
                }

                if (!arg0.toString().contains("@所有成员 ")) {
                    mAtAll = false
                }

            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                temp = s
                if (s.length > 0 && after >= 1 && s.subSequence(start, start + 1)[0] == '@' && !mLongClick) {
                    if (null != mConv && mConv!!.type === ConversationType.group) {
                        ChooseAtMemberActivity.show(activity, ekBar.etChat, mConv!!.targetId)
                    }
                }
            }
        })

        ekBar.etChat.setOnFocusChangeListener { v, hasFocus ->
            val content: String
            if (hasFocus) {
                content = "{\"type\": \"input\",\"content\": {\"message\": \"对方正在输入\"}}"
            } else {
                content = "{\"type\": \"input\",\"content\": {\"message\": \"\"}}"
            }
            if (mIsSingle) {
                JMessageClient.sendSingleTransCommand(activity.mTargetId, null, content, object : BasicCallback() {
                    override fun gotResult(i: Int, s: String) {

                    }
                })
            }
        }

        mChatView.chatListView.setOnTouchListener { v, event ->
            mChatView.chatListView.isFocusable = true
            mChatView.chatListView.isFocusableInTouchMode = true
            mChatView.chatListView.requestFocus()
            CommonUtils.hideKeyboard(activity)
            false
        }
    }

    private fun initListView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var longClickListener = object : ChattingListAdapter.ContentLongClickListener() {
        override fun onContentLongClick(position: Int, view: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }


    companion object {
        private val REFRESH_LAST_PAGE = 0x1023
        private val REFRESH_CHAT_TITLE = 0x1024
        private val REFRESH_GROUP_NAME = 0x1025
        private val REFRESH_GROUP_NUM = 0x1026

        class UIHandler : Handler {
            var mActivity: WeakReference<ChatRoomActivity>

            constructor(activity: ChatRoomActivity) {
                this.mActivity = WeakReference<ChatRoomActivity>(activity)
            }

            override fun handleMessage(msg: android.os.Message?) {
                super.handleMessage(msg)


            }
        }

    }


}
package com.cstec.administrator.chart_module.ViewModel

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.cstec.administrator.chart_module.Activity.ChatRoomActivity
import com.cstec.administrator.chart_module.View.ChatUtils.DropDownListView
import com.cstec.administrator.chart_module.View.ChatUtils.SimpleCommonUtils
import com.zk.library.Base.BaseViewModel
import com.cstec.administrator.chart_module.Adapter.ChattingListAdapter
import cn.jpush.im.android.api.callback.GetGroupInfoCallback
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.Conversation
import android.text.TextUtils
import android.view.View
import com.cstec.administrator.chart_module.R
import java.lang.ref.WeakReference
import cn.jpush.im.api.BasicCallback
import cn.jpush.im.android.api.enums.ConversationType
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener.*
import android.widget.Toast
import cn.jpush.im.android.api.ChatRoomManager
import cn.jpush.im.android.api.callback.GetUserInfoListCallback
import cn.jpush.im.android.api.content.EventNotificationContent
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.event.*
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.options.MessageSendingOptions
import com.cstec.administrator.chart_module.Activity.ChooseAtMemberActivity
import com.cstec.administrator.chart_module.Activity.pickImage.PickImageActivity
import com.cstec.administrator.chart_module.Data.EmoticonEntity
import com.cstec.administrator.chart_module.Even.Event
import com.cstec.administrator.chart_module.Even.EventType
import com.cstec.administrator.chart_module.Even.ImageEvent
import com.cstec.administrator.chart_module.Inteface.EmoticonClickListener
import com.cstec.administrator.chart_module.Model.Constants
import com.cstec.administrator.chart_module.Utils.*
import com.cstec.administrator.chart_module.View.*
import com.cstec.administrator.chart_module.View.ChatUtils.FuncLayout
import com.cstec.administrator.chart_module.View.Emoji.EmojiBean
import com.tencent.smtt.sdk.TbsLogReport
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.RouterUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.uiContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.io.File


class ChatRoomViewModel : BaseViewModel(), FuncLayout.OnFuncKeyBoardListener {
    override fun OnFuncPop(height: Int) {
        scrollToBottom()
    }

    override fun OnFuncClose() {

    }

    override fun onPause() {
        super.onPause()
        JMessageClient.exitConversation()
        ekBar.reset()
    }

    override fun onResume() {
        if (mIsSingle) {
            if (null != activity.mTargetId) {
                JMessageClient.enterSingleConversation(activity.mTargetId, activity.mTargetAppKey)
            }
        } else if (!isChatRoom) {
            if (activity.mGroupId != 0L) {
                BaseApplication.isAtMe
                BaseApplication.isAtMe[activity.mGroupId] = false
                BaseApplication.isAtall[activity.mGroupId] = false
                JMessageClient.enterGroupConversation(activity.mGroupId)
            }
        }

        //历史消息中删除后返回到聊天界面刷新界面
        if (BaseApplication.ids != null && BaseApplication.ids.size > 0) {
            BaseApplication.ids.forEach {
                mChatAdapter?.removeMessage(it)
            }
        }
        if (mChatAdapter != null)
            mChatAdapter?.notifyDataSetChanged();
        //发送名片返回聊天界面刷新信息
        if (SharePreferenceManager.getIsOpen()) {
            if (!isChatRoom) {
                initData()
            }
            SharePreferenceManager.setIsOpen(false)
        }
        super.onResume()

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
    protected var mWidth: Int = 0
    protected var mHeight: Int = 0
    protected var mDensity: Float = 0.toFloat()
    protected var mDensityDpi: Int = 0
    var mWindow: Window? = null
    var mImm: InputMethodManager? = null
    protected var mRatio: Float = 0.toFloat()
    protected var mAvatarSize: Int = 0
    lateinit var mUIHandler: UIHandler
    fun inject(chatRoomActivity: ChatRoomActivity) {
        this.activity = chatRoomActivity
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        this.mChatView = chatRoomActivity.findViewById(R.id.chat_view)
        var dm = DisplayMetrics()
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mRatio = Math.min(mWidth / 720, mHeight / 1280).toFloat()
        mAvatarSize = ((50 * mDensity).toInt())
        this.mChatView.initModule(mDensity, mDensityDpi)
        this.activity = chatRoomActivity
        this.ekBar = chatRoomActivity.findViewById(R.id.ek_bar)
        this.lvChat = chatRoomActivity.findViewById(R.id.lv_chat)
        this.mChatView.setListeners(chatRoomActivity)
        this.mWindow = chatRoomActivity.window;
        this.mImm = chatRoomActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    fun dismissSoftInput() {
        if (mShowSoftInput) {
            if (mImm != null) {
                mImm?.hideSoftInputFromWindow(ekBar.etChat.windowToken, 0)
                mShowSoftInput = false
            }
            try {
                Thread.sleep(200)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun returnBtn() {
        mConv?.resetUnreadCount()
        dismissSoftInput()
        if (mChatAdapter != null) {
            mChatAdapter?.stopMediaPlayer()
        }
        JMessageClient.exitConversation()
        //发送保存为草稿事件到会话列表
        //TODO
        EventBus.getDefault().post(Event.Builder().setType(EventType.draft)
                .setConversation(mConv)
                .setDraft(ekBar.getEtChat().getText().toString())
                .build());
        BaseApplication.delConversation = null
        if (mConv?.allMessage == null || mConv?.allMessage!!.size == 0) {
            if (mIsSingle) {
                JMessageClient.deleteSingleConversation(activity.mTargetId)
            } else {
                JMessageClient.deleteGroupConversation(activity.mGroupId)
            }
            BaseApplication.delConversation = mConv;
        }
        if (isChatRoom) {
            ChatRoomManager.leaveChatRoom(activity.mTargetId?.toLong()!!, object : BasicCallback() {
                override fun gotResult(p0: Int, p1: String?) {
                    finish()
                    onBackPressed()
                }
            });
        } else {
            finish()
            super.onBackPressed()
        }
    }

    fun startChatRoomActivity(chatRoomId: Long) {
//TODO

    }

    fun startChatDetailActivity(mTargetId: String?, mTargetAppKey: String?, mGroupId: Long) {
        //TODO
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


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()


    }


    fun refreshGroupNum() {
        var conv = JMessageClient.getGroupConversation(activity.mGroupId)
        var groupInfo = conv.targetInfo as GroupInfo
        if (!TextUtils.isEmpty(groupInfo.groupName)) {
            var handleMessage = mUIHandler.obtainMessage()
            handleMessage.what = REFRESH_GROUP_NAME
            var bundle = Bundle()
            bundle.putString(RouterUtils.Chat_Module.Chat_GROUP_NAME, groupInfo.groupName)
            bundle.putInt(RouterUtils.Chat_Module.Chat_MEMBERS_COUNT, groupInfo.groupMembers.size)
            handleMessage.data = bundle;
            handleMessage.sendToTarget()
        } else {
            var handleMessage = mUIHandler.obtainMessage();
            handleMessage.what = REFRESH_GROUP_NUM
            var bundle = Bundle()
            bundle.putInt(RouterUtils.Chat_Module.Chat_MEMBERS_COUNT, groupInfo.groupMembers.size)
            handleMessage.data = bundle;
            handleMessage.sendToTarget()
        }
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
        lvChat.setAdapter(mChatAdapter);
        lvChat.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                when (scrollState) {
                    SCROLL_STATE_FLING -> {
                    }
                    SCROLL_STATE_IDLE -> {
                    }
                    SCROLL_STATE_TOUCH_SCROLL -> {
                        ekBar.reset()
                    }

                }
            }
        });
    }

    var longClickListener = object : ChattingListAdapter.ContentLongClickListener() {
        override fun onContentLongClick(position: Int, view: View?) {
            if (isChatRoom) {
                return;
            }
            var msg = mChatAdapter?.getMessage(position);

            if (msg == null) {
                return;
            }
            //如果是文本消息
            if ((msg.getContentType() == ContentType.text) && (msg?.content as TextContent).getStringExtra("businessCard") == null) {
                //接收方
                if (msg.getDirect() == MessageDirect.receive) {
                    var location = intArrayOf(2)
                    view?.getLocationOnScreen(location);
                    var OldListY = location[1]
                    var OldListX = location[0]
                    TipView.Builder(activity, mChatView, OldListX + view?.width!! / 2, OldListY + view?.height!!)
                            .addItem(TipItem("复制"))
                            .addItem(TipItem("转发"))
                            .addItem(TipItem("删除"))
                            .setOnItemClickListener(object : TipView.OnItemClickListener {
                                override fun onItemClick(name: String?, position: Int) {
                                    if (position == 0) {
                                        if (msg.getContentType() == ContentType.text) {
                                            var content = (msg.getContent() as TextContent).text;
                                            if (Build.VERSION.SDK_INT > 11) {
                                                var clipboard = activity
                                                        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                var clip = ClipData.newPlainText("Simple text", content);
                                                clipboard.primaryClip = clip;
                                            } else {
                                                var clip = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                if (clip.hasText()) {
                                                    clip.getText();
                                                }
                                            }
//                                            Toast.makeText(ChatActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                                        } else {
//                                            Toast.makeText(ChatActivity.this, "只支持复制文字", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 1) {
//                                        var intent = Intent(activity, ForwardMsgActivity.class);
//                                        JGApplication.forwardMsg.clear();
//                                        JGApplication.forwardMsg.add(msg);
//                                        startActivity(intent);
                                    } else {
                                        //删除
                                        mConv?.deleteMessage(msg.getId())
                                        mChatAdapter?.removeMessage(msg)
                                    }
                                }

                                override fun dismiss() {

                                }
                            })
                            .create();
                    //发送方
                } else {
                    var location = intArrayOf(2)
                    view?.getLocationOnScreen(location)
                    var OldListY = location[1]
                    var OldListX = location[0]
                    TipView.Builder(activity, mChatView, OldListX + view?.width!! / 2, OldListY + view?.height!!)
                            .addItem(TipItem("复制"))
                            .addItem(TipItem("转发"))
                            .addItem(TipItem("撤回"))
                            .addItem(TipItem("删除"))
                            .setOnItemClickListener(object : TipView.OnItemClickListener {
                                override fun onItemClick(name: String?, position: Int) {
                                    if (position == 0) {
                                        if (msg.getContentType() == ContentType.text) {
                                            var content = (msg.getContent() as TextContent).getText();
                                            if (Build.VERSION.SDK_INT > 11) {
                                                var clipboard = activity
                                                        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                var clip = ClipData.newPlainText("Simple text", content);
                                                clipboard.setPrimaryClip(clip);
                                            } else {
                                                var clip = activity
                                                        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                if (clip.hasText()) {
                                                    clip.getText();
                                                }
                                            }
                                            Toast.makeText(activity, "已复制", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(activity, "只支持复制文字", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 1) {
                                        //转发
                                        if (msg.getContentType() == ContentType.text || msg.getContentType() == ContentType.image ||
                                                (msg.getContentType() == ContentType.file && (msg.getContent()).getStringExtra("video") != null)) {
//                                            Intent intent = new Intent(ChatActivity.this, ForwardMsgActivity.class);
//                                            JGApplication.forwardMsg.clear();
//                                            JGApplication.forwardMsg.add(msg);
//                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(activity, "只支持转发文本,图片,小视频", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 2) {
                                        //撤回
                                        mConv?.retractMessage(msg, object : BasicCallback() {
                                            override fun gotResult(p0: Int, p1: String?) {
                                                if (p0 == 855001) {
                                                    Toast.makeText(activity, "发送时间过长，不能撤回", Toast.LENGTH_SHORT).show();
                                                } else if (p0 == 0) {
                                                    mChatAdapter?.delMsgRetract(msg)
                                                }
                                            }
                                        });
                                    } else {
                                        //删除
                                        mConv?.deleteMessage(msg.getId())
                                        mChatAdapter?.removeMessage(msg)
                                    }
                                }

                                override fun dismiss() {
                                }
                            })
                            .create();
                }
                //除了文本消息类型之外的消息类型
            } else {
                //接收方
                if (msg.getDirect() == MessageDirect.receive) {
                    var location = intArrayOf(2)
                    view?.getLocationOnScreen(location)
                    var OldListY = location[1]
                    var OldListX = location[0]
                    TipView.Builder(activity, mChatView, OldListX + view?.width!! / 2, OldListY + view?.height!!)
                            .addItem(TipItem("转发"))
                            .addItem(TipItem("删除"))
                            .setOnItemClickListener(object : TipView.OnItemClickListener {
                                override fun onItemClick(name: String?, position: Int) {
                                    if (position == 1) {
                                        //删除
                                        mConv?.deleteMessage(msg.getId())
                                        mChatAdapter?.removeMessage(msg)
                                    } else {
//                                        Intent intent = new Intent(ChatActivity.this, ForwardMsgActivity.class);
//                                        JGApplication.forwardMsg.clear();
//                                        JGApplication.forwardMsg.add(msg);
//                                        startActivity(intent);
                                    }
                                }

                                override fun dismiss() {
                                }
                            })
                            .create();
                    //发送方
                } else {
                    var location = intArrayOf(2)
                    view?.getLocationOnScreen(location);
                    var OldListY = location[1];
                    var OldListX = location[0];
                    TipView.Builder(activity, mChatView, OldListX + view?.width!! / 2, OldListY + view?.height!!)
                            .addItem(TipItem("转发"))
                            .addItem(TipItem("撤回"))
                            .addItem(TipItem("删除"))
                            .setOnItemClickListener(object : TipView.OnItemClickListener {
                                override fun onItemClick(name: String?, position: Int) {
                                    if (position == 1) {
                                        //撤回
                                        mConv?.retractMessage(msg, object : BasicCallback() {
                                            override fun gotResult(i: Int, s: String?) {
                                                if (i == 855001) {
                                                    Toast.makeText(activity, "发送时间过长，不能撤回", Toast.LENGTH_SHORT).show();
                                                } else if (i == 0) {
                                                    mChatAdapter?.delMsgRetract(msg)
                                                }
                                            }
                                        });
                                    } else if (position == 0) {
//                                        Intent intent = new Intent(ChatActivity.this, ForwardMsgActivity.class);
//                                        JGApplication.forwardMsg.clear();
//                                        JGApplication.forwardMsg.add(msg);
//                                        startActivity(intent);
                                    } else {
                                        //删除
                                        mConv?.deleteMessage(msg.getId())
                                        mChatAdapter?.removeMessage(msg)
                                    }
                                }

                                override fun dismiss() {
                                }
                            })
                            .create()
                }
            }
        }
    }


    open fun onEventMainThread(event: ChatRoomNotificationEvent) {
        try {
            var constructor = EventNotificationContent::class.java.getDeclaredConstructor()
            constructor.isAccessible = true;
            var messages = ArrayList<Message>()
            when (event.getType()) {
                ChatRoomNotificationEvent.Type.add_chatroom_admin -> {
                }
                ChatRoomNotificationEvent.Type.del_chatroom_admin -> {
                    event.getTargetUserInfoList(object : GetUserInfoListCallback() {
                        override fun gotResult(i: Int, s: String?, list: MutableList<UserInfo>?) {
                            if (i == 0) {
                                list?.forEach {
                                    try {
                                        var content = constructor as EventNotificationContent
                                        var field = content.javaClass.superclass.getDeclaredField("contentType")
                                        field.isAccessible = true;
                                        field.set(content, ContentType.eventNotification);
                                        var user = ""
                                        var result = ""
                                        if (it.userID == JMessageClient.getMyInfo().userID) {
                                            user = "你"
                                        } else {
                                            if (TextUtils.isEmpty(it.nickname)) {
                                                user = it.userName
                                            } else {
                                                user = it.nickname
                                            }
                                        }
                                        if (event.type == ChatRoomNotificationEvent.Type.add_chatroom_admin) {
                                            result = "被设置成管理员"
                                        } else {
                                            result = "被取消管理员"
                                        }
                                        content.setStringExtra("msg", user + result);
                                        if (mConv != null) {
                                            messages.add(mConv?.createSendMessage(content)!!);
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                if (messages.size > 0) {
                                    mChatAdapter?.addMsgListToList(messages)
                                }
                            }
                        }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun tempFile(): String {
        var filename = StringUtil.get32UUID() + ".jpg"
        return StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.PICK_IMAGE -> {
                onPickImageActivityResult(requestCode, data);
            }
        }
    }

    private fun onPickImageActivityResult(requestCode: Int, data: Intent?) {
        if (data == null) {
            return;
        }
        var local = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false);
        if (local) {
            // 本地相册
            sendImageAfterSelfImagePicker(data);
        }
    }

    private fun sendImageAfterSelfImagePicker(data: Intent) {
        SendImageHelper.sendImageAfterSelfImagePicker(activity, data, SendImageHelper.Callback { file, isOrig ->
            //所有图片都在这里拿到
            ImageContent.createImageContentAsync(file, object : ImageContent.CreateImageContentCallback() {
                override fun gotResult(responseCode: Int, responseMessage: String, imageContent: ImageContent) {
                    if (responseCode == 0) {
                        val msg = mConv?.createSendMessage(imageContent)
                        handleSendMsg(msg!!)
                    }
                }
            })
        })
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEventMainThread(event: ImageEvent) {
        var intent: Intent? = null
        when (event.flag) {
            BaseApplication.IMAGE_MESSAGE -> {
                var from = PickImageActivity.FROM_LOCAL;
                var requestCode = RequestCode.PICK_IMAGE;
                PickImageActivity.start(activity, requestCode, from, tempFile()!!, true, 9,
                        true, false, 0, 0);
            }
            BaseApplication.TAKE_PHOTO_MESSAGE -> {
                if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(activity, "请在应用管理中打开“相机,读写存储,录音”访问权限！", Toast.LENGTH_LONG).show();
                } else {
//                    var intent = Intent(this, CameraActivity::class.java)
//                            startActivityForResult (intent, RequestCode.TAKE_PHOTO);
                }
            }
            BaseApplication.TAKE_LOCATION -> {
//                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "请在应用管理中打开“位置”访问权限！", Toast.LENGTH_LONG).show();
//                } else {
//                    intent = new Intent (mContext, MapPickerActivity.class);
//                    intent.putExtra(JGApplication.CONV_TYPE, mConv.getType());
//                    intent.putExtra(JGApplication.TARGET_ID, mTargetId);
//                    intent.putExtra(JGApplication.TARGET_APP_KEY, mTargetAppKey);
//                    intent.putExtra("sendLocation", true);
//                    startActivityForResult(intent, JGApplication.REQUEST_CODE_SEND_LOCATION);
//                }
            }
            BaseApplication.FILE_MESSAGE -> {
                if (ContextCompat.checkSelfPermission(activity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();

                } else {
//                    intent = new Intent (mContext, SendFileActivity.class);
//                    intent.putExtra(JGApplication.TARGET_ID, mTargetId);
//                    intent.putExtra(JGApplication.TARGET_APP_KEY, mTargetAppKey);
//                    intent.putExtra(JGApplication.CONV_TYPE, mConv.getType());
//                    startActivityForResult(intent, JGApplication.REQUEST_CODE_SEND_FILE);
                }
            }
            BaseApplication.BUSINESS_CARD -> {
//                intent =  Intent (activity, FriendListActivity.class);
//                intent.putExtra(JGApplication.CONV_TYPE, mConv.getType());
//                intent.putExtra(JGApplication.TARGET_ID, mTargetId);
//                intent.putExtra(JGApplication.TARGET_APP_KEY, mTargetAppKey);;
//                activity.startActivityForResult(intent, JGApplication.REQUEST_CODE_FRIEND_LIST);
            }
            BaseApplication.TACK_VIDEO -> {
            }
            BaseApplication.TACK_VOICE -> {
            }
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
                var activity = mActivity.get()
                if (activity != null) {
                    when (msg?.what) {
                        REFRESH_LAST_PAGE -> {
                            activity.mViewModel?.mChatAdapter?.dropDownToRefresh();
                            activity.mViewModel?.mChatView!!.listView.onDropDownComplete();
                            if (activity.mViewModel?.mChatAdapter?.isHasLastPage!!) {
                                if (Build.VERSION.SDK_INT >= 21) {
                                    activity.mViewModel!!.mChatView.listView
                                            .setSelectionFromTop(activity.mViewModel!!.mChatAdapter!!.offset,
                                                    activity.mViewModel!!.mChatView.listView.headerHeight)
                                } else {
                                    activity.mViewModel!!.mChatView.getListView().setSelection(activity.mViewModel!!.mChatAdapter
                                    !!.offset)
                                }
                                activity.mViewModel!!.mChatAdapter!!.refreshStartPosition();
                            } else {
                                activity.mViewModel!!.mChatView.listView.setSelection(0);
                            }
                            //显示上一页的消息数18条
                            activity.mViewModel!!.mChatView.getListView()
                                    .setOffset(activity.mViewModel!!.mChatAdapter?.offset!!)
                        }
                        REFRESH_GROUP_NAME -> {
                            if (activity.mViewModel?.mConv != null) {
                                var num = msg.data.getInt(RouterUtils.Chat_Module.Chat_MEMBERS_COUNT)
                                var groupName = msg.data.getString(RouterUtils.Chat_Module.Chat_GROUP_NAME)
                                activity.mViewModel?.mChatView!!.setChatTitle(groupName, num)
                            }
                        }
                        REFRESH_GROUP_NUM -> {
                            val num = msg.data.getInt(RouterUtils.Chat_Module.Chat_MEMBERS_COUNT)
                            activity.mViewModel?.mChatView!!.setChatTitle(R.string.group, num)
                        }
                        REFRESH_CHAT_TITLE -> {
                            if (activity.mViewModel?.mGroupInfo != null) {
                                //检查自己是否在群组中
                                var info = activity.mViewModel?.mGroupInfo!!.getGroupMemberInfo(activity.mViewModel!!.mMyInfo?.userName,
                                        activity.mViewModel?.mMyInfo?.appKey)
                                if (!TextUtils.isEmpty(activity.mViewModel?.mGroupInfo!!.groupName)) {
                                    if (info != null) {
                                        activity.mViewModel!!.mChatView.setChatTitle(activity.mTitle,
                                                activity.mViewModel!!.mGroupInfo!!.groupMembers.size)
                                        activity.mViewModel?.mChatView?.showRightBtn()
                                    } else {
                                        activity.mViewModel?.mChatView!!.setChatTitle(activity.mTitle)
                                        activity.mViewModel?.mChatView!!.dismissRightBtn()
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

    }


}
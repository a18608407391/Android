package com.cstec.administrator.chart_module.Adapter

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.cstec.administrator.chart_module.View.ConversationListView
import com.cstec.administrator.chart_module.View.SharePreferenceManager

import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.content.CustomContent
import cn.jpush.im.android.api.content.MessageContent
import cn.jpush.im.android.api.content.PromptContent
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.enums.MessageStatus
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.model.UserInfo
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.View.SwipeLayoutConv
import com.zk.library.Base.BaseApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.uiContext

/**
 * Created by ${chenyn} on 2017/3/30.
 */

class ConversationListAdapter(private val mContext: Activity, private val mDatas: MutableList<Conversation>?, private val mConversationListView: ConversationListView) : BaseAdapter() {
    private val mDraftMap = HashMap<String, String>()
    private val mUIHandler = UIHandler(this)
    private val mArray = SparseBooleanArray()
    private val mAtAll = SparseBooleanArray()
    private val mAtConvMap = HashMap<Conversation, Int>()
    private val mAtAllConv = HashMap<Conversation, Int>()
    private var mUserInfo: UserInfo? = null
    private var mGroupInfo: GroupInfo? = null

    internal var topConv: MutableList<Conversation>? = ArrayList<Conversation>()
    internal var forCurrent: MutableList<Conversation> = ArrayList()

    /**
     * 收到消息后将会话置顶
     *
     * @param conv 要置顶的会话
     */
    fun setToTop(conv: Conversation) {
        var oldCount = 0
        var newCount = 0


        CoroutineScope(uiContext).launch {
            mConversationListView.setNullConversation(true)
        }


        //如果是旧的会话
        for (conversation in mDatas!!) {
            if (conv.id == conversation.id) {
                //如果是置顶的,就直接把消息插入,会话在list中的顺序不变
                if (!TextUtils.isEmpty(conv.extra)) {
                    mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200)
                    //这里一定要return掉,要不还会走到for循环之后的方法,就会再次添加会话
                    return
                    //如果不是置顶的,就在集合中把原来的那条消息移出,然后去掉置顶的消息数量,插入到集合中
                } else {
                    //因为后面要改变排序位置,这里要删除
                    mDatas.remove(conversation)
                    //这里要排序,因为第一次登录有漫游消息.离线消息(其中群组变化也是用这个事件下发的);所以有可能会话的最后一条消息
                    //时间比较早,但是事件下发比较晚,这就导致乱序.所以要重新排序.

                    //排序规则,每一个进来的会话去和倒叙list中的会话比较时间,如果进来的会话的最后一条消息就是最早创建的
                    //那么这个会话自然就是最后一个.所以直接跳出循环,否则就一个个向前比较.
                    for (i in mDatas.size downTo SharePreferenceManager.getCancelTopSize() + 1) {
                        if (conv.latestMessage != null && mDatas[i - 1].latestMessage != null) {
                            if (conv.latestMessage.createTime > mDatas[i - 1].latestMessage.createTime) {
                                oldCount = i - 1
                            } else {
                                oldCount = i
                                break
                            }
                        } else {
                            oldCount = i
                        }
                    }
                    mDatas.add(oldCount, conv)
                    mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200)
                    return
                }
            }
        }
        if (mDatas.size == 0) {
            mDatas.add(conv)
        } else {
            //如果是新的会话,直接去掉置顶的消息数之后就插入到list中
            for (i in mDatas.size downTo SharePreferenceManager.getCancelTopSize() + 1) {
                if (conv.latestMessage != null && mDatas[i - 1].latestMessage != null) {
                    if (conv.latestMessage.createTime > mDatas[i - 1].latestMessage.createTime) {
                        newCount = i - 1
                    } else {
                        newCount = i
                        break
                    }
                } else {
                    newCount = i
                }
            }
            mDatas.add(newCount, conv)
        }
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200)
    }

    //置顶会话
    fun setConvTop(conversation: Conversation) {
        var count = 0
        //遍历原有的会话,得到有几个会话是置顶的
        for (conv in mDatas!!) {
            if (!TextUtils.isEmpty(conv.extra)) {
                count++
            }
        }
        conversation.updateConversationExtra(count.toString() + "")
        mDatas.remove(conversation)
        mDatas.add(count, conversation)
        mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST)
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200)

    }

    //取消会话置顶
    fun setCancelConvTop(conversation: Conversation) {
        forCurrent.clear()
        topConv!!.clear()
        var i = 0
        for (oldConv in mDatas!!) {
            //在原来的会话中找到取消置顶的这个,添加到待删除中
            if (oldConv.id == conversation.id) {
                oldConv.updateConversationExtra("")
                break
            }
        }
        //全部会话排序
        val sortConvList = SortConvList()
        Collections.sort(mDatas, sortConvList)

        //遍历会话找到原来设置置顶的
        for (con in mDatas) {
            if (!TextUtils.isEmpty(con.extra)) {
                forCurrent.add(con)
            }
        }
        topConv!!.addAll(forCurrent)
        SharePreferenceManager.setCancelTopSize(topConv!!.size)
        mDatas.removeAll(forCurrent)
        if (topConv != null && topConv!!.size > 0) {
            val top = SortTopConvList()
            Collections.sort(topConv!!, top)
            for (conv in topConv!!) {
                mDatas.add(i, conv)
                i++
            }
        }
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mDatas?.size ?: 0
    }

    override fun getItem(position: Int): Conversation? {
        return if (mDatas == null) {
            null
        } else mDatas[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        val convItem = mDatas!![position]
        mConversationListView.setUnReadMsg(JMessageClient.getAllUnReadMsgCount())
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conv_list, null)
        }
        var headIcon = ViewHolder.get(convertView, R.id.msg_item_head_icon) as ImageView
        val convName = ViewHolder.get(convertView, R.id.conv_item_name) as TextView
        val content = ViewHolder.get(convertView, R.id.msg_item_content) as TextView
        val datetime = ViewHolder.get(convertView, R.id.msg_item_date) as TextView
        val newGroupMsgNumber = ViewHolder.get(convertView, R.id.new_group_msg_number) as TextView
        val newMsgNumber = ViewHolder.get(convertView, R.id.new_msg_number) as TextView
        val groupBlocked = ViewHolder.get(convertView, R.id.iv_groupBlocked) as ImageView
        val newMsgDisturb = ViewHolder.get(convertView, R.id.new_group_msg_disturb) as ImageView
        val newGroupMsgDisturb = ViewHolder.get(convertView, R.id.new_msg_disturb) as ImageView
        val convListSendFail = ViewHolder.get(convertView, R.id.iv_convListSendFail) as ImageView

        val swipeLayout = ViewHolder.get(convertView, R.id.swp_layout) as SwipeLayoutConv
        val delete = ViewHolder.get(convertView, R.id.tv_delete) as TextView

        var draft = mDraftMap[convItem.id]
        if (!TextUtils.isEmpty(convItem.extra)) {
            swipeLayout.setBackgroundColor(mContext.resources.getColor(R.color.conv_list_background))
        } else {
            swipeLayout.setBackgroundColor(mContext.resources.getColor(R.color.white))
        }

        //如果会话草稿为空,显示最后一条消息
        if (TextUtils.isEmpty(draft)) {
            val lastMsg = convItem.latestMessage
            if (lastMsg != null) {
                val timeFormat = TimeFormat(mContext, lastMsg.createTime)
                //会话界面时间
                datetime.setText(timeFormat.getTime())
                var contentStr: String
                when (lastMsg.contentType) {
                    ContentType.image -> contentStr = mContext.getString(R.string.type_picture)
                    ContentType.voice -> contentStr = mContext.getString(R.string.type_voice)
                    ContentType.location -> contentStr = mContext.getString(R.string.type_location)
                    ContentType.file -> {
                        val extra = lastMsg.content.getStringExtra("video")
                        if (!TextUtils.isEmpty(extra)) {
                            contentStr = mContext.getString(R.string.type_smallvideo)
                        } else {
                            contentStr = mContext.getString(R.string.type_file)
                        }
                    }
                    ContentType.video -> contentStr = mContext.getString(R.string.type_video)
                    ContentType.eventNotification -> contentStr = mContext.getString(R.string.group_notification)
                    ContentType.custom -> {
                        val customContent = lastMsg.content as CustomContent
                        val isBlackListHint = customContent.getBooleanValue("blackList")
                        if (isBlackListHint != null && isBlackListHint) {
                            contentStr = mContext.getString(R.string.jmui_server_803008)
                        } else {
                            contentStr = mContext.getString(R.string.type_custom)
                        }
                    }
                    ContentType.prompt -> contentStr = (lastMsg.content as PromptContent).promptText
                    else -> contentStr = (lastMsg.content as TextContent).text
                }

                if (lastMsg.status == MessageStatus.send_fail) {
                    convListSendFail.setVisibility(View.VISIBLE)
                } else {
                    convListSendFail.setVisibility(View.GONE)
                }

                val msgContent = lastMsg.content
                val isRead = msgContent.getBooleanExtra("isRead")
                val isReadAtAll = msgContent.getBooleanExtra("isReadAtAll")
                if (lastMsg.isAtMe) {
                    if (null != isRead && isRead) {
                        mArray.delete(position)
                        mAtConvMap.remove(convItem)
                    } else {
                        mArray.put(position, true)
                    }
                }
                if (lastMsg.isAtAll) {
                    if (null != isReadAtAll && isReadAtAll) {
                        mAtAll.delete(position)
                        mAtAllConv.remove(convItem)
                    } else {
                        mAtAll.put(position, true)
                    }

                }
                var gid: Long = 0
                if (convItem.type == ConversationType.group) {
                    gid = java.lang.Long.parseLong(convItem.targetId)
                }

                if (mAtAll.get(position) && BaseApplication.isAtall.get(gid) != null && BaseApplication.isAtall.get(gid)!!) {
                    contentStr = "[@所有人] $contentStr"
                    val builder = SpannableStringBuilder(contentStr)
                    builder.setSpan(ForegroundColorSpan(Color.RED), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    content.setText(builder)
                } else if (mArray.get(position) && BaseApplication.isAtMe.get(gid) != null && BaseApplication.isAtMe.get(gid)!!) {
                    //有人@我 文字提示
                    contentStr = mContext.getString(R.string.somebody_at_me) + contentStr
                    val builder = SpannableStringBuilder(contentStr)
                    builder.setSpan(ForegroundColorSpan(Color.RED), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    content.setText(builder)
                } else {
                    if (lastMsg.targetType == ConversationType.group && contentStr != "[群成员变动]") {
                        val info = lastMsg.fromUser
                        val fromName = info.displayName
                        if (BaseApplication.isAtall.get(gid) != null && BaseApplication.isAtall.get(gid)!!) {
                            content.setText("[@所有人] $fromName: $contentStr")
                        } else if (BaseApplication.isAtMe.get(gid) != null && BaseApplication.isAtMe.get(gid)!!) {
                            content.setText("[有人@我] $fromName: $contentStr")
                            //如果content是撤回的那么就不显示最后一条消息的发起人名字了
                        } else if (msgContent.contentType == ContentType.prompt) {
                            content.setText(contentStr)
                        } else if (info.userName == JMessageClient.getMyInfo().userName) {
                            content.setText(contentStr)
                        } else {
                            content.setText("$fromName: $contentStr")
                        }
                    } else {
                        if (BaseApplication.isAtall.get(gid) != null && BaseApplication.isAtall.get(gid)!!) {
                            content.setText("[@所有人] $contentStr")
                        } else if (BaseApplication.isAtMe.get(gid) != null && BaseApplication.isAtMe.get(gid)!!) {
                            content.setText("[有人@我] $contentStr")
                        } else {
                            if (lastMsg.unreceiptCnt == 0) {
                                if (lastMsg.targetType == ConversationType.single &&
                                        lastMsg.direct == MessageDirect.send &&
                                        lastMsg.contentType != ContentType.prompt &&
                                        //排除自己给自己发送消息
                                        (lastMsg.targetInfo as UserInfo).userName != JMessageClient.getMyInfo().userName) {
                                    if (lastMsg.status == MessageStatus.send_fail) {
                                        content.setText(contentStr)
                                    } else {
                                        content.setText("[已读]$contentStr")
                                    }
                                } else {
                                    content.setText(contentStr)
                                }
                            } else {
                                if (lastMsg.targetType == ConversationType.single &&
                                        lastMsg.direct == MessageDirect.send &&
                                        lastMsg.contentType != ContentType.prompt &&
                                        (lastMsg.targetInfo as UserInfo).userName != JMessageClient.getMyInfo().userName) {
                                    contentStr = "[未读]$contentStr"
                                    val builder = SpannableStringBuilder(contentStr)
                                    builder.setSpan(ForegroundColorSpan(mContext.resources.getColor(R.color.line_normal)),
                                            0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    content.setText(builder)
                                } else {
                                    content.setText(contentStr)
                                }

                            }
                        }
                    }
                }
            } else {
                if (convItem.lastMsgDate == 0L) {
                    //会话列表时间展示的是最后一条会话,那么如果最后一条消息是空的话就不显示
                    datetime.setText("")
                    content.setText("")
                } else {
                    val timeFormat = TimeFormat(mContext, convItem.lastMsgDate)
                    datetime.setText(timeFormat.getTime())
                    content.setText("")
                }
            }
        } else {
            draft = mContext.getString(R.string.draft) + draft
            val builder = SpannableStringBuilder(draft)
            builder.setSpan(ForegroundColorSpan(Color.RED), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            content.setText(builder)
        }

        if (convItem.type == ConversationType.single) {
            groupBlocked.setVisibility(View.GONE)
            convName.setText(convItem.title)
            mUserInfo = convItem.targetInfo as UserInfo
            if (mUserInfo != null) {
                mUserInfo!!.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                    override fun gotResult(status: Int, desc: String, bitmap: Bitmap) {
                        if (status == 0) {
                            headIcon.setImageBitmap(bitmap)
                        } else {
                            headIcon.setImageResource(R.drawable.jmui_head_icon)
                        }
                    }
                })
            } else {
                headIcon.setImageResource(R.drawable.jmui_head_icon)
            }
        } else if (convItem.type == ConversationType.group) {
            mGroupInfo = convItem.targetInfo as GroupInfo
            if (mGroupInfo != null) {
                mGroupInfo!!.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                    override fun gotResult(i: Int, s: String, bitmap: Bitmap) {
                        if (i == 0) {
                            headIcon.setImageBitmap(bitmap)
                        } else {
                            headIcon.setImageResource(R.drawable.group)
                        }
                    }
                })
                val blocked = mGroupInfo!!.isGroupBlocked
                if (blocked == 1) {
                    groupBlocked.setVisibility(View.VISIBLE)
                } else {
                    groupBlocked.setVisibility(View.GONE)
                }
            }
            convName.setText(convItem.title)
        }

        if (convItem.unReadMsgCnt > 0) {
            newGroupMsgDisturb.setVisibility(View.GONE)
            newMsgDisturb.setVisibility(View.GONE)
            newGroupMsgNumber.setVisibility(View.GONE)
            newMsgNumber.setVisibility(View.GONE)
            if (convItem.type == ConversationType.single) {
                if (mUserInfo != null && mUserInfo!!.noDisturb == 1) {
                    newMsgDisturb.setVisibility(View.VISIBLE)
                } else {
                    newMsgNumber.setVisibility(View.VISIBLE)
                }
                if (convItem.unReadMsgCnt < 100) {
                    newMsgNumber.setText(convItem.unReadMsgCnt.toString())
                } else {
                    newMsgNumber.setText("99+")
                }
            } else {
                if (mGroupInfo != null && mGroupInfo!!.noDisturb == 1) {
                    newGroupMsgDisturb.setVisibility(View.VISIBLE)
                } else {
                    newGroupMsgNumber.setVisibility(View.VISIBLE)
                }
                if (convItem.unReadMsgCnt < 100) {
                    newGroupMsgNumber.setText(convItem.unReadMsgCnt.toString())
                } else {
                    newGroupMsgNumber.setText("99+")
                }
            }

        } else {
            newGroupMsgDisturb.setVisibility(View.GONE)
            newMsgDisturb.setVisibility(View.GONE)
            newGroupMsgNumber.setVisibility(View.GONE)
            newMsgNumber.setVisibility(View.GONE)
        }

        //禁止使用侧滑功能.如果想要使用就设置为true
        swipeLayout.setSwipeEnabled(false)
        //侧滑删除会话
        swipeLayout.addSwipeListener(object : SwipeLayoutConv.SwipeListener {
            override fun onStartOpen(layout: SwipeLayoutConv) {

            }

            override fun onOpen(layout: SwipeLayoutConv) {
                delete.setOnClickListener(View.OnClickListener {
                    if (convItem.type == ConversationType.single) {
                        JMessageClient.deleteSingleConversation((convItem.targetInfo as UserInfo).userName)
                    } else {
                        JMessageClient.deleteGroupConversation((convItem.targetInfo as GroupInfo).groupID)
                    }
                    mDatas.removeAt(position)
                    if (mDatas.size > 0) {
                        mConversationListView.setNullConversation(true)
                    } else {
                        mConversationListView.setNullConversation(false)
                    }
                    notifyDataSetChanged()
                })
            }

            override fun onStartClose(layout: SwipeLayoutConv) {

            }

            override fun onClose(layout: SwipeLayoutConv) {

            }

            override fun onUpdate(layout: SwipeLayoutConv, leftOffset: Int, topOffset: Int) {

            }

            override fun onHandRelease(layout: SwipeLayoutConv, xvel: Float, yvel: Float) {

            }
        })


        return convertView
    }

    fun sortConvList() {
        forCurrent.clear()
        topConv!!.clear()
        var i = 0
        val sortConvList = SortConvList()
        Collections.sort(mDatas!!, sortConvList)
        for (con in mDatas) {
            if (!TextUtils.isEmpty(con.extra)) {
                forCurrent.add(con)
            }
        }
        topConv!!.addAll(forCurrent)
        mDatas.removeAll(forCurrent)
        if (topConv != null && topConv!!.size > 0) {
            val top = SortTopConvList()
            Collections.sort(topConv!!, top)
            for (conv in topConv!!) {
                mDatas.add(i, conv)
                i++
            }
        }
        notifyDataSetChanged()
    }

    fun addNewConversation(conv: Conversation) {
        mDatas!!.add(0, conv)
        if (mDatas.size > 0) {
            mConversationListView.setNullConversation(true)
        } else {
            mConversationListView.setNullConversation(false)
        }
        notifyDataSetChanged()
    }

    fun addAndSort(conv: Conversation) {
        mDatas!!.add(conv)
        val sortConvList = SortConvList()
        Collections.sort(mDatas, sortConvList)
        notifyDataSetChanged()
    }

    fun deleteConversation(conversation: Conversation) {
        mDatas!!.remove(conversation)
        notifyDataSetChanged()
    }

    fun putDraftToMap(conv: Conversation, draft: String) {
        mDraftMap[conv.id] = draft
    }

    fun delDraftFromMap(conv: Conversation) {
        mArray.delete(mDatas!!.indexOf(conv))
        mAtConvMap.remove(conv)
        mAtAllConv.remove(conv)
        mDraftMap.remove(conv.id)
        notifyDataSetChanged()
    }

    fun getDraft(convId: String): String {
        return mDraftMap[convId]!!
    }

    fun includeAtMsg(conv: Conversation): Boolean {
        if (mAtConvMap.size > 0) {
            val iterator = mAtConvMap.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (conv === entry.key) {
                    return true
                }
            }
        }
        return false
    }

    fun includeAtAllMsg(conv: Conversation): Boolean {
        if (mAtAllConv.size > 0) {
            val iterator = mAtAllConv.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (conv === entry.key) {
                    return true
                }
            }
        }
        return false
    }

    fun getAtMsgId(conv: Conversation): Int? {
        return mAtConvMap[conv]
    }

    fun getatAllMsgId(conv: Conversation): Int? {
        return mAtAllConv[conv]
    }

    fun putAtConv(conv: Conversation, msgId: Int) {
        mAtConvMap[conv] = msgId
    }

    fun putAtAllConv(conv: Conversation, msgId: Int) {
        mAtAllConv[conv] = msgId
    }

    private class UIHandler(adapter: ConversationListAdapter) : Handler() {

        private val mAdapter: WeakReference<ConversationListAdapter>

        init {
            mAdapter = WeakReference(adapter)
        }

        override fun handleMessage(msg: android.os.Message) {
            super.handleMessage(msg)
            val adapter = mAdapter.get()
            if (adapter != null) {
                when (msg.what) {
                    REFRESH_CONVERSATION_LIST -> adapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        private val REFRESH_CONVERSATION_LIST = 0x3003
    }


}

package com.cstec.administrator.chart_module.Receiver

import android.content.Context
import android.content.Intent
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.event.NotificationClickEvent
import com.cstec.administrator.chart_module.Activity.ChatActivity
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.RouterUtils


class NotificationClickEventReceiver(private val mContext: Context) {

    init {
        //注册接收消息事件
        JMessageClient.registerEventReceiver(this)
    }

    /**
     * 收到消息处理
     * @param notificationClickEvent 通知点击事件
     */
    fun onEvent(notificationClickEvent: NotificationClickEvent?) {
        if (null == notificationClickEvent) {
            return
        }
        val msg = notificationClickEvent.message
        if (msg != null) {
            val targetId = msg.targetID
            val appKey = msg.fromAppKey
            val type = msg.targetType
            val conv: Conversation
            val notificationIntent = Intent(mContext, ChatActivity::class.java)
            if (type == ConversationType.single) {
                conv = JMessageClient.getSingleConversation(targetId, appKey)
                notificationIntent.putExtra(RouterUtils.Chat_Module.Chat_TARGET_ID, targetId)
                notificationIntent.putExtra(RouterUtils.Chat_Module.Chat_App_Key, appKey)
            } else {
                conv = JMessageClient.getGroupConversation(java.lang.Long.parseLong(targetId))
                notificationIntent.putExtra(RouterUtils.Chat_Module.Chat_GROUP_ID, java.lang.Long.parseLong(targetId))
            }
            notificationIntent.putExtra(RouterUtils.Chat_Module.Chat_CONV_TITLE, conv.title)
            conv.resetUnreadCount()
            //        notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.putExtra("fromGroup", false)
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            mContext.startActivity(notificationIntent)
        }
    }
}

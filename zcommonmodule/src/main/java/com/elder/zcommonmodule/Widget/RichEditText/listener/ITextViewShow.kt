package com.elder.zcommonmodule.Widget.RichEditText.listener

import android.content.Context
import android.text.method.MovementMethod

import com.elder.zcommonmodule.Widget.RichEditText.model.TopicModel
import com.elder.zcommonmodule.Widget.RichEditText.model.UserModel
import com.elder.zcommonmodule.Widget.RichEditText.span.ClickAtUserSpan
import com.elder.zcommonmodule.Widget.RichEditText.span.ClickTopicSpan
import com.elder.zcommonmodule.Widget.RichEditText.span.LinkSpan

/**
 * textview 显示接口
 * Created by guoshuyu on 2017/8/22.
 */

open interface ITextViewShow {

    var text: CharSequence

    fun setMovementMethod(movementMethod: MovementMethod)

    fun setAutoLinkMask(flag: Int)

    fun getCustomClickAtUserSpan(context: Context, userModel: UserModel, color: Int, spanClickCallBack: SpanAtUserCallBack): ClickAtUserSpan?

    fun getCustomClickTopicSpan(context: Context, topicModel: TopicModel, color: Int, spanTopicCallBack: SpanTopicCallBack): ClickTopicSpan?

    fun getCustomLinkSpan(context: Context, url: String, color: Int, spanUrlCallBack: SpanUrlCallBack): LinkSpan?

    fun emojiSize(): Int

    fun verticalAlignment(): Int
}

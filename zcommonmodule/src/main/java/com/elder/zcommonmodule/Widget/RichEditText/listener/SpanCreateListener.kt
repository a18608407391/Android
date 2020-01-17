package com.elder.zcommonmodule.Widget.RichEditText.listener

import android.content.Context

import com.elder.zcommonmodule.Widget.RichEditText.model.TopicModel
import com.elder.zcommonmodule.Widget.RichEditText.model.UserModel
import com.elder.zcommonmodule.Widget.RichEditText.span.ClickAtUserSpan
import com.elder.zcommonmodule.Widget.RichEditText.span.ClickTopicSpan
import com.elder.zcommonmodule.Widget.RichEditText.span.LinkSpan

/**
 * Created by guoshuyu on 2017/8/31.
 */

open interface SpanCreateListener {

    fun getCustomClickAtUserSpan(context: Context, userModel: UserModel, color: Int, spanClickCallBack: SpanAtUserCallBack): ClickAtUserSpan

    fun getCustomClickTopicSpan(context: Context, topicModel: TopicModel, color: Int, spanTopicCallBack: SpanTopicCallBack): ClickTopicSpan

    fun getCustomLinkSpan(context: Context, url: String, color: Int, spanUrlCallBack: SpanUrlCallBack): LinkSpan
}

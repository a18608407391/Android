package org.cs.tec.library.binding.command.ViewAdapter.Banner

import android.databinding.ObservableField


/**
 * @Author : 郭富东
 * @Date ：2018/8/3 - 11:02
 * @Email：878749089@qq.com
 * @descriptio：首页视频列表item数据实体类
 */
data class VideoItemData(var tagName: ObservableField<String> = ObservableField(),//视频标签
                         var type: ObservableField<Int> = ObservableField(1),//item类型
                         var videoName: ObservableField<String> = ObservableField(),//视频的名字
                         var videoImg: ObservableField<String> = ObservableField(),//视频图片
                         var videoLink: ObservableField<String> = ObservableField(),//视频链接
                         var title: ObservableField<String> = ObservableField(""),//标题
                         var titleType: ObservableField<Int> = ObservableField(-1)
) : MultiItemEntity {
    override fun getItemType(): Int {
        return this.type.get()!!
    }
    constructor(titleType: Int) : this(ObservableField(""), ObservableField(0), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(titleType))

}
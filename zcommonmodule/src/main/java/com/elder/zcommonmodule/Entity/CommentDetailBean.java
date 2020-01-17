package com.elder.zcommonmodule.Entity;

import java.util.List;

/**
 * Created by moos on 2018/4/20.
 */

public class CommentDetailBean {
    public int id;
    public String memberName;
    public String memberImages;
    public String commentContent;
    public String imgId;
    public int replyCommentCount;
    public String createDate;
    public List<CommentDetailBean> dynamicCommentList;
    public String replyMemberImages;
    public int isLike = 0;
    public String memberId = "";
    public String replyCommentId = "";           //二级评论评论ID
    public String replyMemberId = "";            //二级评论被评论ID
    public String replyMemberName = "";           //评论人名字


    public CommentDetailBean(String content) {
        this.commentContent = content;
    }


    public CommentDetailBean(String nickName, String content) {
        this.memberName = nickName;
        this.commentContent = content;
    }
}

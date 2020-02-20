package com.cstec.administrator.social.ViewAdapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.elder.zcommonmodule.Entity.CommentDetailBean;
import com.cstec.administrator.social.Entity.ReplyDetailBean;
import com.cstec.administrator.social.R;
import com.elder.zcommonmodule.LocalUtilsKt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Author: Moos
 * E-mail: moosphon@gmail.com
 * Date:  18/4/20.
 * Desc: 评论与回复列表的适配器
 */

public class CommentExpandAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CommentExpandAdapter";
    private List<CommentDetailBean> commentBeanList;
    private List<ReplyDetailBean> replyBeanList;
    private Context context;
    private int pageIndex = 1;
    long CurrentClickTime = 0;

    public void setLoadData(List<CommentDetailBean> commentBeanList) {


        for (int i = 0; i < commentBeanList.size(); i++) {
            CommentDetailBean bena = commentBeanList.get(i);
            if (bena.replyCommentCount > 5) {
                CommentDetailBean bean = new CommentDetailBean("");
                bean.id = -1;
                bena.dynamicCommentList.add(bean);
                commentBeanList.set(i, bena);
            }
//                if (commentBeanList.get(i).dynamicCommentList.get(j).replyCommentCount > 2) {
//                    CommentDetailBean bean = new CommentDetailBean("");
//                    bean.id = -1;
//                    CommentDetailBean s = configList.get(i);
//                    s.dynamicCommentList.add(bean);
//                    configList.set(i, s);
//                }
        }
        this.commentBeanList = commentBeanList;
        notifyDataSetChanged();
    }

    public CommentExpandAdapter(Context context) {
        this.context = context;
    }

    public CommentExpandAdapter(Context context, List<CommentDetailBean> commentBeanList) {
        this.context = context;
        this.commentBeanList = commentBeanList;
    }

    @Override
    public int getGroupCount() {
        if (commentBeanList != null) {
            return commentBeanList.size();
        } else {
            return 0;
        }

    }

    @Override
    public int getChildrenCount(int i) {
        if (commentBeanList.get(i).dynamicCommentList == null) {
            return 0;
        } else {
            return commentBeanList.get(i).dynamicCommentList.size() > 0 ? commentBeanList.get(i).dynamicCommentList.size() : 0;
        }

    }

    @Override
    public Object getGroup(int i) {
        return commentBeanList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return commentBeanList.get(i).dynamicCommentList.get(i1);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public View getGroupView(final int groupPosition, boolean isExpand, View convertView, ViewGroup viewGroup) {
        final GroupHolder groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, viewGroup, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        Glide.with(context).load(LocalUtilsKt.getImageUrl(commentBeanList.get(groupPosition).memberImages))
                .into(groupHolder.logo);

        groupHolder.tv_name.setText(commentBeanList.get(groupPosition).memberName);
        if (commentBeanList.get(groupPosition).isLike == 1) {
            groupHolder.iv_like.setImageResource(R.drawable.like_icon);
        } else {
            groupHolder.iv_like.setImageResource(R.drawable.like_default);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long lo = sdf.parse(commentBeanList.get(groupPosition).createDate).getTime();
            Long k = (System.currentTimeMillis() - lo) / 1000;
            if (k < 60) {
                groupHolder.tv_time.setText("刚刚");
            } else if (k < 3600) {
                groupHolder.tv_time.setText((k / 60) + "分钟前");
            } else if (k < 3600 * 24) {
                groupHolder.tv_time.setText((k / 3600) + "小时前");
            } else if (k > 3600 * 24) {
                groupHolder.tv_time.setText((k / 3600 / 24) + "天前");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        groupHolder.tv_time.setText(commentBeanList.get(groupPosition).createDate);
        groupHolder.tv_content.setText(commentBeanList.get(groupPosition).commentContent);
        groupHolder.logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupImgNet != null) {
                    groupImgNet.GroupImgNetWork(commentBeanList.get(groupPosition));
                }
            }
        });
        groupHolder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis() - CurrentClickTime < 1000) {
                    return;
                } else {
                    CurrentClickTime = System.currentTimeMillis();
                }
                CommentDetailBean t = commentBeanList.get(groupPosition);
                if (t.isLike == 1) {
                    t.isLike = 0;
//                    groupHolder.iv_like.setImageResource(R.drawable.like_icon);
                } else {
                    t.isLike = 1;
//                    groupHolder.iv_like.setImageResource(R.drawable.like_default);
                }
                commentBeanList.set(groupPosition, t);
                if (groupNet != null) {
                    groupNet.sendGroupNetWork(t);
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public ClickNetListener childNet = null;

    public GroupClickNetListener groupNet = null;
    public GroupImgClickNetListener groupImgNet = null;

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean b, View convertView, ViewGroup viewGroup) {
        final ChildHolder childHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_reply_item_layout, viewGroup, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        String replyUser = commentBeanList.get(groupPosition).dynamicCommentList.get(childPosition).memberName;
        String replyMemberName = commentBeanList.get(groupPosition).dynamicCommentList.get(childPosition).replyMemberName;
        int id = commentBeanList.get(groupPosition).dynamicCommentList.get(childPosition).id;
        childHolder.tv_return.setVisibility(View.GONE);
        childHolder.tv_reply.setVisibility(View.GONE);
        childHolder.tv_name.setVisibility(View.GONE);

        if (id == -1) {
            childHolder.tv_content.setText("查看更多评论>>");
//            childHolder.iv_like.setVisibility(View.GONE);
        } else {
            String content = "";
            String replyName = "";
            String memberName = "";
            String returnStr = "";
//            SpannableStringBuilder spannableStringBuilder1 = null;

            if (replyMemberName == null || replyMemberName.isEmpty() || replyMemberName.equalsIgnoreCase(replyUser)) {
                if (!TextUtils.isEmpty(replyUser)) {
//                    childHolder.tv_name.setText(replyUser + ":");
                    replyName = replyUser + ": ";
                } else {
//                    childHolder.tv_name.setText("无名" + ":");
                    replyName = "无名" + ": ";
                }
            } else {
//                childHolder.tv_name.setText(replyUser);
                replyName = replyUser;
                returnStr = " 回复 ";
                if (!TextUtils.isEmpty(replyUser)) {
//                    childHolder.tv_reply.setText(replyMemberName + ":");
                    memberName = replyMemberName + "：";
                } else {
//                    childHolder.tv_reply.setText("无名" + ":");
                    memberName = "无名" + "：";
                }
            }
            content = commentBeanList.get(groupPosition).dynamicCommentList.get(childPosition).commentContent;
            SpannableStringBuilder spannableStringBuilder1 = new SpannableStringBuilder(replyName + returnStr + memberName + content);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#3FC5C9"));
            spannableStringBuilder1.setSpan(foregroundColorSpan, 0, replyName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            ForegroundColorSpan foregroundColorSpan1 = new ForegroundColorSpan(Color.parseColor("#3FC5C9"));
            spannableStringBuilder1.setSpan(foregroundColorSpan1, replyName.length() + returnStr.length() - 1, replyName.length() + returnStr.length() + memberName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            childHolder.tv_content.setText(spannableStringBuilder1);
//            childHolder.iv_like.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    CommentDetailBean bean = commentBeanList.get(groupPosition).dynamicCommentList.get(childPosition);
//                    if (bean.isLike == 1) {
//                        bean.isLike = 0;
//                    } else {
//                        bean.isLike = 1;
//                    }
//                    commentBeanList.get(groupPosition).dynamicCommentList.set(childPosition, bean);
//                    if (childNet != null) {
//                        childNet.sendNetWork(bean);
//                    }
//                    notifyDataSetChanged();
//                }
//            });


//            spannableStringBuilder1.setSpan(new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                }
//
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setUnderlineText(false);
//                }
//            }, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


//            childHolder.iv_like.setVisibility(View.VISIBLE);
//            if (commentBeanList.get(groupPosition).dynamicCommentList.get(childPosition).isLike == 0) {
//                childHolder.iv_like.setImageResource(R.drawable.like_default);
//            } else if (commentBeanList.get(groupPosition).dynamicCommentList.get(childPosition).isLike == 1) {
//                childHolder.iv_like.setImageResource(R.drawable.like_icon);
//            }


//            childHolder.tv_content.setText(commentBeanList.get(groupPosition).dynamicCommentList.get(childPosition).commentContent);
        }
        return convertView;
    }


    public interface ClickNetListener {

        void sendNetWork(CommentDetailBean bean);

    }

    public interface GroupClickNetListener {

        void sendGroupNetWork(CommentDetailBean bean);

    }

    public interface GroupImgClickNetListener {

        void GroupImgNetWork(CommentDetailBean bean);

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder {
        private CircleImageView logo;
        private TextView tv_name, tv_content, tv_time;
        private ImageView iv_like;

        public GroupHolder(View view) {
            logo = (CircleImageView) view.findViewById(R.id.comment_item_logo);
            tv_content = (TextView) view.findViewById(R.id.comment_item_content);
            tv_name = (TextView) view.findViewById(R.id.comment_item_userName);
            tv_time = (TextView) view.findViewById(R.id.comment_item_time);
            iv_like = (ImageView) view.findViewById(R.id.comment_item_like);
        }
    }

    private class ChildHolder {
        private TextView tv_name, tv_content, tv_return, tv_reply;
//        private ImageView iv_like;


        public ChildHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.reply_item_user);
            tv_content = (TextView) view.findViewById(R.id.reply_item_content);
            tv_reply = view.findViewById(R.id.second_reply_item_user);
            tv_return = view.findViewById(R.id.return_talk);
//            iv_like = view.findViewById(R.id.reply_comment_item_like);
        }
    }


    /**
     * by moos on 2018/04/20
     * func:评论成功后插入一条数据
     *
     * @param commentDetailBean 新的评论数据
     */
    public void addTheCommentData(CommentDetailBean commentDetailBean) {
        if (commentDetailBean != null) {

            commentBeanList.add(commentDetailBean);
            notifyDataSetChanged();
        } else {
            throw new IllegalArgumentException("评论数据为空!");
        }

    }

    /**
     * by moos on 2018/04/20
     * func:回复成功后插入一条数据
     *
     * @param replyDetailBean 新的回复数据
     */
    public void addTheReplyData(CommentDetailBean replyDetailBean, int groupPosition) {
        if (replyDetailBean != null) {
            if (commentBeanList.get(groupPosition).dynamicCommentList != null) {
                if (commentBeanList.get(groupPosition).dynamicCommentList.size() > 5) {
                    commentBeanList.get(groupPosition).dynamicCommentList.add(commentBeanList.get(groupPosition).dynamicCommentList.size() - 2, replyDetailBean);
                } else {
                    commentBeanList.get(groupPosition).dynamicCommentList.add(replyDetailBean);
                }
            } else {
                List<CommentDetailBean> replyList = new ArrayList<>();
                replyList.add(replyDetailBean);
                commentBeanList.get(groupPosition).dynamicCommentList = replyList;
            }
            notifyDataSetChanged();
        } else {
            throw new IllegalArgumentException("回复数据为空!");
        }

    }

    /**
     * by moos on 2018/04/20
     * func:添加和展示所有回复
     *
     * @param replyBeanList 所有回复数据
     * @param groupPosition 当前的评论
     */

    public void addReplyList(List<CommentDetailBean> replyBeanList, int groupPosition) {
        if (commentBeanList.get(groupPosition).dynamicCommentList != null) {
            commentBeanList.get(groupPosition).dynamicCommentList.clear();
            commentBeanList.get(groupPosition).dynamicCommentList.addAll(replyBeanList);
        } else {
            commentBeanList.get(groupPosition).dynamicCommentList = replyBeanList;
        }

        notifyDataSetChanged();
    }

}

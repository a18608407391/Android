package com.cstec.administrator.chart_module.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstec.administrator.chart_module.Even.ImageEvent;
import com.cstec.administrator.chart_module.Model.AppBean;
import com.cstec.administrator.chart_module.R;
import com.zk.library.Base.BaseApplication;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class AppsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<AppBean> mDdata = new ArrayList<AppBean>();

    public AppsAdapter(Context context, ArrayList<AppBean> data) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        if (data != null) {
            this.mDdata = data;
        }
    }

    @Override
    public int getCount() {
        return mDdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mDdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_app, null);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AppBean appBean = mDdata.get(position);
        if (appBean != null) {
            viewHolder.iv_icon.setBackgroundResource(appBean.getIcon());
            viewHolder.tv_name.setText(appBean.getFuncName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (appBean.getFuncName().equals("图片")) {
//                        RxBus.Companion.getDefault().post(new ImageEvent(BaseApplication.Companion.getIMAGE_MESSAGE()));
                        EventBus.getDefault().post(new ImageEvent(BaseApplication.Companion.getIMAGE_MESSAGE()));
                    } else if (appBean.getFuncName().equals("拍摄")) {
//                        RxBus.Companion.getDefault().post(new ImageEvent(BaseApplication.Companion.getTAKE_PHOTO_MESSAGE()));
                        EventBus.getDefault().post(new ImageEvent(BaseApplication.Companion.getTAKE_PHOTO_MESSAGE()));
                    }else if (appBean.getFuncName().equals("位置")) {
//                        RxBus.Companion.getDefault().post(new ImageEvent(BaseApplication.Companion.getTAKE_LOCATION()));
                        EventBus.getDefault().post(new ImageEvent(BaseApplication.Companion.getTAKE_LOCATION()));
                    }else if (appBean.getFuncName().equals("文件")) {
//                        RxBus.Companion.getDefault().post(new ImageEvent(BaseApplication.Companion.getFILE_MESSAGE()));
                        EventBus.getDefault().post(new ImageEvent(BaseApplication.Companion.getFILE_MESSAGE()));
                    }else if (appBean.getFuncName().equals("视频")) {
//                        RxBus.Companion.getDefault().post(new ImageEvent(BaseApplication.Companion.getTACK_VIDEO()));
                        EventBus.getDefault().post(new ImageEvent(BaseApplication.Companion.getTACK_VIDEO()));
                    }else if (appBean.getFuncName().equals("语音")) {
//                        RxBus.Companion.getDefault().post(new ImageEvent(BaseApplication.Companion.getTACK_VOICE()));
                        EventBus.getDefault().post(new ImageEvent(BaseApplication.Companion.getTACK_VOICE()));
                    }else if (appBean.getFuncName().equals("名片")) {
//                        RxBus.Companion.getDefault().post(new ImageEvent(BaseApplication.Companion.getBUSINESS_CARD()));
                        EventBus.getDefault().post(new ImageEvent(BaseApplication.Companion.getBUSINESS_CARD()));
                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_name;
    }
}
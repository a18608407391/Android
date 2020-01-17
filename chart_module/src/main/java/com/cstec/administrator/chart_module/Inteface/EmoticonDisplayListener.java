package com.cstec.administrator.chart_module.Inteface;

import android.view.ViewGroup;

import com.cstec.administrator.chart_module.Adapter.EmoticonsAdapter;


public interface EmoticonDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, T t, boolean isDelBtn);
}

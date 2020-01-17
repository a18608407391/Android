package com.cstec.administrator.chart_module.Inteface;

import android.view.View;
import android.view.ViewGroup;

import com.cstec.administrator.chart_module.Data.PageEntity;


public interface PageViewInstantiateListener<T extends PageEntity> {

    View instantiateItem(ViewGroup container, int position, T pageEntity);
}

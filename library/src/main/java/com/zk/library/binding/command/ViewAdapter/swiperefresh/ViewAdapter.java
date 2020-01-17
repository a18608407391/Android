package com.zk.library.binding.command.ViewAdapter.swiperefresh;

import android.databinding.BindingAdapter;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;

import org.cs.tec.library.binding.command.BindingCommand;
import org.cs.tec.library.binding.command.BindingFunction;


/**
 * Created by goldze on 2017/6/18.
 */
public class ViewAdapter {
    @BindingAdapter({"onRefreshCommand"})
    public static void onRefreshCommand(final SwipeRefreshLayout swipeRefreshLayout, final BindingCommand onRefreshCommand) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (onRefreshCommand != null) {
                    onRefreshCommand.execute();
                }
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 3000);
            }
        });
    }

    @BindingAdapter({"RefreshCommand"})
    public static void RefreshCommand(final SwipeRefreshLayout swipeRefreshLayout, boolean flag) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(flag);
        }

    }

}

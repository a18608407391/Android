package com.elder.amoski.Service.Mina;

import android.util.Log;

import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.cs.tec.library.Bus.RxBus;

/**
 * Created by Administrator on 2018/1/10 0010.
 */

public class HeartBeatListener implements IoServiceListener {
    public NioSocketConnector connector;
    public HeartBeatListener(NioSocketConnector mConnection) {
        this.connector = mConnection;
    }

    @Override
    public void serviceActivated(IoService ioService) throws Exception {
        Log.e("result","服务活动");
    }

    @Override
    public void serviceIdle(IoService ioService, IdleStatus idleStatus) throws Exception {
        Log.e("result","服务空闲");
    }

    @Override
    public void serviceDeactivated(IoService ioService) throws Exception {
        Log.e("result","服务活动123");
    }

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {
        Log.e("result","创建Session");
        RxBus.Companion.getDefault().post("MinaConnected");
    }

    @Override
    public void sessionClosed(IoSession ioSession) throws Exception {
        Log.e("result","Session关闭");
    }

    @Override
    public void sessionDestroyed(IoSession ioSession) throws Exception {
        Log.e("result","Session销毁");
    }

}

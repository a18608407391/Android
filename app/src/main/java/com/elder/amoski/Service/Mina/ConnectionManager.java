package com.elder.amoski.Service.Mina;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.elder.zcommonmodule.Entity.SoketBody.BasePacketReceive;
import com.google.gson.Gson;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.cs.tec.library.Bus.RxBus;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;


/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class ConnectionManager {
    private static final String BROADCAST_ACTION = "com.commonlibrary.mina";
    private static final String MESSAGE = "message";
    private ConnectionConfig mConfig;
    private WeakReference<Context> mContext;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    public ConnectionManager(ConnectionConfig config) {
        this.mConfig = config;
        this.mContext = new WeakReference<Context>(config.getContext());
        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.getFilterChain().addLast("logger", new LoggingFilter());
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        MyTextCode code = new MyTextCode(Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue());
        code.setDecoderMaxLineLength(1024 * 1024);
        code.setEncoderMaxLineLength(1024 * 1024);

        mConnection.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(code));


        mConnection.setHandler(new DefaultHandler(mContext.get()));
        mConnection.setDefaultRemoteAddress(mAddress);
        mConnection.addListener(new HeartBeatListener(mConnection));
    }

    public boolean connect() {
        try {
            ConnectFuture future = mConnection.connect();
            future.awaitUninterruptibly();
            mSession = future.getSession();
            SessionManager.getInstance().setSeesion(mSession);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return mSession != null ? true : false;
    }

    public void disConnection() {
        mConnection.dispose();
        mConnection = null;
        mSession = null;
        mAddress = null;
        mContext = null;
    }

    private static class DefaultHandler extends IoHandlerAdapter {
        private final Context mContext;

        public DefaultHandler(Context context) {
            this.mContext = context;
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            super.sessionIdle(session, status);
            Log.e("result", "idle");
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            super.exceptionCaught(session, cause);
            Log.e("result", "异常" + cause.getMessage() + session.toString());
        }

        @Override
        public void inputClosed(IoSession session) throws Exception {
            super.inputClosed(session);
            Log.e("result", "输入关闭");
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);
            Log.e("result", "创建");
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            RxBus.Companion.getDefault().post("MinaConnected");
            SessionManager.getInstance().setSeesion(session);
            //将我们的session保存到我们的session manager类中， 从而可以发送消息到服务器
//            SessionManager.getInstance().writeToServer("AMC" + BaseApplication.getmApp().getMesh().name);
        }


        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);

//            if (NetworkUtil.INSTANCE.isNetworkAvailable(UtilsKt.getContext())) {
//                Log.e("result","执行了这里1");
//            } else {
//                Log.e("result","执行了这里");
            RxBus.Companion.getDefault().post("MINA_FORCE_CLOSE");
//            }


            Log.e("result", "关闭");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            Log.e("result", "当前数据" + message.toString());
            if (message.toString().isEmpty() || message.toString() == null) {
                return;
            }

            if (message.toString().split("body").length > 2 && message.toString().split("type").length > 2) {
                BasePacketReceive receive = null;
                if (message.toString().contains("redisData")) {
                    String[] s = message.toString().split("body");
                    String k = s[0] + "body" + s[1];
                    String d = k.substring(0, k.length() - 3);
                    receive = JSON.parseObject(d, BasePacketReceive.class);
                } else {
                    String[] sp = message.toString().split("\\}");
                    receive = JSON.parseObject(sp[0] + "}" + sp[1] + "}", BasePacketReceive.class);
                }
                if (receive != null) {
                    RxBus.Companion.getDefault().post(receive);
                }
                if (receive.getCode() == 0) {
                    if (receive != null) {

                    } else {
//                    Toast.makeText(mContext,UtilsKt.getString(R.string.data_error), Toast.LENGTH_SHORT).show();
                    }
                } else if (receive.getCode() == 10007) {
//                Toast.makeText(mContext, receive.getMsg(), Toast.LENGTH_SHORT).show();
                } else {
//                Toast.makeText(mContext, receive.getMsg(), Toast.LENGTH_SHORT).show();
                }
            } else {
                BasePacketReceive receive = JSON.parseObject(message.toString(), BasePacketReceive.class);
                if (receive != null) {
                    RxBus.Companion.getDefault().post(receive);
                }
                if (receive.getCode() == 0) {
                    if (receive != null) {

                    } else {
//                    Toast.makeText(mContext,UtilsKt.getString(R.string.data_error), Toast.LENGTH_SHORT).show();
                    }
                } else if (receive.getCode() == 10007) {
//                Toast.makeText(mContext, receive.getMsg(), Toast.LENGTH_SHORT).show();
                } else {
//                Toast.makeText(mContext, receive.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }


//            BasePacketReceive obj = JSON.parseObject(message.toString(), BasePacketReceive.class);
//            if (obj.getType() == 1000) {
//                Soket so = new Soket();
//                so.setType(1000);
//                SessionManager.getInstance().writeToServer(new Gson().toJson(so) + "\r\n");
//            } else {
//                if (mContext != null) {
//                    Intent intent = new Intent(BROADCAST_ACTION);
//                    intent.putExtra(MESSAGE, message.toString());
//                    mContext.sendBroadcast(intent);
//                }
//            }
            super.messageReceived(session, message);
        }

        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
        }
    }
}

package com.elder.amoski.Service.Mina;

import org.apache.mina.core.session.IoSession;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class SessionManager {
    private static SessionManager mInstance = null;
    //最终与服务器进行通信的对象
    private IoSession mSession;
    public static SessionManager getInstance() {
        if (mInstance == null)
        {
            synchronized (SessionManager.class) {
                if (mInstance == null) {
                    mInstance = new SessionManager();
                }
            }
        }
        return mInstance;
    }

    public void setSeesion(IoSession session){
        this.mSession = session;
    }
    public SessionManager() {
    }

    public SessionManager(IoSession mSession) {
        this.mSession = mSession;
    }

    /**
     * 将对象写到服务端
     * @param msg
     */
    public IoSession writeToServer(Object msg)
    {
        if (mSession != null) {
            mSession.write(msg);
        }
        return mSession;
    }

    public void closeSession()
    {
        if (mSession != null)
            mSession.closeOnFlush();
    }
    public void removeSession()
    {
        this.mSession = null;
    }
}

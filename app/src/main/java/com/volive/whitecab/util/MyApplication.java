package com.volive.whitecab.util;

import android.app.Application;


/**
 * Created by volive on 4/20/2017.
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;
    MyObservab myObservab;

    @Override
    public void onCreate() {
        super.onCreate();

        myObservab = new MyObservab();
        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public MyObservab getObserver() {
        return myObservab;
    }
}

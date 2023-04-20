package com.ble.blescansdk.ble.proxy;

import com.ble.blescansdk.ble.proxy.request.ConnectRequest;
import com.ble.blescansdk.ble.proxy.request.NotifyRequest;
import com.ble.blescansdk.ble.proxy.request.ScanRequest;
import com.ble.blescansdk.ble.proxy.request.WriteRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by LiuLei on 2017/9/1.
 */

public class RequestProxy implements InvocationHandler {
    private static final String TAG = "RequestProxy";

    private RequestProxy() {
    }

    private Object receiver;

    public static RequestProxy newProxy() {
        return new RequestProxy();
    }

    //Bind the delegate object and return the proxy class
    public Object bindProxy(Object tar) {
        this.receiver = tar;
        //绑定委托对象，并返回代理类
        Rproxy.init(ScanRequest.class, ConnectRequest.class, WriteRequest.class, RequestImpl.class,
                NotifyRequest.class);
        return Proxy.newProxyInstance(
                tar.getClass().getClassLoader(),
                tar.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(receiver, args);
    }
}

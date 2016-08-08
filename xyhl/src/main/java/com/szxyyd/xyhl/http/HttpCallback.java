package com.szxyyd.xyhl.http;

import java.io.IOException;

/**
 *
 * Created by fq on 2016/8/4.
 */
public abstract class HttpCallback {
    public  abstract void onStart();
    public  abstract void onFinsh();
    public  abstract void onSuccess(String data);
    public  abstract void onFail(String msg);
}

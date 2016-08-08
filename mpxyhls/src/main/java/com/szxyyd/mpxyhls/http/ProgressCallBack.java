package com.szxyyd.mpxyhls.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.lang.ref.WeakReference;

/**
 * Created by fq on 2016/8/4.
 */
public class ProgressCallBack extends HttpCallback{
    private ProgressCallBackListener mProgressCallBackListener;
    private WeakReference<Context> mActivity;
    //    是否能取消请求
    private boolean cancel;
    //    加载框可自己定义
    private ProgressDialog pd;
    public ProgressCallBack(ProgressCallBackListener mProgressCallBackListener, Context context) {
        this.mProgressCallBackListener = mProgressCallBackListener;
        this.mActivity = new WeakReference<>(context);
        this.cancel = true;
        initProgressDialog();
    }
    /**
     * 初始化加载框
     */
    private void initProgressDialog() {
        Context context = mActivity.get();
        if (pd == null && context != null) {
            pd = new ProgressDialog(context);
            pd.setCancelable(cancel);
            if (cancel) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dismissProgressDialog();
                    }
                });
            }
        }
    }
    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        Context context = mActivity.get();
        if (pd == null || context == null) return;
        if (!pd.isShowing()) {
            pd.show();
        }
    }

    /**
     * 隐藏
     */
    private void dismissProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    public void onStart() {
        showProgressDialog();
    }

    @Override
    public void onFinsh() {
        dismissProgressDialog();
    }

    @Override
    public void onSuccess(String data) {
        if(mProgressCallBackListener != null){
            mProgressCallBackListener.onSuccess(data);
        }
    }
    @Override
    public void onFail(String msg) {
        dismissProgressDialog();
    }
}

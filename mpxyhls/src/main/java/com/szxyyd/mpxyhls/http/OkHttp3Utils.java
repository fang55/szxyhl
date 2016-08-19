package com.szxyyd.mpxyhls.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.szxyyd.mpxyhls.activity.BaseApplication;
import com.szxyyd.mpxyhls.activity.Constant;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 类名称：OkHttp3Utils
 * 类描述：封装一个OkHttp3的获取类
 *
 */
public class OkHttp3Utils {
    private static OkHttpClient mOkHttpClient;
    //设置缓存目录
    private static File cacheDirectory = new File(BaseApplication.getInstance().getApplicationContext().getCacheDir().getAbsolutePath(), "hlsCache");
    private static okhttp3.Cache cache = new okhttp3.Cache(cacheDirectory, 10 * 1024 * 1024);
    private MyHandler mHandler;
    private static OkHttp3Utils okHttp3Utils;
    /**
     * 获取OkHttpClient对象
     */
    public  OkHttp3Utils() {
            Log.e("OkHttp3Utils","OkHttp3Utils");
            //同样okhttp3后也使用build设计模式
            mOkHttpClient = new OkHttpClient.Builder()
                    //设置一个自动管理cookies的管理器
                   // .cookieJar(new CookiesManager())
                    //添加拦截器
                    //.addInterceptor(new MyIntercepter())
                    //添加网络连接器
                    //.addNetworkInterceptor(new CookiesInterceptor(MyApplication.getInstance().getApplicationContext()))
                    //设置请求读写的超时时间
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(cache)
                    .build();
        mHandler = new MyHandler();
    }

    public static OkHttp3Utils getInstance() {
        if (okHttp3Utils == null) {
            okHttp3Utils = new OkHttp3Utils();
        }
        return okHttp3Utils;
    }
    /**
     * 拦截器
     */
    private static class MyIntercepter implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!isNetworkReachable(BaseApplication.getInstance().getApplicationContext())) {
                Toast.makeText(BaseApplication.getInstance().getApplicationContext(), "暂无网络", Toast.LENGTH_SHORT).show();
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)//无网络时只从缓存中读取
                        .build();
            }

            Response response = chain.proceed(request);
            if (isNetworkReachable(BaseApplication.getInstance().getApplicationContext())) {
                int maxAge = 60 * 60; // 有网络时 设置缓存超时时间1个小时
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }
   /* *//**
     * 自动管理Cookies
     *//*
    private static class CookiesManager implements CookieJar {
        private final PersistentCookieStore cookieStore = new PersistentCookieStore(MyApplication.getInstance().getApplicationContext());

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    cookieStore.add(url, item);
                }
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url);
            return cookies;
        }
    }*/

    /**
     * 判断网络是否可用
     *
     * @param context Context对象
     */
    public static Boolean isNetworkReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.isAvailable());
    }
    /**
     * 异步http请求
     * @param builder
     * @param callback
     */
    public void callAsyn(HttpBuilder builder, final HttpCallback callback) {
        final Request request = new Request.Builder().url(builder.getGetUrl()).build();
        Call call = mOkHttpClient.newCall(request);
        if(callback != null){
            callback.onStart();
        }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    HttpContent content = new HttpContent();
                    content.callback = callback;
                    content.e = e;
                    mHandler.obtainMessage(HTTP_FAIL, content).sendToTarget();
                    callback.onFinsh();
                   }
                }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    HttpContent content = new HttpContent();
                    content.callback = callback;
                    content.msg = response.body().string();
                    mHandler.obtainMessage(HTTP_SUCCESS, content).sendToTarget();
                    callback.onFinsh();
                 }
                }
        });
    }
    /**
     * 表单数据上传(文字+图片)
     */
   public void callAsynTextImageData(String url,Map<String,String> map,String imagePath,final HttpCallback callback){
       MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
       MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
       //遍历map中所有参数到builder
       for (String key : map.keySet()) {
           multipartBody.addFormDataPart(key, map.get(key));
       }
       if(!TextUtils.isEmpty(imagePath)){
           Log.e("BasicMessageFragment","callAsynTextImageData--imageUrl=="+imagePath);
           File file = new File(imagePath);
           multipartBody.addFormDataPart("icon",file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
       }
       RequestBody requestBody = multipartBody.build();
       Request request = new Request.Builder()
               .url(url)
               .post(requestBody)
               .build();
       Call call = mOkHttpClient.newCall(request);
       if(callback != null){
           callback.onStart();
       }
       call.enqueue(new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
               if (callback != null) {
                   HttpContent content = new HttpContent();
                   content.callback = callback;
                   content.e = e;
                   content.msg = e.getMessage();
                   mHandler.obtainMessage(HTTP_FAIL, content).sendToTarget();
                   callback.onFinsh();
               }
           }
           @Override
           public void onResponse(Call call, Response response) throws IOException {
               if (callback != null) {
                   HttpContent content = new HttpContent();
                   content.callback = callback;
                   content.msg = response.body().string();
                   mHandler.obtainMessage(HTTP_SUCCESS, content).sendToTarget();
                   callback.onFinsh();
               }
           }
       });
   }
    class MyHandler extends Handler {
        public MyHandler() {
            super(Looper.getMainLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            HttpContent content = (HttpContent)msg.obj;
            switch (msg.what) {
                case HTTP_SUCCESS:
                    content.callback.onSuccess(content.msg);
                    break;
                case HTTP_FAIL:
                    if (content.e instanceof SocketTimeoutException) {
                        Toast.makeText(BaseApplication.getInstance(), "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
                    } else if (content.e instanceof ConnectException) {
                        Toast.makeText(BaseApplication.getInstance(), "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ProgressSubscriber","error:==="+content.e.getMessage());
                    }
                    content.callback.onFail(content.msg);
                    break;
            }
        }
    }
    class HttpContent {
        public int code;
        public IOException e;
        public String msg;
        public HttpCallback callback;
    }
    private static final int HTTP_SUCCESS = 200;
    private static final int HTTP_FAIL = 300;
}

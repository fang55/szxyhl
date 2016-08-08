package com.szxyyd.xyhl.activity;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szxyyd.xyhl.R;
import com.szxyyd.xyhl.http.HttpBuilder;
import com.szxyyd.xyhl.http.OkHttp3Utils;
import com.szxyyd.xyhl.http.ProgressCallBack;
import com.szxyyd.xyhl.http.ProgressCallBackListener;
import com.szxyyd.xyhl.modle.Cst;
import com.szxyyd.xyhl.modle.User;
import com.szxyyd.xyhl.utils.ExampleUtil;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 登录界面
 *
 * @author fq
 * @deta 2016/5/17
 */
public class LoginActivity extends Activity implements OnClickListener {
    private Button btn_login;  //登录
    private Button btn_weixin;
    private Button btn_friends;
    private Button btn_qq;
    private TextView tv_forget_password; //忘记密码
    private TextView tv_register; //注册
    private EditText et_phone;  //手机
    private EditText et_password; //密码
    private TextView tv_title;
    private Button btn_back;
    public static boolean isForeground = false;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.TUISONG:
                    Log.e("LoginActivity", "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                default:
                    break;
            }
            Log.e("LoginActivity", "Unhandled msg - " + msg.what);
        }

        ;
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();
        registerMessageReceiver();
    }
    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                Log.e("LoginActivity","MessageReceiver--messge==="+messge);
                Log.e("LoginActivity","MessageReceiver--extras==="+extras);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }
    private void setCostomMsg(String msg){
         Toast.makeText(LoginActivity.this,"msg=="+msg,Toast.LENGTH_SHORT).show();
    }
    /**
     * 初始化
     */
    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_weixin = (Button) findViewById(R.id.btn_weixin);
        btn_friends = (Button) findViewById(R.id.btn_microblog);
        btn_qq = (Button) findViewById(R.id.btn_qq);
        tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
        tv_register = (TextView) findViewById(R.id.tv_register);
    }

    /**
     * 点击事件
     */
    private void initEvent() {
        btn_back.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_weixin.setOnClickListener(this);
        btn_friends.setOnClickListener(this);
        btn_qq.setOnClickListener(this);
        tv_forget_password.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        String usr = preferences.getString("usr", "");
        String password = preferences.getString("password", "");
        Constant.cstId = preferences.getString("cstId", "");
        Constant.usrId = preferences.getString("userid","");
        Log.e("LoginActivity", "onResume--Constant.cstId==" + Constant.cstId);
        Log.e("LoginActivity", "onResume--Constant.usrId==" + Constant.usrId);
        Log.e("LoginActivity", "onResume--usr==" + usr);
        submitUserData(usr);
    }
    /**
     * 验证用户是否存在
     */
    private void submitUserData(String usr){
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.findUsrUrl);
        builder.put("usr",usr);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    String jsonData = json.getString("usr");
                    Type listType = new TypeToken<LinkedList<User>>() {}.getType();
                    Gson gson = new Gson();
                    List<User> list = gson.fromJson(jsonData, listType);
                    if(list.size() == 0){ //去注册
                        setContentView(R.layout.activity_login);
                        initView();
                        initEvent();
                    }else{  //直接进入首页
                        setAlias();
                        Intent hpIntent = new Intent(LoginActivity.this, HomePageActivity.class);
                        startActivity(hpIntent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this));
    }
    /**
     * 登录
     */
    private void submitLogin(){
        String phone = et_phone.getText().toString().trim();
        String passWord = et_password.getText().toString().trim();
        if (phone == null) {
            setCostomMsg("手机号码不能为空");
            return;
        }
        if (phone.length() < 11) {
            setCostomMsg("请输入正确的手机号码");
            return;
        }
        if (passWord == null) {
            setCostomMsg("密码不能为空");
            return;
        }
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.loginUrl);
        builder.put("usr",phone);
        builder.put("pwd",passWord);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String result) {
                parserLoginData(result);
            }
        },this));
    }

    /**
     * 解析登录数据
     * @param result
     */
    private void parserLoginData(String result){
        try {
            JSONObject json = new JSONObject(result);
            Gson gson = new Gson();
            if(json.isNull("type")){
                String jsonCst = json.getString("cst");
                Type cstType = new TypeToken<LinkedList<Cst>>() {}.getType();
                List<Cst> listCst = gson.fromJson(jsonCst, cstType);
                String jsonUser = json.getString("cst");
                Type userType = new TypeToken<LinkedList<User>>() {}.getType();
                List<User> listUser = gson.fromJson(jsonUser, userType);
                Cst cst = listCst.get(0);
                User user = listUser.get(0);
                Constant.usrId = user.getId();
                Constant.cstId = cst.getId();
                Constant.cityId = cst.getCity();
                Constant.cityName = cst.getCityName();
                editor.putString("cstId", Constant.cstId);
                editor.putString("usr", et_phone.getText().toString());
                editor.putString("password", et_password.getText().toString());
                editor.putString("icon", user.getIcon());
                editor.putString("userid", user.getId());
                editor.putString("nickname", user.getNickname());
                editor.commit();
                setAlias();
                Intent hpIntent = new Intent(LoginActivity.this, HomePageActivity.class);
                startActivity(hpIntent);
                finish();
            }else{
                setCostomMsg(json.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_login:
                submitLogin();
                break;
            case R.id.tv_forget_password:
                Intent fpIntent = new Intent(this, ForgetPasswordActivity.class);
                fpIntent.putExtra("phone", et_phone.getText().toString().trim());
                startActivity(fpIntent);
                break;
            case R.id.tv_register:
                Intent rsIntent = new Intent(this, RegisterActivity.class);
                startActivity(rsIntent);
                break;
            case R.id.btn_weixin:

                break;
            case R.id.btn_microblog:

                break;
            case R.id.btn_qq:

                break;
            default:
                break;
        }
    }
    /*极光推送别名*/
    private void setAlias() {
        String alias = Constant.usrId ;
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(LoginActivity.this,"错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ExampleUtil.isValidTagAndAlias(alias)) {
            Toast.makeText(LoginActivity.this,"错误", Toast.LENGTH_SHORT).show();
            return;
        }
        // 调用 Handler 来异步设置别名
        handler.sendMessage(handler.obtainMessage(Constant.TUISONG, alias));
    }
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.e("LoginActivity", logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.e("LoginActivity", logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    handler.sendMessageDelayed(handler.obtainMessage(Constant.TUISONG, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e("LoginActivity", logs);
            }
         //   ExampleUtil.showToast(logs, getApplicationContext());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }
}

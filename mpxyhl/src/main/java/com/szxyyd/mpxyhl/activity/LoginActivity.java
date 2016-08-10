package com.szxyyd.mpxyhl.activity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.http.HttpMethods;
import com.szxyyd.mpxyhl.inter.SubscriberOnNextListener;
import com.szxyyd.mpxyhl.modle.Cst;
import com.szxyyd.mpxyhl.modle.ProgressSubscriber;
import com.szxyyd.mpxyhl.modle.User;
import com.szxyyd.mpxyhl.utils.ExampleUtil;
import java.util.List;
import java.util.Set;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 登录
 * Created by fq on 2016/7/4.
 */
public class LoginActivity extends Activity implements View.OnClickListener{
    private EditText et_phone = null;
    private EditText et_password = null;
    public static boolean isForeground = false;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.TUISONG:
                 //   Log.e("LoginActivity", "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();
        registerMessageReceiver();
        String usr = preferences.getString("usr", "");
        Constant.cstId = preferences.getString("cstId", "");
        Constant.usrId = preferences.getString("usrId", "");
        Log.e("LoginActivity","usr=="+usr);
        Log.e("LoginActivity","Constant.cstId=="+Constant.cstId);
        Log.e("LoginActivity","Constant.usrId=="+Constant.usrId);
        lodeFindUserData(usr);
    }
  private void initView(){
      TextView tv_register = (TextView) findViewById(R.id.tv_register);
      TextView tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
      et_phone = (EditText) findViewById(R.id.et_phone);
      et_password = (EditText) findViewById(R.id.et_password);
      Button btn_back = (Button) findViewById(R.id.btn_back);
      Button btn_login = (Button) findViewById(R.id.btn_login);
      tv_register.setOnClickListener(this);
      tv_forget_password.setOnClickListener(this);
      btn_back.setOnClickListener(this);
      btn_login.setOnClickListener(this);
  }
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
    /**
     * 验证用户是否存在
     */
    private void lodeFindUserData(String usr){
       HttpMethods.getInstance().getFindUsrData("findUsr", usr, new ProgressSubscriber(getUserOnNext, LoginActivity.this));
    }
    /**
     * 登录接口
     */
    private void lodeLoginData(){
        String phone = et_phone.getText().toString().trim();
        String passWord = et_password.getText().toString().trim();
        if (passWord.length() == 0) {
            ExampleUtil.showToast(getString(R.string.tv_psd_right),this);
            return;
        }
        HttpMethods.getInstance().geLoginData("login",phone,passWord,new ProgressSubscriber(getLoginOnNext, LoginActivity.this));
    }

    /**
     * 验证用户返回的信息
     */
        SubscriberOnNextListener   getUserOnNext  = new SubscriberOnNextListener<User>() {
            @Override
            public void onNext(User user) {
                List<User.UsrBean> list = user.getUsr();
                Log.e("onNext", "list.size()==" + list.size());
                if(list.size() == 0){  //用户不存在，登录
                    ExampleUtil.showToast(getString(R.string.tv_user_null), LoginActivity.this);
                    setContentView(R.layout.activity_login);
                    initView();
                }else{
                    User.UsrBean userData = list.get(0);
                    saveUserData(userData);
                    setAlias();
                    Intent hpIntent = new Intent(LoginActivity.this, HomePagerActivity.class);
                    hpIntent.putExtra("type","home");
                    startActivity(hpIntent);
                    finish();
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
            }
        };

    private void saveUserData(User.UsrBean userData){
        Constant.usrId = userData.getId();
        editor.putString("icon", userData.getIcon());
        editor.putString("usrId", userData.getId());
        editor.putString("nickname", userData.getNickname());
        editor.commit();
    }
    /**
     * 登录返回来的信息
     */
        SubscriberOnNextListener  getLoginOnNext = new SubscriberOnNextListener<Cst>() {
            @Override
            public void onNext(Cst cst) {
                List<Cst.CstBean> listCst = cst.getCst();
                if(listCst.size() != 0){
                    Cst.CstBean cstBean = listCst.get(0);
                    Constant.cstId = cstBean.getId();
                    editor.putString("cstId", Constant.cstId);
                    editor.putString("usr", et_phone.getText().toString());
                    editor.putString("password", et_password.getText().toString());
                    editor.commit();
                    setAlias();
                    Intent hpIntent = new Intent(LoginActivity.this, HomePagerActivity.class);
                    hpIntent.putExtra("type","home");
                    startActivity(hpIntent);
                    finish();
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
            }
        };
    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.btn_back:
               finish();
               break;
           case R.id.tv_register:
               Intent intentRegister = new Intent(LoginActivity.this,RegisterActivity.class);
               startActivity(intentRegister);
               overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
               break;
           case R.id.tv_forget_password:
               Intent intentForget = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
               startActivity(intentForget);
               overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
               break;
           case R.id.btn_login:
               lodeLoginData();
               break;
       }
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
                  //  Log.e("LoginActivity", logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                 //   Log.e("LoginActivity", logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    handler.sendMessageDelayed(handler.obtainMessage(Constant.TUISONG, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
               //     Log.e("LoginActivity", logs);
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

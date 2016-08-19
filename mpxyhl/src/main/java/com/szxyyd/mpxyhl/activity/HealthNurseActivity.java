package com.szxyyd.mpxyhl.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import com.szxyyd.mpxyhl.modle.PriceLvl;
import com.szxyyd.mpxyhl.modle.Reladdr;
import com.szxyyd.mpxyhl.modle.SelectBtn;
import com.szxyyd.mpxyhl.utils.CommUtils;
import com.szxyyd.mpxyhl.view.TimePickerDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.text.SimpleDateFormat;
/**
 * Created by fq on 2016/7/5.
 */
public class HealthNurseActivity extends BaseActivity implements View.OnClickListener{
    private GridLayout ll_level = null;  //服务星级
    private GridLayout gl = null; //服务选项
    private GridLayout gl_people = null; //服务人员
    private TextView tv_content = null; // 服务内容
    private LinearLayout ll_notaddr = null; //未有服务地址
    private RelativeLayout rl_time = null; //服务时间
    private RelativeLayout rl_location = null; // 服务地址
    private RelativeLayout rl_addr = null;
    private TextView tv_addr_name = null;
    private TextView tv_addr_phone = null;
    private TextView tv_addr = null;
    private TextView tv_money = null; //价钱
    public TextView tv_date = null; //服务时间
    public EditText et_remark = null; //备忘信息
    private List<PriceLvl> listPriceLvl = null;
    public  int svrid;
    private String orderTitle;
    private int baseMoney;
    private String levelData = null;  //级别选择
    private String levelId = null;   //级别选择Id
    public String orderTime;
    public List<String> nameData = new ArrayList<>(); //个性化的多选
    private  SelectBtn selectBtn = new SelectBtn();
    private int mYear = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        svrid = getIntent().getIntExtra("svrid",-1);
        orderTitle = getIntent().getStringExtra("title");
        initView();
        loadSvrLevelData();
        loadLevelOptionData();
        loadServiceData();
        BaseApplication.getInstance().addActivity(this);

    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(orderTitle);
        ll_level = (GridLayout) findViewById(R.id.ll_level);
        gl = (GridLayout) findViewById(R.id.gl);
        gl_people = (GridLayout) findViewById(R.id.gl_people);
        tv_content = (TextView) findViewById(R.id.tv_content);
        Button btn_next = (Button) findViewById(R.id.btn_next);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        tv_money= (TextView) findViewById(R.id.tv_money);
        tv_date = (TextView) findViewById(R.id.tv_serdate);
        et_remark = (EditText)findViewById(R.id.et_remark);
        rl_time = (RelativeLayout) findViewById(R.id.rl_time);
        ll_notaddr = (LinearLayout) findViewById(R.id.ll_notaddr);
        rl_location = (RelativeLayout) findViewById(R.id.rl_location);
        rl_addr = (RelativeLayout) findViewById(R.id.rl_addr);
        tv_addr_name = (TextView) findViewById(R.id.tv_addr_name);
        tv_addr_phone = (TextView) findViewById(R.id.tv_addr_phone);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        btn_next.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        rl_location.setOnClickListener(this);
        rl_time.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSharedPreferencesfAddr();
    }
    /**
     * 本地获取地址
     */
    private void showSharedPreferencesfAddr(){
        SharedPreferences preferences = getSharedPreferences(Constant.cstId+"defaddr", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "");
        String phone = preferences.getString("mobile", "");
        String addr = preferences.getString("addr", "");
        if(name.length() > 0 ){
            rl_addr.setVisibility(View.VISIBLE);
            ll_notaddr.setVisibility(View.GONE);
            tv_addr_name.setText(name);
            tv_addr_phone.setText(phone);
            tv_addr.setText(addr);
        }else{
            loadDefAddrData();
        }
    }
    /**
     * 加载默认地址
     */
    private void loadDefAddrData(){
        HttpBuilder builder  = new HttpBuilder();
        builder.url(Constant.defAdressUrl);
        builder.put("cstid",Constant.cstId);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject json = new JSONObject(data);
                    String jsonData = json.getString("reladdr");
                    Type reladdrType = new TypeToken<LinkedList<Reladdr>>() {}.getType();
                    Gson gson = new Gson();
                    List<Reladdr> defList  = gson.fromJson(jsonData, reladdrType);
                    if(defList.size() == 0){
                        ll_notaddr.setVisibility(View.VISIBLE);
                    }else{
                        rl_addr.setVisibility(View.VISIBLE);
                        ll_notaddr.setVisibility(View.GONE);
                        tv_addr_name.setText(defList.get(0).getName().toString());
                        tv_addr_phone.setText(defList.get(0).getMobile().toString());
                        tv_addr.setText(defList.get(0).getAddr().toString());
                        SharedPreferences preferences = getSharedPreferences(Constant.cstId+"defaddr", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("name", tv_addr_name.getText().toString());
                        editor.putString("mobile", tv_addr_phone.getText().toString());
                        editor.putString("addr", tv_addr.getText().toString());
                        editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this));
    }
    /**
     * 获取级别
     */
    private void loadSvrLevelData(){
        HttpBuilder builder  = new HttpBuilder();
        builder.url(Constant.getPriceAndLvUrl);
        builder.put("svrid",String.valueOf(svrid));
        builder.put("city","440300");
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject json = new JSONObject(data);
                    String jsonData = json.getString("yxvPriceLvl");
                    Type priceLvlType = new TypeToken<LinkedList<PriceLvl>>() {}.getType();
                    Gson gson = new Gson();
                    listPriceLvl = gson.fromJson(jsonData, priceLvlType);
                    if(listPriceLvl.size() != 0 && listPriceLvl != null) {
                        showPriceLvl(listPriceLvl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this));

    }
    /**
     * 获取级别选项数据
     */
    private void loadLevelOptionData(){
        HttpBuilder builder  = new HttpBuilder();
        builder.url(Constant.funUrl);
        builder.put("svrid",svrid);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject json = new JSONObject(data);
                    String jsonData = json.getString("svrFun");
                    Type svrFunType = new TypeToken<LinkedList<PriceLvl>>() {}.getType();
                    Gson gson = new Gson();
                    List<PriceLvl> list = gson.fromJson(jsonData, svrFunType);
                    if(list.size() != 0 && list != null) {
                        showLevelOption(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this));
    }
    /**
     * 获取服务人员数据
     */
    private void loadServiceData(){
        HttpBuilder builder  = new HttpBuilder();
        builder.url(Constant.nurseServiceUrl);
        builder.put("svrid",svrid);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject json = new JSONObject(data);
                    String jsonData = json.getString("svrCal");
                    Type svrCalType = new TypeToken<LinkedList<PriceLvl>>() {}.getType();
                    Gson gson = new Gson();
                    List<PriceLvl> list = gson.fromJson(jsonData, svrCalType);
                    if(list.size() != 0 && list != null) {
                        showServicePeople(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this));

    }
    /**
     * 改变价格选项
    */
    private void loadGetPriceData(){
        HttpBuilder builder  = new HttpBuilder();
        builder.url(Constant.getPriceUrl);
        builder.put("svrid",svrid);
        builder.put("lvl",levelId);
        builder.put("city",440300);
        int leng = Constant.listpople.size();
        for(int i = 0; i < leng;i++){
           /* StringBuilder sb = new StringBuilder();
            result += sb.append("&idx=").append(Constant.listpople.get(i)).toString();*/
            builder.put("idx",Constant.listpople.get(i).toString());
            //  Log.e("getIdxsList", "result=="+result);
        }
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String result) {
                Log.e("HealthNurseActivity","result=="+result);
                tv_money.setText(result);
            }
        },this));

    }
    private void showPriceLvl(List<PriceLvl> list) {
        ll_level.removeAllViews();
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 0, 0);
        Button btn = null;
        for (int i = 0; i < list.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.view_level, null);
            btn = (Button) view.findViewById(R.id.btn_level1);
            btn.setTag(i+1);
            btn.setText(list.get(i).getName());
            if(Integer.parseInt(btn.getTag().toString()) == 1) {
                btn.setBackgroundResource(R.drawable.shape_blue);
                selectBtn.setId(1);
                selectBtn.setView(btn);
                btn.setTextColor(getResources().getColor(R.color.color_bule));
            }
            btn.setOnClickListener(new btnListener(btn,list));
            ll_level.addView(view);
        }
        showSelectData(list, 0);
    }
    /*
   * 创建一个按钮监听器类
   */
    class btnListener implements View.OnClickListener {
        //定义一个 Button 类型的变量
        private Button btn;
        private List<PriceLvl> list;
        private btnListener(Button btn,List<PriceLvl> list) {
            this.btn = btn;//将引用变量传递给实体变量
            this.list = list;
        }
        @Override
        public void onClick(View view) {
            int tag = Integer.parseInt(btn.getTag().toString());
           if(selectBtn.getId() != tag){
               if(selectBtn.getView() != null){
                   selectBtn.getView().setBackgroundResource(R.drawable.shape_gray);
                   selectBtn.getView().setTextColor(getResources().getColor(R.color.color_six_three));
               }
               selectBtn.setId(tag);
               selectBtn.setView(btn);
               btn.setBackgroundResource(R.drawable.shape_blue);
               btn.setTextColor(getResources().getColor(R.color.color_bule));
               showSelectData(list, (tag-1));
           }
        }
    }
    /**
     * 显示选择星级数据
     */
    private void showSelectData(List<PriceLvl> list,int index){
        tv_content.setText(list.get(index).getContent());
        tv_money.setText(list.get(index).getPrice()+"");
        levelData = list.get(index).getName();
        baseMoney = list.get(index).getPrice();
        Constant.lvlTitle = list.get(index).getName();
        Constant.lvlId = list.get(index).getLvl();
        levelId = list.get(index).getLvl();
        if(getIdxsList().length() != 0){
            loadGetPriceData();
        }
    }
    /**
     * 显示级别选项数据
     * @param data
     */
    private void showLevelOption(final List<PriceLvl> data){
        gl.removeAllViews();
        for(int i = 0;i<data.size();i++){
            View view = getLayoutInflater().inflate(R.layout.include_border,null,false);
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
            cb.setText(data.get(i).getName().toString());
            cb.setTag(data.get(i).getIdx());
            cb.setTextColor(this.getResources().getColor(R.color.color_six_a));
            cb.setChecked(true);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if(isChecked){
                        Constant.listLevel.add(buttonView.getTag().toString());
                    }else{
                        Constant.listLevel.remove(buttonView.getTag().toString());
                    }
                }
            });
            gl.addView(view);
        }
    }
    private String getIdxsList(){
        String result = "";
        for(int i = 0; i < Constant.listpople.size();i++){
            StringBuilder sb = new StringBuilder();
            result += sb.append("&idx=").append(Constant.listpople.get(i)).toString();
          //  Log.e("getIdxsList", "result=="+result);
        }
        return result;
    }
    /**
     * 显示服务人员的数据
     */
    private void showServicePeople(final List<PriceLvl> data){
        gl_people.removeAllViews();
       // PriceLvl svrCal;
        for(int i = 0;i<data.size();i++){
            View view = getLayoutInflater().inflate(R.layout.include_border, null);
           final CheckBox cb  = (CheckBox) view.findViewById(R.id.cb);
           //  svrCal = data.get(i);
            cb.setText(data.get(i).getName().toString());
            cb.setTag(data.get(i).getId());
            cb.setTextColor(this.getResources().getColor(R.color.color_six_a));
            Constant.listpople.clear();
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if(isChecked){
                        Constant.listpople.add(buttonView.getTag().toString());
                        nameData.add(cb.getText().toString());
                        loadGetPriceData();
                    }else{
                        Constant.listpople.remove(buttonView.getTag().toString());
                        nameData.remove(cb.getText().toString());
                        tv_money.setText(baseMoney+"");
                    }
                }
            });
            gl_people.addView(view);
        }
    }
    /**
     * 缓存服务内容
     */
    public  void sharedSerContent(){
        SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences("sercontent", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("level", orderTitle+ " "+levelData);
        StringBuilder sb = new StringBuilder();
        for(String str : nameData){
            sb.append(" ").append(str).toString();
        }
        String result = sb.toString();
        editor.putString("serpeople", result);
        if(orderTime == null){
            editor.putString("sertime", tv_date.getText().toString());
        }else{
            editor.putString("sertime", orderTime);
        }
        editor.putString("note",  et_remark.getText().toString());
        editor.putString("price", tv_money.getText().toString());
        editor.commit();
    }

    /**
     * 判断服务时间
     */
    private String judgementTime(){
       Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR); //获取当前年份
       /* int month = c.get(Calendar.MONTH);//获取当前月份
        int day = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码
        int hour = c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数
        int mMinute = c.get(Calendar.MINUTE);//获取当前的分钟数*/
        String result = null;
        String seleTime = tv_date.getText().toString().trim();
        try {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date begin = dfs.parse(CommUtils.showToDay());
            Date end = dfs.parse(seleTime);
            long between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒
            long day=between/(24*3600);
            long hour=between%(24*3600)/3600;
            if(day < 0){
                Toast.makeText(HealthNurseActivity.this,"选择年份不能小于365天",Toast.LENGTH_SHORT).show();
                return null;
            }
            if(mYear != year){
                Toast.makeText(HealthNurseActivity.this,"选择年份不能大于365天",Toast.LENGTH_SHORT).show();
                return null;
            }
            result = String.valueOf(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case R.id.btn_next:
                if(ll_notaddr.getVisibility() == View.VISIBLE){
                    Toast.makeText(HealthNurseActivity.this,"请选择服务地址",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tv_date.getText().toString().equals("请选择服务时间")){
                    Toast.makeText(HealthNurseActivity.this,"请选择服务时间",Toast.LENGTH_SHORT).show();
                    return;
                }
                String result = judgementTime();
                if(TextUtils.isEmpty(result)){
                    return;
                }
                sharedSerContent();
                Intent intent = new Intent(HealthNurseActivity.this,NurselistActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.rl_location:
                Intent intentAddress = new Intent(HealthNurseActivity.this,ServiceAddressActivity.class);
                startActivity(intentAddress);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.rl_time:
                Calendar c = Calendar.getInstance();
                 new TimePickerDialog(HealthNurseActivity.this, 0, new TimePickerDialog.OnDateSetListener() {
                     @Override
                     public void onDateSet(int startYear, int startMonthOfYear, int startDayOfMonth,
                                           int hourOfDay,int minute) {
                         mYear = startYear;
                         tv_date.setText(startYear + "-" + (startMonthOfYear+1) + "-" + startDayOfMonth +" " +hourOfDay + ":" + minute);

                     }
                 },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE),true).show();
                break;
        }
    }
}

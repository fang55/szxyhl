package com.szxyyd.mpxyhl.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.adapter.EvaluateAdapter;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import com.szxyyd.mpxyhl.modle.DetailFile;
import com.szxyyd.mpxyhl.modle.ImageItem;
import com.szxyyd.mpxyhl.modle.NurseList;
import com.szxyyd.mpxyhl.modle.NurseTrain;
import com.szxyyd.mpxyhl.utils.Bimp;
import com.szxyyd.mpxyhl.utils.CommUtils;
import com.szxyyd.mpxyhl.utils.FileUtils;
import com.szxyyd.mpxyhl.utils.PicassoUtils;
import com.szxyyd.mpxyhl.view.RoundImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jq on 2016/7/5.
 */
public class NurseDetailsActivity extends BaseActivity implements View.OnClickListener{
    private RoundImageView image_theach;
    private LinearLayout ll_certificate;//存储证书
    private LinearLayout ll_image; //生活照
    private LinearLayout ll_train; //培训经历
    private LinearLayout ll_video; //视频
    private WebView wv_video;  //播放视频
    private Button btn_start;
    private Button btn_collect; //收藏
    private LinearLayout ll_evaluate;
    private RelativeLayout ll_evaMore;
    private NurseList nurse;
    private List<String> cerImage = new ArrayList<>(); //存储生活照
    private String videoUrl;
    private boolean isCollect = false;
    private  String fvrid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nurse = (NurseList) getIntent().getSerializableExtra("nurse");
        setContentView(R.layout.activity_details);
        initView();
        showData();
        showNurStart(nurse.getSrvscore());
        lodeDetailsData();
        lodeEvaluateData();
        lodeTrainData();
        showVideoData();
        BaseApplication.getInstance().addActivity(this);
    }
    private void initView(){
      ((TextView)findViewById(R.id.tv_title)).setText("护理师详情");
        image_theach = (RoundImageView) findViewById(R.id.image_theach);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        Button btn_order = (Button) findViewById(R.id.btn_order);
        btn_collect = (Button) findViewById(R.id.btn_collect);
        ll_train = (LinearLayout) findViewById(R.id.ll_train);
        ll_image = (LinearLayout) findViewById(R.id.ll_image);
        ll_evaMore = (RelativeLayout) findViewById(R.id.ll_evaMore);
        ll_evaluate = (LinearLayout) findViewById(R.id.ll_evaluate);
        ll_video = (LinearLayout) findViewById(R.id.ll_video);
        ImageView nurse_top = (ImageView) findViewById(R.id.nurse_top);
        PicassoUtils.loadImageViewHolder(this,Constant.nurseImage + nurse.getIcon(),409,442,R.mipmap.nurse_top,nurse_top);
       PicassoUtils.loadImageViewHolder(this,Constant.nurseImage + nurse.getIcon(),130,125,R.mipmap.teach,image_theach);
       int fvlnur = nurse.getFvrnur();
        btn_collect.setBackgroundResource(fvlnur > 0 ? R.mipmap.nurse_collectsel : R.mipmap.nurse_collect);
        isCollect = fvlnur > 0 ? true : false;
        btn_back.setOnClickListener(this);
        btn_order.setOnClickListener(this);
        btn_collect.setOnClickListener(this);
        ll_evaMore.setOnClickListener(this);
    }

    /**
     * 显示个人信息
     */
    private void showData(){
      //  nurseId = nurse.getId();
      videoUrl = Constant.workvideo + nurse.getWorkvideo();
        int nurseAge = CommUtils.showAge(nurse.getBirth());
        ((TextView)findViewById(R.id.tv_name)).setText(nurse.getName());
        ((TextView)findViewById(R.id.tv_health)).setText(nurse.getSvrid());
        ((TextView)findViewById(R.id.tv_detail_age)).setText(nurseAge+"岁");
        ((TextView)findViewById(R.id.tv_detail_city)).setText(nurse.getCity());
        ((TextView)findViewById(R.id.tv_detail_marriage)).setText(nurse.getMarried());
        ((TextView)findViewById(R.id.tv_detail_nation)).setText(nurse.getNation());
        ((TextView)findViewById(R.id.tv_detail_type)).setText(nurse.getSvrid());
        ((TextView)findViewById(R.id.tv_detail_grade)).setText(nurse.getLvl());
    }

    /**
     * 评分星数
     * @param number
     */
    private void showNurStart(int number){
        ImageView iv_star1 = (ImageView) findViewById(R.id.iv_star1);
        ImageView iv_star2 = (ImageView) findViewById(R.id.iv_star2);
        ImageView iv_star3 = (ImageView) findViewById(R.id.iv_star3);
        ImageView iv_star4 = (ImageView) findViewById(R.id.iv_star4);
        ImageView iv_star5 = (ImageView) findViewById(R.id.iv_star5);
        switch (number){
            case 1:
                iv_star1.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                break;
            case 2:
                iv_star1.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                iv_star2.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                break;
            case 3:
                iv_star1.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                iv_star2.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                iv_star3.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                break;
            case 4:
                iv_star1.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                iv_star2.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                iv_star3.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                iv_star4.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                break;
            case 5:
                iv_star1.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                iv_star2.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                iv_star3.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                iv_star4.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                iv_star5.setBackground(this.getResources().getDrawable(R.mipmap.nurse_starsel));
                break;
        }
    }


    private void showStar(LinearLayout llStar,int size){
        llStar.removeAllViews();
        int leng = size > 5 ? 5: size;
        for(int i = 0; i <leng;i++){
            ImageView imageView = new ImageView(this);
            imageView.setBackground(this.getResources().getDrawable(R.mipmap.star));
            llStar.addView(imageView);
        }
    }
    /**
     * 显示视频
     */
    private void showVideoData(){
        ll_video.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.view_video,null,false);
        wv_video = (WebView) view.findViewById(R.id.wv_video);
        btn_start = (Button) view.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_start.setVisibility(View.INVISIBLE);
                playVideo(videoUrl);
            }
        });
        ll_video.addView(view);

    }
    private void playVideo(String strPath) {
        wv_video.getSettings().setJavaScriptEnabled(true) ;
        wv_video.loadUrl(strPath) ;
        //获取WebSettings对象,设置缩放
        wv_video.getSettings().setUseWideViewPort(true) ;
        wv_video.getSettings().setLoadWithOverviewMode(true) ;
        wv_video.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("访问网址：",url) ;
                view.loadUrl(url) ;
                return true ;
            }
        }) ;
    }
    /**
     * 显示生活照
     */
    private void showImageLive(List<String> cerImagez){
        ll_image.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20,10,10,10);
        int leng = cerImagez.size() > 3 ? 3 : cerImagez.size();
        Bimp.tempSelectBitmap.clear();
        for(int i = 0;i<leng;i++){
            ImageView imageView = new ImageView(this);
            PicassoUtils.loadImageViewRoundTransform(this,Constant.lifePic + cerImagez.get(i),200,200,R.mipmap.teach,imageView);
            ImageItem takePhoto = new ImageItem();
            takePhoto.setImagePath(Constant.lifePic + cerImagez.get(i));
            Bimp.tempSelectBitmap.add(takePhoto);
            imageView.setOnClickListener(new imageListener(Constant.lifePic + cerImagez.get(i)));
            ll_image.addView(imageView,layoutParams);
        }
    }
    class imageListener implements View.OnClickListener {
        private String imagePath = null;
         public imageListener(String imagePath){
             this.imagePath = imagePath;
         }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(NurseDetailsActivity.this, GalleryActivity.class);
            intent.putExtra("type", "detail");
            intent.putExtra("position", "0");
            intent.putExtra("ID", 0);
            intent.putExtra("imagePath",imagePath);
            startActivity(intent);
        }
    }

    /**
     * 获取护理师详情
     */
    private void lodeDetailsData(){
        //1294016
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.nurseDetailUrl);
        builder.put("nurid",nurse.getNursvrid());
        builder.put("cstid",Constant.cstId);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                parserData(data);
            }
        },this));

    }
    private void parserData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String jsonCst = json.getString("files");
            Type cstType = new TypeToken<LinkedList<DetailFile>>() {}.getType();
            Gson gsonCst = new Gson();
            List<DetailFile> listFile = gsonCst.fromJson(jsonCst, cstType);
            JSONArray jsonArray = json.getJSONArray("nurse");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                fvrid = jsonObject.getString("fvrid");
            }
            if(listFile.size() != 0){
                for(DetailFile detailFile : listFile){
                    if(detailFile.getType().equals("6000")){
                        cerImage.add(detailFile.getFiles());
                    }
                }
                showImageLive(cerImage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取护理师评论列表
     */
    private void lodeEvaluateData(){
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.nurseCmtAllUrl);
        builder.put("nurseid",nurse.getNursvrid());
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject json = new JSONObject(data);
                    String jsonData = json.getString("nurse");
                    Type nurseType = new TypeToken<LinkedList<NurseList>>() {}.getType();
                    Gson gson = new Gson();
                    List<NurseList> nurseLists = gson.fromJson(jsonData, nurseType);
                    if(nurseLists.size() != 0){
                        if(nurseLists.size() > 3){
                            ll_evaMore.setVisibility(View.VISIBLE);
                        }
                        showEvaluateData(nurseLists);
                    }else{
                        ll_evaMore.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this));
    }

    /**
     * 显示评论列表
     * @param list
     */
    private void showEvaluateData(List<NurseList> list){
        ll_evaluate.removeAllViews();
        int leng = list.size() > 3 ? 3 : list.size();
        for(int i = 0;i<leng;i++){
            View view = getLayoutInflater().inflate(R.layout.listview_item_evaluate,null,false);
            TextView tv_evcontent = (TextView) view.findViewById(R.id.tv_evcontent);
            tv_evcontent.setText(list.get(i).getContent());
            TextView tv_commTime = (TextView) view.findViewById(R.id.tv_commTime);
            tv_commTime.setText(CommUtils.showData(list.get(i).getAtpub()));
            LinearLayout ll_commStar = (LinearLayout) view.findViewById(R.id.ll_commStar);
            LinearLayout ll_commImage = (LinearLayout) view.findViewById(R.id.ll_commImage);
            int starNum = Integer.valueOf(list.get(i).getStar());
            showStar(ll_commStar,starNum);
            showImae(ll_commImage,list.get(i));
            ll_evaluate.addView(view);
        }
    }
    private void showImae(LinearLayout ll_commImage,NurseList nurseList){
        ll_commImage.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20,10,10,10);
        List<DetailFile> detailFiles = nurseList.getOrdFiles();
        for(int i = 0;i<detailFiles.size();i++){
            String imagePath = detailFiles.get(i).getImgname();
            View view = LayoutInflater.from(this).inflate(R.layout.item_image,null,false);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_item);
            PicassoUtils.loadImageViewRoundTransform(this,Constant.evaluateImage + imagePath,100,100,R.mipmap.teach,imageView);
          //  Log.e("EvaluateAdapter","imagePath==="+Constant.evaluateImage + imagePath);
            ll_commImage.addView(view);
        }
    }
    /**
     * 获取培训经历
     */
    private void lodeTrainData(){
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.nurseTrainListUrl);
        builder.put("nurseid",nurse.getNursvrid());
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject json = new JSONObject(data);
                    String jsonData = json.getString("nurseTrain");
                    Type nurseTrainType = new TypeToken<LinkedList<NurseTrain>>() {}.getType();
                    Gson gson = new Gson();
                    List<NurseTrain> trainList = gson.fromJson(jsonData, nurseTrainType);
                    if(trainList.size() != 0 && trainList != null) {
                        showTrainData(trainList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this));

    }
    /**
     * 显示培训经历
     */
    private void showTrainData(List<NurseTrain> trainList){
        ll_train.removeAllViews();
        for(int i = 0;i<trainList.size();i++){
            View view = LayoutInflater.from(this).inflate(R.layout.listview_item_train, null, false);
            String time1 = CommUtils.showData(trainList.get(i).getAtrec1());
            String time2 = CommUtils.showData(trainList.get(i).getAtrec2());
            TextView tv_time = (TextView) view.findViewById(R.id.tv_train_time);
            tv_time.setText(time1 + "-" + time2);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_train_content);
            tv_content.setText(trainList.get(i).getTitle());
            ll_train.addView(view);
        }
    }
    /**
     * 添加收藏
     */
    private void addCollect(final String type){
        // cstid  （客户id）  svrid   （服务id） nurseid   （护理师id） 80001953 1000 1294016
        Log.e("NurseDetailsActivity", "addCollect()--cstid=="+Constant.cstId);
        Log.e("NurseDetailsActivity", "addCollect()--svrid=="+Constant.svrId);
        Log.e("NurseDetailsActivity", "addCollect()--nurseid=="+Constant.nurseId);
        HttpBuilder builder = new HttpBuilder();
        if(type.equals("add")){
            builder.url(Constant.cstFvrnurAddUrl);
            builder.put("cstid",Constant.cstId);
            builder.put("svrid",Constant.svrId);
            builder.put("nurseid",nurse.getNursvrid());
        }else if(type.equals("delect")){
            builder.url(Constant.cstFvrnurDelUrl);
            builder.put("id",fvrid);
        }
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                if(type.equals("add")){
                    Toast.makeText(NurseDetailsActivity.this,"已收藏",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(NurseDetailsActivity.this,"已取消",Toast.LENGTH_SHORT).show();
                }
            }
        },this));

    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.btn_back:
               finish();
               overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
               break;
           case R.id.btn_order:
               Intent intent = new Intent(NurseDetailsActivity.this,OrderNurseActivity.class);
               Bundle bundle = new Bundle();
               bundle.putSerializable("nurse", nurse);
               intent.putExtras(bundle);
               startActivity(intent);
               overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
               break;
           case R.id.btn_collect:
               if(isCollect){
                   btn_collect.setBackgroundResource(R.mipmap.nurse_collect);
                   isCollect = false;
                   addCollect("delect");
               }else{
                   btn_collect.setBackgroundResource(R.mipmap.nurse_collectsel);
                   isCollect = true;
                   addCollect("add");
               }
               break;
           case R.id.ll_evaMore:
               Intent intentEva = new Intent(NurseDetailsActivity.this,EvaluateActivity.class);
               Bundle bundleEva = new Bundle();
               bundleEva.putSerializable("nurse", nurse);
               intentEva.putExtras(bundleEva);
               startActivity(intentEva);
               overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
               break;
       }
    }
}

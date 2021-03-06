package com.szxyyd.mpxyhl.activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import com.szxyyd.mpxyhl.modle.ImageItem;
import com.szxyyd.mpxyhl.utils.Bimp;
import com.szxyyd.mpxyhl.utils.FileUtils;
import com.szxyyd.mpxyhl.utils.PicassoUtils;
import com.szxyyd.mpxyhl.view.RoundImageView;

/**
 * 设置内容
 * Created by fq on 2016/7/6.
 */
public class SetContentActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_title  = null;
    private TextView tv_add  = null;
    private TextView tv_sex = null; //性别
    private TextView tv_boy = null;
    private TextView tv_gril = null;
    private TextView tv_pepleName = null; //昵称
    private LinearLayout ll_content = null;
    private LayoutInflater inflater = null;
    private int type;
    private EditText et_updname = null;
    private EditText et_original = null;
    private EditText et_new = null;
    private EditText et_renew = null;
    private ImageView iv_photo; //头像
    private  LinearLayout ll_boy = null;
    private  LinearLayout ll_gril = null;
    private Dialog  dialog = null;
    private SharedPreferences preferences;
    private String usrId;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private  ImageView iv_boyRight;
    private  ImageView iv_grilRight;
    private int seleSex = 0;
    private static final int TAKE_PICTURE = 0x000001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type",0);
        parentView = getLayoutInflater().inflate(R.layout.activity_setcontent, null);
        setContentView(parentView);
        inflater = LayoutInflater.from(this);
        initView();
        accordingToType(type);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        usrId = preferences.getString("usrId","");
        initPopupWindow();
        BaseApplication.getInstance().addActivity(this);
    }
    private void initView(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_add = (TextView) findViewById(R.id.tv_add);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        tv_add.setOnClickListener(this);
    }
    public void initPopupWindow() {
        pop = new PopupWindow(SetContentActivity.this);
        View view = getLayoutInflater().inflate(R.layout.item_comm_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
    }
    /**
     * 根据类型显示界面
     * @param type
     */
 private void accordingToType(int type){
     switch (type){
         case 1:
             showInfoView();
             break;
         case 2:
             showRevisePsdView();
             break;
         case 4:
             showAboutView();
             break;
         case 6:
             showServiceProtocolView();
             break;
     }
 }
    private void showHeaderImage(){
        String imagePath = FileUtils.SDPATH + "clipheader" + ".png";
        if(FileUtils.isFileExist(imagePath)){
            Bitmap bitmap = FileUtils.getPhotoFromSDCard(imagePath);
            iv_photo.setImageBitmap(bitmap);
        }
    }
    /**
     * 显示个人资料
     */
    private void showInfoView(){
        tv_title.setText(getString(R.string.text_info));
        ll_content.removeAllViews();
        View view = inflater.inflate(R.layout.view_info,null,false);
        tv_sex = (TextView) view.findViewById(R.id.tv_sex);
        tv_pepleName = (TextView) view.findViewById(R.id.tv_pepleName);
        iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
        RelativeLayout rl_data = (RelativeLayout) view.findViewById(R.id.rl_data);
        RelativeLayout rl_set_sex = (RelativeLayout) view.findViewById(R.id.rl_set_sex);
        RelativeLayout rl_set_name = (RelativeLayout) view.findViewById(R.id.rl_set_name);
        rl_set_sex.setOnClickListener(this);
        rl_set_name.setOnClickListener(this);
        rl_data.setOnClickListener(this);
        ll_content.addView(view);
    }
    //修改称呢
    private void nameDialog(){
        dialog = new Dialog(SetContentActivity.this,R.style.dialog);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_change_name);
        TextView tv_set_title = (TextView) window.findViewById(R.id.tv_set_title);
        tv_set_title.setText("修改称呢");
        et_updname = (EditText) window.findViewById(R.id.et_updname);
        Button btn_cleak = (Button) window.findViewById(R.id.btn_cleak);
        Button btn_set_back = (Button) window.findViewById(R.id.btn_set_back);
        TextView btn_set_add = (TextView) window.findViewById(R.id.btn_set_submit);
        btn_cleak.setOnClickListener(new nameClick());
        btn_set_back.setOnClickListener(new nameClick());
        btn_set_add.setOnClickListener(new nameClick());
        btn_cleak.setTag(1);
        btn_set_back.setTag(2);
        btn_set_add.setTag(3);
    }
    class nameClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int tag = (int) view.getTag();
            switch (tag){
                case 1:
                   et_updname.setText("");
                    break;
                case 2:
                    dialog.cancel();
                    break;
                case 3:
                    tv_pepleName.setText(et_updname.getText().toString());
                    submitData(1);
                    dialog.cancel();
                    break;
            }
        }
    }
    //性别对话框
    private void sexDialog(){
        dialog = new Dialog(SetContentActivity.this,R.style.dialog);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_sex);
        TextView tv_set_title = (TextView) window.findViewById(R.id.tv_set_title);
        tv_set_title.setText("选择性别");
        tv_boy = (TextView) window.findViewById(R.id.tv_boy);
        tv_gril = (TextView) window.findViewById(R.id.tv_gril);
        iv_boyRight = (ImageView) window.findViewById(R.id.iv_boyRight);
        iv_grilRight = (ImageView) window.findViewById(R.id.iv_grilRight);
        Button btn_return = (Button) window.findViewById(R.id.btn_set_back);
        TextView btn_set_submit = (TextView) window.findViewById(R.id.btn_set_submit);
        ll_boy = (LinearLayout) window.findViewById(R.id.ll_boy);
        ll_gril = (LinearLayout) window.findViewById(R.id.ll_gril);
        btn_return.setTag(1);
        btn_set_submit.setTag(2);
        ll_boy.setTag(3);
        ll_gril.setTag(4);
        btn_return.setOnClickListener(new sexClick());
        btn_set_submit.setOnClickListener(new sexClick());
        ll_boy.setOnClickListener(new sexClick());
        ll_gril.setOnClickListener(new sexClick());
    }
    class sexClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int tag = (int) view.getTag();
            switch (tag){
                case 1:
                    dialog.cancel();
                    break;
                case 2:  //提交
                    submitData(3);
                    dialog.cancel();
                    break;
                case 3:
                    seleSex = 1;
                    tv_sex.setText("男");
                    iv_boyRight.setVisibility(View.VISIBLE);
                    iv_grilRight.setVisibility(View.GONE);
                    break;
                case 4:
                    seleSex = 2;
                    tv_sex.setText("女");
                    iv_boyRight.setVisibility(View.GONE);
                    iv_grilRight.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
    /**
     * 显示修改密码
     */
    private void showRevisePsdView(){
        tv_title.setText(getString(R.string.text_revise));
        tv_add.setVisibility(View.VISIBLE);
        tv_add.setText("提交");
        ll_content.removeAllViews();
        View view = inflater.inflate(R.layout.view_revise,null,false);
        et_original = (EditText) view.findViewById(R.id.et_original);
        et_new = (EditText) view.findViewById(R.id.et_new);
        et_renew = (EditText) view.findViewById(R.id.et_renew);
        ll_content.addView(view);
    }

    /**
     * 显示关于我们
     */
    private void showAboutView(){
        tv_title.setText(getString(R.string.text_about));
        ll_content.removeAllViews();
        View view = inflater.inflate(R.layout.view_about,null,false);
        ll_content.addView(view);
    }
    /**
     * 显示服务协议
     */
    private void showServiceProtocolView(){
        tv_title.setText(getString(R.string.text_protocol));
        ll_content.removeAllViews();
        View view = inflater.inflate(R.layout.view_protocol,null,false);
        ll_content.addView(view);
    }
    /**
     * 提交数据
     */
    private void submitData(final int type){
        String url = null;
        HttpBuilder builder = new HttpBuilder();
        switch (type){
            case 1:  //修改昵称
                String name = et_updname.getText().toString();
                if(name.length() == 0){
                    Toast.makeText(this,"请输入昵称",Toast.LENGTH_SHORT).show();
                    return;
                }
                builder.url(Constant.cstNameUpdUrl);
                builder.put("id",usrId);
                builder.put("nickname",name);
                break;
            case 2: //修改密码
                String originalPsd = et_original.getText().toString();
                String etNew = et_new.getText().toString();
                String surePsd = et_renew.getText().toString();
                if(originalPsd.length() == 0){
                    Toast.makeText(this,"请原始密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etNew.length() == 0){
                    Toast.makeText(this,"请输入新密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(surePsd.length() == 0){
                    Toast.makeText(this,"请确认新密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!etNew.equals(surePsd)){
                    Toast.makeText(this,"密码输入不一致",Toast.LENGTH_SHORT).show();
                    return;
                }
                builder.url(Constant.cstPwdUpdUrl);
                builder.put("id",usrId);
                builder.put("pwd",originalPsd);
                builder.put("pwd2",surePsd);
                break;
            case 3:   //修改性别
                builder.url(Constant.sexUpdUrl);
                builder.put("id",usrId);
                builder.put("sex",seleSex);
                break;
        }
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(SetContentActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                parserData(result,type);
            }
        },this));

    }
    private void parserData(String result,int type){
        switch (type){
            case 1:
                Toast.makeText(this,"提交成功",Toast.LENGTH_SHORT).show();
                break;
            case 2:

                break;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
               finish();
                break;
            case R.id.rl_data: //修改头像
                ll_popup.startAnimation(AnimationUtils.loadAnimation(SetContentActivity.this,R.anim.activity_translate_in));
                pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.rl_set_name: //修改昵称
                nameDialog();
                break;
            case R.id.rl_set_sex: //选择性别
                sexDialog();
                break;
            case R.id.tv_add: //提交
             if(tv_title.getText().equals(getString(R.string.text_revise))){ //修改密码
                 submitData(2);
             }
                break;
            case R.id.parent:
                pop.dismiss();
                ll_popup.clearAnimation();
                break;
            case R.id.item_popupwindows_camera: //照相
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
                break;
            case R.id.item_popupwindows_Photo: //选择照片
                Intent intent = new Intent(SetContentActivity.this, AlbumActivity.class);
                intent.putExtra("type","set");
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
                break;
            case R.id.item_popupwindows_cancel: //取消
                pop.dismiss();
                ll_popup.clearAnimation();
                break;
        }
    }
    /**
     * 拍照
     */
    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    /*Bitmap bm = (Bitmap) data.getExtras().get("data");
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Intent intent = new Intent(SetContentActivity.this, ClipHeaderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bitmap", takePhoto);
                    intent.putExtras(bundle);
                    startActivity(intent);*/
                }
                break;
        }
    }
}

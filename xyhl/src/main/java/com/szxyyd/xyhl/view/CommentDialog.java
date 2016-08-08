package com.szxyyd.xyhl.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.szxyyd.xyhl.R;
import com.szxyyd.xyhl.activity.BaseApplication;
import com.szxyyd.xyhl.activity.Constant;
import com.szxyyd.xyhl.http.BitmapCache;
import com.szxyyd.xyhl.http.OkHttp3Utils;
import com.szxyyd.xyhl.http.ProgressCallBack;
import com.szxyyd.xyhl.http.ProgressCallBackListener;
import com.szxyyd.xyhl.inf.OnFinshListener;
import com.szxyyd.xyhl.modle.Order;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户评论
 * Created by jq on 2016/6/11.
 */
public class CommentDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private Dialog alertDialog;
    private RatingBar rb_skill;
    private EditText et_commcontent;
    private Button btn_comm_sunmit;
    private ImageView tv_commpeople;
    private Order order;
    private int starNum = 0;
    private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    private OnFinshListener mListener;
    public CommentDialog(Context context, Order order) {
        super(context);
        mContext = context;
        this.order = order;
        mQueue = BaseApplication.getRequestQueue();
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
    }
    public void init(){
        alertDialog = new Dialog(mContext,R.style.dialog);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.view_comment);
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("评论");
        tv_commpeople = (ImageView) window.findViewById(R.id.tv_commpeople);
        et_commcontent = (EditText) window.findViewById(R.id.et_commcontent);
        btn_comm_sunmit = (Button) window.findViewById(R.id.btn_comm_sunmit);
        Button btn_back = (Button) window.findViewById(R.id.btn_back);
        rb_skill = (RatingBar) window.findViewById(R.id.rb_skill);
        rb_skill.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                  if(b){
                      String result = String.valueOf(rating);
                      String str = result.substring(0,result.indexOf("."));
                      starNum = Integer.valueOf(str);
                  }
            }
        });
        btn_comm_sunmit.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(tv_commpeople, 0, R.drawable.teach);
        mImageLoader.get(Constant.nurseImage + order.getIcon(), listener);
    }
    @Override
    public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_back:
                    alertDialog.cancel();
                    break;
                case R.id.btn_comm_sunmit:
                    if(et_commcontent.length() == 0){
                        Toast.makeText(BaseApplication.getInstance(),"请进行评说",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(starNum == 0){
                        Toast.makeText(BaseApplication.getInstance(),"请进行评分",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    submitData();
                    break;
            }
    }
    /**
     * 提交评论
     */
    private void submitData(){
        Map<String,String> map = new HashMap<>();
        map.put("bycstid",Constant.cstId);
        map.put("nurseid",order.getNurseid());
        map.put("content",et_commcontent.getText().toString().trim());
        map.put("id",order.getId());
        map.put("star",String.valueOf(starNum));
        OkHttp3Utils.getInstance().callAsynTextData(map,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String result) {
                if(mListener != null){
                    mListener.onFinsh();
                }
                alertDialog.cancel();
            }
        },mContext));
    }
    public void setOnFinshListener(OnFinshListener listener ){
          mListener = listener;
    }
}

package com.szxyyd.mpxyhl.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.adapter.IncomeAdapter;

/**
 * 我的收益
 * Created by fq on 2016/8/9.
 */
public class IncomeActivity extends BaseActivity{
    private GridView gv_income;
    private IncomeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        initView();
    }
    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的收益");
        gv_income = (GridView) findViewById(R.id.gv_income);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        adapter = new IncomeAdapter(this);
        gv_income.setAdapter(adapter);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
    }
}

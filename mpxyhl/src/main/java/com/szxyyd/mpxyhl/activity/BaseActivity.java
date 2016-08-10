package com.szxyyd.mpxyhl.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.szxyyd.mpxyhl.R;

/**
 * Created by fq on 2016/8/9.
 */
public class BaseActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

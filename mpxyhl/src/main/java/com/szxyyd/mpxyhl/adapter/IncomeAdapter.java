package com.szxyyd.mpxyhl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.szxyyd.mpxyhl.R;

/**
 * Created by fq on 2016/8/9.
 */
public class IncomeAdapter extends BaseAdapter{
    private Context mContent;
    private LayoutInflater inflater;
    public IncomeAdapter(Context content){
        mContent = content;
        inflater = LayoutInflater.from(mContent);
    }
    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(contentView == null){
            contentView = inflater.inflate(R.layout.gridview_item_income,null ,false);
        }else{

        }

        return contentView;
    }
    private class ViewHolder{

    }
}

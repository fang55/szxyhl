package com.szxyyd.mpxyhls.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.szxyyd.mpxyhls.R;

import java.util.Map;

/**
 * Created by fq on 2016/8/9.
 */
public class IncomeAdapter extends BaseAdapter{
    private Context mContent;
    private LayoutInflater inflater;
    private Map<String,String> mData;
    public IncomeAdapter(Context content, Map<String,String> data){
        mContent = content;
        mData =  data;
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
            viewHolder = new ViewHolder();
            viewHolder.tv_incomeNum = (TextView) contentView.findViewById(R.id.tv_incomeNum);
            viewHolder.tv_incomeMoney = (TextView) contentView.findViewById(R.id.tv_incomeMoney);
            viewHolder.tv_waitNum = (TextView) contentView.findViewById(R.id.tv_waitNum);
            viewHolder.tv_waitMoney = (TextView) contentView.findViewById(R.id.tv_waitMoney);
            contentView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) contentView.getTag();
        }
           String price = mData.get("price");
          String orderNum = mData.get("orderNum");
        viewHolder.tv_incomeNum.setText(orderNum);
        viewHolder.tv_waitNum.setText(orderNum);
        viewHolder.tv_incomeMoney.setText(price);
        viewHolder.tv_waitMoney.setText(price);
        return contentView;
    }
    private class ViewHolder{
        TextView tv_incomeNum;
        TextView tv_incomeMoney;
        TextView tv_waitNum;
        TextView tv_waitMoney;
    }
}

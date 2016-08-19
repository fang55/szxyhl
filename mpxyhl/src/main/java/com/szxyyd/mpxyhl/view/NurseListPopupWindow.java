package com.szxyyd.mpxyhl.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.adapter.PopupWindowAdapter;

/**
 * Created by jq on 2016/7/7.
 */
public class NurseListPopupWindow extends PopupWindow {
    private Context mContext = null;
    private View conentView = null;
    private OnPopuItemClickListener mListener = null;
    private ListView lv = null;
    private PopupWindowAdapter adapter = null;
    private String[] mData = null;
    public NurseListPopupWindow(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView  = inflater.inflate(R.layout.pw_list, null, false);
        this.setBackgroundDrawable(mContext.getResources().getDrawable(R.mipmap.home_bg));
        lv = (ListView) conentView.findViewById(R.id.listview);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(mListener != null){
                    mListener.onSure(position);
                }
                dismiss();
            }
        });
        this.setWidth((int) context.getResources().getDimension(R.dimen.w720));
        this.setHeight((int) context.getResources().getDimension(R.dimen.h1085));
        setContentView(conentView);
    }
    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent,String[] data) {
        this.showAsDropDown(parent);
        mData = data;
        adapter = new PopupWindowAdapter(mContext,mData);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void setonPopuItemListener(OnPopuItemClickListener listener){
        mListener = listener;
    }
    public interface OnPopuItemClickListener{
        void onSure(int position);
    }
}

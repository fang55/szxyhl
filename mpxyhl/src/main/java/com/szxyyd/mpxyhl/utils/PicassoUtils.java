package com.szxyyd.mpxyhl.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.szxyyd.mpxyhl.inter.CircleTransform;
import com.szxyyd.mpxyhl.inter.PicassoRoundTransform;

/**
 * Created by fq on 2016/8/9.
 */
public class PicassoUtils {
    /**
     * 指定大小加载图片
     *
     * @param mContext   上下文
     * @param path       图片路径
     * @param width      宽
     * @param height     高
     * @param mImageView 控件
     */
    public static void loadImageViewSize(Context mContext, String path, int width, int height, ImageView mImageView) {
        Picasso.with(mContext).load(path).resize(width, height).centerCrop().into(mImageView);
    }
    /**
     * 加载有默认图片和失败有默认图片
     *
     * @param mContext   上下文
     * @param path       图片路径
     * @param resId      默认图片资源
     * @param width      宽
     * @param height     高
     * @param mImageView 控件
     */
    public static void loadImageViewHolder(Context mContext, String path, int width, int height,int resId, ImageView mImageView) {
        Picasso.with(mContext).load(path).centerCrop().resize(width, height).placeholder(resId).error(resId).into(mImageView);
    }
    /**
     * 加载有圆角矩形的默认图片和失败有默认图片
     *
     * @param mContext   上下文
     * @param path       图片路径
     * @param resId      默认图片资源
     * @param width      宽
     * @param height     高
     * @param mImageView 控件
     */
    public static void loadImageViewRoundTransform(Context mContext, String path, int width, int height,int resId, ImageView mImageView){
        Picasso.with(mContext).load(path).centerCrop().resize(width, height).placeholder(resId).error(resId)
                .transform(new PicassoRoundTransform()).into(mImageView);
    }
    
    public static void loadImageViewCircleTransform(Context mContext, String path, int width, int height,int resId, ImageView mImageView){
        Picasso.with(mContext).load(path).centerCrop().resize(width, height).placeholder(resId).error(resId)
                .transform(new CircleTransform()).into(mImageView);
    }
}

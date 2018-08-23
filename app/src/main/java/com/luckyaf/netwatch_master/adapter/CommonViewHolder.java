package com.luckyaf.netwatch_master.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/21
 */
public class CommonViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private int mLayoutId;

    private CommonViewHolder(@NonNull View v) {
        super(v);
        mConvertView = v;
        mViews = new SparseArray<>();
    }

    public static CommonViewHolder get(View view) {
        return new CommonViewHolder(view);
    }

    public static CommonViewHolder get(ViewGroup parent, int layoutId) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        CommonViewHolder holder = new CommonViewHolder(convertView);
        holder.mLayoutId = layoutId;
        return holder;
    }

    public int getLayoutId() {
        return mLayoutId;
    }


    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        View v = mViews.get(id);
        if (v == null) {
            v = mConvertView.findViewById(id);
            mViews.put(id, v);
        }
        return (T) v;
    }

    public void setText(int id, String value) {
        TextView view = getView(id);
        if(null != view){
            view.setText(value);
        }
    }
}

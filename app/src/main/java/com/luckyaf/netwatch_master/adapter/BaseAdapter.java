package com.luckyaf.netwatch_master.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/21
 */
public abstract class BaseAdapter <T> extends RecyclerView.Adapter<CommonViewHolder>{

    protected Context mContext;
    protected int mLayoutId;
    protected List<T> dataSource;
    protected LayoutInflater mInflater;
    protected ViewGroup mViewGroup;

    private OnItemClickListener mOnItemClickListener;



    public BaseAdapter(Context context, List<T> data, int layoutId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        dataSource = data;
    }

    public BaseAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommonViewHolder viewHolder = CommonViewHolder.get(parent, mLayoutId);
        if (null == mViewGroup) {
            mViewGroup = parent;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
        setListener(position, holder);
        convert(holder, dataSource.get(position),position);
    }

    protected boolean clickable(int viewType) {
        return true;
    }

    protected int getPosition(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition();
    }


    @SuppressWarnings("unchecked")
    protected void setListener(final int position, final CommonViewHolder viewHolder) {
        if (!clickable(getItemViewType(position))) {
            return;
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mViewGroup, v, dataSource.get(position), position);
                }
            }
        });


        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = getPosition(viewHolder);
                    return mOnItemClickListener.onItemLongClick(mViewGroup, v, dataSource.get(position), position);
                }
                return false;
            }
        });
    }

    /**
     * 显示item内容
     * @param holder  holder
     * @param data  数据
     * @param position  位置
     */

    public abstract void convert(CommonViewHolder holder, T data, int position);

    @Override
    public int getItemCount() {
        return dataSource != null ? dataSource.size() : 0;
    }

}
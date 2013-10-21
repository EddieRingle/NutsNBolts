package io.github.eddieringle.android.libs.nutsnbolts.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;

public abstract class NListAdapter<T> extends BaseAdapter {

    private boolean mNotifyOnChange = true;

    private ArrayList<T> mData;

    private Context mContext;

    private LayoutInflater mInflater;

    public NListAdapter(Context context) {
        super();
        mContext = context;
        mData = new ArrayList<T>();
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

    public void addAll(Collection<? extends T> collection) {
        mData.addAll(collection);
        if (mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void appendWithItems(Collection<T> data) {
        fillWithItems(data, true);
    }

    public void clear() {
        mData.clear();
        if (mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    protected void fillWithItems(Collection<T> data, boolean append) {
        if (!append) {
            clear();
        }
        addAll(data);
    }

    public void fillWithItems(Collection<T> data) {
        fillWithItems(data, false);
    }

    public Context getContext() {
        return mContext;
    }

    public ArrayList<T> getAll() {
        return mData;
    }

    public void remove(int position) {
        mData.remove(position);
        if (mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }
}

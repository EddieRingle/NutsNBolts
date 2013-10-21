package io.github.eddieringle.android.libs.nutsnbolts.widget;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;

import java.util.ArrayList;

/**
 * Utility adapter that supports adding headers and footers
 */
public class HeaderFooterListAdapter<E extends BaseAdapter> extends HeaderViewListAdapter {

    private final ListView mList;

    private final ArrayList<FixedViewInfo> mHeaders;

    private final ArrayList<FixedViewInfo> mFooters;

    private final E mWrapped;

    /**
     * Create header footer adapter
     */
    public HeaderFooterListAdapter(ListView view, E adapter) {
        this(new ArrayList<FixedViewInfo>(), new ArrayList<FixedViewInfo>(),
                view, adapter);
    }

    private HeaderFooterListAdapter(ArrayList<FixedViewInfo> headerViewInfos,
                                    ArrayList<FixedViewInfo> footerViewInfos, ListView view, E adapter) {
        super(headerViewInfos, footerViewInfos, adapter);

        mHeaders = headerViewInfos;
        mFooters = footerViewInfos;
        mList = view;
        mWrapped = adapter;
    }

    /**
     * Add header
     *
     * @return this adapter
     */
    public HeaderFooterListAdapter<E> addHeader(View view, Object data,
                                                boolean isSelectable) {
        FixedViewInfo info = mList.new FixedViewInfo();
        info.view = view;
        info.data = data;
        info.isSelectable = isSelectable;

        mHeaders.add(info);
        mWrapped.notifyDataSetChanged();
        return this;
    }

    /**
     * Add header
     *
     * @return this adapter
     */
    public HeaderFooterListAdapter<E> addFooter(View view, Object data,
                                                boolean isSelectable) {
        FixedViewInfo info = mList.new FixedViewInfo();
        info.view = view;
        info.data = data;
        info.isSelectable = isSelectable;

        mFooters.add(info);
        mWrapped.notifyDataSetChanged();
        return this;
    }

    @Override
    public boolean removeHeader(View v) {
        boolean removed = super.removeHeader(v);
        if (removed) {
            mWrapped.notifyDataSetChanged();
        }
        return removed;
    }

    @Override
    public boolean removeFooter(View v) {
        boolean removed = super.removeFooter(v);
        if (removed) {
            mWrapped.notifyDataSetChanged();
        }
        return removed;
    }

    @Override
    public E getWrappedAdapter() {
        return mWrapped;
    }

    @Override
    public boolean isEmpty() {
        return mWrapped.isEmpty();
    }
}

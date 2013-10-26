package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.github.eddieringle.android.libs.nutsnbolts.R;
import io.github.eddieringle.android.libs.nutsnbolts.widget.HeaderFooterListAdapter;
import io.github.eddieringle.android.libs.nutsnbolts.widget.NListAdapter;

public abstract class NListFragment<T> extends NFragment
        implements AdapterView.OnItemClickListener,
                   AdapterView.OnItemLongClickListener {

    ListView mListView;

    ProgressBar mProgress;

    TextView mEmptyView;

    ViewGroup mContent;

    /**
     * Implementations of this method should create an instance of a subclass of NListAdapter
     * specialized to the specified template type (e.g., PagedListFragment<Book>
     * implementations should return an instance of BookListAdapter).
     *
     * @return NListAdapter
     */
    public abstract NListAdapter<T> onCreateListAdapter();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setAdapter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        if (v != null) {
            mListView = (ListView) v.findViewById(R.id.list);
            mProgress = (ProgressBar) v.findViewById(R.id.progress);
            mEmptyView = (TextView) v.findViewById(R.id.empty);
            mContent = (ViewGroup) v.findViewById(R.id.content);
        }
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    public ViewGroup getContentView() {
        return mContent;
    }

    public TextView getEmptyView() {
        return mEmptyView;
    }

    public HeaderFooterListAdapter<NListAdapter<T>> getListAdapter() {
        if (getListView() != null) {
            return (HeaderFooterListAdapter<NListAdapter<T>>) getListView().getAdapter();
        } else {
            return null;
        }
    }

    public ListView getListView() {
        return mListView;
    }

    public ProgressBar getProgressBar() {
        return mProgress;
    }

    public NListAdapter<T> getWrappedAdapter() {
        HeaderFooterListAdapter<NListAdapter<T>> wrappingAdapter = getListAdapter();
        if (wrappingAdapter != null) {
            return wrappingAdapter.getWrappedAdapter();
        } else {
            return null;
        }
    }

    public void notifyDataSetChanged() {
        getWrappedAdapter().notifyDataSetChanged();
        setListShown(true);
    }

    public void setListShown(boolean shown) {
        if (shown) {
            mProgress.setVisibility(View.GONE);
            if (!getWrappedAdapter().isEmpty()) {
                mEmptyView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            } else {
                mListView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } else {
            mListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
        }
    }
}

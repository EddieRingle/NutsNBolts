package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;

public class NFragment extends Fragment {

    private boolean mCreateActionBarCalled = false;

    private Configuration mConfiguration;

    public NFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof NActivity)) {
            throw new IllegalStateException("NFragment must only be attached to a NActivity.");
        }
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfiguration = getResources().getConfiguration();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!isAdded()) {
            return;
        }
        mCreateActionBarCalled = false;
        onCreateActionBar(getBaseActivity().getSupportActionBar(), menu, inflater);
        if (!mCreateActionBarCalled) {
            throw new IllegalStateException("You must call super() in onCreateActionBar()");
        }
    }

    public NApplication getApp() {
        return (NApplication) getBaseActivity().getApplication();
    }

    public NActivity getBaseActivity() {
        return (NActivity) getActivity();
    }

    public void onCreateActionBar(ActionBar bar, Menu menu, MenuInflater inflater) {
        mCreateActionBarCalled = true;
        bar.setHomeButtonEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
    }
}

package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class NDialogFragment extends DialogFragment {

    private boolean mCreateActionBarCalled = false;

    private Configuration mConfiguration;

    public NDialogFragment() {
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
        onCreateActionBar(getBaseActivity().getActionBar(), menu, inflater);
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

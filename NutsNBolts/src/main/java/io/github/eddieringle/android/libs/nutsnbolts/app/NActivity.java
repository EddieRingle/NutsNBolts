package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.concurrent.LinkedBlockingQueue;

import io.github.eddieringle.android.libs.nutsnbolts.R;
import io.github.eddieringle.android.libs.nutsnbolts.app.events.RequestWorkEvent;
import io.github.eddieringle.android.libs.nutsnbolts.ext.ScopedBus;

public class NActivity extends Activity {

    public static int NO_LAYOUT = -1;

    private boolean mBoundToWorkerService = false;

    private boolean mDrawerEnabled = false;

    private boolean mDrawerOpened;

    private ActionBarDrawerToggle mDrawerToggle;

    private Class<? extends NFragment> mDrawerClazz;

    private DrawerLayout mDrawerLayout;

    private LinkedBlockingQueue<RequestWorkEvent> mWorkRequests;

    private NServiceConnection mServiceConnection = new NServiceConnection();

    private ScopedBus mBus = new ScopedBus();

    private SharedPreferences mPrefs;

    private SharedPreferences.Editor mPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, NO_LAYOUT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getBus().paused();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBoundToWorkerService = savedInstanceState.getBoolean("boundToWorkerService");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBus().resumed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("boundToWorkerService", mBoundToWorkerService);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBoundToWorkerService) {
            Intent intent = new Intent(getApplicationContext(), WorkerService.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBoundToWorkerService) {
            unbindService(mServiceConnection);
            mBoundToWorkerService = false;
        }
    }

    public ScopedBus getBus() {
        return mBus;
    }

    public SharedPreferences getPrefs() {
        return mPrefs;
    }

    public SharedPreferences.Editor getPrefsEditor() {
        return mPrefsEditor;
    }

    protected void onCreate(Bundle savedInstanceState, int layout) {
        super.onCreate(savedInstanceState);
        if (!(getApplication() instanceof NApplication)) {
            throw new IllegalStateException("Application must be a subclass of NApplication");
        }
        if (layout != NO_LAYOUT) {
            setContentView(layout);
        }
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mPrefsEditor = mPrefs.edit();
        mWorkRequests = new LinkedBlockingQueue<RequestWorkEvent>();
    }

    public boolean queueWorkRequest(RequestWorkEvent event) {
        if (mBoundToWorkerService) {
            getBus().post(event);
            return true;
        } else {
            try {
                mWorkRequests.put(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean replaceLayout() {
        int drawerWidth;
        DrawerLayout.LayoutParams leftLps;
        ViewGroup contentFrame;
        ViewGroup leftDrawer;
        ViewGroup content = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        ViewGroup parent = (ViewGroup) content.getParent();
        if (mDrawerEnabled) {
            if (mDrawerLayout != null) {
                return false;
            }
            mDrawerLayout = new DrawerLayout(NActivity.this);
            contentFrame = new FrameLayout(NActivity.this);
            contentFrame.setId(R.id.content_frame);
            mDrawerLayout.addView(contentFrame,
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    DrawerLayout.LayoutParams.MATCH_PARENT);
            leftDrawer = new FrameLayout(NActivity.this);
            leftDrawer.setId(R.id.left_drawer);
            drawerWidth = getResources().getDimensionPixelSize(R.dimen.left_drawer_width);
            leftLps = new DrawerLayout.LayoutParams(drawerWidth,
                                                    DrawerLayout.LayoutParams.MATCH_PARENT,
                                                    Gravity.START);
            mDrawerLayout.addView(leftDrawer, leftLps);
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
            mDrawerToggle = new ActionBarDrawerToggle(NActivity.this,
                                                      mDrawerLayout,
                                                      R.drawable.ic_drawer_dark,
                                                      0, 0) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    mDrawerOpened = true;
                    invalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    mDrawerOpened = false;
                    invalidateOptionsMenu();
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            parent.removeView(content);
            contentFrame.addView(content);
            parent.addView(mDrawerLayout);
            return true;
        } else {
            if (mDrawerLayout != null) {
                mDrawerLayout = null;
                mDrawerToggle = null;
            }
        }
        return false;
    }

    public boolean requestDrawer(Class<? extends NFragment> clazz) {
        final FragmentManager fm;
        final NFragment drawerFragment;
        mDrawerClazz = clazz;
        if (mDrawerClazz != null) {
            try {
                mDrawerEnabled = true;
                if (replaceLayout()) {
                    drawerFragment = mDrawerClazz.newInstance();
                    fm = getFragmentManager();
                    fm.beginTransaction()
                      .add(R.id.left_drawer, drawerFragment, mDrawerClazz.getName())
                      .commit();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private class NServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RequestWorkEvent event;
            mBoundToWorkerService = true;
            while ((event = mWorkRequests.poll()) != null) {
                getBus().post(event);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
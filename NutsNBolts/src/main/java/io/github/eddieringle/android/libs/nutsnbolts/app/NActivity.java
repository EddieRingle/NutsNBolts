package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import io.github.eddieringle.android.libs.nutsnbolts.R;
import io.github.eddieringle.android.libs.nutsnbolts.ext.ScopedBus;

public class NActivity extends ActionBarActivity {

    public static int NO_LAYOUT = -1;

    private boolean mDrawerEnabled = false;

    private boolean mDrawerOpened;

    private ActionBarDrawerToggle mDrawerToggle;

    private Class<? extends NFragment> mDrawerClazz;

    private DrawerLayout mDrawerLayout;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    }

    void replaceLayout() {
        int drawerWidth;
        DrawerLayout.LayoutParams leftLps;
        ViewGroup contentFrame;
        ViewGroup leftDrawer;
        ViewGroup content = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        ViewGroup parent = (ViewGroup) content.getParent();
        if (mDrawerEnabled) {
            if (mDrawerLayout != null) {
                return;
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
                    supportInvalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    mDrawerOpened = false;
                    supportInvalidateOptionsMenu();
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            parent.removeView(content);
            contentFrame.addView(content);
            parent.addView(mDrawerLayout);
        } else {
            if (mDrawerLayout != null) {
                mDrawerLayout = null;
                mDrawerToggle = null;
            }
        }
    }

    public void requestDrawer(Class<? extends NFragment> clazz) {
        mDrawerClazz = clazz;
        if (mDrawerClazz != null) {
        }
    }

}
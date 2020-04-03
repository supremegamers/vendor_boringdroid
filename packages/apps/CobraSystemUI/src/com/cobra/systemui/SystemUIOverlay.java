package com.cobra.systemui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.systemui.plugins.OverlayPlugin;
import com.android.systemui.plugins.annotations.Requires;

@Requires(target = OverlayPlugin.class, version = OverlayPlugin.VERSION)
public class SystemUIOverlay implements OverlayPlugin {
    private static final String TAG = "SystemUIOverlay";
    private Context mPluginContext;
    private View mNavBar;
    private ViewGroup mBtAllAppsGroup;
    private View mBtAllApps;
    private AllAppsWindow mAllAppsWindow;

    @Override
    public void setup(View statusBar, View navBar) {
        Log.e(TAG, "setup status bar " + statusBar + ", nav bar " + navBar);
        mNavBar = navBar;
        if (navBar instanceof ViewGroup) {
            ((ViewGroup) navBar).addView(mBtAllAppsGroup);
        }
    }

    @Override
    public boolean holdStatusBarOpen() {
        return false;
    }

    @Override
    public void setCollapseDesired(boolean collapseDesired) {

    }

    @Override
    public void onCreate(Context sysUIContext, Context pluginContext) {
        mPluginContext = pluginContext;
        mBtAllAppsGroup = initializeAllAppsButton(mPluginContext, mBtAllAppsGroup);
        mBtAllApps = mBtAllAppsGroup.findViewById(R.id.bt_all_apps);
        mAllAppsWindow = new AllAppsWindow(mPluginContext);
        mBtAllApps.setOnClickListener(mAllAppsWindow);
    }

    @Override
    public void onDestroy() {
        mBtAllAppsGroup.setOnClickListener(null);
        if (mNavBar instanceof ViewGroup) {
            ((ViewGroup) mNavBar).removeView(mBtAllAppsGroup);
        }
        mPluginContext = null;
    }

    @SuppressLint("InflateParams")
    private ViewGroup initializeAllAppsButton(Context context, ViewGroup btAllApps) {
        if (btAllApps != null) {
            return btAllApps;
        }
        btAllApps =
                (ViewGroup) LayoutInflater
                        .from(context)
                        .inflate(R.layout.layout_bt_all_apps, null);
        return (ViewGroup) btAllApps;
    }
}

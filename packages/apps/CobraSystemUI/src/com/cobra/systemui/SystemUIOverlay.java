package com.cobra.systemui;

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
    private View mBtAllApps;

    @Override
    public void setup(View statusBar, View navBar) {
        Log.e(TAG, "setup status bar " + statusBar + ", nav bar " + navBar);
        mNavBar = navBar;
        if (navBar instanceof ViewGroup) {
            ((ViewGroup) navBar).addView(mBtAllApps);
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
        mBtAllApps = initializeAllAppsButton(mPluginContext, mBtAllApps);
    }

    @Override
    public void onDestroy() {
        if (mNavBar instanceof ViewGroup) {
            ((ViewGroup) mNavBar).removeView(mBtAllApps);
        }
    }

    private View initializeAllAppsButton(Context context, View btAllApps) {
        if (btAllApps != null) {
            return btAllApps;
        }
        btAllApps =
                LayoutInflater
                        .from(context)
                        .inflate(R.layout.layout_all_apps, null);
        return btAllApps;
    }
}

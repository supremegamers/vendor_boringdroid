package com.cobra.systemui;

import android.content.Context;
import android.view.View;

import com.android.systemui.plugins.OverlayPlugin;
import com.android.systemui.plugins.annotations.Requires;

@Requires(target = OverlayPlugin.class, version = OverlayPlugin.VERSION)
public class SystemUIOverlay implements OverlayPlugin {
    @Override
    public void setup(View statusBar, View navBar) {

    }

    @Override
    public boolean holdStatusBarOpen() {
        return false;
    }

    @Override
    public void setCollapseDesired(boolean collapseDesired) {

    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void onCreate(Context sysUIContext, Context pluginContext) {

    }

    @Override
    public void onDestroy() {

    }
}

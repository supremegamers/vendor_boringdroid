package com.cobra.systemui;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class TaskInfo {
    private int mId;
    private ComponentName mOriginActivityComponentName;
    private ComponentName mRealActivityComponentName;
    private String mPackageName;
    private Drawable mIcon;

    public int getId() {
        return mId;
    }

    public ComponentName getOriginActivityComponentName() {
        return mOriginActivityComponentName;
    }

    public ComponentName getRealActivityComponentName() {
        return mRealActivityComponentName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setOriginActivityComponentName(ComponentName originActivityComponentName) {
        mOriginActivityComponentName = originActivityComponentName;
    }

    public void setRealActivityComponentName(ComponentName realActivityComponentName) {
        mRealActivityComponentName = realActivityComponentName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof TaskInfo)) {
            return false;
        }
        TaskInfo task = (TaskInfo) another;
        // The task id is unique in system.
        return mId == task.mId;
    }

    @Override
    public int hashCode() {
        return mId;
    }

    @Override
    public String toString() {
        return "Task id " + mId + ", origin " + mOriginActivityComponentName
                + ", real " + mRealActivityComponentName + ", package " + mPackageName;
    }
}

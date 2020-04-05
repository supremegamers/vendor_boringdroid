package com.cobra.systemui;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppStateLayout extends RecyclerView {
    private static final String TAG = "AppStateLayout";
    private static final String APP_STATE_KEY_ADD_TASK = "cobra_app_state_add_task";
    private static final String APP_STATE_KEY_REMOVE_TASK = "cobra_app_state_remove_task";
    private static final String APP_STATE_KEY_TOP_TASK = "cobra_app_state_top_task";

    private LauncherApps mLaunchApps;
    private UserManager mUserManager;
    private ContentResolver mContentResolver;
    private AppStateObserver mObserver = new AppStateObserver();
    private ArrayMap<Uri, String> mListenUris = new ArrayMap<>();
    private List<TaskInfo> mTasks = new ArrayList<>();
    private TaskAdapter mAdapter;

    public AppStateLayout(Context context) {
        this(context, null);
    }

    public AppStateLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppStateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLaunchApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        mUserManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        mContentResolver = context.getContentResolver();
        LinearLayoutManager manager =
                new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        setLayoutManager(manager);
        setHasFixedSize(true);
        mAdapter = new TaskAdapter(context);
        setAdapter(mAdapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Uri addTaskUri = Settings.Secure.getUriFor(APP_STATE_KEY_ADD_TASK);
        mContentResolver.registerContentObserver(addTaskUri, false, mObserver);
        mListenUris.put(addTaskUri, APP_STATE_KEY_ADD_TASK);
        Uri removeTaskUri = Settings.Secure.getUriFor(APP_STATE_KEY_REMOVE_TASK);
        mContentResolver.registerContentObserver(removeTaskUri, false, mObserver);
        mListenUris.put(removeTaskUri, APP_STATE_KEY_REMOVE_TASK);
        Uri topTaskUri = Settings.Secure.getUriFor(APP_STATE_KEY_TOP_TASK);
        mContentResolver.registerContentObserver(topTaskUri, false, mObserver);
        mListenUris.put(topTaskUri, APP_STATE_KEY_TOP_TASK);
    }

    @Override
    protected void onDetachedFromWindow() {
        mContentResolver.unregisterContentObserver(mObserver);
        super.onDetachedFromWindow();
    }

    private void onAppStateChanged(Uri uri) {
        String key = mListenUris.get(uri);
        if (key == null) {
            Log.e(TAG, "Can't find key for uri " + uri);
            return;
        }
        String value = Settings.Secure.getString(mContentResolver, key);
        String[] splits = value.split(";");
        if (splits.length != 3) {
            Log.e(TAG, "The value of " + key + " size is not correct, " + value);
            return;
        }
        String[] originSplits = splits[0].split(":");
        if (originSplits.length != 2) {
            Log.e(TAG, "The part of origin activity component name is not correct " + splits[0]);
            return;
        }
        ComponentName originActivityComponentName =
                ComponentName.unflattenFromString(originSplits[1]);
        String[] realSplits = splits[1].split(":");
        if (realSplits.length != 2) {
            Log.e(TAG, "The part of real activity component name is not correct " + splits[1]);
            return;
        }
        ComponentName realActivityComponentName =
                ComponentName.unflattenFromString(realSplits[1]);
        if (originActivityComponentName == null && realActivityComponentName == null) {
            Log.e(TAG, "The origin and real activity component name can't be null both");
            return;
        }
        String[] idSplits = splits[2].split(":");
        if (idSplits.length != 2) {
            Log.e(TAG, "The part of id is not correct " + splits[2]);
            return;
        }
        int id = -1;
        try {
            id = Integer.parseInt(idSplits[1]);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse id " + idSplits[1]);
            return;
        }
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setId(id);
        taskInfo.setOriginActivityComponentName(originActivityComponentName);
        taskInfo.setRealActivityComponentName(realActivityComponentName);
        if (originActivityComponentName != null) {
            taskInfo.setPackageName(originActivityComponentName.getPackageName());
        } else {
            taskInfo.setPackageName(realActivityComponentName.getPackageName());
        }
        String packageName = taskInfo.getPackageName();
        List<UserHandle> userHandles = mUserManager.getUserProfiles();
        for (UserHandle userHandle : userHandles) {
            List<LauncherActivityInfo> infoList = mLaunchApps.getActivityList(packageName, userHandle);
            if (infoList.size() > 0 && infoList.get(0) != null) {
                taskInfo.setIcon(infoList.get(0).getIcon(0));
                android.util.Log.e(TAG, "package " + packageName + ", icon " + taskInfo.getIcon());
                break;
            }
        }
        int topTaskId = -1;
        switch (key) {
            case APP_STATE_KEY_ADD_TASK:
                mTasks.add(taskInfo);
                topTaskId = taskInfo.getId();
                android.util.Log.e(TAG, "add task " + taskInfo + ", id " + topTaskId);
                break;
            case APP_STATE_KEY_REMOVE_TASK:
                mTasks.remove(taskInfo);
                break;
            case APP_STATE_KEY_TOP_TASK:
                mTasks.remove(taskInfo);
                mTasks.add(taskInfo);
                topTaskId = taskInfo.getId();
                android.util.Log.e(TAG, "top task " + taskInfo + ", id " + topTaskId);
                break;
            default:
                break;
        }
        mAdapter.setData(mTasks);
        if (topTaskId > 0) {
            mAdapter.setTopTaskId(topTaskId);
        }
        mAdapter.notifyDataSetChanged();
    }

    private class AppStateObserver extends ContentObserver {
        public AppStateObserver() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            onAppStateChanged(uri);
        }
    }

    private static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
        private List<TaskInfo> mTasks = new ArrayList<>();
        private Context mContext;
        private int mTopTaskId = -1;

        public TaskAdapter(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewGroup taskInfoLayout =
                    (ViewGroup) LayoutInflater
                            .from(mContext)
                            .inflate(R.layout.layout_task_info, parent, false);
            return new ViewHolder(taskInfoLayout);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TaskInfo taskInfo = mTasks.get(position);
            holder.mIconIV.setImageDrawable(taskInfo.getIcon());
            if (taskInfo.getId() == mTopTaskId) {
                holder.mHighLightLineTV.setImageResource(R.drawable.line_long);
            } else {
                holder.mHighLightLineTV.setImageResource(R.drawable.line_short);
            }
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        public void setData(List<TaskInfo> tasks) {
            mTasks.clear();
            mTasks.addAll(tasks);
        }

        public void setTopTaskId(int id) {
            mTopTaskId = id;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView mIconIV;
            private ImageView mHighLightLineTV;

            public ViewHolder(@NonNull ViewGroup taskInfoLayout) {
                super(taskInfoLayout);
                mIconIV = taskInfoLayout.findViewById(R.id.iv_task_info_icon);
                mHighLightLineTV = taskInfoLayout.findViewById(R.id.iv_highlight_line);
            }
        }
    }
}

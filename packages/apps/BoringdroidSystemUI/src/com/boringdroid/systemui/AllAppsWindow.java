package com.boringdroid.systemui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.boringdroid.systemui.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AllAppsWindow implements View.OnClickListener {
    private static final String TAG = "AppAppsWindow";
    private Context mContext;
    private WindowManager mWindowManager;
    private View mWindowContentView;
    private GridView mAllAppsLayout;
    private AllAppsAdapter mAllAppsAdapter;
    private boolean mShown = false;

    private AppLoaderTask mAppLoaderTask;
    private Handler mHandler = new H(this);

    public AllAppsWindow(Context context) {
        mContext = context;
        mAllAppsAdapter = new AllAppsAdapter(mContext, mHandler);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mAppLoaderTask = new AppLoaderTask(mContext, mHandler);
    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    @Override
    public void onClick(View v) {
        if (mShown) {
            dismiss();
            return;
        }
        WindowManager.LayoutParams layoutParams = generateLayoutParams(mContext, mWindowManager);
        mWindowContentView = LayoutInflater.from(mContext).inflate(R.layout.layout_all_apps, null);
        mAllAppsLayout = mWindowContentView.findViewById(R.id.gv_all_apps);
        mAllAppsLayout.setEmptyView(mWindowContentView.findViewById(R.id.gv_empty_holder));
        mAllAppsLayout.setAdapter(mAllAppsAdapter);
        int elevation = mContext.getResources().getInteger(R.integer.all_apps_elevation);
        mWindowContentView.setElevation(elevation);
        mWindowContentView.setOnTouchListener(
                (windowView, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        dismiss();
                    }
                    return false;
                }
        );
        mWindowManager.addView(mWindowContentView, layoutParams);
        mAppLoaderTask.start();
        mShown = true;
    }

    private WindowManager.LayoutParams generateLayoutParams(Context context,
                                                            WindowManager windowManager) {
        int windowWidth =
                context.getResources().getDimensionPixelSize(R.dimen.all_apps_window_width);
        int windowHeight =
                context.getResources().getDimensionPixelSize(R.dimen.all_apps_window_height);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                windowWidth,
                windowHeight,
                WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.RGB_565
        );
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 0;
        layoutParams.y = displayMetrics.heightPixels - windowHeight;
        return layoutParams;
    }

    private void dismiss() {
        try {
            mWindowManager.removeViewImmediate(mWindowContentView);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Catch exception when remove all apps window", e);
        }
        mWindowContentView = null;
        mShown = false;
    }

    private void notifyLoadSucceed() {
        List<AppInfo> appInfoList = mAppLoaderTask.getAllApps();
        mAllAppsAdapter.setData(appInfoList);
        mAllAppsAdapter.notifyDataSetChanged();
    }

    private static final class AllAppsAdapter extends BaseAdapter {
        private List<AppInfo> mAppInfoList = new ArrayList<>();
        private Context mContext;
        private Handler mHandler;

        public AllAppsAdapter(Context context, Handler handler) {
            mContext = context;
            mHandler = handler;
        }

        public void setData(List<AppInfo> appInfoList) {
            mAppInfoList.clear();
            mAppInfoList.addAll(appInfoList);
        }

        @Override
        public int getCount() {
            return mAppInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView =
                        LayoutInflater
                                .from(mContext)
                                .inflate(R.layout.layout_app_info, parent, false);
            }
            AppInfo appInfo = (AppInfo) getItem(position);
            TextView nameTv = convertView.findViewById(R.id.app_info_name);
            nameTv.setText(appInfo.getName());
            ImageView iconIv = convertView.findViewById(R.id.app_info_icon);
            iconIv.setImageDrawable(appInfo.getIcon());
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setComponent(appInfo.getComponentName());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                mHandler.sendEmptyMessage(HandlerConstant.H_DISMISS_ALL_APPS_WINDOW);
            });
            return convertView;
        }
    }

    private static final class H extends Handler {
        private WeakReference<AllAppsWindow> mAllAppsWindow;

        public H(AllAppsWindow allAppsWindow) {
            mAllAppsWindow = new WeakReference<>(allAppsWindow);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerConstant.H_LOAD_SUCCEED:
                    runMethodSafely(AllAppsWindow::notifyLoadSucceed);
                    break;
                case HandlerConstant.H_DISMISS_ALL_APPS_WINDOW:
                    runMethodSafely(AllAppsWindow::dismiss);
                    break;
                default:
                    break;
            }
        }

        private void runMethodSafely(RunAllAppsWindowMethod method) {
            if (mAllAppsWindow != null && mAllAppsWindow.get() != null) {
                method.run(mAllAppsWindow.get());
            }
        }

        private interface RunAllAppsWindowMethod {
            void run(AllAppsWindow allAppsWindow);
        }
    }
}

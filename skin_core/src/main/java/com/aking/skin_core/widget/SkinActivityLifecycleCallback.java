package com.aking.skin_core.widget;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aking.skin_core.i.ISkinNotifyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rick at 2023/3/18 22:38.
 */
public class SkinActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks, ISkinNotifyEvent {
    private final List<Activity> mActivities = new ArrayList<>();

    @Override
    public void notifySkinHolder() {
        for (Activity activity : mActivities) {
            if (activity instanceof SkinBaseActivity) {
                ((SkinBaseActivity) activity).mFactory.apply();
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (activity instanceof SkinBaseActivity) {
            mActivities.remove(activity);
            mActivities.add(activity);
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activity instanceof SkinBaseActivity) {
            ((SkinBaseActivity) activity).mFactory.destroy();
            ((SkinBaseActivity) activity).mFactory = null;
            mActivities.remove(activity);
        }
    }
}

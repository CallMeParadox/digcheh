package com.digcheh.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class DigchehApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FontHelper.init(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(Activity activity) {}

            @Override
            public void onActivityResumed(Activity activity) {
                if (activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
                    FontHelper.applyVazirmatn(activity.getWindow().getDecorView(), activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivityStopped(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}
        });
    }
}

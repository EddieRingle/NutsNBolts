package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.app.Application;
import android.content.Intent;

import com.squareup.otto.Bus;

public class NApplication extends Application {

    private static WorkManager sWorkManager = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent workerServiceIntent = new Intent(this, WorkerService.class);
        startService(workerServiceIntent);
        sWorkManager = new WorkManager(this);

    }

    public static Bus getBus() {
        return NBusProvider.getBus();
    }

    public static WorkManager getWorkManager() {
        return sWorkManager;
    }
}

package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class NApplication extends Application {

    private static AndroidBus sBUS = null;

    public static Bus getBus() {
        if (sBUS == null) {
            sBUS = new AndroidBus();
        }
        return sBUS;
    }

    static class AndroidBus extends Bus {

        private final Handler mMainThread = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                mMainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        AndroidBus.super.post(event);
                    }
                });
            }
        }
    }
}

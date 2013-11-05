package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.app.Application;

import com.squareup.otto.Bus;

public class NApplication extends Application {

    public static Bus getBus() {
        return NBusProvider.getBus();
    }
}

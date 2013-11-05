package io.github.eddieringle.android.libs.nutsnbolts.ext;

import com.squareup.otto.Bus;

import java.util.HashSet;
import java.util.Set;

import io.github.eddieringle.android.libs.nutsnbolts.app.NApplication;
import io.github.eddieringle.android.libs.nutsnbolts.app.NBusProvider;

public class ScopedBus {

    private boolean mActive;

    private final Bus mBus = NBusProvider.getBus();

    private final Set<Object> mObjects = new HashSet<Object>();

    public void paused() {
        mActive = false;
        for (Object obj : mObjects) {
            mBus.unregister(obj);
        }
    }

    public void post(Object event) {
        mBus.post(event);
    }

    public void register(Object obj) {
        mObjects.add(obj);
        if (mActive) {
            mBus.register(obj);
        }
    }

    public void resumed() {
        mActive = true;
        for (Object obj : mObjects) {
            mBus.register(obj);
        }
    }

    public void unregister(Object obj) {
        mObjects.remove(obj);
        if (mActive) {
            mBus.unregister(obj);
        }
    }
}

package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.content.Context;

import java.util.Collections;
import java.util.Map;

public abstract class RequestWorkEvent<T> {

    private Context mReceivingContext = null;

    private WorkStatus mWorkStatus = WorkStatus.UNKNOWN;

    public RequestWorkEvent(Context receivingContext) {
        mReceivingContext = receivingContext;
    }

    public abstract T doWork();

    public Map getIdentifyingParams() {
        return Collections.EMPTY_MAP;
    }

    public Context getReceivingContext() {
        return mReceivingContext;
    }

    public WorkStatus getWorkStatus() {
        return mWorkStatus;
    }

    public void setReceivingContext(Context receivingContext) {
        mReceivingContext = receivingContext;
    }

    public void setWorkStatus(WorkStatus status) {
        mWorkStatus = status;
    }

}

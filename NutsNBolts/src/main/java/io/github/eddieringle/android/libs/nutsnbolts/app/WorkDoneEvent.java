package io.github.eddieringle.android.libs.nutsnbolts.app;

public abstract class WorkDoneEvent<T> {

    private T mResult;

    public WorkDoneEvent(T result) {
        mResult = result;
    }

    public T getResult() {
        return mResult;
    }

    public void setResult(T result) {
        mResult = result;
    }
}

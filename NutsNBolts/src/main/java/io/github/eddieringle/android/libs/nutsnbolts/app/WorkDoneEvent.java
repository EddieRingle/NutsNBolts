package io.github.eddieringle.android.libs.nutsnbolts.app;

public abstract class WorkDoneEvent<T> {

    private RequestWorkEvent<T> mRequest;

    private T mResult;

    public WorkDoneEvent(RequestWorkEvent<T> request, T result) {
        mRequest = request;
        mResult = result;
    }

    public RequestWorkEvent<T> getRequest() {
        return mRequest;
    }

    public T getResult() {
        return mResult;
    }

    public boolean isRedelivered() {
        return WorkStatus.RESULT_REDELIVERED.equals(getRequest().getWorkStatus());
    }

    public void setRequest(RequestWorkEvent<T> request) {
        mRequest = request;
    }

    public void setResult(T result) {
        mResult = result;
    }
}

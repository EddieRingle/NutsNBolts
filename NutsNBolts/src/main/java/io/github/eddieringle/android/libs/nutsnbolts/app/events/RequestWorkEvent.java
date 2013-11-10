package io.github.eddieringle.android.libs.nutsnbolts.app.events;

public final class RequestWorkEvent {

    private Callbacks mCallbacks;

    public RequestWorkEvent(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public Callbacks getCallbacks() {
        return mCallbacks;
    }

    public static interface Callbacks<T> {

        public void onThreadIdentified(Thread thread);

        public T onWork();

        public WorkDoneEvent getResultEvent(T result);
    }
}

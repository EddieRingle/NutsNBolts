package io.github.eddieringle.android.libs.nutsnbolts.app.events;

public abstract class WorkDoneEvent<T> {

    public T data;

    public WorkDoneEvent(T data) {
        this.data = data;
    }
}

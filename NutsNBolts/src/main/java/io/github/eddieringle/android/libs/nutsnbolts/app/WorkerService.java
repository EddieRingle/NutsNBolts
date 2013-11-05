package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;

import com.squareup.otto.Subscribe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.github.eddieringle.android.libs.nutsnbolts.app.events.RequestWorkEvent;

public class WorkerService extends Service {

    private static final int KEEP_ALIVE_TIME = 1;

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final BlockingQueue<Runnable> mWorkQueue = new LinkedBlockingDeque<Runnable>();

    private ThreadPoolExecutor mThreadPool;

    @Override
    public void onCreate() {
        super.onCreate();
        NApplication.getBus().register(this);
        mThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mWorkQueue
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Subscribe
    public void onRequestWork(RequestWorkEvent event) {
        if (event != null && event.getCallbacks() != null) {
            mThreadPool.execute(new WorkerRunnable(event.getCallbacks()));
        }
    }

    private static class WorkerRunnable implements Runnable {

        private RequestWorkEvent.Callbacks mCallbacks;

        public WorkerRunnable(RequestWorkEvent.Callbacks callbacks) {
            mCallbacks = callbacks;
        }

        @Override
        public void run() {
            final Object result;
            Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            if (mCallbacks != null) {
                mCallbacks.onThreadIdentified(Thread.currentThread());
                if (Thread.interrupted()) {
                    return;
                }
                result = mCallbacks.onWork();
                NApplication.getBus().post(mCallbacks.getResultEvent(result));
            }
        }
    }
}

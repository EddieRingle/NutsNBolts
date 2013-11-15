package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;

import com.squareup.otto.Subscribe;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkerService extends Service {

    private static final int KEEP_ALIVE_TIME = 1;

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final Set<WorkBundle> mSavedResults = new HashSet<WorkBundle>();

    private final HashMap<Integer, RequestWorkEvent> mWorkRequests = new HashMap<Integer, RequestWorkEvent>();

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
        checkSavedRequests();
        return new Binder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        checkSavedRequests();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Subscribe
    public void onRequestResultFlush(FlushResultEvent event) {
        if (event != null && event.getTargetClazz() != null) {
            clearResultsOfType(event.getTargetClazz());
        }
    }

    @Subscribe
    public void onRequestWork(RequestWorkEvent event) {
        int hashCode = event.hashCode();
        if (event != null && !mWorkRequests.containsKey(hashCode)) {
            try {
                mThreadPool.execute(new WorkerRunnable(event));
            } catch (RejectedExecutionException ignored) {
                return;
            }
            mWorkRequests.put(hashCode, event);
            event.setWorkStatus(WorkStatus.QUEUED);
        }
    }

    void checkSavedRequests() {
        Iterator<WorkBundle> savedItr = mSavedResults.iterator();
        WorkBundle bundle;
        while (savedItr.hasNext()) {
            bundle = savedItr.next();
            if (bundle.request != null) {
                if (bundle.request.getReceivingContext() != null) {
                    whenDone(bundle.request, bundle.result);
                }
            }
        }
    }

    void clearResultsOfType(Class<? extends RequestWorkEvent> clazz) {
        Iterator<WorkBundle> savedItr = mSavedResults.iterator();
        WorkBundle bundle;
        if (clazz == null) {
            return;
        }
        while (savedItr.hasNext()) {
            bundle = savedItr.next();
            if (bundle.request != null) {
                if (bundle.request.getClass().getName().equals(clazz.getName())) {
                    savedItr.remove();
                }
            }
        }
    }

    void saveResult(WorkBundle bundle) {
        Iterator<WorkBundle> bundleItr = mSavedResults.iterator();
        WorkBundle itrBundle;
        while (bundleItr.hasNext()) {
            itrBundle = bundleItr.next();
            if (bundle.request.getClass().getName().equals(itrBundle.request.getClass().getName())) {
                if (bundle.request.getIdentifyingParams().equals(itrBundle.request.getIdentifyingParams())) {
                    bundleItr.remove();
                    break;
                }
            }
        }
        mSavedResults.add(bundle);
    }

    void whenDone(RequestWorkEvent workRequest, Object result) {
        final Class<? extends WorkDoneEvent> workDoneClass;
        final WhenDone whenDone;
        WorkDoneEvent workDoneEvent = null;
        if (workRequest != null) {
            whenDone = workRequest.getClass().getAnnotation(WhenDone.class);
            if (whenDone != null) {
                workDoneClass = whenDone.value();
                for (Constructor c : workDoneClass.getDeclaredConstructors()) {
                    if (c.getGenericParameterTypes().length == 2) {
                        try {
                            workDoneEvent = workDoneClass.cast(c.newInstance(workRequest, result));
                        } catch (Exception ignored) {
                        }
                        break;
                    }
                }
                if (workDoneEvent == null) {
                    try {
                        workDoneEvent = workDoneClass.newInstance();
                    } catch (Exception e) {
                        return;
                    }
                }
                if (WorkStatus.RESULT_DELIVERED.equals(workRequest.getWorkStatus())) {
                    workRequest.setWorkStatus(WorkStatus.RESULT_REDELIVERED);
                } else {
                    workRequest.setWorkStatus(WorkStatus.RESULT_DELIVERED);
                }
                NApplication.getBus().post(workDoneEvent);
            }
        }
    }

    private class WorkerRunnable implements Runnable {

        private volatile RequestWorkEvent mWorkRequest;

        public WorkerRunnable(RequestWorkEvent workRequest) {
            mWorkRequest = workRequest;
        }

        @Override
        public void run() {
            final Object result;
            Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            if (mWorkRequest != null) {
                mWorkRequest.setWorkStatus(WorkStatus.WORKING);
                if (Thread.interrupted()) {
                    mWorkRequest.setWorkStatus(WorkStatus.CANCELED);
                    return;
                }
                try {
                    result = mWorkRequest.doWork();
                } catch (Exception e) {
                    mWorkRequest.setWorkStatus(WorkStatus.CANCELED);
                    return;
                }
                if (mWorkRequest.getReceivingContext() != null) {
                    whenDone(mWorkRequest, result);
                } else {
                    mWorkRequest.setWorkStatus(WorkStatus.WAITING_TO_DELIVER);
                }
                saveResult(new WorkBundle(mWorkRequest, result));
            }
        }
    }

    private static class WorkBundle {

        public RequestWorkEvent request;

        public Object result;

        public WorkBundle(RequestWorkEvent request, Object result) {
            this.request = request;
            this.result = result;
        }
    }

    public static class FlushResultEvent {

        private Class<? extends RequestWorkEvent> mTargetClazz;

        public FlushResultEvent(Class<? extends RequestWorkEvent> targetClazz) {
            mTargetClazz = targetClazz;
        }

        public Class<? extends RequestWorkEvent> getTargetClazz() {
            return mTargetClazz;
        }

        public void setTargetClazz(Class<? extends RequestWorkEvent> targetClazz) {
            mTargetClazz = targetClazz;
        }
    }
}

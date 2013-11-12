package io.github.eddieringle.android.libs.nutsnbolts.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkManager {

    private boolean mBoundToWorkerService = false;

    private Set<RequestWorkEvent> mWorkRequests = new HashSet<RequestWorkEvent>();

    private Set<RequestWorkEvent> mSavedRequests = new HashSet<RequestWorkEvent>();

    private LinkedBlockingQueue<RequestWorkEvent> mWorkQueue = new LinkedBlockingQueue<RequestWorkEvent>();

    private Context mContext;

    private NActivity mCurrentActivity;

    private NServiceConnection mServiceConnection = new NServiceConnection();

    WorkManager(Context context) {
        mContext = context;
    }

    public void readyActivity(NActivity activity) {
        Intent serviceIntent = new Intent(mContext, WorkerService.class);
        mCurrentActivity = activity;
        activity.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void releaseActivity(NActivity activity) {
        if (mCurrentActivity == activity) {
            mBoundToWorkerService = false;
            activity.unbindService(mServiceConnection);
            mCurrentActivity = null;
        }
    }

    public void restoreState(NActivity activity, Bundle savedState) {
        Iterator<RequestWorkEvent> requestItr;
        RequestWorkEvent request;
        if (activity == null || savedState == null) {
            if (savedState == null) {
                mSavedRequests.clear();
            }
            return;
        }
        requestItr = mSavedRequests.iterator();
        while (requestItr.hasNext()) {
            request = requestItr.next();
            request.setReceivingContext(activity);
            mWorkRequests.add(request);
            requestItr.remove();
        }
    }

    public void saveState(NActivity activity, Bundle savedState) {
        Iterator<RequestWorkEvent> requestItr;
        RequestWorkEvent request;
        if (activity == null || savedState == null) {
            return;
        }
        mSavedRequests.clear();
        requestItr = mWorkRequests.iterator();
        while (requestItr.hasNext()) {
            request = requestItr.next();
            if (activity == request.getReceivingContext()) {
                request.setReceivingContext(null);
                mSavedRequests.add(request);
                requestItr.remove();
            }
        }
    }

    public boolean queueRequest(NActivity activity, RequestWorkEvent request) {
        if (request == null || mWorkRequests.contains(request)) {
            return false;
        }
        request.setReceivingContext(activity);
        request.setWorkStatus(WorkStatus.QUEUED);
        mWorkRequests.add(request);
        if (mBoundToWorkerService) {
            NApplication.getBus().post(request);
        } else {
            try {
                mWorkQueue.put(request);
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    private class NServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RequestWorkEvent event;
            mBoundToWorkerService = true;
            while ((event = mWorkQueue.poll()) != null) {
                NApplication.getBus().post(event);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }
}

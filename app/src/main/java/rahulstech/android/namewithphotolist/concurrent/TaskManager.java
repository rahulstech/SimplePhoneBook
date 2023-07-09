package rahulstech.android.namewithphotolist.concurrent;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import rahulstech.android.namewithphotolist.util.Callback;

@SuppressWarnings("unchecked")
public class TaskManager {

    private static final String TAG = "TaskManager";

    private static final Object mCallbackLock = new Object();
    private static final Object mResultLock = new Object();

    private final Map<Integer, TaskCallback> mCallbacks;
    private final Map<Integer, Object> mResults;

    public TaskManager() {
        mCallbacks = new HashMap<>();
        mResults = new HashMap<>();
    }

    public void addTaskCallback(int code, Callback callback) {
        if (callback == null) throw new NullPointerException("null == callback");
        if (hasResult(code)) {
            Object result = getResult(code);
            callback.call(result);
            clear(code);
            return;
        }
        synchronized (mCallbackLock) {
            TaskCallback taskCallback = new TaskCallback(code);
            mCallbacks.put(code, taskCallback);
            taskCallback.update(callback);
        }
    }

    public void execute(int code, Callback<Void,Object> task) {
        if (task == null) throw new NullPointerException("null == task");
        AppExecutors.backgroundExecutor().execute(()->{
            try {
                Object result = task.call(null);
                notifyResultOnMain(code, result);
            }
            catch (Exception ex) {
                Log.e(TAG,"execute",ex);
            }
        });
    }

    public void notifyResultOnMain(int code, @Nullable Object result) {
        AppExecutors.mainExecutor().execute(()-> {
            TaskCallback callback = mCallbacks.get(code);
            Log.d(TAG,"notify result on main thread");
            callback.call(result);
        });
    }

    public boolean hasResult(int code) {
        synchronized (mResultLock) {
            return mResults.containsKey(code);
        }
    }

    @Nullable
    public Object getResult(int code) throws IllegalStateException {
        synchronized (mResultLock) {
            if (!mResults.containsKey(code)) {
                throw new IllegalStateException("no result found for code="+code);
            }
            return mResults.get(code);
        }
    }

    private void clear(int code) {
        synchronized (mCallbackLock) {
            mCallbacks.remove(code);
        }
        synchronized (mResultLock) {
            mResults.remove(code);
        }
    }

    private void saveResult(int code, @Nullable Object result) {
        synchronized (mResults) {
            mResults.put(code,result);
        }
    }

    private class TaskCallback implements Callback {

        final int code;

        Callback mCallback;

        TaskCallback(int code) {
            this.code = code;
        }

        void update(@NonNull Callback callback) {
            mCallback = callback;
        }

        @Override
        public Object call(Object param) {
            Callback callback = mCallback;
            if (null != callback) {
                callback.call(param);
                clear(code);
            }
            else {
                saveResult(code, param);
            }
            return null;
        }
    }
}

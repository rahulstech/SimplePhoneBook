package rahulstech.android.namewithphotolist.concurrent;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class AppExecutors {

    private static class MainExecutor implements Executor {

        private final Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mHandler.post(command);
        }
    }

    private static MainExecutor mMainExecutor = null;

    public static Executor mainExecutor() {
        if (null == mMainExecutor) {
            mMainExecutor = new MainExecutor();
        }
        return mMainExecutor;
    }

    public static Executor backgroundExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}

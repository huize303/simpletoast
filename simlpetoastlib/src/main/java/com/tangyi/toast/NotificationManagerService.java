package com.tangyi.toast;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tangyi on 2017/12/26.
 *
 * @see com.android.server.notification.NotificationManagerService
 */

public class NotificationManagerService {
    private static int MAXSIZE = 10;

    public static final int LONG_DELAY = 3500;
    public static final int SHORT_DELAY = 2000; // 2 seconds

    // message codes
    static final int MESSAGE_TIMEOUT = 2;

    private static NotificationManagerService instance;

    private static Handler mHandler;
    private WindowManager manger;
    private List<SimpleToast> mToastQueue = new LinkedList<>();

    private SimpleToast displayingToast;

    public static synchronized NotificationManagerService getInstance() {
        if (instance == null) {
            instance = new NotificationManagerService();
        }
        return instance;
    }

    public NotificationManagerService() {
        mHandler = new WorkerHandler();
    }

    private void handleTimeout(SimpleToast record) {

        synchronized (mToastQueue) {
            int index = mToastQueue.indexOf(record);
            if (index >= 0) {
                cancelToastLocked(index);
            }
        }
    }

    void cancelToastLocked(int index) {
        SimpleToast record = mToastQueue.get(index);

        hide(record);
        SimpleToast lastToast = mToastQueue.remove(index);
        if (mToastQueue.size() > 0) {
            // Show the next one. If the callback fails, this will remove
            // it from the list, so don't assume that the list hasn't changed
            // after this point.
            showNextToastLocked();
        }
    }

    public void enqueueToast(SimpleToast simperToast) {

        synchronized (mToastQueue) {
            int index = 0;

            int n = mToastQueue.size();
            if (n >= MAXSIZE) {
                return;
            }
            mToastQueue.add(simperToast);
            index = mToastQueue.size() - 1;

            if (index == 0) {
                showNextToastLocked();
            }
        }
    }

    public void showNextToastLocked() {
        SimpleToast record = mToastQueue.get(0);
        if (record != null) {
            show(record);
            scheduleTimeoutLocked(record);
        }
    }

    public void clearNotifications(Activity activity) {
        synchronized (mToastQueue) {
            clearDirtyToasts(activity);
        }
    }

    private void clearDirtyToasts(Context context) {
        if (mHandler.hasMessages(MESSAGE_TIMEOUT)) {
            mHandler.removeMessages(MESSAGE_TIMEOUT);
        }
        if (displayingToast != null) {
            hide(displayingToast);
        }

        List<SimpleToast> list = mToastQueue;
        List<SimpleToast> removeList = new LinkedList<>();
        for (SimpleToast simperToast : list) {
            if (context == simperToast.context) {
                removeList.add(simperToast);
            }
        }
        mToastQueue.removeAll(removeList);

        int index = mToastQueue.size() - 1;
        if (index == 0) {
            showNextToastLocked();
        }
    }

    private void scheduleTimeoutLocked(SimpleToast record) {
        mHandler.removeCallbacksAndMessages(record);
        Message m = Message.obtain(mHandler, MESSAGE_TIMEOUT, record);
        long delay = record.toast.getDuration() == Toast.LENGTH_LONG ? LONG_DELAY : SHORT_DELAY;
        mHandler.sendMessageDelayed(m, delay);
    }

    public void show(SimpleToast toast) {

        try {
            View view = toast.toast.getView();
            manger = (WindowManager) toast.context.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.TRANSLUCENT;

            params.setTitle("Toast");
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

            int windowAnimations = getReflactField("com.android.internal.R$style", "Animation_Toast");

            if (windowAnimations != -1) {
                params.windowAnimations = windowAnimations;
            }

            int y = getReflactField("com.android.internal.R$dimen", "toast_y_offset");
            if (y == -1) {
                params.y = dpToPixel(toast.context, 64);
            } else {
                params.y = toast.context.getResources().getDimensionPixelOffset(y);
            }

            int gravity = getReflactField("com.android.internal.R$integer", "config_toastDefaultGravity");

            if (y == -1) {
                params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            } else {
                params.gravity = toast.context.getResources().getInteger(gravity);
            }

            manger.addView(view, params);
            toast.view = view;
            displayingToast = toast;
        } catch (Exception e) {
            e.printStackTrace();
            clearDirtyToasts(toast.context);
        }
    }

    public void hide(SimpleToast toast) {
        try {
            if (toast.view != null && ViewCompat.isAttachedToWindow(toast.view)) {
                WindowManager manger = (WindowManager) toast.context.getSystemService(Context.WINDOW_SERVICE);
                manger.removeView(toast.view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            displayingToast = null;
        }
    }

    public static int getReflactField(String className, String fieldName) {
        int result = -1;
        try {
            Class<?> clz = Class.forName(className);
            Field field = clz.getField(fieldName);
            field.setAccessible(true);
            result = field.getInt(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {

        } catch (IllegalAccessException e) {

        }

        return result;
    }

    public static int dpToPixel(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private final class WorkerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TIMEOUT:
                    handleTimeout((SimpleToast) msg.obj);
                    break;
            }
        }
    }
}

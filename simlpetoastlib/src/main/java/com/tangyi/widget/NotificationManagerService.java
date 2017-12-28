package com.tangyi.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tangyi on 2017/12/26.
 *
 * @see com.android.server.notification.NotificationManagerService
 */

public class NotificationManagerService {
    private static int MAXSIZE = 50;

    public static final int LONG_DELAY = 3500;
    public static final int SHORT_DELAY = 2000; // 2 seconds

    // message codes
    static final int MESSAGE_TIMEOUT = 2;

    private static NotificationManagerService instance;

    private static Handler mHandler;
    private WindowManager manger;
    private List<Toast> mToastQueue = new LinkedList<>();

    private Toast displayingToast;

    public static synchronized NotificationManagerService getInstance() {
        if (instance == null) {
            instance = new NotificationManagerService();
        }
        return instance;
    }

    public NotificationManagerService() {
        mHandler = new WorkerHandler();
    }

    private void handleTimeout(Toast record) {

        synchronized (mToastQueue) {
            int index = mToastQueue.indexOf(record);
            if (index >= 0) {
                cancelToastLocked(index);
            }
        }
    }

    void cancelToastLocked(int index) {
        Toast record = mToastQueue.get(index);

        hide(record);
        Toast lastToast = mToastQueue.remove(index);
        if (mToastQueue.size() > 0) {
            // Show the next one. If the callback fails, this will remove
            // it from the list, so don't assume that the list hasn't changed
            // after this point.
            showNextToastLocked();
        }
    }

    public void enqueueToast(Toast simperToast) {

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
        Toast record = mToastQueue.get(0);
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

        List<Toast> list = mToastQueue;
        List<Toast> removeList = new LinkedList<>();
        for (Toast simperToast : list) {
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

    private void scheduleTimeoutLocked(Toast record) {
        mHandler.removeCallbacksAndMessages(record);
        Message m = Message.obtain(mHandler, MESSAGE_TIMEOUT, record);
        long delay = record.toast.getDuration() == android.widget.Toast.LENGTH_LONG ? LONG_DELAY : SHORT_DELAY;
        mHandler.sendMessageDelayed(m, delay);
    }

    public void show(Toast toast) {

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
            if(toast.mX != -1) {
                params.x = toast.mX;
            }

            int y = getReflactField("com.android.internal.R$dimen", "toast_y_offset");

            if(toast.mY != -1) {
                params.y = toast.mY;
            } else {
                if (y == -1) {
                    params.y = dpToPixel(toast.context, 64);
                } else {
                    params.y = toast.context.getResources().getDimensionPixelOffset(y);
                }
            }



            int gravity = toast.mGravity;
            if(gravity != -1) {
                params.gravity = gravity;
                if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                    params.horizontalWeight = 1.0f;
                }
                if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                    params.verticalWeight = 1.0f;
                }
            } else {
                gravity = getReflactField("com.android.internal.R$integer", "config_toastDefaultGravity");
                if (gravity == -1) {
                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                } else {
                    params.gravity = toast.context.getResources().getInteger(gravity);
                }
            }

            if(toast.mVerticalMargin != -1) {
                params.verticalMargin = toast.mVerticalMargin;
            }
            if(toast.mHorizontalMargin != -1) {
                params.horizontalMargin = toast.mHorizontalMargin;
            }

            manger.addView(view, params);
            toast.view = view;
            displayingToast = toast;
        } catch (Exception e) {
            e.printStackTrace();
            clearDirtyToasts(toast.context);
        }
    }


    public void cancelToast(Toast toast) {
        synchronized (mToastQueue) {
                int index = -1;
                for(int i = 0;i<mToastQueue.size();i++) {
                    if(mToastQueue.get(i) == toast) {
                        index = i;
                        break;
                    }
                }

                if (index >= 0) {
                    Toast record = mToastQueue.remove(index);
                    hide(record);
                } else {

                }
            }
    }


    public void hide(Toast toast) {
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
                    handleTimeout((Toast) msg.obj);
                    break;
            }
        }
    }
}

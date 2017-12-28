package com.tangyi.widget;

import android.app.AppOpsManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by tangyi on 2017/12/26.
 *
 */
public class Toast {

    private boolean systemEnable;

    public Context context;
    public android.widget.Toast toast;
    public View view;
    public int mGravity = -1;
    public int mX = -1;
    public int mY = -1;
    public float mHorizontalMargin = -1;
    public float mVerticalMargin = -1;

    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private static int checkNotification = 0;

    public Toast(Context context) {
        this(context,"",LENGTH_SHORT);
    }

    private Toast(Context context, String message, int duration) {
        this.context = context;
        this.toast = toast;
        if(context instanceof Application)
            checkNotification = 0;
        else
            checkNotification = isNotificationEnabled(context) ? 0 : 1;
        if (checkNotification != 1) {
            systemEnable = true;

        } else {
            systemEnable = false;
        }
        toast = android.widget.Toast.makeText(context, message, duration);
    }


    private Toast(Context context, int idRes, int duration) {
        this(context,context.getResources().getString(idRes),duration);

    }


    public static Toast makeText(Context context, String message, int duration) {
        return new Toast(context,message,duration);
    }
    public static Toast makeText(Context context, int resId, int duration) {
        return new Toast(context,resId,duration);
    }

    public void show() {
        if(systemEnable) {
            toast.show();
        } else {
            NotificationManagerService.getInstance().enqueueToast(this);
        }
    }



    private static boolean isNotificationEnabled(Context context){
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();

            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            Class appOpsClass = null; /* Context.APP_OPS_MANAGER */
            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int)opPostNotificationValue.get(Integer.class);
                return ((int)checkOpNoThrowMethod.invoke(mAppOps,value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }else{
            return false;
        }
    }


    public int getGravity() {
        if(systemEnable) {
            return toast.getGravity();
        } else {
            return mGravity;
        }
    }

    /**
     * Return the X offset in pixels to apply to the gravity's location.
     */
    public int getXOffset() {
        if(systemEnable) {
            return toast.getXOffset();
        } else {
            return mX;
        }
    }

    /**
     * Return the Y offset in pixels to apply to the gravity's location.
     */
    public int getYOffset() {
        if(systemEnable) {
            return toast.getYOffset();
        } else {
            return mY;
        }
    }

    /**
     * Return the horizontal margin.
     */
    public float getHorizontalMargin() {
        return mHorizontalMargin;
    }

    /**
     * Return the vertical margin.
     */
    public float getVerticalMargin() {
        return mVerticalMargin;
    }


    /**
     * Return the view.
     * @see #setView
     */
    public View getView() {
        return toast.getView();
    }


    public void setText(int resId) {
        setText(context.getText(resId));
    }

    public void setText(CharSequence s){
        toast.setText(s);
    }

    /**
     * Set the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     * @see #getGravity
     */
    public void setGravity(int gravity, int xOffset, int yOffset) {
        if(systemEnable) {
           toast.setGravity(gravity,xOffset,yOffset);
        } else {
            mGravity = gravity;
            mX = xOffset;
            mY = yOffset;
        }
    }

    public void cancel() {
        if(systemEnable) {
            toast.cancel();
        } else {
            NotificationManagerService.getInstance().cancelToast(this);
        }
    }


    /**
     * Set how long to show the view for.
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     */
    public void setDuration( int duration) {
        toast.setDuration(duration);
    }

    /**
     * Return the duration.
     *
     */

    public int getDuration() {
        return toast.getDuration();
    }


    /**
     * Set the margins of the view.
     *
     * @param horizontalMargin The horizontal margin, in percentage of the
     *        container width, between the container's edges and the
     *        notification
     * @param verticalMargin The vertical margin, in percentage of the
     *        container height, between the container's edges and the
     *        notification
     */
    public void setMargin(float horizontalMargin, float verticalMargin) {
        mHorizontalMargin = horizontalMargin;
        mVerticalMargin = verticalMargin;
    }

    /**
     * Set the view to show.
     * @see #getView
     */
    public void setView(View view) {
        toast.setView(view);
    }

}
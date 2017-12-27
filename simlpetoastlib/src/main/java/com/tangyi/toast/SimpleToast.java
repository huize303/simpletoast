package com.tangyi.toast;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.IDN;


public class SimpleToast {

    private boolean systemEnable;

    public Context context;
    public android.widget.Toast toast;
    public View view;

    public static final int LENGTH_SHORT = NotificationManagerService.SHORT_DELAY;
    public static final int LENGTH_LONG = NotificationManagerService.LONG_DELAY;

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private static int checkNotification = 0;


    public SimpleToast(Context context, String message, int duration) {
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


    private SimpleToast(Context context, int idRes, int duration) {
        this(context,context.getResources().getString(idRes),duration);

    }


    public static SimpleToast makeText(Context context, String message, int duration) {
        return new SimpleToast(context,message,duration);
    }
    public static SimpleToast makeText(Context context, int resId, int duration) {
        return new SimpleToast(context,resId,duration);
    }

    public void show() {
        if(systemEnable) {
            toast.show();
        } else {
            NotificationManagerService.getInstance().enqueueToast(this);
        }
    }

    public void setText(CharSequence s){
            toast.setText(s);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleToast that = (SimpleToast) o;

        return context.equals(that.context);

    }

    @Override
    public int hashCode() {
        return context.hashCode();
    }
}
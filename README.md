解决android通知被禁用toast无法显示问题，参照NotificationManagerService编写,支持自定义view,兼容toast所有方法。

dependencies {
	        compile 'com.github.huize303:simpletoast:1.1'
	}
  
  
  
  Application onCreate 加入
  
  registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
      @Override
            public void onActivityPaused(Activity activity) {
                NotificationManagerService.getInstance().clearNotifications(activity);
            }
  }


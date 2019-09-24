package com.mskmz.main.activity;

import android.app.Service;
import android.content.Intent;

public interface ServiceInterface {
  /**
   * 把宿主(app)的环境  给  插件
   *
   * @param service
   */
  void insertServiceContext(Service service);

  int onStartCommand(Intent intent, int flags, int startId);

  void onDestroy();
}

package com.mskmz.plugin_comm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.mskmz.main.activity.ServiceInterface;

public class ServiceImpl extends Service implements ServiceInterface {
  /**
   * 使用转发 将某些请求转发给父级应用
   */
  Service mService;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    //不做onBind的考虑 暂时
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void insertServiceContext(Service service) {
    mService = service;
  }
}

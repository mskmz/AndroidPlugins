package com.mskmz.plugins_demo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.mskmz.plugin_comm.ActivityImpl;
import com.mskmz.plugin_comm.ServiceImpl;

//没有环境 需要从父类中偷环境
public class Demo_Service extends ServiceImpl {
  //---------------DEBUG配置---------------------------------------------------------------------------
  private static final String TAG = "Demo_Service>>>";
  private static final boolean DEBUG = true;

  @Override
  public void onCreate() {
    super.onCreate();

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // 开启子线程，执行耗时任务
    if (DEBUG) Log.d(TAG, "onStartCommand: 广播被启动");
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          } finally {
            Log.d(TAG, "插件里面的服务 正在执行中....");
          }
        }
      }
    }).start();
    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}

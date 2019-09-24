package com.mskmz.main.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public interface BroaderInterface {
  /**
   * 把宿主(app)的环境  给  插件
   *
   * @param broadcastReceiver
   */
  void insertBroaderContext(BroadcastReceiver broadcastReceiver);

  void onReceive(Context context, Intent intent);
}

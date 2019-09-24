package com.mskmz.plugins_demo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mskmz.plugin_comm.BroaderImpl;

public class Demo_StaticBroader extends BroaderImpl {
  //---------------DEBUG配置---------------------------------------------------------------------------
  private static final String TAG = "Demo_Broader>>>";
  private static final boolean DEBUG = true;

  @Override
  public void onReceive(Context context, Intent intent) {
    if (DEBUG) Log.d(TAG, "onReceive: 广播收到");
  }
}

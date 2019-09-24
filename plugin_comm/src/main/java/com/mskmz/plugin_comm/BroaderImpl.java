package com.mskmz.plugin_comm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mskmz.main.activity.BroaderInterface;

public class BroaderImpl extends BroadcastReceiver implements BroaderInterface {

  BroadcastReceiver mBroader;

  @Override
  public void insertBroaderContext(BroadcastReceiver broadcastReceiver) {
    mBroader = broadcastReceiver;
  }

  @Override
  public void onReceive(Context context, Intent intent) {

  }
}

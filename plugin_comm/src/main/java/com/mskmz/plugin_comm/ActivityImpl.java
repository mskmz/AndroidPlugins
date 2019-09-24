package com.mskmz.plugin_comm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.mskmz.main.activity.ActivityInterface;

public abstract class ActivityImpl extends Activity implements ActivityInterface {
  //---------------DEBUG配置---------------------------------------------------------------------------
  private static final String TAG = "ActivityImpl>>>";
  private static final boolean DEBUG = true;
  public static final String EXTRA_CLASS_NAME = "extraClassName";

  private Activity mActivity;


  @Override
  public void insertAppContext(Activity appActivity) {
    this.mActivity = appActivity;
  }

  @SuppressLint("MissingSuperCall")
  @Override
  public void onCreate(Bundle savedInstanceState) {

  }

  @Override
  public Intent getIntent() {
    return mActivity.getIntent();
  }

  @Override
  public Resources getResources() {
    return mActivity.getResources();
  }

  @SuppressLint("MissingSuperCall")
  @Override
  public void onStart() {

  }

  @SuppressLint("MissingSuperCall")
  @Override
  public void onPause() {

  }

  @SuppressLint("MissingSuperCall")
  @Override
  public void onStop() {

  }

  @SuppressLint("MissingSuperCall")
  @Override
  public void onResume() {

  }

  @SuppressLint("MissingSuperCall")
  @Override
  public void onDestroy() {

  }

  @Override
  public void startActivity(Intent intent) {
//    super.startActivity(intent);
    try {
      Intent intentNew = new Intent();
      intentNew.putExtra(EXTRA_CLASS_NAME, intent.getComponent().getClassName()); // TestActivity 全类名
      intentNew.putExtras(intent);
      mActivity.startActivity(intentNew);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public ComponentName startService(Intent service) {
    Intent intentNew = new Intent();
    intentNew.putExtra(EXTRA_CLASS_NAME, service.getComponent().getClassName()); // TestActivity 全类名
    intentNew.putExtras(service);
    return mActivity.startService(intentNew);
  }

  @Override
  public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
    return mActivity.registerReceiver(receiver, filter);
  }

  @Override
  public void finish() {
    mActivity.finish();
  }

  public Activity getContext() {
    return mActivity;
  }

}

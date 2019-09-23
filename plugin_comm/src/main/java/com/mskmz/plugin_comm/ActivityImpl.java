package com.mskmz.plugin_comm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.mskmz.main.activity.ActivityInterface;

public class ActivityImpl extends Activity implements ActivityInterface {
  //---------------DEBUG配置---------------------------------------------------------------------------
  private static final String TAG = "ActivityImpl>>>";
  private static final boolean DEBUG = true;

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

  public Activity getContext() {
    return mActivity;
  }
}

package com.mskmz.plugins_demo;

import android.os.Bundle;

import com.mskmz.plugin_comm.ActivityImpl;

public class Demo_MainActivity extends ActivityImpl {

  //---------------DEBUG配置---------------------------------------------------------------------------
  private static final String TAG = "Demo_MainActivity>>>";
  private static final boolean DEBUG = true;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.demo_activity_main);
  }
}

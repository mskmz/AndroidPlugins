package com.mskmz.plugins_demo;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mskmz.plugin_comm.ActivityImpl;

public class Demo_MainActivity extends ActivityImpl implements View.OnClickListener {

  //---------------DEBUG配置---------------------------------------------------------------------------
  private static final String TAG = "Demo_MainActivity>>>";
  private static final boolean DEBUG = true;

  public static final String EXTRA_TEST_DATA = "extraTestData";
  private static final String ACTION = "com.mskmz.plugins_demo.ACTION";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.demo_activity_main);
    if (DEBUG) Log.d(TAG, "onCreate: 启动main");
    findViewById(R.id.btn_jump).setOnClickListener(this);
    findViewById(R.id.btn_start_service).setOnClickListener(this);
    findViewById(R.id.btn_register_broader).setOnClickListener(this);
    findViewById(R.id.btn_send_broader).setOnClickListener(this);
    findViewById(R.id.btn_send_static_broader).setOnClickListener(this);
  }


  @Override
  public void onClick(View v) {
    Intent intent;
    switch (v.getId()) {
      case R.id.btn_jump:
        intent = new Intent(this, Demo2_MainActivity.class);
        intent.putExtra(EXTRA_TEST_DATA, "test");
        startActivity(intent);
      case R.id.btn_start_service:
        intent = new Intent(this, Demo_Service.class);
        startService(intent);
        break;
      case R.id.btn_register_broader:
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(new Demo_Broader(), intentFilter);
        break;
      case R.id.btn_send_broader:
        intent = new Intent();
        intent.setAction(ACTION);
        sendBroadcast(intent);
        break;
      case R.id.btn_send_static_broader:
        intent = new Intent();
        intent.setAction("plugin.static_receiver");
        sendBroadcast(intent);
        break;
    }
  }
}

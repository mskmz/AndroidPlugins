package com.mskmz.plugins_demo;

import android.os.Bundle;

import com.mskmz.plugin_comm.ActivityImpl;

//在Demo2上修改为Fragment
public class Demo2_MainActivity extends ActivityImpl {

  //---------------DEBUG配置---------------------------------------------------------------------------
  private static final String TAG = "Demo2_MainActivity>>>";
  private static final boolean DEBUG = true;

  public static final String EXTRA_TEST_DATA = "extraTestData";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.demo2_activity_main);
    getFragmentManager()
        .beginTransaction()
        .add(R.id.ll_fragment, Demo_Fragment.newInstance("hello world"), "f1")        //.addToBackStack("fname")
        .commit();
    //TODO 兼容androidx
  }

}

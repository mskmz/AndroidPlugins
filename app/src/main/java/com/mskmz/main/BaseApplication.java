package com.mskmz.main;

import android.app.Application;

public class BaseApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    PluginManager.getInstance().init(this);
  }
}

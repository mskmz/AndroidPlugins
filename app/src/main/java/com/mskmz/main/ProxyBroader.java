package com.mskmz.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.mskmz.main.activity.BroaderInterface;

import dalvik.system.DexClassLoader;

public class ProxyBroader extends BroadcastReceiver {
  //···············Constant·········································································
  //---------------DEBUG配置-------------------------------------------------------------------------
  private static final String TAG = "ProxyService>>>";
  private static final boolean DEBUG = true;
  //---------------EXTRA----------------------------------------------------------------------------
  public static final String EXTRA_CLASS_NAME = "extraClassName";
  public static final String EXTRA_PLUGIN_NAME = "extraPluginName";

  //···············Field············································································
  private String mPluginName;
  private String mClassName;
  private DexClassLoader mClassLoader;
  private Resources mRes;
  private BroaderInterface mBroader;

  //···············Constructor······································································

  public ProxyBroader(String mPluginName, String mClassName) {
    this.mPluginName = mPluginName;
    this.mClassName = mClassName;
  }

  //···············Method···········································································
  //---------------Overload-------------------------------------------------------------------------
  @Override
  public void onReceive(Context context, Intent intent) {
    if (mPluginName != null) {
      mClassLoader = PluginManager.getInstance().getDexClassLoader(mPluginName);
      mRes = PluginManager.getInstance().getRes(mPluginName);
    }
    if (mClassLoader == null || mRes == null || mPluginName == null || mClassName == null) {
      throw new RuntimeException("" +
          "请求不合法" +
          "\r mClassLoader=" + mClassLoader +
          "\r mRes        =" + mRes +
          "\r mPluginName =" + mPluginName +
          "\r mclassName  =" + mClassName);
    }
    //加载Fragment
    try {
      if (DEBUG) Log.d(TAG, "onStartCommand: 开始注入Fragment");
      mBroader = (BroaderInterface) mClassLoader.loadClass(mClassName).newInstance();
      mBroader.insertBroaderContext(this);
      mBroader.onReceive(context, intent);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException("崩溃");
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
  }

  //---------------Public Method--------------------------------------------------------------------

  //---------------Private Method-------------------------------------------------------------------

  //···············Inner Class······································································

}

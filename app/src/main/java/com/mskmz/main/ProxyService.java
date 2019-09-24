package com.mskmz.main;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mskmz.main.activity.ServiceInterface;

import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

public class ProxyService extends Service {
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
  private ServiceInterface mService;

  //···············Constructor······································································
  //···············Method···········································································
  //---------------Overload-------------------------------------------------------------------------
  @Override
  public Resources getResources() {
    return mPluginName == null ? super.getResources() : PluginManager.getInstance().getRes(mPluginName);
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  //动态注册 只能在这里拿到intent
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (DEBUG) Log.d(TAG, "onStartCommand: 开始加载Fragment");
    if (intent.hasExtra(EXTRA_CLASS_NAME)) {
      mClassName = intent.getStringExtra(EXTRA_CLASS_NAME);
    }
    if (intent.hasExtra(EXTRA_PLUGIN_NAME)) {
      mPluginName = intent.getStringExtra(EXTRA_PLUGIN_NAME);
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
      mService = (ServiceInterface) mClassLoader.loadClass(mClassName).newInstance();
      mService.insertServiceContext(this);
      replaceServiceContext();
      mService.onStartCommand(intent, flags, startId);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException("崩溃");
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return super.onUnbind(intent);
  }

  @Override
  public void onRebind(Intent intent) {
    super.onRebind(intent);
  }

  @Override
  public void onDestroy() {
    mService.onDestroy();
    super.onDestroy();
  }

  @Override
  public void startActivity(Intent intent) {
    if (intent.hasExtra(EXTRA_CLASS_NAME)) {
      Intent newIntent = new Intent(this, ProxyActivity.class);
      newIntent.putExtras(intent);
      newIntent.putExtra(EXTRA_PLUGIN_NAME, mPluginName);
      intent = newIntent;
    }
    super.startActivity(intent);
  }

  @Override
  public ComponentName startService(Intent service) {
    if (service.hasExtra(EXTRA_CLASS_NAME)) {
      Intent newIntent = new Intent(this, ProxyService.class);
      newIntent.putExtras(service);
      newIntent.putExtra(EXTRA_PLUGIN_NAME, mPluginName);
      service = newIntent;
    }
    return super.startService(service);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


  //---------------Public Method--------------------------------------------------------------------

  //---------------Private Method-------------------------------------------------------------------
  public void replaceServiceContext() {
    try {
      //将所有相关值通过遍历进行修改--环境注入
      if (mService instanceof Service) {
        changField(Service.class);
      }
      if (mService instanceof ContextWrapper) {
        changField(ContextWrapper.class);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private void changField(Class clazz) throws IllegalAccessException {
    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      field.set(mService, field.get(this));
      field.setAccessible(false);
    }
    if (DEBUG) Log.d(TAG, "changField: " + clazz.getSimpleName() + "注入成功");
  }
  //···············Inner Class······································································

}

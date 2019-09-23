package com.mskmz.main;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;


import com.mskmz.main.activity.ActivityInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import dalvik.system.DexClassLoader;

public class ProxyActivity extends Activity {
  //···············Constant·········································································
  //---------------DEBUG配置---------------------------------------------------------------------------
  private static final String TAG = "ProxyActivity>>>";
  private static final boolean DEBUG = true;

  public static final String EXTRA_CLASS_NAME = "extraClassName";
  public static final String EXTRA_PLUGIN_NAME = "extraPluginName";
  //···············Field············································································
  private String mClassName;
  private String mPluginName;
  private DexClassLoader mClassLoader;
  private ActivityInterface mActivity;

  //···············Constructor······································································
  //···············Method···········································································
  //---------------Overload-------------------------------------------------------------------------
  @Override
  public Resources getResources() {
    return mPluginName == null ? super.getResources() : PluginManager.getInstance().getRes(mPluginName);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    if (DEBUG) Log.d(TAG, "onCreate: ");
    super.onCreate(savedInstanceState);
    if (DEBUG) Log.d(TAG, "onCreate: ");
    mClassName = getIntent().getStringExtra(EXTRA_CLASS_NAME);
    mPluginName = getIntent().getStringExtra(EXTRA_PLUGIN_NAME);
    if (mClassName == null || mClassName.isEmpty()) {
      //不应该出现ClassName没有的类
      finish();
    }

    try {
      mClassLoader = PluginManager.getInstance().getDexClassLoader(mPluginName);
      if (DEBUG) Log.d(TAG, "onCreate:mClassLoader -> " + mClassLoader);
      if (DEBUG) Log.d(TAG, "onCreate:mClassName ->" + mClassName);
      Class mPluginActivityClass = mClassLoader.loadClass(mClassName);
      // 实例化 插件包里面的 Activity
      Constructor constructor = mPluginActivityClass.getConstructor(new Class[]{});
      Object mPluginActivity = constructor.newInstance(new Object[]{});
      mActivity = (ActivityInterface) mPluginActivity;
      mActivity.insertAppContext(this);
      replaceAppContext();
      mActivity.onCreate(savedInstanceState);
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
      finish();
    }
  }

  //转发所有的生命周期
  @Override
  protected void onStart() {
    super.onStart();
    mActivity.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mActivity.onResume();
  }

  @Override
  protected void onPause() {
    mActivity.onPause();
    super.onPause();
  }

  @Override
  protected void onStop() {
    mActivity.onStop();
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    mActivity.onDestroy();
    super.onDestroy();
  }
  //---------------Public Method--------------------------------------------------------------------

  //---------------Private Method-------------------------------------------------------------------
  public void replaceAppContext() {
    try {
      //将所有相关值通过遍历进行修改--环境注入
      if (mActivity instanceof Activity) {
        changField(Activity.class);
      }
      if (mActivity instanceof ContextThemeWrapper) {
        changField(ContextThemeWrapper.class);
      }
      if (mActivity instanceof ContextWrapper) {
        changField(ContextWrapper.class);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private void changField(Class clazz) throws IllegalAccessException {
    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      field.set(mActivity, field.get(this));
      field.setAccessible(false);
    }
    if (DEBUG) Log.d(TAG, "changField: " + clazz.getSimpleName() + "注入成功");
  }
  //···············Inner Class······································································

}

package com.mskmz.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

public class PluginManager {
  //···············Constant·········································································
  //---------------DEBUG配置-------------------------------------------------------------------------
  private static final String TAG = "PluginManager>>>";
  private static final boolean DEBUG = true;

  //···············Field············································································
  private Context mContext;
  private Map<String, DexClassLoader> dexClassLoaderMap;
  private Map<String, Resources> resMap;

  //···············Constructor······································································
  //---------------单例模式(静态内部类方式)------------------------------------------------------------
  private PluginManager() {
  }

  public static PluginManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  private static class SingletonHolder {
    private static final PluginManager INSTANCE = new PluginManager();
  }

  //···············Method···········································································
  //---------------Overload-------------------------------------------------------------------------
  //---------------Public Method--------------------------------------------------------------------

  public DexClassLoader getDexClassLoader(String key) {
    return dexClassLoaderMap.get(key);
  }

  public Resources getRes(String key) {
    return resMap.get(key);
  }

  public void init(Context context) {
    mContext = context;
  }

  public void updatePluginEnv(File apk) {
    loadPlugin(apk);
  }


  /**
   * 加载Class
   * 主要功能是把apk包中的内容加入到资源路径中
   */
  private void loadPlugin(File apk) {
    try {
      //拿到插件包
      if (!apk.exists()) {
        return;
      }
      String pluginPath = apk.getAbsolutePath();
      //创建缓存目录存放apk
      String name = getApkName(apk);
      if (DEBUG) Log.d(TAG, "loadPlugin: 准备加载" + name);
      if (dexClassLoaderMap == null) {
        dexClassLoaderMap = new HashMap<>();
      }
      if (dexClassLoaderMap.get(name) == null) {
        File cache = mContext.getDir(name + "Dir", Context.MODE_PRIVATE);
        if (!cache.exists()) {
          cache.mkdirs();
        }
        // 加载Activity
        dexClassLoaderMap.put(
            name,
            new DexClassLoader(pluginPath, cache.getAbsolutePath(), null, mContext.getClassLoader())
        );
        if (DEBUG) Log.d(TAG, "loadPlugin: dexClassLoader加载成功");
      }
      if (resMap == null) {
        resMap = new ArrayMap<>();
      }
      if (resMap.get(name) == null) {
        //获取到资源目录
        AssetManager assetManager = AssetManager.class.newInstance();
        //反射资源路径中的私有方法
        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class); // 他是类类型 Class
        addAssetPath.invoke(assetManager, pluginPath);
        if (DEBUG) Log.d(TAG, "loadPlugin: 执行成功");
        Resources r = mContext.getResources();
        resMap.put(
            name,
            new Resources(assetManager, r.getDisplayMetrics(), r.getConfiguration())// 参数2 3  资源配置信息
        );
        if (DEBUG) Log.d(TAG, "loadPlugin: Resources加载成功-"+name);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //默认是打开主页的
  public ActivityInfo getHomeActivityInfo(File apkFile) {
    String apkPath = apkFile.getAbsolutePath();
    PackageManager pm = null;
    pm = mContext.getPackageManager();
    PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
    //包路径第一条？
    return packageInfo.activities[0];

  }

  public void openDemo(Context context, File file) {
    Intent intent = new Intent(context, ProxyActivity.class);
    intent.putExtra(ProxyActivity.EXTRA_CLASS_NAME, getHomeActivityInfo(file).name);
    intent.putExtra(ProxyActivity.EXTRA_PLUGIN_NAME, getApkName(file));
    context.startActivity(intent);
  }

  //---------------Private Method-------------------------------------------------------------------
  private String getApkName(File apkFile) {
    String name = apkFile.getName();
    if (name.contains(".")) {
      name = name.substring(0, name.indexOf("."));
    }
    return name;
  }
  //···············Inner Class······································································


}

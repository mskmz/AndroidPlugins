package com.mskmz.main;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    parseStaticBroader(apk);
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
        if (DEBUG) Log.d(TAG, "loadPlugin: Resources加载成功-" + name);
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

  public void parseStaticBroader(File file) {
    //尝试解析静态广播
    try {
      //根据源码可知  这个类提供源码的解析功能
      Class clazz = Class.forName("android.content.pm.PackageParser");
      Object mPackageParser = clazz.newInstance();//获取到这个方法对象
      //拿到    public Package parsePackage(File packageFile, int flags) throws PackageParserException {
      Method method = clazz.getMethod("parsePackage", File.class, int.class);
      Object pack = method.invoke(mPackageParser, file, PackageManager.GET_ACTIVITIES);
      //解析
      //  <receiver android:name=".StaticReceiver">
      //    <intent-filter>
      //      <action android:name="plugin.static_receiver" />
      //    </intent-filter>
      //  </receiver>

      //获取所有的receivers
      //public final ArrayList<Activity> receivers = new ArrayList<Activity>(0);
      Field receiversField = pack.getClass().getField("receivers");
      ArrayList<Object> receivers = (ArrayList<Object>) receiversField.get(pack);


      //用户状态
      Class mPackageUserState = Class.forName("android.content.pm.PackageUserState");
      //用户id
      Class mUserHandle = Class.forName("android.os.UserHandle");
      int userId = (int) mUserHandle.getMethod("getCallingUserId").invoke(null);

      for (Object receiver : receivers) {
        //拿到 receiver android:name=".StaticReceiver"
        Class componentClass = Class.forName("android.content.pm.PackageParser$Component");
        //获取到意图 - <intent-filter> 解析 public final ArrayList<II> intents;
        ArrayList<Object> intentList = (ArrayList<Object>) componentClass.getField("intents").get(receiver);

        //解析类名
        //这里拿到的不是完整的包名 是删减的 ？
        String className = (String) componentClass.getField("className").get(receiver);
        if (DEBUG) Log.d(TAG, "parseStaticBroader:className-> " + className);

        //开始想办法拿真正的包名
        /**
         * 执行此方法，就能拿到 ActivityInfo
         * public static final ActivityInfo generateActivityInfo(Activity a, int flags,
         *             PackageUserState state, int userId)
         */
        Method generateActivityInfoMethod = clazz.getDeclaredMethod("generateActivityInfo", receiver.getClass()
            , int.class, mPackageUserState, int.class);
        generateActivityInfoMethod.setAccessible(true);
        ActivityInfo mActivityInfo = (ActivityInfo) generateActivityInfoMethod.invoke(null, receiver, 0, mPackageUserState.newInstance(), userId);

        className = mActivityInfo.name;
        if (DEBUG) Log.d(TAG, "parseStaticBroader: mActivityInfo.name->" + mActivityInfo.name);

        Class mStaticReceiverClass = getDexClassLoader(getApkName(file)).loadClass(className);
        BroadcastReceiver broadcastReceiver = (BroadcastReceiver) mStaticReceiverClass.newInstance();

        for (Object obj : intentList) {
          mContext.registerReceiver(broadcastReceiver, (IntentFilter) obj);
        }
      }

    } catch (Exception e) {
      if (DEBUG) Log.d(TAG, "parseStaticBroader: 静态广播解析");
      e.printStackTrace();
    }
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

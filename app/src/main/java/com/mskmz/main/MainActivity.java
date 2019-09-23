package com.mskmz.main;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import java.io.File;

//插件化 - 宿主 需要实现Proxy类的方法
public class MainActivity extends Activity {
  //---------------DEBUG配置---------------------------------------------------------------------------
  private static final String TAG = "MainActivity>>>";
  private static final boolean DEBUG = true;

  File file;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    File dir = new File("/sdcard/Download/");
    if (DEBUG) Log.d(TAG, "onCreate: " + dir.getAbsoluteFile());
    if (!dir.exists()) {
      dir.mkdirs();
    }
    if (DEBUG) Log.d(TAG, String.valueOf(dir.exists()));
    file = new File(dir.getAbsolutePath() + File.separator + "p.apk");
    if (DEBUG) Log.d(TAG, String.valueOf(file.exists()));
  }

  public void updateEnv(View view) {
    //解析环境
    PluginManager.getInstance().updatePluginEnv(file);
  }

  public void openDemo(View view) {
    PluginManager.getInstance().openDemo(this, file);
  }
}

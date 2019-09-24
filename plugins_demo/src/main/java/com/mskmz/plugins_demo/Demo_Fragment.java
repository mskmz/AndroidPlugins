package com.mskmz.plugins_demo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class Demo_Fragment extends Fragment {
  View root;
  public static final String BUNDLE_STR = "bundleStr";

  public static Fragment newInstance(String str) {
    Bundle args = new Bundle();
    args.putString(BUNDLE_STR, str);
    Demo_Fragment fragment = new Demo_Fragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    root = inflater.inflate(R.layout.demo3_fragment_main, container, false);
    if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STR)) {
      TextView textView = root.findViewById(R.id.tv_text);
      textView.setText(savedInstanceState.getString(BUNDLE_STR));
    }
    return root;
  }
}

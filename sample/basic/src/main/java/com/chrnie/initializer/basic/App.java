package com.chrnie.initializer.basic;

import android.app.Application;
import com.chrnie.initializer.Initializer;

public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Initializer.init(this);
  }
}

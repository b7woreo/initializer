package com.chrnie.initializer.sample;

import android.app.Application;
import com.chrnie.initializer.Initializer;

public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Initializer.init(this);
  }
}

package com.chrnie.initializer;

import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.Executor;

class MainExecutor implements Executor {

  private static final MainExecutor INSTANCE = new MainExecutor();

  public static Executor get() {
    return INSTANCE;
  }

  private final Handler handler;

  private MainExecutor() {
    handler = new Handler(Looper.getMainLooper());
  }

  @Override
  public void execute(Runnable command) {
    Message message = Message.obtain(handler, command);
    if (VERSION.SDK_INT >= 22) {
      message.setAsynchronous(true);
    }
    message.sendToTarget();
  }
}

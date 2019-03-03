package com.chrnie.initializer.module;

import android.content.Context;
import android.util.Log;
import com.chrnie.initializer.Task;
import com.chrnie.initializer.basic.Tasks;

public class AModuleTask extends Task {

  private static final String TAG = "Task";

  public AModuleTask() {
    super(Tasks.A_MODULE);
  }

  @Override
  public void call(Context context) {
    Log.i(TAG, "start A module task -" + Thread.currentThread());
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ignore) {
    }
    Log.i(TAG, "end A module task");
  }
}

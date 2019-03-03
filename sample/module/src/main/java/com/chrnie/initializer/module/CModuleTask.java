package com.chrnie.initializer.module;

import android.content.Context;
import android.util.Log;
import com.chrnie.initializer.Task;
import com.chrnie.initializer.basic.Tasks;

public class CModuleTask extends Task {

  private static final String TAG = "Task";

  public CModuleTask() {
    super(Tasks.C_MODULE);
  }

  @Override
  protected void config() {
    dependOn(Tasks.B_APP);
    dependOn(Tasks.B_MODULE);
  }

  @Override
  public void call(Context context) {
    Log.i(TAG, "start C module task -" + Thread.currentThread());
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ignore) {
    }
    Log.i(TAG, "end C module task");
  }
}

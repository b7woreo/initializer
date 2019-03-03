package com.chrnie.initializer.module;

import android.content.Context;
import android.util.Log;
import com.chrnie.initializer.Task;
import com.chrnie.initializer.basic.AppExecutors;
import com.chrnie.initializer.basic.Tasks;

public class BModuleTask extends Task {

  private static final String TAG = "Task";

  public BModuleTask() {
    super(Tasks.B_MODULE);
  }

  @Override
  protected void config() {
    setExecutor(AppExecutors.COMPUTE);
    dependOn(Tasks.A_APP);
  }

  @Override
  public void call(Context context) {
    Log.i(TAG, "start B module task -" + Thread.currentThread());
    try {
      Thread.sleep(5000);
    } catch (InterruptedException ignore) {
    }
    Log.i(TAG, "end B module task");
  }
}

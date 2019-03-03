package com.chrnie.initializer.sample;

import android.content.Context;
import android.util.Log;
import com.chrnie.initializer.Task;
import com.chrnie.initializer.basic.Tasks;

public class AAppTask extends Task {

  private static final String TAG = "Task";

  public AAppTask() {
    super(Tasks.A_APP);
  }

  @Override
  public void call(Context context) {
    Log.i(TAG, "start A App task -" + Thread.currentThread());
    try {
      Thread.sleep(200);
    } catch (InterruptedException ignore) {
    }
    Log.i(TAG, "end A App task");
  }
}

package com.chrnie.initializer.sample;

import android.content.Context;
import android.util.Log;
import com.chrnie.initializer.Task;
import com.chrnie.initializer.basic.AppExecutors;
import com.chrnie.initializer.basic.Tasks;

public class BAppTask extends Task {

  private static final String TAG = "Task";

  public BAppTask() {
    super(Tasks.B_APP);
  }

  @Override
  protected void config() {
    setExecutor(AppExecutors.COMPUTE);
  }

  @Override
  public void call(Context context) {
    Log.i(TAG, "start B app task -" + Thread.currentThread());
    try {
      Thread.sleep(300);
    } catch (InterruptedException ignore) {
    }
    Log.i(TAG, "end B App task");
  }
}

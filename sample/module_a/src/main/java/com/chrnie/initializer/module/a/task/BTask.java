package com.chrnie.initializer.module.a.task;

import android.content.Context;
import com.chrnie.initializer.basic.AppExecutors;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class BTask extends BaseTask {

  public BTask() {
    super(Tasks.ModuleA.B);
  }

  @Override
  protected void config() {
    setExecutor(AppExecutors.COMPUTE);
  }

  @Override
  public void onRun(Context context) {
    try {
      Thread.sleep(500);
    } catch (InterruptedException ignore) {
    }
  }
}

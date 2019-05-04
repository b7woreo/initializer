package com.chrnie.initializer.module.a.task;

import android.content.Context;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class ATask extends BaseTask {

  public ATask() {
    super(Tasks.ModuleA.A);
  }

  @Override
  protected void onRun(Context context) {
    try {
      Thread.sleep(100);
    } catch (InterruptedException ignore) {
    }
  }
}

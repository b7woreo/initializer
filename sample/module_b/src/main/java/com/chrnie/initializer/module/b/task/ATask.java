package com.chrnie.initializer.module.b.task;

import android.content.Context;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class ATask extends BaseTask {

  public ATask() {
    super(Tasks.ModuleB.A);
  }

  @Override
  protected void onRun(Context context) {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}

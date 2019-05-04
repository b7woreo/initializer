package com.chrnie.initializer.sample.task;

import android.content.Context;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class ATask extends BaseTask {

  public ATask() {
    super(Tasks.App.A);
  }

  @Override
  protected void config() {
    dependOn(Tasks.ModuleA.A);
    dependOn(Tasks.ModuleB.A);
  }

  @Override
  protected void onRun(Context context) {
    try {
      Thread.sleep(200);
    } catch (InterruptedException ignore) {
    }
  }
}

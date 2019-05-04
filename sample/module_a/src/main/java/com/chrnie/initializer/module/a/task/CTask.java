package com.chrnie.initializer.module.a.task;

import android.content.Context;
import com.chrnie.initializer.basic.AppExecutors;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class CTask extends BaseTask {

  public CTask() {
    super(Tasks.ModuleA.C);
  }

  @Override
  protected void config() {
    setExecutor(AppExecutors.COMPUTE);
    dependOn(Tasks.UNIMPORTANT);
    dependOn(Tasks.ModuleA.A);
    dependOn(Tasks.ModuleB.A);
  }

  @Override
  public void onRun(Context context) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ignore) {
    }
  }
}

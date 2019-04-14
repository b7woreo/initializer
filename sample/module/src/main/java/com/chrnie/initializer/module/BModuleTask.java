package com.chrnie.initializer.module;

import android.content.Context;
import com.chrnie.initializer.basic.AppExecutors;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class BModuleTask extends BaseTask {

  public BModuleTask() {
    super(Tasks.B_MODULE);
  }

  @Override
  protected void config() {
    setExecutor(AppExecutors.COMPUTE);
    dependOn(Tasks.A_APP);
  }

  @Override
  public void onCall(Context context) {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException ignore) {
    }
  }
}

package com.chrnie.initializer.module;

import android.content.Context;
import android.util.Log;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class AModuleTask extends BaseTask {

  public AModuleTask() {
    super(Tasks.A_MODULE);
  }

  @Override
  protected void onRun(Context context) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ignore) {
    }
  }
}

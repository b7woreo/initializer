package com.chrnie.initializer.module;

import android.content.Context;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class CModuleTask extends BaseTask {

  public CModuleTask() {
    super(Tasks.C_MODULE);
  }

  @Override
  protected void config() {
    dependOn(Tasks.B_APP);
    dependOn(Tasks.B_MODULE);
  }

  @Override
  public void onCall(Context context) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ignore) {
    }
  }
}

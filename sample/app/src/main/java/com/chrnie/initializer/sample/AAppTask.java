package com.chrnie.initializer.sample;

import android.content.Context;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class AAppTask extends BaseTask {

  public AAppTask() {
    super(Tasks.A_APP);
  }

  @Override
  protected void onRun(Context context) {
    try {
      Thread.sleep(200);
    } catch (InterruptedException ignore) {
    }
  }
}

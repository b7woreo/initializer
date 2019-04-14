package com.chrnie.initializer.sample;

import android.content.Context;
import com.chrnie.initializer.basic.AppExecutors;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class BAppTask extends BaseTask {

  public BAppTask() {
    super(Tasks.B_APP);
  }

  @Override
  protected void config() {
    setExecutor(AppExecutors.COMPUTE);
  }

  @Override
  public void onRun(Context context) {
    try {
      Thread.sleep(300);
    } catch (InterruptedException ignore) {
    }
  }
}

package com.chrnie.initializer.sample.task;

import android.content.Context;
import com.chrnie.initializer.basic.AppExecutors;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;

public class BTask extends BaseTask {

  public BTask() {
    super(Tasks.App.B);
  }

  @Override
  protected void config() {
    setExecutor(AppExecutors.COMPUTE);
  }

  @Override
  public void onRun(Context context) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ignore) {
    }
  }
}

package com.chrnie.initializer.sample.task;

import android.content.Context;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;
import java.util.concurrent.TimeUnit;

public class CTask extends BaseTask {

  public CTask() {
    super(Tasks.App.C);
  }

  @Override
  protected void config() {
    setDelay(5, TimeUnit.SECONDS);
    dependOn(Tasks.App.B);
  }

  @Override
  protected void onRun(Context context) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

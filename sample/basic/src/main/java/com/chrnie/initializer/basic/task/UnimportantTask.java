package com.chrnie.initializer.basic.task;

import android.content.Context;
import com.chrnie.initializer.basic.BaseTask;
import com.chrnie.initializer.basic.Tasks;
import java.util.concurrent.TimeUnit;

public class UnimportantTask extends BaseTask {

  public UnimportantTask() {
    super(Tasks.UNIMPORTANT);
  }

  @Override
  protected void config() {
    setDelay(5, TimeUnit.SECONDS);
  }

  @Override
  protected void onRun(Context context) {
    // just trigger unimportant task
  }
}

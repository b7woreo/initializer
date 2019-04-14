package com.chrnie.initializer.basic;

import android.content.Context;
import android.util.Log;
import com.chrnie.initializer.Task;
import java.util.Locale;

public abstract class BaseTask extends Task {

  private static final String TAG = "Task";

  public BaseTask(String name) {
    super(name);
  }

  @Override
  public final void call(Context context) {
    Log.i(TAG, String.format(
        Locale.ENGLISH, "start %s task in %s thread", getName(), Thread.currentThread().getName())
    );
    onCall(context);
    Log.i(TAG, String.format(Locale.ENGLISH, "end %s task", getName()));
  }

  protected abstract void onCall(Context context);
}

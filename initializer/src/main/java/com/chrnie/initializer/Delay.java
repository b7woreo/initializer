package com.chrnie.initializer;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

class Delay {

  static final Delay NONE = new Delay(0, TimeUnit.MILLISECONDS);

  private static final Handler HANDLER = new Handler(Looper.getMainLooper());

  private final long delay;
  private final TimeUnit unit;

  Delay(long delay, TimeUnit unit) {
    if (delay < 0) {
      throw new IllegalArgumentException("delay < 0");
    }

    if (unit == null) {
      throw new NullPointerException("unit == null");
    }

    this.delay = delay;
    this.unit = unit;
  }

  void runDelayed(final Executor executor, final Runnable r) {
    if (delay == 0L) {
      executor.execute(r);
      return;
    }

    final Runnable delayedRunnable = new Runnable() {
      @Override
      public void run() {
        executor.execute(r);
      }
    };

    long delayedMillis = unit.toMillis(delay);
    HANDLER.postDelayed(delayedRunnable, delayedMillis);
  }

}

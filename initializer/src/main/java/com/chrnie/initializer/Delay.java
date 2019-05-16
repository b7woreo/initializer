package com.chrnie.initializer;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

final class Delay {

  static final Delay NONE = new Delay(0, TimeUnit.MILLISECONDS);

  private static final Handler HANDLER = new Handler(Looper.getMainLooper());

  private final long millis;

  Delay(long duration, TimeUnit unit) {
    if (duration < 0) {
      throw new IllegalArgumentException("duration < 0");
    }

    if (unit == null) {
      throw new NullPointerException("unit == null");
    }

    this.millis = unit.toMillis(duration);
  }

  void runDelayed(final Executor executor, final Runnable r) {
    if (millis == 0L) {
      executor.execute(r);
      return;
    }

    final Runnable delayedRunnable = new Runnable() {
      @Override
      public void run() {
        executor.execute(r);
      }
    };
    HANDLER.postDelayed(delayedRunnable, millis);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Delay delay = (Delay) o;
    return delay.millis == millis;
  }

  @Override
  public int hashCode() {
    return (int) millis;
  }
}

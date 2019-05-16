package com.chrnie.initializer;

import static org.junit.Assert.assertEquals;

import android.os.Looper;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.concurrent.Executor;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainExecutorTest {

  @Test
  public void executeInMainThread() {
    Executor executor = MainExecutor.get();

    final Future<Thread> future = new Future<Thread>() {
      @Override
      protected Thread compute() {
        return Thread.currentThread();
      }
    };

    executor.execute(future);

    Thread mainThread = Looper.getMainLooper().getThread();
    Thread runThread = future.get();
    assertEquals(mainThread, runThread);
  }

}

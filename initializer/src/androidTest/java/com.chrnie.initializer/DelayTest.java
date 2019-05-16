package com.chrnie.initializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DelayTest {

  @Test(expected = IllegalArgumentException.class)
  public void construct_failOnNegativeDuration() {
    new Delay(-1, TimeUnit.MILLISECONDS);
  }

  @Test(expected = NullPointerException.class)
  public void construct_failOnNullTimeUnit() {
    new Delay(0, null);
  }

  @Test
  public void equals() {
    Delay delay1 = new Delay(100, TimeUnit.MILLISECONDS);
    Delay delay2 = new Delay(100, TimeUnit.MILLISECONDS);
    assertEquals(delay1, delay2);
    assertEquals(delay1.hashCode(), delay2.hashCode());

    Delay delay3 = new Delay(TimeUnit.MILLISECONDS.toNanos(100), TimeUnit.NANOSECONDS);
    assertEquals(delay1, delay3);
    assertEquals(delay1.hashCode(), delay2.hashCode());

    assertNotEquals(Delay.NONE, delay1);

    Delay delay4 = new Delay(100, TimeUnit.MICROSECONDS);
    assertEquals(Delay.NONE, delay4);
    assertEquals(Delay.NONE, delay4);

    Delay delay5 = new Delay(100, TimeUnit.NANOSECONDS);
    assertEquals(Delay.NONE, delay5);
    assertEquals(Delay.NONE, delay5);
  }

  @Test
  public void runDelayed() {
    Delay delay = new Delay(100, TimeUnit.MILLISECONDS);

    Executor executor = spy(MainExecutor.get());
    Future<Long> future = new Future<Long>() {
      @Override
      protected Long compute() {
        return System.nanoTime();
      }
    };

    long startTime = System.nanoTime();
    delay.runDelayed(executor, future);
    Long runTime = future.get();

    verify(executor).execute(future);
    long delayedTime = TimeUnit.NANOSECONDS.toMillis(runTime - startTime);
    assertTrue(delayedTime >= 100);
  }

}

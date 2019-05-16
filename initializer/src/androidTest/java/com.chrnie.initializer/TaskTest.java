package com.chrnie.initializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TaskTest {

  @Test(expected = NullPointerException.class)
  public void construct_failOnNullTaskName() {
    new MockTask(null);
  }

  @Test
  public void construct_taskName() {
    MockTask task = new MockTask("test");
    assertEquals("test", task.getName());
  }

  @Test
  public void buildConfig_callConfigMethod() {
    MockTask task = new MockTask();
    MockTask spyTask = spy(task);
    spyTask.buildConfig();
    verify(spyTask).config();
  }

  @Test
  public void defaultDependencies() {
    Task task = new MockTask();
    task.buildConfig();

    List<String> dependencies = task.getDependencies();
    assertEquals(0, dependencies.size());
  }

  @Test(expected = IllegalStateException.class)
  public void dependOn_failOnCallNotInConfigMethod() {
    Task task = new MockTask();
    task.dependOn("");
  }

  @Test(expected = NullPointerException.class)
  public void dependOn_failOnDependNullTaskName() {
    Task task = new MockTask() {
      @Override
      protected void config() {
        dependOn(null);
      }
    };

    task.buildConfig();
  }

  @Test
  public void dependOn() {
    MockTask task = new MockTask() {
      @Override
      protected void config() {
        dependOn("test1");
        dependOn("test2");
      }
    };

    task.buildConfig();
    List<String> dependencies = task.getDependencies();

    assertEquals(2, dependencies.size());
    assertTrue(dependencies.contains("test1"));
    assertTrue(dependencies.contains("test2"));
  }

  @Test
  public void defaultExecutor() {
    MockTask task = new MockTask();
    task.buildConfig();
    assertEquals(MainExecutor.get(), task.getExecutor());
  }

  @Test(expected = IllegalStateException.class)
  public void setExecutor_failOnCallNotInConfigMethod() {
    Task task = new MockTask();
    task.setExecutor(MainExecutor.get());
  }

  @Test(expected = NullPointerException.class)
  public void setExecutor_failOnSetNullExecutor() {
    MockTask task = new MockTask() {
      @Override
      protected void config() {
        setExecutor(null);
      }
    };

    task.buildConfig();
  }

  @Test
  public void setExecutor() {
    final Executor executor = new Executor() {
      @Override
      public void execute(Runnable command) {

      }
    };

    MockTask task = new MockTask() {
      @Override
      protected void config() {
        setExecutor(executor);
      }
    };

    task.buildConfig();
    assertEquals(executor, task.getExecutor());
  }

  @Test
  public void defaultDelay() {
    MockTask task = new MockTask();
    task.buildConfig();
    assertEquals(Delay.NONE, task.getDelay());
  }

  @Test(expected = IllegalStateException.class)
  public void setDelay_failOnCallNotInConfigMethod() {
    Task task = new MockTask();
    task.setDelay(0, TimeUnit.MILLISECONDS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setDelay_failOnSetNegativeDelay() {
    MockTask task = new MockTask() {
      @Override
      protected void config() {
        setDelay(-1, TimeUnit.MILLISECONDS);
      }
    };

    task.buildConfig();
  }

  @Test(expected = NullPointerException.class)
  public void setDelay_failOnSetNullTimeUnit() {
    MockTask task = new MockTask() {
      @Override
      protected void config() {
        setDelay(0, null);
      }
    };

    task.buildConfig();
  }

  @Test
  public void setDelay() {
    MockTask task = new MockTask() {
      @Override
      protected void config() {
        setDelay(10, TimeUnit.MILLISECONDS);
      }
    };

    task.buildConfig();
    assertEquals(new Delay(10, TimeUnit.MILLISECONDS), task.getDelay());
  }
}

package com.chrnie.initializer;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.chrnie.initializer.exception.DependencyNotFoundException;
import com.chrnie.initializer.exception.DuplicateTaskException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class InitializerTest {

  @Test(expected = DuplicateTaskException.class)
  public void buildTaskGraph_failOnDuplicateTask() {
    List<Task> taskList = Arrays.<Task>asList(new MockTask("test"), new MockTask("test"));
    Initializer.buildTaskGraph(taskList);
  }

  @Test(expected = DependencyNotFoundException.class)
  public void buildTaskGraph_failOnDependencyNotFound() {
    MockTask task = new MockTask("test") {
      @Override
      protected void config() {
        dependOn("");
      }
    };
    Initializer.buildTaskGraph(Collections.<Task>singletonList(task));
  }

}

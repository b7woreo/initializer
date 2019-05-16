package com.chrnie.initializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.chrnie.initializer.exception.CyclicDependencyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TaskNodeTest {

  @Test(expected = IllegalStateException.class)
  public void execute_failOnMultipleCall() {
    TaskNode node = new TaskNode();
    node.execute(null, false);
    node.execute(null,false);
  }

  @Test(expected = CyclicDependencyException.class)
  public void execute_failOnCyclicNode() {
    TaskNode node1 = new TaskNode("1", null, null, null);
    TaskNode node2 = new TaskNode("2", null, null, null);
    TaskNode node3 = new TaskNode("3", null, null, null);

    node1.addChild(node2);
    node2.addChild(node3);
    node3.addChild(node1);

    node1.execute(null, true);
  }

  @Test(expected = CyclicDependencyException.class)
  public void execute_failOnSelfCycle() {
    TaskNode node = new TaskNode("tset", null, null, null);
    node.addChild(node);
    node.execute(null, true);
  }

  @Test
  public void execute() {
    final List<Action> actionList = new ArrayList<>();
    final CountDownLatch latch = new CountDownLatch(4);

    Executor executor = Executors.newFixedThreadPool(4);

    final Action action0 = new Action() {
      @Override
      public void run(Context context) {
        assertNull(context);
        actionList.add(this);
        latch.countDown();
      }
    };
    final TaskNode node0 = new TaskNode("0", Delay.NONE, executor, action0);

    final Action action1 = new Action() {
      @Override
      public void run(Context context) {
        assertNull(context);
        actionList.add(this);
        latch.countDown();
      }
    };
    TaskNode node1 = new TaskNode("1", Delay.NONE, executor, action1);

    final Action action2 = new Action() {
      @Override
      public void run(Context context) {
        assertNull(context);
        actionList.add(this);
        latch.countDown();

        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          throw new AssertionError(e);
        }
      }
    };
    TaskNode node2 = new TaskNode("2", Delay.NONE, executor, action2);

    Action action3 = new Action() {
      @Override
      public void run(Context context) {
        assertNull(context);
        actionList.add(this);
        latch.countDown();
      }
    };
    TaskNode node3 = new TaskNode(
        "3", new Delay(100, TimeUnit.MILLISECONDS), executor, action3
    );

    node0.addChild(node1);
    node0.addChild(node2);
    node0.addChild(node3);
    node2.addChild(node1);

    node0.execute(null,false);

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new AssertionError(e);
    }

    assertEquals(Arrays.asList(action0, action2, action3, action1), actionList);
  }

  @Test
  public void equals() {
    TaskNode node1 = new TaskNode();
    TaskNode node2 = new TaskNode();
    assertEquals(node1, node2);
    assertEquals(node1.hashCode(), node2.hashCode());

    TaskNode node3 = new TaskNode(null, null, null, null);
    assertEquals(node1, node3);
    assertEquals(node1.hashCode(), node3.hashCode());

    TaskNode node4 = new TaskNode("test", null, null, null);
    assertNotEquals(node3, node4);

    TaskNode node5 = new TaskNode(
        "test",
        new Delay(100, TimeUnit.MILLISECONDS),
        Executors.newSingleThreadExecutor(),
        new Action() {
          @Override
          public void run(Context context) {

          }
        }
    );
    assertEquals(node4, node5);
    assertEquals(node4.hashCode(), node5.hashCode());
  }

}

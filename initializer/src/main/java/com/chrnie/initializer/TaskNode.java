package com.chrnie.initializer;

import android.content.Context;
import android.text.TextUtils;
import com.chrnie.initializer.exception.CyclicDependencyException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

final class TaskNode extends GraphNode<TaskNode> {

  private final String name;
  private final Delay delay;
  private final Executor executor;
  private final Action action;

  private Set<TaskNode> dependencies = null;
  private boolean executed = false;

  TaskNode() {
    this(null, null, null, null);
  }

  TaskNode(String name, Delay delay, Executor executor, Action action) {
    this.name = name;
    this.delay = delay != null ? delay : Delay.NONE;
    this.executor = executor != null ? executor : MainExecutor.get();
    this.action = action != null ? action : Action.EMPTY;
  }

  void execute(Context context, boolean checkCycle) {
    if (checkCycle) {
      ensureNoneCyclicDependency();
    }
    executeInternal(context, this);
  }

  private synchronized void executeInternal(final Context context, TaskNode caller) {
    if (executed) {
      throw new IllegalStateException("task has been executed");
    }

    if (dependencies == null) {
      this.dependencies = new HashSet<>(getParent());
    }

    dependencies.remove(caller);
    if (!dependencies.isEmpty()) {
      return;
    }

    executed = true;

    final Runnable r = new Runnable() {
      @Override
      public void run() {
        action.run(context);

        for (TaskNode child : TaskNode.this.getChildren()) {
          child.executeInternal(context, TaskNode.this);
        }
      }
    };

    delay.runDelayed(executor, r);
  }

  private void ensureNoneCyclicDependency() {
    List<TaskNode> cyclicNode = findCycle();
    if (cyclicNode == null) {
      return;
    }

    throw new CyclicDependencyException(
        "cyclic dependency: [" + TextUtils.join(" -> ", cyclicNode) + ']'
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TaskNode node = (TaskNode) o;

    return name != null ? name.equals(node.name) : node.name == null;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  @Override
  public String toString() {
    return name != null ? name : "";
  }
}

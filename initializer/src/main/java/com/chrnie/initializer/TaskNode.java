package com.chrnie.initializer;

import android.content.Context;
import android.util.Log;
import com.chrnie.initializer.exception.CyclicDependencyException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;

final class TaskNode extends GraphNode<TaskNode> {

  private static final String ROOT_NAME = "Root";
  private static final String TAG = "Initializer";

  private final String name;
  private final Executor executor;
  private final Action action;

  private Set<TaskNode> dependencies = null;

  TaskNode() {
    this(null, null, null);
  }

  TaskNode(String name, Executor executor, Action action) {
    this.name = name != null ? name : ROOT_NAME;
    this.executor = executor != null ? executor : MainExecutor.get();
    this.action = action != null ? action : Action.EMPTY;
  }

  void execute(Context context) {
    ensureNoneCyclicDependency();
    executeInternal(context, this);
  }

  private synchronized void executeInternal(final Context context, TaskNode caller) {
    if (dependencies == null) {
      this.dependencies = new HashSet<>(getParent());
    }
    dependencies.remove(caller);
    if (!dependencies.isEmpty()) {
      return;
    }

    final Runnable r = new Runnable() {
      @Override
      public void run() {
        long startTime = System.currentTimeMillis();

        Log.d(TAG, String.format(Locale.ENGLISH,
            "Start task: [%s : %s]", name, Thread.currentThread().getName()
        ));
        action.call(context);
        Log.d(TAG, String.format(Locale.ENGLISH,
            "End task: [%s : %d]", name, (System.currentTimeMillis() - startTime))
        );

        for (TaskNode child : TaskNode.this.getChildren()) {
          child.executeInternal(context, TaskNode.this);
        }
      }
    };

    executor.execute(r);
  }

  private void ensureNoneCyclicDependency() {
    List<TaskNode> cyclicNode = findCycle();
    if (cyclicNode == null) {
      return;
    }

    StringBuilder builder = new StringBuilder();
    builder.append("cyclic dependency: [");
    int size = cyclicNode.size();
    for (int i = 0; i < size; i++) {
      TaskNode node = cyclicNode.get(i);
      builder.append(node.name);
      if (i != (size - 1)) {
        builder.append(" -> ");
      }
    }
    builder.append(']');
    throw new CyclicDependencyException(builder.toString());
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
}

package com.chrnie.initializer;

import android.content.Context;
import com.chrnie.initializer.exception.DependencyNotFoundException;
import com.chrnie.initializer.exception.DuplicateTaskException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Initializer {

  private static Initializer instance;

  public static void init(Context context) {
    init(context, true);
  }

  public synchronized static void init(Context context, boolean debug) {
    if(context == null){
      throw new NullPointerException("context == null");
    }

    if (instance != null) {
      return;
    }

    instance = new Initializer(debug);
    instance.initialize(context.getApplicationContext());
  }

  public synchronized static boolean isDebug() {
    if (instance == null) {
      throw new IllegalStateException("call Initializer.init() first");
    }
    return instance.debug;
  }

  private final boolean debug;

  private Initializer(boolean debug) {
    this.debug = debug;
  }

  private void initialize(Context context) {
    List<Task> taskList = TaskLoader.requestTaskList();
    TaskNode rootNode = buildTaskGraph(taskList);
    rootNode.execute(context, debug);
  }

  static TaskNode buildTaskGraph(List<Task> taskList) {
    Map<String, TaskNode> nameToNode = new HashMap<>();

    for (Task task : taskList) {
      String taskName = task.getName();
      if (nameToNode.containsKey(taskName)) {
        throw new DuplicateTaskException("duplicate task: " + taskName);
      }

      task.buildConfig();
      TaskNode node = new TaskNode(taskName, task.getDelay(), task.getExecutor(), task);
      nameToNode.put(taskName, node);
    }

    TaskNode rootNode = new TaskNode();
    for (Task task : taskList) {
      String taskName = task.getName();
      TaskNode taskNode = nameToNode.get(taskName);
      rootNode.addChild(taskNode);

      List<String> dependencies = task.getDependencies();
      for (String parentName : dependencies) {
        TaskNode parentNode = nameToNode.get(parentName);
        if (parentNode == null) {
          throw new DependencyNotFoundException("can not found dependency task: " + parentName);
        }

        parentNode.addChild(taskNode);
      }
    }
    return rootNode;
  }
}

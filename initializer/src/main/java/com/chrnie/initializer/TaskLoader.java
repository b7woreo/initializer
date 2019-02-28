package com.chrnie.initializer;

import java.util.ArrayList;
import java.util.List;

final class TaskLoader {

  private static void loadTasks(List<Task> outList) {
    /*
     * hook method for generate code, for example:
     * outList.add(new XXTask());
     */
  }

  static List<Task> requestTaskList() {
    List<Task> result = new ArrayList<>();
    loadTasks(result);
    return result;
  }

  private TaskLoader() {
    // util class
  }
}

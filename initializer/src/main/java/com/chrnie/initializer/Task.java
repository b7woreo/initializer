package com.chrnie.initializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class Task implements Action {

  private final String name;

  private boolean buildingConfig = false;
  private Executor executor = MainExecutor.get();
  private List<String> dependencies = null;

  public Task(String name) {
    if (name == null) {
      throw new NullPointerException("name == null");
    }
    this.name = name;
  }

  final String getName() {
    return name;
  }

  Executor getExecutor() {
    return executor;
  }

  List<String> getDependencies() {
    return dependencies == null ? Collections.<String>emptyList() : dependencies;
  }

  void buildConfig() {
    buildingConfig = true;
    config();
    buildingConfig = false;
  }

  protected void config() {

  }

  protected final void dependOn(String taskName) {
    if (!buildingConfig) {
      throw new IllegalStateException("only call dependOn in config() method");
    }

    if (taskName == null) {
      throw new NullPointerException("taskName == null");
    }

    if (dependencies == null) {
      dependencies = new ArrayList<>(4);
    }

    dependencies.add(taskName);
  }

  protected final void setExecutor(Executor executor) {
    if (!buildingConfig) {
      throw new IllegalStateException("only call setExecutor in config() method");
    }

    if (executor == null) {
      throw new NullPointerException("executor == null");
    }

    this.executor = executor;
  }
}

package com.chrnie.initializer.basic;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

  public static final Executor COMPUTE = Executors.newFixedThreadPool(4);

  private AppExecutors() {

  }
}

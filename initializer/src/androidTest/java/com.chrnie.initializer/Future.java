package com.chrnie.initializer;

import java.util.concurrent.SynchronousQueue;

public abstract class Future<V> implements Runnable {

  private final SynchronousQueue<V> queue = new SynchronousQueue<>();

  protected abstract V compute();

  @Override
  public final void run() {
    V value = compute();
    try {
      queue.put(value);
    } catch (InterruptedException e) {
      throw new AssertionError(e);
    }
  }

  public final V get() {
    try {
      return queue.take();
    } catch (InterruptedException e) {
      throw new AssertionError(e);
    }
  }
}

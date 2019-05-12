package com.chrnie.initializer;

import android.content.Context;

public class MockTask extends Task {

  public MockTask() {
    this("");
  }

  public MockTask(String name) {
    super(name);
  }

  @Override
  public void run(Context context) {

  }

}

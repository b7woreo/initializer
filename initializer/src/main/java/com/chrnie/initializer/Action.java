package com.chrnie.initializer;

import android.content.Context;

interface Action {

  Action EMPTY = new Action() {
    @Override
    public void run(Context context) {
      // empty
    }
  };

  void run(Context context);
}

package com.chrnie.initializer;

import android.content.Context;

interface Action {

  Action EMPTY = new Action() {
    @Override
    public void call(Context context) {
      // empty
    }
  };

  void call(Context context);
}

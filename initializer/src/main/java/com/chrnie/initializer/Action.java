package com.chrnie.initializer;

import android.content.Context;

interface Action {

  Action EMPTY = context -> {};

  void call(Context context);
}

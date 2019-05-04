package com.chrnie.initializer.module.b;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BModuleActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TextView tv = new TextView(this);
    tv.setText("B Module Activity");
    setContentView(tv);
  }

}

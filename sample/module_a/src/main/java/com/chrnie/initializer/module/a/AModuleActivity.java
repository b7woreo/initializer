package com.chrnie.initializer.module.a;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AModuleActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TextView tv = new TextView(this);
    tv.setText("A Module Activity");
    setContentView(tv);
  }
}

package org.lockerz.compare.guideScreens;

import org.lockerz.compare.R;
import org.lockerz.compare.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class DemoActivity3 extends Activity {

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_layout3);
    }
    
    public void onExit(View v)
    {
        finish();
    }
}

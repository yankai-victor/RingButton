package com.victor.ringbutton.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.victor.ringbutton.RingButton;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RingButton ringButton = (RingButton) findViewById(R.id.ringButton);
        ringButton.setOnClickListener(new RingButton.OnClickListener() {
            @Override
            public void clickUp() {
                Toast.makeText(getApplicationContext(), "Click up", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clickDown() {
                Toast.makeText(getApplicationContext(), "Click down", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

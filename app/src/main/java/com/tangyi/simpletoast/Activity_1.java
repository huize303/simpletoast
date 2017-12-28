package com.tangyi.simpletoast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.tangyi.toast.SimpleToast;

import java.util.Random;

public class Activity_1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        findViewById(R.id.to_activity_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_1.this, Activity_2.class));
            }
        });

        findViewById(R.id.enqueuelist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0;i<10;i++) {
                    int time = (i%2 == 0) ? Toast.LENGTH_LONG: Toast.LENGTH_SHORT;
                    SimpleToast.makeText(Activity_1.this, "Activity_1" + new Random().nextLong(), time).show();
                }
            }
        });


    }
}

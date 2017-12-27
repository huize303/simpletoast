package com.tangyi.simpletoast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;


import com.tangyi.toast.SimpleToast;

import java.util.Date;
import java.util.Random;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TwoActivity.class));
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                for(int i = 0;i<10;i++) {
                    SimpleToast.makeText(MainActivity.this, "MainActivity" + new Random().nextLong(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}

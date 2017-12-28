package com.tangyi.simpletoast;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.tangyi.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by tangyi on 2017/12/26.
 *
 */

public class Activity_2 extends Activity {

    private ArrayList<Toast> list = new ArrayList<Toast>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        findViewById(R.id.show_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0;i<10;i++) {
                    Toast toast = Toast.makeText(Activity_2.this, "Activity_1" + new Random().nextLong(),Toast.LENGTH_SHORT);

                    toast.show();
                    list.add(toast);
                }
            }
        });

        findViewById(R.id.cancel_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Toast toast : list) {
                    toast.cancel();
                }
            }
        });

        final Toast toast = new Toast(Activity_2.this);

        findViewById(R.id.custom_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(Activity_2.this).inflate(R.layout.dialog_no_network, null, false);
                toast.setGravity(Gravity.FILL_HORIZONTAL|Gravity.CENTER_VERTICAL, 100, 100);
                toast.setDuration(android.widget.Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();
            }
        });



        findViewById(R.id.custom_toast_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast.cancel();
            }
        });
    }
}

package com.tangyi.simpletoast;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tangyi.toast.SimpleToast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Author: Blincheng.
 * Date: 2017/7/28.
 * Description:
 */

public class Activity_2 extends Activity {

    private ArrayList<SimpleToast> list = new ArrayList<SimpleToast>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        findViewById(R.id.show_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0;i<10;i++) {
                    SimpleToast toast = SimpleToast.makeText(Activity_2.this, "Activity_1" + new Random().nextLong(), Toast.LENGTH_SHORT);

                    toast.show();
                    list.add(toast);
                }
            }
        });

        findViewById(R.id.cancel_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(SimpleToast toast : list) {
                    toast.cancel();
                }
            }
        });

        final SimpleToast toast = new SimpleToast(Activity_2.this);

        findViewById(R.id.custom_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(Activity_2.this).inflate(R.layout.dialog_no_network, null, false);
                toast.setGravity(Gravity.FILL_HORIZONTAL|Gravity.CENTER_VERTICAL, 100, 100);
                toast.setDuration(Toast.LENGTH_LONG);
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

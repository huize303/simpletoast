package com.tangyi.simpletoast;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.tangyi.toast.SimpleToast;

import java.util.Date;
import java.util.Random;

/**
 * Author: Blincheng.
 * Date: 2017/7/28.
 * Description:
 */

public class TwoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleToast.makeText(TwoActivity.this,"TwoActivity" + new Random().nextLong(), SimpleToast.LENGTH_SHORT).show();
            }
        });
    }
}

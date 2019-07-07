package com.wenchao.eventbusdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wenchao.eventbusdemo.eventbus.EventBus;
import com.wenchao.eventbusdemo.eventbus.Subscribe;
import com.wenchao.eventbusdemo.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        tv = findViewById(R.id.tv);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(WCBean bean) {
        tv.setText(bean.getOne() + "," + bean.getTwo());
    }

    public void goSecond(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }
}

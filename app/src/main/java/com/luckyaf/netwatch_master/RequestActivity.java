package com.luckyaf.netwatch_master;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/29
 */
public class RequestActivity extends AppCompatActivity {

    public static void jumpFrom(Context context) {
        Intent intent = new Intent(context, RequestActivity.class);
        Bundle params = new Bundle();
        intent.putExtras(params);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

    }
}

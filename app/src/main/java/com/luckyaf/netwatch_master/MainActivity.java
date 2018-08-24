package com.luckyaf.netwatch_master;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luckyaf.netwatch.utils.Logger;
import com.luckyaf.okdownload.OkDownload;
import com.luckyaf.okdownload.Progress;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * @author xiangzhongfei
 */
public class MainActivity extends AppCompatActivity {
    private TextView txt_result;
    private TextView txtSpeed;
    private ProgressBar mProgressBar;
    ProgressDialog mProgressDialog;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_result = (TextView) findViewById(R.id.txt_result);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        txtSpeed = findViewById(R.id.txtSpeed);
        findViewById(R.id.btnSimpleDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleDownload();
            }
        });
        findViewById(R.id.btnJumpToDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadActivity.jumpFrom(MainActivity.this);
            }
        });
    }


    private void simpleDownload(){
        OkDownload.getInstance()
                .download("http://60.28.125.129/f1.market.xiaomi.com/download/AppStore/0ff41344f280f40c83a1bbf7f14279fb6542ebd2a/com.sina.weibo.apk")
                .subscribe(new Observer<Progress>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.i("start ");

                    }

                    @Override
                    public void onNext(Progress progress) {
                        mProgressBar.setProgress((int) (progress.getFraction() * 100));
                        txt_result.setText(progress.getCurrentSize() + " / " + progress.getTotalSize());
                        txtSpeed.setText(progress.getSpeed() / 1024  + "  kb / s");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}




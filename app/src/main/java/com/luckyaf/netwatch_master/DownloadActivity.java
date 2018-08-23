package com.luckyaf.netwatch_master;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.luckyaf.netwatch.utils.Logger;
import com.luckyaf.netwatch_master.adapter.BaseAdapter;
import com.luckyaf.netwatch_master.adapter.CommonViewHolder;
import com.luckyaf.okdownload.DownloadManager;
import com.luckyaf.okdownload.DownloadRequest;
import com.luckyaf.okdownload.DownloadTask;
import com.luckyaf.okdownload.OkDownload;
import com.luckyaf.okdownload.Progress;
import com.luckyaf.okdownload.constant.StatusConstant;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class DownloadActivity extends AppCompatActivity {

    List<DownloadRequest> requestList;
    Disposable disposable;
    RecyclerView recyclerView;

    private BaseAdapter mInnerAdapter;

    public static void jumpFrom(Context context) {
        Intent intent = new Intent(context, DownloadActivity.class);
        Bundle params = new Bundle();
        intent.putExtras(params);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        requestList = new ArrayList<>();
        mInnerAdapter = new BaseAdapter<DownloadRequest>(this, requestList, R.layout.item_download) {
            @Override
            public void convert(final CommonViewHolder holder, final DownloadRequest data, final int position) {
                final DownloadTask downloadTask = DownloadManager.getInstance().getDownloadTask(data);
                holder.setText(R.id.txtUrl, data.getUrl());
                holder.setText(R.id.txtFileName, data.getFileName());
                int intProgress = (int) (data.getCurrentSize() * 100 / data.getTotalSize());
                ((ProgressBar) holder.getView(R.id.progress)).setProgress(intProgress);
                holder.setText(R.id.txtProgress, intProgress + " %");
                DownloadManager.getInstance()
                        .listen(data.getTag())
                        .subscribe(new Observer<Progress>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Progress progress) {
                                ((ProgressBar) holder.getView(R.id.progress)).setProgress((int) (progress.getFraction() * 100));
                                holder.setText(R.id.txtProgress, progress.getFraction() * 100 + " %");
                                holder.setText(R.id.txtSpeed, progress.getSpeed() / 1024 + " KB/s");

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });


                holder.getView(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadTask.start();
                    }
                });
                holder.getView(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadTask.pause();
                    }
                });
                holder.getView(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadTask.remove();
                    }
                });

            }
        };

        disposable = Observable.create(new ObservableOnSubscribe<List<DownloadRequest>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DownloadRequest>> emitter) throws Exception {
                List<DownloadRequest> requestList = DownloadManager.getInstance().getAll();
                emitter.onNext(requestList);
                emitter.onComplete();
            }
        }).subscribe(new Consumer<List<DownloadRequest>>() {
            @Override
            public void accept(List<DownloadRequest> requests) throws Exception {
                requestList.addAll(requests);
                Logger.d("progressList", requestList);
                mInnerAdapter.notifyDataSetChanged();
                Logger.d("getItemCount", mInnerAdapter.getItemCount());

            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mInnerAdapter);

    }


    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }


}

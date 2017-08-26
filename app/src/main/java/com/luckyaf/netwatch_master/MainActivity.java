package com.luckyaf.netwatch_master;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.luckyaf.netwatch.ContentType;
import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.callBack.CommonCallBack;
import com.luckyaf.netwatch.callBack.DownloadCallBack;
import com.luckyaf.netwatch.callBack.UploadCallBack;
import com.luckyaf.netwatch.upload.UploadFileBody;
import com.luckyaf.netwatch.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.internal.schedulers.NewThreadScheduler;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private TextView txt_result;
    private ProgressBar mProgressBar;
    ProgressDialog mProgressDialog;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_result = findViewById(R.id.txt_result);
        mProgressBar = findViewById(R.id.progress);
        mProgressDialog = new ProgressDialog(this);
        mContext = this;
        NetWatch.init(this,"http://api.laifudao.com")//base url
                .build();

        findViewById(R.id.btn_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get();
            }
        });
        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });
        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });
        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetWatch.cancelRequest(MainActivity.this);
            }
        });
    }

    private void get(){
        Map<String,String> header = new HashMap<>();
        header.put("mobileNumber", "18826412577");
        header.put("loginPassword", "123456");
        Map<String,Object> params = new HashMap<>();
        params.put("start", "0");
        params.put("count", "1");
        NetWatch.open(this,"http://api.douban.com/v2/movie/top250")
                .headers(header)
                .tag(this)
                .get(params, new CommonCallBack() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            txt_result.setText(responseBody.string());
                        }catch (IOException e){
                            //do nothing
                        }
                    }
                });
    }

    private void post(){

        Map<String,Object> map = new HashMap<>();
        map.put("questionId",176);
        map.put("since","FIRST");
        map.put("accountId",9936);
        map.put("globalAppType",0);
        NetWatch.open(this,"http://japi.juhe.cn/joke/content/list.from")
                .tag(this)
                .post(map, new CommonCallBack() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            txt_result.setText(responseBody.string());
                        }catch (IOException e){
                            //do nothing
                        }
                    }
                });

    }

    private void download(){
        NetWatch.getNetBuilder(this)
                .tag(this)
                .download("http://wifiapi02.51y5.net/wifiapi/rd.do?f=wk00003&b=gwanz02&rurl=http%3A%2F%2Fdl.lianwifi.com%2Fdownload%2Fandroid%2FWifiKey-3091-guanwang.apk",
                        new DownloadCallBack() {
                            @Override
                            public void onStart(String key) {
                                mProgressBar.setProgress(0);
                                Logger.d("download","onStart");

                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(mContext,"cancel",Toast.LENGTH_SHORT).show();
                                Logger.d("download","onCancel");

                            }

                            @Override
                            public void onComplete() {
                                Logger.d("download","onComplete");

                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Logger.d("error",throwable);
                                Logger.d("download","onError");

                                Toast.makeText(mContext,"error",Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onProgress(String key, int progress, long fileSizeDownloaded, long totalSize) {
                                mProgressBar.setProgress(progress);
                                txt_result.setText(fileSizeDownloaded + "/" + totalSize + "" + progress + "");
                            }

                            @Override
                            public void onSuccess(String key, String path, String name, long fileSize) {
                                Logger.d("download","onSuccess");

                                Toast.makeText(mContext,"success",Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void upload(){
        try {
            //为了方便就不动态申请权限了,直接将文件放到CacheDir()中
            File file1 = new File(getCacheDir(), "a.java");
            File file2 = new File(getCacheDir(),"ic_launcher.png");
            //读取Assets里面的数据,作为上传源数据
            writeToFile(getAssets().open("a.java"), file1);
            writeToFile(getAssets().open("ic_launcher.png"), file2);
            Map<String ,UploadFileBody> map = new HashMap<>();
            map.put("a.java",new UploadFileBody(MediaType.parse("multipart/form-data"),file1));
            map.put("ic_launcher.png",new UploadFileBody(MediaType.parse("image"),file2));

            NetWatch.getNetBuilder(this)
                    .tag(this)
                    .upload("http://upload.qiniu.com/", null, map, new UploadCallBack() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                txt_result.setText(responseBody.string());
                            }catch (IOException e){
                                //do nothing
                            }
                        }

                        @Override
                        public void onComplete() {
                            Toast.makeText(mContext,"onComplete",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onProgress(int progress, long speed, long transformed, long total) {
                            mProgressBar.setProgress(progress);
                            txt_result.setText(transformed + "/" + total + "" + progress + "");

                        }

                        @Override
                        public void onSuccess(){
                            Toast.makeText(mContext,"onSuccess",Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (IOException e){
            txt_result.setText(e.getMessage());
        }
    }



    public static File writeToFile(InputStream in, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int num = 0;
        while ((num = in.read(buf)) != -1) {
            out.write(buf, 0, buf.length);
        }
        out.close();
        return file;
    }



}

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
        txt_result = (TextView)findViewById(R.id.txt_result);
        mProgressBar = (ProgressBar)findViewById(R.id.progress);
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

            //https://api.iplusmed.com/yjy_doctor/updateDoctorLicenceUrl?globalDeviceRom=5.0.1
            // &deviceId=5b63ee0d-ecbe-3572-88fb-31177f2bd7b3
            // &globalDeviceModel=M351
            // &hasHeadMultiMedia=true
            // &token=9470bd24-4c98-41fa-bb34-fd88a109c949
            // &accountId=33343
            // &globalDeviceType=A
            // &globalAppVersion=471
            // &globalAppChannelId=hzyd_ydd_doctor
            // &globalAppType=0
            //为了方便就不动态申请权限了,直接将文件放到CacheDir()中
            Map<String,Object> params = new HashMap<>();
            Map<String ,UploadFileBody> map = new HashMap<>();
            params.put("deviceId","5b63ee0d-ecbe-3572-88fb-31177f2bd7b3");
            params.put("hasHeadMultiMedia",true);
            params.put("token","9470bd24-4c98-41fa-bb34-fd88a109c949");
            params.put("accountId",33343);
            params.put("globalDeviceType","A");
            params.put("globalAppVersion",471);
            params.put("globalAppChannelId","hzyd_ydd_doctor");
            params.put("globalDeviceRom","5.0.1");
            params.put("globalDeviceModel","M351");
            params.put("globalAppType","0");
            File file1 = new File("/storage/emulated/0/Android/data/cn.medtap.doctor/medtap/Image/1503998760579.JPEG");
            File file2 = new File("/storage/emulated/0/Android/data/cn.medtap.doctor/medtap/Image/1503998766694.JPEG");
            map.put("1503998760579.JPEG",new UploadFileBody(MediaType.parse("image/*"),file1));
            map.put("1503998766694.JPEG",new UploadFileBody(MediaType.parse("image/*"),file2));

            NetWatch.getNetBuilder(this)
                    .tag(this)
                    .upload("https://api.iplusmed.com/yjy_doctor/updateDoctorLicenceUrl", params, map, new UploadCallBack() {
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
                                Logger.d("responseBody",responseBody);
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

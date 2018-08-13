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
import com.luckyaf.netwatch.callback.CancelCallBack;
import com.luckyaf.netwatch.callback.CommonCallBack;
import com.luckyaf.netwatch.callback.DownloadCallBack;
import com.luckyaf.netwatch.callback.DownloadSuccessCallback;
import com.luckyaf.netwatch.callback.ErrorCallBack;
import com.luckyaf.netwatch.callback.ProgressCallBack;
import com.luckyaf.netwatch.callback.StartCallBack;
import com.luckyaf.netwatch.callback.SuccessCallBack;
import com.luckyaf.netwatch.callback.UploadCallBack;
import com.luckyaf.netwatch.upload.UploadFileBody;
import com.luckyaf.netwatch.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;



/**
 * @author xiangzhongfei
 */
public class MainActivity extends AppCompatActivity {
    private TextView txt_result;
    private ProgressBar mProgressBar;
    ProgressDialog mProgressDialog;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_result = (TextView) findViewById(R.id.txt_result);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressDialog = new ProgressDialog(this);
        mContext = this;
        Map<String,Object> commonHeaders= new HashMap<>();
        Map<String,Object> commonParams= new HashMap<>();
        NetWatch.init(this, "http://api.laifudao.com")//base url
                .addCommonHeaders(commonHeaders)
                .addCommonParams(commonParams)
                .openOkHttpLog(HttpLoggingInterceptor.Level.NONE)//log  特别多 最好别开
                .openSimpleLog(true)
                //.andSSL() 证书
                .retryOnConnectionFailure(true)//失败重连
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
        findViewById(R.id.btn_json_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonPost();
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

        findViewById(R.id.btn_new_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGet();
            }
        });
        findViewById(R.id.btn_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPost();
            }
        });
        findViewById(R.id.btn_new_json_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newJsonPost();
            }
        });
        findViewById(R.id.btn_new_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDownload();
            }
        });
        findViewById(R.id.btn_new_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newUpload();
            }
        });


        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetWatch.cancelRequest(MainActivity.this);
            }
        });
    }

    private void get() {
        Map<String, Object> header = new HashMap<>();
        header.put("mobileNumber", "18826412577");
        header.put("loginPassword", "123456");
        Map<String, Object> params = new HashMap<>();
        params.put("start", "0");
        params.put("count", "1");
        NetWatch.open(this, "http://api.douban.com/v2/movie/top250")
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
                        } catch (IOException e) {
                            //do nothing
                        }
                    }
                });
    }

    private void jsonPost() {
        Map<String, Object> header = new HashMap<>();
        header.put("mobileNumber", "18826412577");
        header.put("loginPassword", "123456");
        Map<String, Object> params = new HashMap<>();
        params.put("start", "0");
        params.put("count", "1");
        JSONObject object = new JSONObject();
        try {
            object.put("start", "0");
            object.put("count", "1");
        } catch (JSONException e) {
        }
        txt_result.setText(object.toString());


        NetWatch.open(this, "http://api.douban.com/v2/movie/top250")
                .headers(header)
                .tag(this)
                .jsonPost(object.toString(), new CommonCallBack() {
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
                        } catch (IOException e) {
                            //do nothing
                        }
                    }
                });
    }

    private void post() {
        Map<String, Object> map = new HashMap<>();
        map.put("questionId", 176);
        map.put("since", "FIRST");
        map.put("accountId", 9936);
        map.put("globalAppType", 0);
        NetWatch.open(this, "http://japi.juhe.cn/joke/content/list.from")
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
                        } catch (IOException e) {
                            //do nothing
                        }
                    }
                });

    }

    private void download() {
        NetWatch.getNetBuilder(this)
                .tag(this)
                .download("http://wifiapi02.51y5.net/wifiapi/rd.do?f=wk00003&b=gwanz02&rurl=http%3A%2F%2Fdl.lianwifi.com%2Fdownload%2Fandroid%2FWifiKey-3091-guanwang.apk", new DownloadCallBack() {
                    @Override
                    public void onStart(String key) {
                        mProgressBar.setProgress(0);
                        Logger.d("download", "onStart");

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(mContext, "cancel", Toast.LENGTH_SHORT).show();
                        Logger.d("download", "onCancel");

                    }

                    @Override
                    public void onComplete() {
                        Logger.d("download", "onComplete");

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.d("error", throwable);
                        Logger.d("download", "onError");

                        Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onProgress(String key, int progress, long speed, long downloadedSize, long totalSize) {
                        mProgressBar.setProgress(progress);
                        txt_result.setText(downloadedSize + "/" + totalSize + "    " + progress + "%");
                    }


                    @Override
                    public void onSuccess(String key, String path, String name, long fileSize) {
                        Logger.d("download", "onSuccess");

                        Toast.makeText(mContext, name+" download success", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void upload() {

        try {
            //为了方便就不动态申请权限了,直接将文件放到CacheDir()中
            File file1 = new File(getCacheDir(), "a.java");
            File file2 = new File(getCacheDir(), "ic_launcher.png");
            //读取Assets里面的数据,作为上传源数据
            writeToFile(getAssets().open("a.java"), file1);
            writeToFile(getAssets().open("ic_launcher.png"), file2);
            //若要保持顺序  使用LinkedHashMap
            Map<String, UploadFileBody> map = new HashMap<>();
            map.put("a.java", new UploadFileBody(ContentType.JAVA, file1));
            map.put("ic_launcher.png", new UploadFileBody(ContentType.IMAGE, file2));

            NetWatch.getNetBuilder(this)
                    .tag(this)
                    .upload("http://upload.qiniu.com/", null, map, new UploadCallBack() {
                        @Override
                        public void onStart() {
                            Toast.makeText(mContext, "onStart", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(mContext, "onCancel", Toast.LENGTH_SHORT).show();

                        }


                        @Override
                        public void onComplete() {
                            Toast.makeText(mContext, "onComplete", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Toast.makeText(mContext, "error " + throwable.toString(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onProgress(int progress, long speed, long transformed, long total) {
                            mProgressBar.setProgress(progress);
                            txt_result.setText(transformed + "/" + total + "     " + progress + "%");

                        }

                        @Override
                        public void onSuccess(ResponseBody responseBody) {

                        }

                    });
        } catch (IOException e) {
            txt_result.setText(e.getMessage());
        }

    }


    private void newGet() {
        Map<String, Object> header = new HashMap<>();
        header.put("mobileNumber", "18826412577");
        header.put("loginPassword", "123456");
        Map<String, Object> params = new HashMap<>();
        params.put("start", "0");
        params.put("count", "1");
        NetWatch.get("http://api.douban.com/v2/movie/top250")
                .headers(header)
                .params(params)
                .tag(this)
                .onStart(new StartCallBack() {
                    @Override
                    public void onStart() {

                    }
                })
                .onCancel(new CancelCallBack() {
                    @Override
                    public void onCancel() {

                    }
                })
                .onError(new ErrorCallBack() {
                    @Override
                    public void onError(Throwable error) {

                    }
                })
                .onSuccess(new SuccessCallBack() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            txt_result.setText(responseBody.string());
                        } catch (IOException e) {
                            //do nothing
                        }
                    }
                })
                .run();
    }

    private void newJsonPost() {
        Map<String, Object> header = new HashMap<>();
        header.put("mobileNumber", "18826412577");
        header.put("loginPassword", "123456");
        Map<String, Object> params = new HashMap<>();
        params.put("start", "0");
        params.put("count", "1");
        JSONObject object = new JSONObject();
        try {
            object.put("start", "0");
            object.put("count", "1");
        } catch (JSONException e) {
            // do nothinf
        }
        txt_result.setText(object.toString());
        NetWatch.post("http://japi.juhe.cn/joke/content/list.from")
                .headers(header)
                .jsonString(object.toString())
                .tag(this)
                .onStart(new StartCallBack() {
                    @Override
                    public void onStart() {

                    }
                })
                .onCancel(new CancelCallBack() {
                    @Override
                    public void onCancel() {

                    }
                })
                .onError(new ErrorCallBack() {
                    @Override
                    public void onError(Throwable error) {

                    }
                })
                .onSuccess(new SuccessCallBack() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            txt_result.setText(responseBody.string());
                        } catch (IOException e) {
                            //do nothing
                        }
                    }
                })
                .run();
    }

    private void newPost() {
        Map<String, Object> map = new HashMap<>();
        map.put("questionId", 176);
        map.put("since", "FIRST");
        map.put("accountId", 9936);
        map.put("globalAppType", 0);
        NetWatch.get("http://japi.juhe.cn/joke/content/list.from")
                .params(map)
                .tag(this)
                .onStart(new StartCallBack() {
                    @Override
                    public void onStart() {

                    }
                })
                .onCancel(new CancelCallBack() {
                    @Override
                    public void onCancel() {

                    }
                })
                .onError(new ErrorCallBack() {
                    @Override
                    public void onError(Throwable error) {

                    }
                })
                .onSuccess(new SuccessCallBack() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            txt_result.setText(responseBody.string());
                        } catch (IOException e) {
                            //do nothing
                        }
                    }
                })
                .run();
    }

    private void newDownload() {
        NetWatch.download("http://wifiapi02.51y5.net/wifiapi/rd.do?f=wk00003&b=gwanz02&rurl=http%3A%2F%2Fdl.lianwifi.com%2Fdownload%2Fandroid%2FWifiKey-3091-guanwang.apk")
                .tag(this)
                .onStart(new StartCallBack() {
                    @Override
                    public void onStart() {
                        mProgressBar.setProgress(0);
                    }
                })
                .onCancel(new CancelCallBack() {
                    @Override
                    public void onCancel() {

                    }
                })
                .onError(new ErrorCallBack() {
                    @Override
                    public void onError(Throwable error) {

                    }
                })
                .onProgress(new ProgressCallBack() {
                    @Override
                    public void onProgress(int progress, long speed, long downloadedSize, long totalSize) {
                        mProgressBar.setProgress(progress);
                        txt_result.setText(downloadedSize + "/" + totalSize + "    " + progress + "%");

                    }
                })
                .onSuccess(new DownloadSuccessCallback() {

                    @Override
                    public void onSuccess(String key, String path, String name, long fileSize) {
                        Logger.d("download", "onSuccess");
                        Toast.makeText(mContext, name + " download success", Toast.LENGTH_SHORT).show();


                    }
                })
                .run();
    }

    private void newUpload() {
        try {
            //为了方便就不动态申请权限了,直接将文件放到CacheDir()中
            File file1 = new File(getCacheDir(), "a.java");
            File file2 = new File(getCacheDir(), "ic_launcher.png");
            //读取Assets里面的数据,作为上传源数据
            writeToFile(getAssets().open("a.java"), file1);
            writeToFile(getAssets().open("ic_launcher.png"), file2);
            //若要保持顺序  使用LinkedHashMap
            Map<String, UploadFileBody> map = new HashMap<>();
            map.put("a.java", new UploadFileBody(ContentType.JAVA, file1));
            map.put("ic_launcher1.png", new UploadFileBody(ContentType.IMAGE, file2));


            NetWatch.upload("http://upload.qiniu.com/")
                    .tag(this)
                    .files(map)
                    .onStart(new StartCallBack() {
                        @Override
                        public void onStart() {
                            mProgressBar.setProgress(0);

                        }
                    })
                    .onCancel(new CancelCallBack() {
                        @Override
                        public void onCancel() {
                            Toast.makeText(mContext, "onCancel", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .onError(new ErrorCallBack() {
                        @Override
                        public void onError(Throwable error) {
                            Toast.makeText(mContext, "error " + error.toString(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .onProgress(new ProgressCallBack() {
                        @Override
                        public void onProgress(int progress, long speed, long downloadedSize, long totalSize) {
                            mProgressBar.setProgress(progress);
                            txt_result.setText(downloadedSize + "/" + totalSize + "     " + progress + "%");

                        }
                    })
                    .onSuccess(new SuccessCallBack() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .run();

        } catch (IOException e) {
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

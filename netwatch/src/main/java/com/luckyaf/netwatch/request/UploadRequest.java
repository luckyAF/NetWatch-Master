package com.luckyaf.netwatch.request;

import android.support.annotation.NonNull;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.callback.UploadCallBack;
import com.luckyaf.netwatch.constant.ContentType;
import com.luckyaf.netwatch.upload.UploadFileBody;
import com.luckyaf.netwatch.upload.UploadRequestBody;
import com.luckyaf.netwatch.utils.RxUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/10/31
 */
public class UploadRequest extends BaseRequest<UploadRequest>{


    private List<UploadFileBody> files = new ArrayList<>();

    public UploadRequest(@NonNull String url) {
        super(url);
    }

    public UploadRequest addFile(UploadFileBody fileBody){
        files.add(fileBody);
        return this;
    }
    public UploadRequest addFiles(Collection<UploadFileBody> files){
        this.files.addAll(files);
        return this;
    }





    public void execute(@NonNull final  UploadCallBack mUploadCallBack){
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String,Object> entry:params.entrySet()){
            if(entry.getValue().getClass().isArray()){
                int length = Array.getLength(entry.getValue());
                Object[] os = new Object[length];
                for (int i = 0; i < os.length; i++) {
                    os[i] = Array.get(entry.getValue(), i);
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, os[i].toString()));
                }
            }else{
                builder.addFormDataPart(entry.getKey(),entry.getValue().toString());
            }
        }

        if(null!= files && files.size()>0){
            for(UploadFileBody bean:files){
                builder.addFormDataPart(bean.getKey(),bean.getFileName(), bean.toRequestBody());
            }
        }
        RequestBody requestBody = builder.build();
        UploadRequestBody uploadRequestBody = new UploadRequestBody(requestBody, mUploadCallBack);

        final Call<ResponseBody> call = NetWatch.getService().upload(url, this.headers, uploadRequestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    mUploadCallBack.onNext(response.body());
                } else {
                    mUploadCallBack.onError(message(response.message()));
                }


            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                mUploadCallBack.onError(t);

            }
        });
    }
}

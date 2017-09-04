# NetWatch-Master
retrofit2.0 + rxjava2  网络封装框架
> 支持请求取消 支持上传下载进度监听

## 导入
```
compile 'com.luckyaf:netwatch:1.1.9'
```
## 使用
--------------------
### 初始化
```
NetWatch.init(this,"http://xxxx.com")//base url
        .build();
          
```

###  请求实例  详细例子可根据demo获取
```
        Map<String,String> header = new HashMap<>();
        header.put("mobileNumber", "18826412577");
        header.put("loginPassword", "123456");
        Map<String,Object> params = new HashMap<>();
        params.put("start", "0");
        params.put("count", "1");
        NetWatch.open(this,"http://api.douban.com/v2/movie/top250")
                .headers(header)//可以没有
                .tag(this)//添加标签 可用于取消请求
                .get(params, new CommonCallBack() {
                    @Override
                    public void onCancel() {}
                    @Override
                    public void onComplete() {}
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            txt_result.setText(responseBody.string());
                        }catch (IOException e){
                            //do nothing
                        }
                    }
                });
```

### 新增链式调用  详细例子可根据demo获取
```
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
                })//可以没有
                .onCancel(new CancelCallBack() {
                    @Override
                    public void onCancel() {

                    }
                })//可以没有
                .onError(new ErrorCallBack() {
                    @Override
                    public void onError(Throwable error) {

                    }
                })//可以没有
                .onSuccess(new SuccessCallBack() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            txt_result.setText(responseBody.string());
                        } catch (IOException e) {
                            //do nothing
                        }
                    }
                })//可以没有
                .run();
```
###  取消请求
```
  //将取消所有有该tag的请求 若想取消单个url 可以在tag后加上要取消的url
    NetWatch.cancelRequest(tag)
```

### TODO:
- [ ] 支持https
- [ ] 支持 cache
- [ ] 支持临时设置超时等选项
- [ ] 支持更个性化的调用



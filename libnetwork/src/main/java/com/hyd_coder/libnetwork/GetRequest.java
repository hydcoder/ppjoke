package com.hyd_coder.libnetwork;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/8 17:57
 * description : get请求
 */
public class GetRequest<T> extends Request<T, GetRequest> {

    public GetRequest(String url) {
        super(url);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        // get 请求把参数拼接在 url后面
        String url = UrlCreator.createUrlFromParams(mUrl, mParams);
        okhttp3.Request request = builder.get().url(url).build();
        return request;
    }
}

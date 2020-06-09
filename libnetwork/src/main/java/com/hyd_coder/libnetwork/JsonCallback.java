package com.hyd_coder.libnetwork;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/8 17:56
 * description : 默认的jsonCallBack, 子类可选择醒复写某一回调方法
 */
public abstract class JsonCallback<T> {

    public void onSuccess(ApiResponse<T> response) {

    }

    public void onError(ApiResponse<T> response) {

    }

    public void onCacheSuccess(ApiResponse<T> response) {

    }
}

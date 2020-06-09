package com.hyd_coder.libnetwork;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/8 17:55
 * description : 后台返回数据的包装类
 */
public class ApiResponse<T> {
    public boolean success;
    public int status;
    public String message;
    public T body;
}

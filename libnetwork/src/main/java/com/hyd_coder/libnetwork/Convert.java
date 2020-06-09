package com.hyd_coder.libnetwork;

import java.lang.reflect.Type;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/8 16:59
 * description : 接口数据转换成实体类
 */
public interface Convert<T> {
    T convert(String response, Type type);
}

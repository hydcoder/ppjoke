package com.hyd_coder.libnetwork;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/8 17:01
 * description : 默认的Json转 Java Bean的转换器
 */
public class JsonConvert implements Convert {
    @Override
    public Object convert(String response, Type type) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                Object data1 = data.getObject("data", type);
                return JSON.parseObject(data1.toString(), type);
            }
        }
        return null;
    }
}

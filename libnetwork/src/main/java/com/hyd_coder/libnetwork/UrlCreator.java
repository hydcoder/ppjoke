package com.hyd_coder.libnetwork;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/8 17:15
 * description : 将get请求的参数拼接到url上
 */
public class UrlCreator {

    public static String createUrlFromParams(String url, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        builder.append(url);

        if (url.contains("?") || url.contains("&")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                String value = URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8");
                builder.append(entry.getKey()).append("=").append(value).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}

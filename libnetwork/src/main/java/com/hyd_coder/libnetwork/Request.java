package com.hyd_coder.libnetwork;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.hyd_coder.libnetwork.cache.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/8 17:28
 * description : 请求的基
 */
public abstract class Request<T, R extends Request> implements Cloneable {
    protected String mUrl;
    protected HashMap<String, String> mHeaders = new HashMap<>();
    protected HashMap<String, Object> mParams = new HashMap<>();

    // 仅仅只访问本地缓存，即便本地缓存不存在，也不会发起网络请求
    public static final int CACHE_ONLY = 1;
    // 先访问缓存，同时发起网络的请求，成功后缓存到本地
    public static final int CACHE_FIRST = 2;
    // 仅仅只访问服务器，本地不做任何存储
    public static final int NET_ONLY = 3;
    // 先访问网络，成功后缓存到本地
    public static final int NET_CACHE = 4;

    private String mCacheKey;
    private Type mType;

    private int mCacheStrategy;

    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_ONLY, NET_CACHE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CacheStrategy {

    }

    public Request(String url) {
        // /list
        mUrl = url;
    }

    public R addHeader(String key, String value) {
        mHeaders.put(key, value);
        return (R) this;
    }

    public R addParam(String key, Object value) {
        if (value == null) {
            return (R) this;
        }

        // value只能为int byte char short long double float boolean 和他们的包装类型，但是除了String.class 所以要额外判断
        if (value.getClass() == String.class) {
            mParams.put(key, value);
        } else {
            try {
                Field field = value.getClass().getField("TYPE");
                Class clazz = (Class) field.get(null);
                if (clazz.isPrimitive()) {
                    mParams.put(key, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (R) this;
    }

    public R cacheStraregy(@CacheStrategy int cacheStrategy) {
        mCacheStrategy = cacheStrategy;
        return (R) this;
    }

    public R cacheKey(String cacheKey) {
        mCacheKey = cacheKey;
        return (R) this;
    }

    public R responseType(Type type) {
        mType = type;
        return (R) this;
    }

    public R responseType(Class clazz) {
        mType = clazz;
        return (R) this;
    }

    public Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders();
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.sOkHttpClient.newCall(request);
        return call;
    }

    /**
     * 由子类实现具体的请求类型
     *
     * @param builder okhttp3.Request.Builder
     * @return okhttp3.Request
     */
    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);


    private void addHeaders() {
        for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
            mHeaders.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 同步请求
     *
     * @return 解析后的数据bean
     */
    public ApiResponse<T> execute() {
        if (mType == null) {
            throw new RuntimeException("同步方法,response 返回值 类型必须设置");
        }

        if (mCacheStrategy == CACHE_ONLY) {
            return readCache();
        }
        ApiResponse<T> result = null;
        try {
            Response response = getCall().execute();
            result = parseResponse(response, null);
        } catch (IOException e) {
            e.printStackTrace();
            if (result == null) {
                result = new ApiResponse<>();
                result.message = e.getMessage();
            }
        }
        return result;
    }

    @SuppressLint("RestrictedApi")
    public void execute(final JsonCallback<T> callback) {
        if (mCacheStrategy != NET_ONLY) {
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ApiResponse<T> response = readCache();
                    if (callback != null && response.body != null) {
                        callback.onCacheSuccess(response);
                    }
                }
            });

            if (mCacheStrategy != CACHE_ONLY) {
                getCall().enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        ApiResponse<T> response = new ApiResponse<>();
                        response.message = e.getMessage();
                        response.success = false;
                        callback.onError(response);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        ApiResponse<T> apiResponse = parseResponse(response, callback);
                        if (apiResponse.success) {
                            callback.onSuccess(apiResponse);
                        } else {
                            callback.onError(apiResponse);
                        }
                    }
                });
            }
        }
    }

    private ApiResponse<T> parseResponse(Response response, JsonCallback<T> callback) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();

        Convert convert = ApiService.sConvert;
        try {
            String content = response.body().toString();
            if (success) {
                if (callback != null) {
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                }
            } else {
                message = content;
            }
        } catch (Exception e) {
            message = e.getMessage();
            status = 0;
            success = false;
        }

        result.success = success;
        result.status = status;
        result.message = message;

        if (mCacheStrategy != NET_ONLY && result.success && result.body instanceof Serializable) {
            saveCache(result.body);
        }

        return result;
    }

    private void saveCache(T body) {
        String key = TextUtils.isEmpty(mCacheKey) ? generateCacheKey() : mCacheKey;
        CacheManager.save(body, key);
    }

    private  ApiResponse<T> readCache(){
        String key = TextUtils.isEmpty(mCacheKey) ? generateCacheKey() : mCacheKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = "缓存获取成功";
        response.status = 304;
        response.body = (T) cache;
        return response;
    }

    private String generateCacheKey() {
        mCacheKey = UrlCreator.createUrlFromParams(mUrl, mParams);
        return mCacheKey;
    }

    @NonNull
    @Override
    public Request clone() throws CloneNotSupportedException {
        return (Request<T, R>) super.clone();
    }
}

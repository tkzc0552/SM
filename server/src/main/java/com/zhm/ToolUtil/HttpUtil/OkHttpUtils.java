package com.zhm.ToolUtil.HttpUtil;

import okhttp3.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by 赵红明 on 2019/9/6.
 */
public class OkHttpUtils {

    private static OkHttpClient client;

    static {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    public static Response post(String url, Headers headers, String body, MediaType mediaType) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder().url(url).headers(headers).post(requestBody).build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response postForm(String url, Headers headers, FormBody body) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if(headers != null) {
            requestBuilder.headers(headers);
        }
        if(body != null) {
            requestBuilder.post(body);
        }
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response get(String url, Headers headers) throws IOException {
        Request request = new Request.Builder().url(url).headers(headers).get().build();
        Response response = client.newCall(request).execute();
        return response;
    }



    public static Response get(String url, Map<String, String> params, Cookie cookie) throws IOException {
        String requestUrl = buildUrl(url, params);
        return get(requestUrl, cookie);
    }

    public static Response get(String url, Map<String, String> params, String cookieName, String cookieValue) throws IOException {
        String requestUrl = buildUrl(url, params);
        return get(requestUrl, cookieName, cookieValue);
    }

    public static String buildUrl(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if(params != null && !params.isEmpty()) {
            urlBuilder.append("?");
            Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, String> entry = entryIterator.next();
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                if(entryIterator.hasNext()) {
                    urlBuilder.append("&");
                }
            }
        }
        String requestUrl = urlBuilder.toString();
        return requestUrl;
    }

    public static Response get(String url, Cookie cookie) throws IOException {
        Request request = new Request.Builder().url(url).addHeader("Cookie", cookie.name() + "=" + cookie.name()).get().build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response get(String url, String cookieName, String cookieValue) throws IOException {
        Request request = new Request.Builder().url(url).addHeader("Cookie", cookieName + "=" + cookieValue).get().build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response postJson(String url, String body) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = client.newCall(request).execute();
        return response;
    }

}

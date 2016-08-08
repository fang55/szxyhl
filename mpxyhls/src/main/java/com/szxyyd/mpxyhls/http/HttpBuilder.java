package com.szxyyd.mpxyhls.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fq on 2016/8/4.
 */
public class HttpBuilder {
    private StringBuffer url;
    private Map<String, Object> data;

    public HttpBuilder() {
        data = new HashMap<>();
        url = new StringBuffer();
    }
    public HttpBuilder put(String name, Object value) {
        data.put(name, value);
        return this;
    }
    public HttpBuilder url(String url) {
        this.url.append(url);
        return this;
    }
    public String getGetUrl() {
        for (String key: data.keySet()) {
            if (!url.toString().contains("?")) {
                url.append("?").append(key).append("=").append(data.get(key));
            } else {
                url.append("&").append(key).append("=").append(data.get(key));
            }
        }
        return url.toString();
    }
}

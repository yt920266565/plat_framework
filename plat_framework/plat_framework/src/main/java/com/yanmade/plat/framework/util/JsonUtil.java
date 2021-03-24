package com.yanmade.plat.framework.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonUtil {
    private JsonUtil() {
    }

    public static JSONObject getRequestJsonObject(HttpServletRequest request) throws IOException {
        String json = getRequestJsonString(request);
        return JSON.parseObject(json);
    }

    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request
     * @return : <code>byte[]</code>
     * @throws IOException
     */
    public static String getRequestJsonString(HttpServletRequest request) throws IOException {
        String submitMehtod = request.getMethod();
        // GET
        if (submitMehtod.equals("GET")) {
            return new String(request.getQueryString().getBytes("iso-8859-1"), "utf-8").replace("%22", "\"");
            // POST
        } else {
            return getRequestPostStr(request);
        }
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     * 
     * <pre>
     * 举例：
     * </pre>
     * 
     * @param request
     * @return
     * @throws IOException
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return new byte[0];
        }
        byte[] buffer = new byte[contentLength];
        int i = 0;
        while (i < contentLength) {
            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }

        return buffer;
    }

    /**
     * 描述:获取 post 请求内容
     * 
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request) throws IOException {
        byte[] buffer = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

}

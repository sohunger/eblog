package com.huang.common;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class RepeatableReadRequestWrapper extends HttpServletRequestWrapper {
    private byte[] cachedBody;
    private boolean hasReadBody = false; // 关键标志位

    public RepeatableReadRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 关键：不要在构造函数中读取流！
        // 只做初始化，不调用 request.getInputStream()
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!hasReadBody) {
            // 第一次调用时才真正读取并缓存
            cacheRequestBody();
        }
        // 每次返回一个基于缓存数据的新流
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }

    private void cacheRequestBody() throws IOException {
        // 读取原始请求的流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream inputStream = super.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        }
        this.cachedBody = baos.toByteArray();
        this.hasReadBody = true;
    }

    public String getBodyString() throws IOException {
        if (!hasReadBody) {
            cacheRequestBody();
        }
        return new String(cachedBody, getCharacterEncoding());
    }

    // 内部类：提供基于缓存的ServletInputStream
    private static class CachedBodyServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream byteArrayInputStream;
        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        }
        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
        }
        @Override
        public boolean isReady() {
            return true;
        }
        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }
        @Override
        public int read() {
            return byteArrayInputStream.read();
        }
    }
}

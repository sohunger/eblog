package com.huang.util;

import java.io.File;
import java.io.IOException;

import cn.hutool.core.util.StrUtil;
import com.huang.common.lang.Result;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.Base64;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

@Component
public class QiniuCloudUtil {

    // 设置需要操作的账号的AK和SK
    private static final String ACCESS_KEY = "Z2xzTBh7GJjlTXXaG4_1t2BRqAXAsO7gH3H6wzTz";
    private static final String SECRET_KEY = "miW1WeBkMSy5ajkIuSuiNR9T176GOxOIIIvl_WJM";

    private static final Configuration cfg = new Configuration(Zone.zone2());

    // 要上传的空间
    private static final String bucketname = "eblogmassege";

    // 密钥
    private static final Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    private static final String DOMAIN = "http://r989vp5cr.hn-bkt.clouddn.com/";


    public String getUpToken() {
        return auth.uploadToken(bucketname, null, -1, new StringMap().put("insertOnly", 1));
    }

    // 普通上传
    public Result upload(String filePath, String fileName, File file) throws IOException {
        // 创建上传对象
        UploadManager uploadManager = new UploadManager(cfg);
        try {
            // 调用put方法上传
            String token = auth.uploadToken(bucketname);
            if (StrUtil.isEmpty(token)) {
                return Result.fail("未获取到token，请重试！");
            }
            Response res = uploadManager.put(filePath, fileName, token);
            // 打印返回的信息
            if (res.isOK()) {
                Ret ret = res.jsonToObject(Ret.class);
                //如果不需要对图片进行样式处理，则使用以下方式即可
                return Result.success(DOMAIN + ret.key);

            }
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());
            try {
                // 响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException e1) {
                // ignore
            }
        } finally {
            //将本地副本删除
            File del = new File(file.toURI());
            del.delete();
        }
        return null;
    }

    class Ret {
        public long fsize;
        public String key;
        public String hash;
        public int width;
        public int height;
    }
}
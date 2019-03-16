package com.zeal.zealsay.common.third.qiniu;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 七牛云对象云存储服务.
 *
 * @author  zhanglei
 * @date 2019-03-15  15:55
 */
@Slf4j
@Component
public class QiniuService implements InitializingBean {

  @Autowired
  UploadManager uploadManager;
  @Autowired
  BucketManager bucketManager;
  @Autowired
  Auth auth;


  @Value("${qiniu.Bucket}")
  private String bucket;

  @Value("${qiniu.Domain}")
  private String domain;

  /**
   * 定义七牛云上传的相关策略.
   *
   * @author  zhanglei
   * @date 2019-03-15  16:00
   */
  private StringMap putPolicy;

  public String  uploadFile(File file, String key) throws QiniuException {
    Response response = this.uploadManager.put(file, key , getUploadToken());
    int retry = 0;
    while (response.needRetry() && retry < 3) {
      response = this.uploadManager.put(file, key, getUploadToken());
      retry++;
    }
    Ret ret = response.jsonToObject(Ret.class);
    log.info("文件上传成功,key={},文件url={}",ret.key,domain+key);
    return domain+ret.key;
  }

  /**
   * 以流的形式上传.
   *
   * @author  zhanglei
   * @date 2019-03-15  15:59
   */
  public String uploadFile(InputStream inputStream,String key) throws QiniuException {
    Response response = this.uploadManager.put(inputStream, key, getUploadToken(), null, null);
    int retry = 0;
    while (response.needRetry() && retry < 3) {
      response = this.uploadManager.put(inputStream, key, getUploadToken(), null, null);
      retry++;
    }
    Ret ret = response.jsonToObject(Ret.class);
    log.info("文件上传成功,key={},文件url={}",ret.key,domain+key);
    return domain+ret.key;
  }

  /**
   * 删除七牛云上的相关文件.
   *
   * @author  zhanglei
   * @date 2019-03-15  15:59
   */
  public Response delete(String key) throws QiniuException {
    Response response = bucketManager.delete(this.bucket, key);
    int retry = 0;
    while (response.needRetry() && retry++ < 3) {
      response = bucketManager.delete(bucket, key);
    }
    return response;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.putPolicy = new StringMap();
//    putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
  }

  /**
  * 生成文件名称.
  *
  * @author  zeal
  * @date 2019/3/17 0:33
  */
  public String createFileName(MultipartFile file) {
    LocalDateTime now= LocalDateTime.now();
    StringBuffer sb = new StringBuffer();
    sb.append(now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
            .append(now.getNano());
    if (StringUtils.isNotBlank(file.getOriginalFilename())
            && file.getOriginalFilename().indexOf(".") >= 0) {
      sb.append(file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")));
    }
    return sb.toString();
  }

  /**
   * 获取上传凭证.
   *
   * @author  zhanglei
   * @date 2019-03-15  15:59
   */
  private String getUploadToken() {
    return this.auth.uploadToken(bucket, null, 3600, putPolicy);
  }

  class Ret {
    public long fsize;
    public String key;
    public String hash;
    public int width;
    public int height;
  }

}

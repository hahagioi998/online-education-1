package com.atguigu.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.oss.service.OssService;
import com.atguigu.oss.utils.ConstantPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        String endpoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;
        String fileName = file.getOriginalFilename();

        //1.在文件名称里面添加随机唯一的值
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        fileName = uuid + fileName;

        //2.把文件按照日期进行分类
        String datePath = new DateTime().toString("yyyy/MM/dd");   //获取当前日期
        fileName = datePath + "/" +fileName;    //拼接 2021/04/06/uuid111.jpg

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        InputStream inputStream = null;
        String url = "";
        try {
            inputStream = file.getInputStream();
            // 上传文件到指定的存储空间（bucketName）并将其保存为指定的文件名称（objectName）。
            ossClient.putObject(bucketName, fileName, inputStream);
            //需要把上传到阿里云oss路径手动拼接出来
            url ="https://" + bucketName + "." + endpoint + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
        return url;
    }
}

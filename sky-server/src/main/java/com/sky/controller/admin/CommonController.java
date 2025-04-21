package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequestMapping("/admin/common")
@RestController
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    // 使用阿里云oss来做文件存储
    @PostMapping("/upload")
    public Result upload(MultipartFile file){
        //1.调用ALiOssUtil工具类来upload文件上传方法
        String url = null;
        try {
            String objectName = UUID.randomUUID().toString() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            url = aliOssUtil.upload(file.getBytes(),objectName);
        } catch (IOException e) {
            log.info("文件上传失败！！{}",e.getMessage());
        }

        //2.返回图片路径结果
        return Result.success(url);
    }
}

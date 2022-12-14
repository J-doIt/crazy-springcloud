package com.crazymaker.cloud.ha.middleware.controller;

import com.crazymaker.cloud.ha.middleware.service.impl.MinIOService;
import io.minio.GetObjectResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

//@RestController
@RequestMapping("/upload/")
public class MinIOFileController {

    @Autowired
    private MinIOService minIOService;
    @Autowired
    @Value("${minio.url}" + "/" + "${minio.bucket}" + "/")
    private String imgUrl;

    // 上传，上传成功会返回文件名
    @PostMapping
    public String upload(MultipartFile file) throws Exception {
        // 获取文件后缀名
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        // 为了避免文件名重复，使用UUID重命名文件，将横杠去掉
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        // 上传
        minIOService.putObject(file.getInputStream(), fileName, file.getContentType());
        return fileName;
    }

    // 根据文件名下载文件
    @GetMapping("{fileName}")
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileName") String fileName) throws Exception {
        // 设置响应类型
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        // 获取文件流
        GetObjectResponse objectResponse = minIOService.getObject(fileName);
        // 将文件流输出到响应流
        IOUtils.copy(objectResponse, response.getOutputStream());
        // 结束
        response.flushBuffer();
        objectResponse.close();
    }

    // 根据文件名删除文件
    @DeleteMapping("{fileName}")
    public String remove(@PathVariable("fileName") String fileName) throws Exception {
        minIOService.removeObject(fileName);
        return "success";
    }
}
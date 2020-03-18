package com.changgou.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.util.FastDFSClient;
import com.changgou.util.FastDFSFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {


    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file){
        try {
            //1.获取文件名称
            String orgName = file.getOriginalFilename();
            //2.获取文件扩展名
            int index = orgName.lastIndexOf(".");
            String extName = orgName.substring(index);

            //3.获取文件内容
            byte[] content = file.getBytes();

            //4.拼装上传文件对象
            FastDFSFile fastDFSFile = new FastDFSFile(orgName, content, extName);


            //5.执行上传
            String[] upload = FastDFSClient.upload(fastDFSFile);


            //6.处理上传结果，拼接URL
            if(upload==null || upload.length!=2){
                return new Result(false, StatusCode.ERROR, "上传文件失败");
            }

            String groupName = upload[0];
            String remoteFilePath = upload[1];
            //TODO 这里应该将用户上传的文件保存到用户文件表中
            String url = FastDFSClient.getTrackerUrl() + groupName + "/" + remoteFilePath;

            return new Result(true, StatusCode.OK, "上传成功" ,url);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR, "上传文件失败");
        }
    }
}

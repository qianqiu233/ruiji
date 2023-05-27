package com.qianqiu.ruiji_take_out.controller;

import com.qianqiu.ruiji_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${ruiji.imagePath}")
    private String imagePath;
    /**
     *     上传图片
     */

    @PostMapping("/upload")
    public R<String> upLoadImage(MultipartFile file){
//        file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix= originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID重新生成文件名,防止文件名称重复，造成文件覆盖
        String fileName= UUID.randomUUID().toString()+suffix;

        //创建目录对象
        File fileDir=new File(imagePath);
        //判断该目录是否存在
        if(!fileDir.exists()){
           //目录不存在，需要创建
            fileDir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(imagePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 下载文件
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void downloadImage(String name, HttpServletResponse response){

        try {
            //输入流,通过输入流读取文件内容
            FileInputStream fileInputStream=new FileInputStream(new File(imagePath+name));
            //输出流，通过输出流将文件写回浏览器，在浏览器中展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            //设置响应格式
            response.setContentType("image/jpeg");
            int len=0;
            byte[] bytes=new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

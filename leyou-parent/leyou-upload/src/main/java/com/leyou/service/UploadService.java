package com.leyou.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {

    public static final List<String> CONTENT_TYPE= Arrays.asList("image/jepg","image/gif");
    public static final Logger logger= LoggerFactory.getLogger(UploadService.class);
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    public String uploadFile(MultipartFile file) {
        //获取文件的原始名字
        String originalFilename = file.getOriginalFilename();
        //获取文件的头部信息
        String contentType = file.getContentType();
        //校验文件头部信息是否合法
        if (CONTENT_TYPE.contains(contentType)) {
            logger.info("文件信息不合法：{}",originalFilename);
            return null;
        }
        //获取文件的内容
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage==null){
                logger.info("文件信息不合法：{}",originalFilename);
                return null;
            }
            //转存文件
          /*  file.transferTo(new File("D:\\IDEA_work\\leyou\\image\\"+originalFilename));*/
            String extend = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extend, null);
            return "http://image.leyou.com/"+storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return null;
    }
}

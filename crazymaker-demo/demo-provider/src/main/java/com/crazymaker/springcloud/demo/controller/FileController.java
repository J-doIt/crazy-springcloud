package com.crazymaker.springcloud.demo.controller;


import com.crazymaker.springcloud.common.util.FileUtil;
import com.crazymaker.springcloud.seckill.api.dto.AttachmentOutDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.regex.Pattern;

import static com.crazymaker.springcloud.common.constants.ConfigConstants.UPLOAD_FILE_DIRECTORY;
import static com.crazymaker.springcloud.common.util.UUIDUtil.uuid;


@Api(value = "文件上传", tags = {"文件上传demo"})

@RestController
@RequestMapping("/api/file")
public class FileController {
    public final static int MAX_UPLOAD_SIZE = 5 * 1024 * 1024;

    private final static Logger logger = LoggerFactory.getLogger(FileController.class);
    /**
     * 文件类型
     */
    public static final String ALLOW_TYPE = ".xml|.docx|.doc|.pdf|.xlsx|.xls|.pdf|.txt|.png|.jpg|.bmp";


    @ApiOperation(value = "附件上传")
    @PostMapping("/upload/v1")
    public AttachmentOutDTO upload(
            @RequestParam(value = "file") MultipartFile file) {

        AttachmentOutDTO attachmentOutDTO = new AttachmentOutDTO();
        String srcFileName = file.getOriginalFilename();  //原名字

        try {
            String reg = ".+(" + ALLOW_TYPE + ")$";
            Pattern pattern = Pattern.compile(reg);
            String extendName = "";

            extendName = srcFileName.substring(srcFileName.indexOf("."), srcFileName.length());

            if (file.getSize() > MAX_UPLOAD_SIZE) {
                attachmentOutDTO.setMsg("超出附件大小限制" + MAX_UPLOAD_SIZE + "M");
                attachmentOutDTO.setStatus("1024");
                return attachmentOutDTO;

            } else if (file.getSize() == 0) {
                attachmentOutDTO.setStatus("1024");
                attachmentOutDTO.setMsg("上传附件不能为空！");
                return attachmentOutDTO;
            } else {

                String attachid = uuid();
                String root = UPLOAD_FILE_DIRECTORY;

                String path = attachid + extendName;
                File rootFile = new File(root + path);

                if (!rootFile.exists()) {
                    rootFile.mkdirs();
                }
                FileUtil.uploadFile(file,
                        root,
                        attachid + extendName);


                attachmentOutDTO.setName(srcFileName);
                attachmentOutDTO.setMsg("上传成功");
                attachmentOutDTO.setStatus("200");
                return attachmentOutDTO;

            }
        } catch (Exception e) {
            logger.error("附件上传异常", e);
            attachmentOutDTO.setMsg("附件上传异常");
            attachmentOutDTO.setStatus("1024");
            return attachmentOutDTO;

        }
    }


}

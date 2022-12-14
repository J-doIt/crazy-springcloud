package com.crazymaker.springcloud.common.util;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.crazymaker.springcloud.common.constants.ConfigConstants.UPLOAD_FILE_DIRECTORY;

public class DownLoadUtils {


    /**
     * 实现附件下载
     *
     * @param filePath 需要下载的附件路径
     * @param fileName 下载附件名
     * @param response 请求的响应
     */
    public static void downFile(String filePath, String fileName, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        BufferedInputStream bufferedinputstream = null;
        InputStream is = null;
        try {
            File dFile = new File(filePath);
            if (dFile != null) {
                response.setContentLength((int) dFile.length());
            }
            is = new FileInputStream(filePath);
            bufferedinputstream = new BufferedInputStream(is);
            byte abyte0[] = new byte[1024];//MAX_UPLOAD_SIZE
            int len = 0;
            while ((len = bufferedinputstream.read(abyte0)) > 0) {
                response.getOutputStream().write(abyte0, 0, len);
            }
            bufferedinputstream.close();
        } finally {
            try {
                if (bufferedinputstream != null) {
                    bufferedinputstream.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                System.out.println("从附件\"" + filePath + "\"中使用流方式进行下载后清理使用资源出现异常:" + e2.getMessage());
            }
        }
    }

    /**
     * @param @param  file
     * @param @return 设定附件
     * @return byte[]    返回类型
     * @throws
     * @Title: readFileToByteArray
     */
    public static void viewFile(String filePath, String fileName, int maxUploadSize, HttpServletResponse response) throws IOException {
        OutputStream os = null;
        FileInputStream fis = null;
//		response.setContentType("text/html; charset=UTF-8");
        response.setContentType("image/jpeg");
        try {
            fis = new FileInputStream(UPLOAD_FILE_DIRECTORY + filePath);
            os = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[maxUploadSize];
            while ((count = fis.read(buffer)) != -1) {
                os.write(buffer, 0, count);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                os.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }
}

package com.crazymaker.springcloud.common.constants;

/**
 * Created by 尼恩 on 2019/7/23.
 */
public class ConfigConstants
{

    /**
     * 配置类型缓存的key
     */

    public static final String CONFIG_SECKILL_skuS = "seckill:skus:";

    /**
     * 缓存配置的前缀
     */
    public static final String CONFIG_SECKILL_ORDER = "sku:orders:";


    /**
     * 系统级配置的名称
     */
    public static final String CONFIG_CLASS_SYS = "系统级配置";

   /**
     * 文件上传目录
     */
    public static final String UPLOAD_FILE_DIRECTORY =
           System.getProperty("upload_directory")==null?"e:/app/upload/":System.getProperty("upload_directory");


}

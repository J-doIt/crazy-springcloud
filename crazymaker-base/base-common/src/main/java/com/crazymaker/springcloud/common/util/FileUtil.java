package com.crazymaker.springcloud.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileAlreadyExistsException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;


public class FileUtil
{


    /**
     * 生成源码文件
     *
     * @param filePath 文件路径
     * @param content  文件内容
     */
    public static void generateFile(String filePath, String content) throws FileAlreadyExistsException
    {
        File file = new File(filePath);
        if (file.exists())
        {
            throw new FileAlreadyExistsException(filePath + "文件已经存在");
        } else
        {
            FileUtil.saveWriter(file, content);
        }
    }

    /**
     * 字符编码
     */
    public static final String ENCODE = "UTF-8";

    /**
     * 获取模板文件路径
     *
     * @param clazz 类对象
     */
    public static String templatePath(Class<?> clazz)
    {
        return clazz.getResource("").getPath() + clazz.getSimpleName() + ".tpl";
    }

    /**
     * 保存文本文件
     *
     * @param file    文件对象
     * @param content 文件内容
     */
    public static void saveWriter(File file, String content)
    {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try
        {
            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, ENCODE);
            osw.write(content);
            osw.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (osw != null)
            {
                try
                {
                    osw.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static String fileUpload(String attachid, String attachname, Blob accessorycontent, String filepath)
    {

        int one = attachname.lastIndexOf(".");
        String filetype = attachname.substring((one + 1), attachname.length());
        String urlfile = filepath + "/" + attachid + "." + filetype;

        if (null != accessorycontent)
        {

            CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
            {
                //用来检测程序运行时间
                long startTime = System.currentTimeMillis();
                System.out.println(startTime);
                System.out.println("开始附件保存本地:" + attachname);
                File updir = new File(filepath);
                if (!updir.exists())
                {
                    System.out.println("dir not exists, build it ...");
                    updir.mkdir();
                }
                FileOutputStream fos = null;
                InputStream in = null;
                File files = new File(urlfile);
                if (!files.exists())
                {
                    try
                    {

                        fos = new FileOutputStream(files);
                        in = accessorycontent.getBinaryStream();
                        int len = (int) accessorycontent.length();

                        byte[] buffer = new byte[len]; // 建立缓冲区

                        while ((len = in.read(buffer)) != -1)
                        {

                            fos.write(buffer, 0, len);

                        }
                        fos.close();
                        in.close();

                        long endTime = System.currentTimeMillis();
                        System.out.println(endTime);
                        System.out.println("附件保存本地完成:" + attachname);

                    } catch (Exception e)
                    {
                        e.printStackTrace();

                        long endTime = System.currentTimeMillis();
                        System.out.println(endTime);
                        System.out.println("附件保存本地失败:" + attachname);

                    } finally
                    {
                        if (in != null)
                        {
                            try
                            {
                                in.close();
                            } catch (IOException e)
                            {
                            }
                        }
                        if (fos != null)
                        {
                            try
                            {
                                fos.close();
                            } catch (IOException e)
                            {
                            }
                        }
                    }
                } else
                {
                    long endTime = System.currentTimeMillis();
                    System.out.println(endTime);
                    System.out.println("附件本地已存在无需保存本地 :" + attachname);
                }

                return "task true";
            });

            future.whenComplete((result, exception) ->
            {
                if (null == exception)
                {
                    System.out.println("datas from previous task: " + result);
                }
            });

            return attachid + "." + filetype;
        } else
        {

            return "";
        }

    }

    public static String fileUp(String attachid, String attachname, byte[] accessorycontent, String filepath)
    {
        int one = attachname.lastIndexOf(".");
        String filetype = attachname.substring((one + 1), attachname.length());
        String urlfile = filepath + "/" + attachid + "." + filetype;

        if (null != accessorycontent)
        {

            CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
            {
                // long running task
                //用来检测程序运行时间
                long startTime = System.currentTimeMillis();
                System.out.println(startTime);
                System.out.println("开始附件保存本地:" + attachname);
                File updir = new File(filepath);
                if (!updir.exists())
                {
                    System.out.println("dir not exists, build it ...");
                    updir.mkdir();
                }
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                File files = new File(urlfile);
                if (!files.exists())
                {
                    try
                    {

                        fos = new FileOutputStream(files);
                        bos = new BufferedOutputStream(fos);
                        bos.write(accessorycontent);
                        bos.flush();
                        bos.close();
                        fos.flush();
                        fos.close();

                        long endTime = System.currentTimeMillis();
                        System.out.println(endTime);
                        System.out.println("附件保存本地完成:" + attachname);

                    } catch (Exception e)
                    {
                        e.printStackTrace();

                        long endTime = System.currentTimeMillis();
                        System.out.println(endTime);
                        System.out.println("附件保存本地失败:" + attachname);

                    } finally
                    {
                        if (bos != null)
                        {
                            try
                            {
                                bos.close();
                            } catch (IOException e)
                            {
                            }
                        }
                        if (fos != null)
                        {
                            try
                            {
                                fos.close();
                            } catch (IOException e)
                            {
                            }
                        }
                    }
                } else
                {
                    long endTime = System.currentTimeMillis();
                    System.out.println(endTime);
                    System.out.println("附件本地已存在无需保存本地 :" + attachname);
                }
                return "task true";
            });

            future.whenComplete((result, exception) ->
            {
                if (null == exception)
                {
                    System.out.println("datas from previous task: " + result);
                }
            });

            return attachid + "." + filetype;
        } else
        {

            return "";
        }

    }

    /**
     * 获取指定附件夹下的所有附件信息列表
     *
     * @param strFolder 指定的附件夹
     * @return 返回的下级附件数组
     */
    public static String[] getFileList(String strFolder)
    {
        String[] subFiles = new String[0];
        File srcFolder = null;
        try
        {
            srcFolder = new File(strFolder);
            if (srcFolder.isDirectory())
            {
                subFiles = srcFolder.list();
            } else
            {
                subFiles = null;
            }
        } catch (Exception e)
        {
            subFiles = null;
            System.out.println("获取指定附件夹\"" + strFolder + "\"下的所有附件列表出现异常：" + e.getMessage());
        } finally
        {
            srcFolder = null;
        }
        return subFiles;
    }
    //附件上传工具类服务方法

    public static File uploadFile(MultipartFile file, String filePath, String fileName) throws Exception
    {

        File targetFile = new File(filePath);
        if (!targetFile.exists())
        {
            targetFile.mkdirs();
        }


//         op.append(str);
//        op.flush();
        file.transferTo(new File(filePath + fileName));

//        FileOutputStream out = new FileOutputStream(filePath + fileName);
//        OutputStreamWriter op = new OutputStreamWriter(out, "utf-8");
//        op.append(new String(file, "utf-8"));
//        op.flush();
//        op.close();
////        out.write(file);
////        out.flush();
////        out.close();
        return targetFile;
    }

    /**
     * 创建一个指定的空附件
     *
     * @param strPath 附件路径
     * @return
     */
    public static int createFile(String strPath)
    {
        int iFlag = 0;
        File file = null;
        try
        {
            file = new File(strPath);
            if (!file.exists())
            {
                if (!file.createNewFile())
                {
                    iFlag = -2;
                }
            }
        } catch (Exception e)
        {
            iFlag = -1;
            System.out.println("创建附件\"" + strPath + "\"出现异常:" + e.getMessage());
        } finally
        {
            file = null;
        }
        return iFlag;
    }

    /**
     * 创建一个指定的空附件夹
     *
     * @param strFolder 附件夹路径
     * @return
     */
    public static int createFolder(String strFolder)
    {
        int iFlag = 0;
        File file = null;
        try
        {
            file = new File(strFolder);
            if (!file.exists())
            {
                file.mkdir();
            }
        } catch (Exception e)
        {
            iFlag = -1;
            System.out.println("创建附件夹\"" + strFolder + "\"出现异常:" + e.getMessage());
        } finally
        {
            file = null;
        }
        return iFlag;
    }

    /**
     * 创建一个指定的空附件夹
     *
     * @param strBaseFolder 附件夹基础路径
     * @param strNewFolder  新增附件夹路径
     * @return
     */
    public static int createAllFolder(String strBaseFolder, String strNewFolder)
    {
        int iFlag = 0;
        try
        {
            String folders[] = tokenizeToStringArray(strNewFolder, "/");
            if (folders != null)
            {
                String strCurFolder = strBaseFolder;
                for (int i = 0; i < folders.length; i++)
                {
                    strCurFolder += "/" + folders[i];
                    createFolder(strCurFolder);
                }
            }
        } catch (Exception e)
        {
            iFlag = -1;
            System.out.println("创建附件夹\"" + strBaseFolder + "\"出现异常:" + e.getMessage());
        }
        return iFlag;
    }


    public static String[] tokenizeToStringArray(String str, String delimiters)
    {
        return tokenizeToStringArray(str, delimiters, true, true);
    }


    public static int deleteAllFolder(String strBaseFolder, String strDelFolder)
    {
        int iFlag = 0;
        try
        {
            String folders[] = tokenizeToStringArray(strDelFolder, "/");
            if (folders != null)
            {
                String strCurFolder = strBaseFolder;
                for (int i = 0; i < folders.length; i++)
                {
                    strCurFolder += folders[i];
                    deleteDirectory(strCurFolder);
                }
            }
        } catch (Exception e)
        {
            iFlag = -1;
            System.out.println("删除附件夹\"" + strBaseFolder + "\"出现异常:" + e.getMessage());
        }
        return iFlag;
    }

    public static String[] tokenizeToStringArray(String str, String delimiters,
                                                 boolean trimTokens, boolean ignoreEmptyTokens)
    {

        if (str == null)
        {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (trimTokens)
            {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0)
            {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] toStringArray(Collection<String> collection)
    {
        if (collection == null)
        {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }


    /**
     * 删除目录（附件夹）以及目录下的附件
     *
     * @param sPath 被删除目录的附件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath)
    {
        //如果sPath不以附件分隔符结尾，自动添加附件分隔符
        if (!sPath.endsWith(File.separator))
        {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的附件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory())
        {
            return false;
        }
        boolean flag = true;
        //删除附件夹下的所有附件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            //删除子附件
            if (files[i].isFile())
            {
                flag = delFile(files[i].getAbsolutePath()) >= 0;
                if (!flag)
                {
                    break;
                }
            } //删除子目录
            else
            {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                {
                    break;
                }
            }
        }
        if (!flag)
        {
            return false;
        }
        //删除当前目录
        if (dirFile.delete())
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * 获取文本附件的所有内容(可指定字符集)
     *
     * @param strPath  附件路径
     * @param encoding 字符集
     * @return
     */
    public static String getFileContent(String strPath, String encoding)
    {
        String strContent = "";
        //先判断是否存在，不存在就创建一个新的。
        createFile(strPath);
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(strPath), encoding));
            String line = null;
            while ((line = br.readLine()) != null)
            {
                strContent += line + "\r\n";
            }
            br.close();
        } catch (IOException e)
        {
            strContent = "";
            System.out.println("读取附件\"" + strPath + "\"内容出现异常:" + e.getMessage());
        } finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                    br = null;
                }
            } catch (Exception e)
            {
                System.out.println("FileUtil类中getFileContent释放资源时出现异常:" + e.getMessage());
            }
        }

        return strContent;
    }

    /**
     * 删除指定的附件
     *
     * @param strPath 附件路径
     * @return
     */
    public static int delFile(String strPath)
    {
        int iFlag = 0;
        File file = null;
        try
        {
            file = new File(strPath);
            if (file.exists())
            {
                file.delete();
            }
            file = null;
        } catch (Exception e)
        {
            iFlag = -1;
            System.out.println("删除附件\"" + strPath + "\"出现异常：" + e.getMessage());
        }
        return iFlag;
    }

    /**
     * 删除指定的附件
     *
     * @param strPath 附件路径
     * @return
     */
    public static boolean isExists(String strPath)
    {
        boolean bFlag = false;
        File file = null;
        try
        {
            file = new File(strPath);
            if (file.exists())
            {
                bFlag = true;
            } else
            {
                bFlag = false;
            }
        } catch (Exception e)
        {
            bFlag = false;
            System.out.println("判断附件\"" + strPath + "\"是否存在出现异常：" + e.getMessage());
        }
        return bFlag;
    }

    /**
     * 从附件路径中获取附件名
     *
     * @param strPath 附件路径
     * @return
     */
    public static String getFileName(String strPath)
    {
        if (StringUtils.isBlank(strPath))
        {
            return "";
        }
        String strTempFilePath = StringUtils.replace(strPath, "\\", "/");
        if (StringUtils.isBlank(strTempFilePath))
        {
            return "";
        }
        //如果最后一个是/，需要先排除掉
        /*if(strTempFilePath.charAt(strTempFilePath.length()-1)=='/'){
            strTempFilePath = strTempFilePath.substring(0,strTempFilePath.length()-1);
		}*/
        int iEndPos = strTempFilePath.lastIndexOf("/");
        if (iEndPos > 0)
        {
            return strTempFilePath.substring(iEndPos + 1);
        } else
        {
            return null;
        }
    }

    /**
     * 移动附件
     *
     * @param fileSrc    原始附件
     * @param fileTarget 目标附件
     */
    public static void moveFile(String fileSrc, String fileTarget)
    {
        File file = null;
        File file2 = null;
        try
        {
            createFolder(fileTarget);
            file = new File(fileSrc);
            if (file.exists())
            {
                file2 = new File(fileTarget, file.getName());
                file.renameTo(file2);
            }
        } catch (Exception e)
        {
            System.out.println("移动附件\"" + fileSrc + "\"到\"" + fileTarget + "\"出现异常：" + e.getMessage());
        }
        file = null;
        file2 = null;
    }

    /**
     * 动态为名字前增加指定的字符(一般为/，根据第一个是否为/来判断是否需要插入)
     *
     * @param folder
     * @param folderChar
     * @return
     */
    public static String insertFolderCharFront(String folder, String folderChar)
    {
        String result = folder;
        if (StringUtils.isBlank(folder))
        {
            return folderChar + "";
        }
        if (StringUtils.isBlank(folderChar))
        {
            folderChar = "/";
        }
        if (folder.charAt(0) != folderChar.charAt(0))
        {
            result = folderChar + result;
        }
        return result;
    }

    /**
     * @param @param  file
     * @param @return 设定附件
     * @return byte[]    返回类型
     * @throws
     * @Title: readFileToByteArray
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public static byte[] readFileToByteArray(File file)
    {

        return null;
    }

}

package xpp.apk2jar;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {

    private static final int buffer = 2048;

    /**
     * 解压Zip文件
     *
     * @param path 文件目录
     */
    public static String unZip(String path) {
        int count = -1;
        String savepath = null;

        File file = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        savepath = path.substring(0, path.lastIndexOf(".")) + File.separator; //保存解压文件目录
        File dir = new File(savepath);
        if (dir.exists()) {
            BaseUtils.deleteDir(dir);
        }
        dir.mkdir(); //创建保存目录

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(path); //解决中文乱码问题
            Enumeration<?> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                byte buf[] = new byte[buffer];

                ZipEntry entry = (ZipEntry) entries.nextElement();

                String filename = entry.getName();
                boolean ismkdir = false;
                if (filename.lastIndexOf("/") != -1) { //检查此文件是否带有文件夹
                    ismkdir = true;
                }
                filename = savepath + filename;

                if (entry.isDirectory()) { //如果是文件夹先创建
                    file = new File(filename);
                    file.mkdirs();
                    continue;
                }
                file = new File(filename);
                if (!file.exists()) { //如果是目录先创建
                    if (ismkdir) {
                        new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); //目录先创建
                    }
                }
                file.createNewFile(); //创建文件

                is = zipFile.getInputStream(entry);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, buffer);

                while ((count = is.read(buf)) > -1) {
                    bos.write(buf, 0, count);
                }
                bos.flush();
                bos.close();
                fos.close();

                is.close();
            }

            zipFile.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return savepath;
    }


    public static String unZip2CurrentDir(String path) {
        int count = -1;
        String savepath = null;

        File file = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        savepath = new File(path).getParentFile().getPath();

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(path); //解决中文乱码问题
            Enumeration<?> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                byte buf[] = new byte[buffer];

                ZipEntry entry = (ZipEntry) entries.nextElement();

                String filename = entry.getName();
                boolean ismkdir = false;
                if (filename.lastIndexOf(File.separator) != -1) { //检查此文件是否带有文件夹
                    ismkdir = true;
                }
                filename = savepath +File.separator+ filename;

                if (entry.isDirectory()) { //如果是文件夹先创建
                    file = new File(filename);
                    file.mkdirs();
                    continue;
                }
                file = new File(filename);
                if (!file.exists()) { //如果是目录先创建
                    if (ismkdir) {
                        new File(filename.substring(0, filename.lastIndexOf(File.separator))).mkdirs(); //目录先创建
                    }
                }
                file.createNewFile(); //创建文件

                is = zipFile.getInputStream(entry);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, buffer);

                while ((count = is.read(buf)) > -1) {
                    bos.write(buf, 0, count);
                }
                bos.flush();
                bos.close();
                fos.close();

                is.close();
            }

            zipFile.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return savepath;
    }
}

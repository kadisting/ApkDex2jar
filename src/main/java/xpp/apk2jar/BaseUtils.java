package xpp.apk2jar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class BaseUtils {

    public static void moveDirAndDelete(String oldPath, String newPath) {
        try {
            // 如果文件夹不存在，则建立新文件夹
            (new File(newPath)).mkdirs();
            // 读取整个文件夹的内容到file字符串数组，下面设置一个游标i，不停地向下移开始读这个数组
            File filelist = new File(oldPath);
            String[] file = filelist.list();
            // 要注意，这个temp仅仅是一个临时文件指针
            // 整个程序并没有创建临时文件
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                // 如果oldPath以路径分隔符/或者\结尾，那么则oldPath/文件名就可以了
                // 否则要自己oldPath后面补个路径分隔符再加文件名
                // 谁知道你传递过来的参数是f:/a还是f:/a/啊？
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                // 如果游标遇到文件
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    // 复制并且改名
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/" + "rename_" + (temp.getName()).toString());
                    byte[] bufferarray = new byte[1024 * 64];
                    int prereadlength;
                    while ((prereadlength = input.read(bufferarray)) != -1) {
                        output.write(bufferarray, 0, prereadlength);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                // 如果游标遇到文件夹
                if (temp.isDirectory()) {
                    moveDirAndDelete(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
        }
    }

    public static void moveAndDelete(File oldFile, File newFile) {
        if (newFile.exists()) {
            newFile.delete();
        }
        BaseUtils.copyFile(oldFile, newFile);
        oldFile.delete();
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    public static void copyFile(File oldFile, File newFile) {
        try {
            int bytesum = 0;
            int byteread = 0;
            InputStream inStream = new FileInputStream(oldFile); //读入原文件
            FileOutputStream fs = new FileOutputStream(newFile);
            byte[] buffer = new byte[1444];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread; //字节数 文件大小
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();
            oldFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(InputStream inStream, File newFile) {
        try {
            int bytesum = 0;
            int byteread = 0;
            FileOutputStream fs = new FileOutputStream(newFile);
            byte[] buffer = new byte[1444];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread; //字节数 文件大小
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static File getLocalBinPath() {

        File file = new File(System.getProperty("user.home") + "/.ApkDex2Jar/bin/");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }


    public static void initBin(){

        getDex2JarShellPath();
        getApktoolShellPath();
        changeFolderPermission(BaseUtils.getLocalBinPath());
    }

    public static String getDex2JarShellPath() {

        String path;

        File dex2jarDir = new File(BaseUtils.getLocalBinPath(), "dex2jar");
        if (!dex2jarDir.exists()) {
            dex2jarDir.mkdirs();
        }
        File vDir = new File(dex2jarDir, "dex2jar-2.0");
        File shFile = new File(vDir, "d2j-dex2jar.sh");
        if (vDir.exists() && shFile.exists()) {
            path = shFile.getPath();
        } else {
            File zipFile = new File(dex2jarDir, "dex2jar-2.0.zip");
            InputStream resourceAsStream = Main.class.getResourceAsStream("/dex2jar-2.0.zip");
            BaseUtils.copyFile(resourceAsStream, zipFile);
            ZipUtils.unZip2CurrentDir(zipFile.getPath());
            File filed = new File(zipFile.getPath().replace(".zip", ""));
            File shpath = new File(filed, "d2j-dex2jar.sh");
            zipFile.delete();
            path = shpath.getPath();
        }

        return path;
    }

    public static String getApktoolShellPath() {

        String path;

        File apktoolDir = new File(BaseUtils.getLocalBinPath(), "apktool");
        if (!apktoolDir.exists()) {
            apktoolDir.mkdirs();
        }
        File vDir = new File(apktoolDir, "apktool-2.4.1");
        File shFile = new File(vDir, "apktool");
        if (vDir.exists() && shFile.exists()) {
            path = shFile.getPath();
        } else {
            File zipFile = new File(apktoolDir, "apktool-2.4.1.zip");
            InputStream resourceAsStream = Main.class.getResourceAsStream("/apktool-2.4.1.zip");
            BaseUtils.copyFile(resourceAsStream, zipFile);
            ZipUtils.unZip2CurrentDir(zipFile.getPath());
            File filed = new File(zipFile.getPath().replace(".zip", ""));
            File shpath = new File(filed, "apktool");
            zipFile.delete();
            path = shpath.getPath();
        }

        return path;
    }


    private static void changeFolderPermission(File dirFile) {
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        changeAllPermission(dirFile, perms);
    }

    private static void changeAllPermission(File file, Set<PosixFilePermission> perms) {

        if (file.isFile()) {
            changePermission(file, perms);
        } else {
            changePermission(file, perms);
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    changePermission(f, perms);
                } else {
                    changeAllPermission(f, perms);
                }
            }
        }
    }


    private static void changePermission(File file, Set<PosixFilePermission> perms) {

        try {
            Path path = Paths.get(file.getAbsolutePath());
            Files.setPosixFilePermissions(path, perms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

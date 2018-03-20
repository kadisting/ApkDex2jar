package xpp.apk2jar;

import java.io.File;
import java.io.IOException;

public class ApktoolUtils {


    public static void unPressApk(File apkFile, File dir) {

        // String path = CmdUtils.class.getResource("/res/apktool2.3.1/apktool").getPath();
        String path = "/Users/xupanpan/IdeaProjects/ApkDex2jar/src/res/apktool2.3.1/apktool";

        String command2 = path + " d -f -o resFile" + " " + apkFile.getPath();
        try {
            Runtime.getRuntime().exec(command2).waitFor();
            BaseUtils.moveDirAndDelete(new File("resFile").getPath(), dir.getPath());
            BaseUtils.deleteDir(new File("resFile"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

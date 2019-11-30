package xpp.apk2jar;

import java.io.File;
import java.io.IOException;

public class ApktoolUtils {


    public static boolean unPressApk(File apkFile, File dir) {

        String command2 = BaseUtils.getApktoolShellPath() + " d -f -o resFile" + " " + apkFile.getPath();
        try {
            Runtime.getRuntime().exec(command2).waitFor();
            BaseUtils.moveDirAndDelete(new File("resFile").getPath(), dir.getPath());
            BaseUtils.deleteDir(new File("resFile"));
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}

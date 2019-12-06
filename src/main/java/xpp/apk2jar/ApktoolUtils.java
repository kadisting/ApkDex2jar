package xpp.apk2jar;

import java.io.File;
import java.io.IOException;

public class ApktoolUtils {


    public static boolean unPressApk(File apkFile, File dir) {

        String command2 = BaseUtils.getApktoolShellPath() + " d -f -o " + dir.getPath() + "_ApkDex2jar " + apkFile.getPath();
        try {
            Runtime.getRuntime().exec(command2).waitFor();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}

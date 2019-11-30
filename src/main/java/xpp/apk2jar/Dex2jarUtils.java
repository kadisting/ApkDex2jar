package xpp.apk2jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class Dex2jarUtils {

    public static boolean dex2jar(File dexFile) {

        File newFile = new File(dexFile.getParentFile(), dexFile.getName() + ".jar");
        String command2 = BaseUtils.getDex2JarShellPath() + " -f -o " + newFile.getPath() + " " + dexFile.getPath();
        try {
            Runtime.getRuntime().exec(command2).waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}

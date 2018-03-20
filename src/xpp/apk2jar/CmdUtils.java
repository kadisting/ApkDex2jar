package xpp.apk2jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CmdUtils {

    public static void dex2jar(File dexFile) {

        String path = CmdUtils.class.getResource("/res/dex2jar/d2j-dex2jar.sh").getPath();

        String command2 = path + " -f -o temp " + dexFile.getPath();
        try {
            Runtime.getRuntime().exec(command2).waitFor();
            File oldFile = new File("temp");
            File newFile = new File(dexFile.getParentFile(), dexFile.getName()+".jar");
            BaseUtils.moveAndDelete(oldFile,newFile);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}

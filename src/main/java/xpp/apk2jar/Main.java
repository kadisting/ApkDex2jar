package xpp.apk2jar;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    private VBox mVBox;
    private static final double WIDTH = 600d;
    private static final double HIGTH = 500d;
    private Label label;
    private StringBuilder sb = new StringBuilder();
    private ExecutorService executorService;

    public static void main(String[] args) {
        launch(args);
    }

    private List<File> apkList;


    @Override
    public void start(Stage primaryStage) {
        mVBox = new VBox();
        mVBox.setPadding(new Insets(10, 10, 10, 10));
        mVBox.setSpacing(10);
        mVBox.setAlignment(Pos.TOP_LEFT);
        mVBox.setBackground(Background.EMPTY);
        Scene scene = new Scene(mVBox, WIDTH, HIGTH);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HIGTH);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        initView();
        BaseUtils.initBin();
    }


    private void initView() {
        label = new Label("请将apk文件拖动到此界面");
        label.setFont(Font.font(16));
        mVBox.getChildren().add(label);

        mVBox.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getGestureSource() != mVBox) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        });

        mVBox.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (isExecute) {
                    return;
                }
                Dragboard dragboard = event.getDragboard();
                List<File> files = dragboard.getFiles();
                if (files != null && files.size() > 0) {
                    index = 0;
                    apkList = files;
                    executorService = Executors.newSingleThreadExecutor();
                    parseList();
                }
            }
        });
    }

    private void showProgress() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label.setText(sb.toString());
            }
        });
    }


    private void addProgressStr(String str) {
        sb.append(str + "\n");
        showProgress();
    }


    private int index = 0;

    private void parseList() {

        if (index < apkList.size()) {
            execute(apkList.get(index));
        } else {
            addProgressStr("=======执行完成！=========");
            isExecute = false;
            index = 0;
            apkList.clear();
            executorService.shutdown();
        }

    }


    private boolean isExecute;

    private void execute(File apkFile) {
        sb.delete(0, sb.length());

        if (!apkFile.exists()) {
            addProgressStr("文件不存在！");
            stopExe();
            return;
        }
        if (apkFile.isDirectory() || !apkFile.getName().endsWith(".apk")) {
            addProgressStr(apkFile.getName() + "不是apk文件！");
            stopExe();
            return;
        }

        isExecute = true;
        executorService.execute(new Executer(apkFile));
    }


    private class Executer implements Runnable {

        private File apkFile;

        public Executer(File apkFile) {
            this.apkFile = apkFile;
        }

        @Override
        public void run() {
            addProgressStr("反编译：" + apkFile.getName());

            addProgressStr("1.开始解压缩");
            String unZipPath = ZipUtils.unZip(apkFile.getPath());
            if (unZipPath == null) {
                addProgressStr("  解压缩失败！");
                stopExe();
                return;
            }
            addProgressStr("  解压缩完成!");

            File dirFile = new File(unZipPath);
            if (!dirFile.exists()) {
                addProgressStr("  解压后文件夹不存在！");
                stopExe();
                return;
            }

            File[] files = dirFile.listFiles();
            if (files == null || files.length == 0) {
                addProgressStr("  解压后文件夹无文件！");
                stopExe();
                return;
            }

            List<File> dexFileList = new ArrayList<>();

            for (File file : files) {
                if (file.isDirectory()) {
                    BaseUtils.deleteDir(file);
                    continue;
                }
                if (file.getName().endsWith(".dex")) {
                    dexFileList.add(file);
                } else {
                    file.delete();
                }
            }
            if (dexFileList.size() == 0) {
                addProgressStr("  解压后文件夹无dex文件！");
                stopExe();
                return;
            } else {
                addProgressStr("  解压出" + dexFileList.size() + "个dex文件");
            }

            addProgressStr("2.开始反编译......");
            for (int i = 0; i < dexFileList.size(); i++) {
                addProgressStr("  正在反编译第" + (i + 1) + "个dex文件");
                Dex2jarUtils.dex2jar(dexFileList.get(i));
            }
            addProgressStr("  dex反编译完成!");

            addProgressStr("3.解压res文件......");

            boolean b = ApktoolUtils.unPressApk(apkFile, dirFile);
            String unPressStatus = b ? "成功" : "失败";
            addProgressStr("  res解压" + unPressStatus);
            if(b){
                BaseUtils.moveDirAndDelete(dirFile.getPath(),dirFile.getPath() + "_ApkDex2jar");
                BaseUtils.deleteDir(dirFile);
            }
            stopExe();
        }
    }

    private void stopExe() {
        index++;
        parseList();
    }

}

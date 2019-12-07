package xpp.apk2jar;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    private VBox centerBox;
    private static final double WIDTH = 600d;
    private static final double HIGTH = 500d;
    private Label label, statusLabel;
    private StringBuilder sb = new StringBuilder();
    private ExecutorService executorService;
    BorderPane borderPane;
    private Stage mStage;

    public static void main(String[] args) {
        launch(args);
    }

    private List<File> apkList;


    @Override
    public void start(Stage primaryStage) {
        mStage = primaryStage;
        borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, WIDTH, HIGTH);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HIGTH);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        initView();
        BaseUtils.initBin();
    }


    private void initView() {

        HBox topBox = new HBox(8);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setBackground(FxViewUtil.getBackground(Color.valueOf("8F8F8F")));
        topBox.setPadding(new Insets(10, 10, 10, 10));
        Button chooseApk = new Button("选择apk文件");
        chooseApk.setBackground(FxViewUtil.getBackground(Color.valueOf("DCDCDC")));
        chooseApk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("可以选择多个apk", "*.apk")
                );
                List<File> files = chooser.showOpenMultipleDialog(mStage);
                findApk(files);
            }
        });
        Button openDir = new Button("查看反编译文件");
        openDir.setBackground(FxViewUtil.getBackground(Color.valueOf("DCDCDC")));
        openDir.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (apkList != null && apkList.size() > 0) {
                    if (isExecute) {
                        statusLabel.setText("请等待反编译完成！");
                    } else {
                        File file = apkList.get(0);
                        if (file.exists()) {
                            File ff = new File(file.getParentFile(), file.getName().replace(".apk", "_ApkDex2jar"));
                            try {
                                statusLabel.setText("");
                                java.awt.Desktop.getDesktop().open(ff);
                            } catch (IOException e) {
                                e.printStackTrace();
                                statusLabel.setText("查看反编译文件，打开失败");
                            }
                        } else {
                            statusLabel.setText("apk文件不存在");
                        }
                    }

                } else {
                    statusLabel.setText("请先选择要反编译的apk文件！");
                }
            }
        });
        Button openGUI = new Button();
        openGUI.setBackground(Background.EMPTY);
        openGUI.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    java.awt.Desktop.getDesktop().open(new File(BaseUtils.getJdJuiPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView lastBgView = new ImageView(new Image(Main.class.getResourceAsStream("/icon.png")));
        lastBgView.setFitHeight(20);
        lastBgView.setFitWidth(20);
        openGUI.setGraphic(lastBgView);

        topBox.getChildren().addAll(chooseApk, openDir, openGUI);

        new Separator();

        borderPane.setTop(topBox);

        centerBox = new VBox();
        centerBox.setPadding(new Insets(10, 10, 10, 10));
        centerBox.setSpacing(10);
        centerBox.setAlignment(Pos.TOP_LEFT);
        centerBox.setBackground(Background.EMPTY);

        label = new Label("请将apk文件拖动到此区域");
        label.setFont(Font.font(16));
        centerBox.getChildren().addAll(label);

        centerBox.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getGestureSource() != centerBox) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        });

        centerBox.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (isExecute) {
                    return;
                }
                Dragboard dragboard = event.getDragboard();
                List<File> files = dragboard.getFiles();
                findApk(files);
            }
        });
        borderPane.setCenter(centerBox);

        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(5, 5, 5, 5));
        statusLabel = new Label();
        statusLabel.setFont(Font.font(12));
        statusLabel.setTextFill(Color.valueOf("D81B60"));
        bottomBox.getChildren().addAll(statusLabel);
        borderPane.setBottom(bottomBox);

    }

    private void findApk(List<File> files) {
        if (files != null && files.size() > 0) {
            if (apkList == null) {
                apkList = new ArrayList<>();
            } else {
                apkList.clear();
            }
            index = 0;
            apkList.addAll(files);
            executorService = Executors.newSingleThreadExecutor();
            parseList();
        }
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
                BaseUtils.dex2jar(dexFileList.get(i));
            }
            addProgressStr("  dex反编译完成!");

            addProgressStr("3.解压res文件......");

            boolean b = BaseUtils.unPressApk(apkFile, dirFile);
            String unPressStatus = b ? "成功" : "失败";
            addProgressStr("  res解压" + unPressStatus);
            if (b) {
                BaseUtils.moveDirAndDelete(dirFile.getPath(), dirFile.getPath() + "_ApkDex2jar");
                BaseUtils.deleteDir(dirFile);
            }else {

            }
            stopExe();
        }
    }

    private void stopExe() {
        index++;
        parseList();
    }

}

package xpp.apk2jar;

import java.awt.Desktop;
import java.net.URI;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Created by xupanpan on 3/17/17.
 */
public class FxViewUtil {


    public static Background getDefBackground() {

        return getBackground(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY);
    }

    public static Background getBackground(Paint color) {

        return getBackground(color, CornerRadii.EMPTY, Insets.EMPTY);
    }

    public static Background getBackground(Paint color, int corner) {

        return getBackground(color, new CornerRadii(corner), Insets.EMPTY);
    }

    public static Background getBackground(Paint color, CornerRadii corner) {

        return getBackground(color, corner, Insets.EMPTY);
    }


    public static Background getBackground(Paint color, CornerRadii corner, Insets insets) {
        return new Background(new BackgroundFill(color, corner, insets));
    }


    public static Border getDefBorder() {

        return getBorder(Color.WHITE, 5, 1);
    }

    public static Border getBorder(Paint color) {

        return getBorder(color, 5, 1);
    }

    public static Border getBorder(Paint color, double corner, double borderWidths) {

        return getBorder(color, BorderStrokeStyle.SOLID, new CornerRadii(corner), new BorderWidths(borderWidths));
    }


    public static Border getBorder(Paint color, BorderStrokeStyle style, CornerRadii corner, BorderWidths borderWidths) {
        return new Border(new BorderStroke(color, style, corner, borderWidths));
    }


    public static void setDefTextFileStyle(Region textField) {
        textField.setBackground(FxViewUtil.getDefBackground());
        textField.setBorder(FxViewUtil.getBorder(Color.WHITE, 3, 1));
    }

    public static void setDefButtonStyle(Button btn) {
        setDefTextFileStyle(btn);
        btn.setTextFill(Color.WHITE);
    }

    public static void setButtonBlueStyle(Button btn) {
        btn.setBackground(FxViewUtil.getDefBackground());
        btn.setBorder(FxViewUtil.getBorder(Color.LIGHTSKYBLUE, 3, 1));
        btn.setTextFill(Color.LIGHTSKYBLUE);
    }


    public static void openSystemBrowser(String url) {

        if (Desktop.isDesktopSupported()) {
            try {
                URI uri = URI.create(url);
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

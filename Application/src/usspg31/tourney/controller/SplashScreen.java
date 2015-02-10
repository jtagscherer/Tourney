package usspg31.tourney.controller;

import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreen extends Preloader {
    private static Stage stage;
    private static ImageView iconView;

    private Scene createPreloaderScene() {
        VBox preloaderPane = new VBox();
        preloaderPane.setAlignment(Pos.CENTER);
        preloaderPane.setSpacing(50.0f);

        SplashScreen.iconView = new ImageView(new Image(this.getClass()
                .getResourceAsStream("/ui/icon/icon-256.png")));
        preloaderPane.getChildren().add(SplashScreen.iconView);

        ProgressIndicator indicator = new ProgressIndicator(-1.0f);
        indicator.setStyle(" -fx-progress-color: white;");
        preloaderPane.getChildren().add(indicator);

        Scene preloaderScene = new Scene(preloaderPane, 600, 400);
        preloaderPane.setBackground(new Background(new BackgroundFill(Color
                .rgb(17, 119, 255), CornerRadii.EMPTY, Insets.EMPTY)));
        preloaderScene.setFill(Color.rgb(17, 119, 255));

        return preloaderScene;
    }

    @Override
    public void start(Stage stage) throws Exception {
        SplashScreen.stage = new Stage();
        SplashScreen.stage.setTitle("Loading Tourney");
        Scene preloaderScene = createPreloaderScene();
        SplashScreen.stage.setScene(preloaderScene);
        SplashScreen.stage.initStyle(StageStyle.UNDECORATED);
        SplashScreen.stage.show();
    }

    public static void hide() {
        if (SplashScreen.stage != null) {
            SplashScreen.stage.close();
        }
    }
}

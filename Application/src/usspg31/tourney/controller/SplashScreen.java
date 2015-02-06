package usspg31.tourney.controller;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreen extends Preloader {
    private static Stage stage;
    private static ImageView iconView;

    private Scene createPreloaderScene() {
        AnchorPane preloaderPane = new AnchorPane();
        SplashScreen.iconView = new ImageView(new Image(this.getClass()
                .getResourceAsStream("/ui/icon/icon-256.png")));
        preloaderPane.getChildren().add(SplashScreen.iconView);
        preloaderPane.setStyle("-fx-background-color: transparent;");
        Scene preloaderScene = new Scene(preloaderPane, 256, 256);
        preloaderScene.setFill(Color.TRANSPARENT);

        return preloaderScene;
    }

    @Override
    public void start(Stage stage) throws Exception {
        SplashScreen.stage = new Stage();
        SplashScreen.stage.setTitle("Loading Tourney");
        Scene preloaderScene = createPreloaderScene();
        SplashScreen.stage.setScene(preloaderScene);
        SplashScreen.stage.initStyle(StageStyle.TRANSPARENT);
        SplashScreen.stage.show();
    }

    public static void hide() {
        if (SplashScreen.stage != null) {
            SplashScreen.stage.close();
        }
    }
}

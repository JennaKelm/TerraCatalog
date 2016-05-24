import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;




public class Main extends Application {

    public static CheckboxenFuerStatistikAuswahl bo;
    private static EventHandling ma;
    public static StatistikInhalteZaehlen au;
    private Scene sc ;

    public static void main(String[] args) {
        ma = new EventHandling();
        bo = new CheckboxenFuerStatistikAuswahl();
        au = new StatistikInhalteZaehlen();
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = new UserInterface();

        primaryStage.setTitle("XML Auslesen");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("logo_bottrop.jpg")));
        sc = new Scene(root,850,500);
        primaryStage.setScene(sc);
        sc.getStylesheets().add("Layout.css");
        primaryStage.setResizable(true);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}

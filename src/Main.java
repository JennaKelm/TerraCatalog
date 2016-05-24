import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {
	private static final CheckboxenFuerStatistikAuswahl bo = new CheckboxenFuerStatistikAuswahl();
	private static final StatistikInhalteZaehlen au = new StatistikInhalteZaehlen();

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = new UserInterface();

		primaryStage.setTitle("XML Auslesen");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("logo_bottrop.jpg")));
		Scene sc = new Scene(root, 850, 500);
		primaryStage.setScene(sc);
		sc.getStylesheets().add("Layout.css");
		primaryStage.setResizable(true);
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	public static StatistikInhalteZaehlen getAu() {
		return au;
	}

	public static CheckboxenFuerStatistikAuswahl getBo() {
		return bo;
	}
}

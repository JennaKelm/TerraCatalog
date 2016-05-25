import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * testmp
 * Created by Dennis Rassmann (showp1984@gmail.com) on 25.05.16.
 */
public class ProgessDialog extends Stage {
	private String headerString = "headerText";
	private String contentString = "contentText";
	private Label contentLabel = new Label();

	private BorderPane root = new BorderPane();
	private Scene scene = new Scene(root, 500, 150);
	private ProgressIndicator pro = new ProgressIndicator();
	private boolean closeable = false;

	public ProgessDialog(String header, String content) {
		headerString = header;
		contentString = content;

		init();
	}

	public ProgessDialog() {
		headerString = "Dauer";
		contentString = "Einen Moment bitte...";

		init();
	}

	private void init() {
		setResizable(false);
		setTitle(headerString);

		root.setPadding(new Insets(20d, 20d, 20d, 20d));
		pro.setPadding(new Insets(0, 20d, 0, 0));

		VBox vbox = new VBox();
		vbox.getChildren().add(pro);
		vbox.setAlignment(Pos.CENTER);
		root.setLeft(vbox);

		contentLabel.setText(contentString);
		contentLabel.setWrapText(true);
		contentLabel.setTextAlignment(TextAlignment.JUSTIFY);
		root.setCenter(contentLabel);

		setScene(scene);
		show();

		scene.getWindow().setOnCloseRequest(ev -> {
			if (!closeable)
				ev.consume();
		});
	}

	@Override
	public void close() {
		if (closeable)
			super.close();
	}

	public void closeMe() {
		closeable = true;
		close();
	}

	public void setProgress(double progress) {
		System.out.println(progress);
		pro.setProgress(progress);
	}
}

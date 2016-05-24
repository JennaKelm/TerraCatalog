import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class UserInterface extends javafx.scene.layout.BorderPane {

	private static UserInterface instance;
	private TextField filename = new TextField();
	private final Button outputFileButton = new Button("Datei Auswahl...");
	private final TextField outputFileTextField = new TextField();
	private final Button statistikButton = new Button("Datei Auswahl Statistik...");
	private final TextField statistikTextFilde = new TextField();
	private final Button okAuslesen = new Button("Auslesen");
	private final Button okSchreiben = new Button("Ausgabe generieren");
	private final GridPane gi = new GridPane();
	private Button openXmlFolderButton = new Button("XML Ordner Auswähl...");
	private final Label xmlDateiPfard = new Label("XML Ordner ");
	private final Label outputFileLabel = new Label("Ausgabe Datei");
	private final Label statistikLabel = new Label("Statistik Datei");
	private final Button zurueck = new Button("Zurücksetzen");
	private final GridPane start = new GridPane();
	private final GridPane filewahl = new GridPane();
	private final Button stat = new Button("Schreiben");
	private final TitledPane tipa = new TitledPane("Statistic", Main.getBo().getBox());
	private final Image img = new Image(getClass().getResourceAsStream("logo_bottrop.jpg"));
	private final ImageView iv = new ImageView(img);
	private ArrayList<CheckBox> isCheck = new ArrayList<>();
	private ScrollPane sp = new ScrollPane();

	public ArrayList<CheckBox> getIsCheck() {
		return isCheck;
	}
	public Button getOkAuslesen() {
		return okAuslesen;
	}

	public TextField getFilename() {
		return filename;
	}

	public Button getOkSchreiben() {
		return okSchreiben;
	}

	public Button getOutputFileButton() {
		return outputFileButton;
	}

	public TextField getOutputFileTextField() {
		return outputFileTextField;
	}

	public TextField getStatistikTextFilde() {
		return statistikTextFilde;
	}

	public Button getStatistikButton() {
		return statistikButton;
	}

	// erstellen der Grafischen Elemente und zuweisen der Button Elemente zuden passenden Methoden in Control
	public UserInterface() {
		instance = this;

		outputFileTextField.setEditable(false);
		iv.setId("iv");


		filename.setEditable(false);
		statistikTextFilde.setEditable(false);
		okAuslesen.setDisable(true);
		okSchreiben.setDisable(true);
		start.setPadding(new Insets(20, 20, 20, 20));
		start.setHgap(10);
		start.setVgap(10);
		filewahl.setPadding(new Insets(20, 20, 20, 20));
		filewahl.setHgap(10);
		filewahl.setVgap(10);

		EventHandling.FolderAuswahl(openXmlFolderButton, filename);
		EventHandling.FileAuswahl(outputFileButton, outputFileTextField);
		EventHandling.FileAuswahl(statistikButton, statistikTextFilde);
		EventHandling.Output(okSchreiben, outputFileTextField, statistikTextFilde);
		EventHandling.XmlDateieneinlesen(okAuslesen, filename);
		EventHandling.Back(zurueck);

		start.add(xmlDateiPfard, 0, 0);
		start.add(filename, 0, 1);
		start.add(openXmlFolderButton, 1, 1);
		start.add(okAuslesen, 0, 2);
		filewahl.add(outputFileLabel, 0, 0);
		filewahl.add(outputFileTextField, 0, 1);
		filewahl.add(outputFileButton, 1, 1);
		filewahl.add(statistikLabel, 0, 2);
		filewahl.add(statistikTextFilde, 0, 3);
		filewahl.add(statistikButton, 1, 3);
		filewahl.add(okSchreiben, 0, 4);
		filewahl.add(zurueck, 1, 4);
		filewahl.setVisible(false);

		gi.add(start, 0, 0);
		gi.add(filewahl, 0, 0);


		this.setId("root");

		setCenter(gi);
		setLeft(iv);
		setOnKeyPressed(event ->
			{
				if (event.getCode() == KeyCode.ENTER) {
					if (EventHandling.istan()) {
						okAuslesen.fire();
					} else {
						okSchreiben.fire();
					}
				}
			}
		);
	}

	public static UserInterface getInstance() {
		return instance;
	}

	// schaltet jenach Programmteil Bedienelemente dazu oder auch wieder weg
	public void Sichtbar(Boolean istan) {
		start.setVisible(istan);
		filewahl.setVisible(!istan);
		if (!istan) {
			setRight(sp);
			sp.setContent(tipa);
			sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
			sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			tipa.setVisible(true);

		} else {
			tipa.setVisible(false);
		}
	}
}





import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class UserInterface extends javafx.scene.layout.BorderPane {

    private final GridPane gi;
    private final Button openXmlFolderButton;
    final TextField filename;
    private final Label xmlDateiPfard;
    final Button outputFileButton;
    final TextField outputFileTextField;
    private final Label outputFileLabel;
    final Button statistikButton;
    final TextField statistikTextFilde;
    private final Label statistikLabel;
    final Button okAuslesen;
    final Button okSchreiben;
    private final Button zurueck;
    private final GridPane start = new GridPane();
    private final GridPane filewahl = new GridPane();
    private static UserInterface instance;
    ArrayList<CheckBox> isCheck;
    private final Button stat;
    private final TitledPane tipa;
    private final Image img;
    private final ImageView iv;
    ScrollPane sp;


    // erstellen der Grafischen Elemente und zuweisen der Button Elemente zuden passenden Methoden in Control
    public UserInterface() {
        instance = this;

        gi = new GridPane();
        openXmlFolderButton = new Button("XML Ordner Auswähl...");
        filename = new TextField();
        xmlDateiPfard = new Label("XML Ordner ");
        outputFileButton = new Button("Datei Auswahl...");
        outputFileTextField = new TextField();
        outputFileTextField.setEditable(false);
        outputFileLabel = new Label("Ausgabe Datei");
        statistikButton = new Button("Datei Auswahl Statistik...");
        statistikTextFilde = new TextField();
        statistikLabel = new Label("Statistik Datei");
        okAuslesen = new Button("Auslesen");
        zurueck = new Button("Zurücksetzen");
        okSchreiben = new Button("Ausgabe generieren");
        stat = new Button("Schreiben");
        tipa = new TitledPane("Statistic", Main.bo.box);
        img = new Image(getClass().getResourceAsStream("logo_bottrop.jpg"));
        iv = new ImageView(img);
        iv.setId("iv");
        sp = new ScrollPane();

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

        EventHandling c = EventHandling.getInstanceOf();
        c.FolderAuswahl(openXmlFolderButton, filename);
        c.FileAuswahl(outputFileButton, outputFileTextField);
        c.FileAuswahl(statistikButton, statistikTextFilde);
        c.Output(okSchreiben, outputFileTextField, statistikTextFilde);
        c.XmlDateieneinlesen(okAuslesen, filename);
        c.Back(zurueck);

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
        setOnKeyPressed(event
                        ->
                {
                    if (event.getCode() == KeyCode.ENTER) {
                        if (EventHandling.getInstanceOf().istan) {
                            okAuslesen.fire();
                        } else {
                            okSchreiben.fire();
                        }
                    }
                }
        );
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

    public static UserInterface getInstance() {
        return instance;
    }
}


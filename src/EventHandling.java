import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


public class EventHandling {
    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser Chooser = new DirectoryChooser();
    private String out;
    private String statistik;
    private String folder;
    private int xmlDateienZaehlen;
    private int outZaehlen;
    private  final ArrayList<AuslesenXMLInformationen> mylist = new ArrayList<>();
    Boolean istan = true;
    private static EventHandling instanceOf;
    private Fortschritsanzeige myProgress;
    private OpenOutputDatei opOuDa;
    private Boolean o = false;
    private Boolean s = false;
    private File fileHilf;
    private File fileHilf1;
    private int falscheXml;
    private Boolean hilfXml;
    private final ArrayList<String> fehlerhafteXMLPfade = new ArrayList<>();

    public EventHandling() {
        instanceOf = this;
        FileSystemView view = FileSystemView.getFileSystemView();
        fileHilf = view.getDefaultDirectory();
        fileHilf1 = view.getDefaultDirectory();
    }
// setzt alles auf Programm start wenn der zurück Button gedrück wird
    public void Back(Button back) {
        back.setOnAction(
                e -> {
                    mylist.clear();
                    fehlerhafteXMLPfade.clear();
                    StatistikInhalteZaehlen.getInstenceOF().auZa.clear();
                    StatistikInhalteZaehlen.getInstenceOF().name.clear();
                    UserInterface.getInstance().filename.clear();
                    xmlDateienZaehlen = 0 ;
                    falscheXml = 0;
                    istan = !istan;
                    UserInterface.getInstance().Sichtbar(istan);
                    UserInterface.getInstance().okAuslesen.setDisable(true);
                });
    }
// auswahl der ordner in denen sich die xml dateien befinden

    public void FolderAuswahl(Button xmlFolder, TextField filename) {
        xmlFolder.setOnAction(
                e -> {
                    Chooser.setInitialDirectory(fileHilf1);
                    File file = Chooser.showDialog(new Stage());
                    if (file != null) {
                        fileHilf1 = file.getParentFile();
                        filename.setText(file.toString());
                        UserInterface.getInstance().okAuslesen.setDisable(false);
                    }
                });
    }

//    Auswahl der CSV Datei pfade überprüfung ob die neu generirt werden müssen oder überschrieben
    public void FileAuswahl(Button btn,   TextField filename) {
        btn.setOnAction(
                e -> {
                    fileChooser.setInitialDirectory(fileHilf);
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
                    File file = fileChooser.showSaveDialog(new Stage());
                    if (file != null && file.getName().toLowerCase().endsWith(".csv")) {
                        filename.setText(file.toString());
                        fileHilf = file.getParentFile();
                        if (btn.equals(UserInterface.getInstance().outputFileButton)) {
                                o = true;
                        } else if (btn.equals(UserInterface.getInstance().statistikButton)) {
                                s = true;
                        }
                    }
                    if(o || s){
                        UserInterface.getInstance().okSchreiben.setDisable(false);
                    }
                });
    }

    //    einlesen aller xml dateien die sich im ordner befinden
    public void XmlDateieneinlesen(Button btn, TextField text1) {
        btn.setOnAction(
                e -> new Thread(() -> {
                    try {
                        Platform.runLater(this::AlProgress);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        if (!text1.getText().isEmpty()) {
                            folder = text1.getText();
                            xmlDateienZaehlen = 0;
                            File dir = new File(folder);
                            File[] directoryListing = dir.listFiles((dir1, name) -> {
                                return name.toLowerCase().endsWith(".xml");
                            });
                            System.out.println("Start");
                            if (directoryListing.length != 0) {
                                for (File child : directoryListing) {
                                    String filename = child.getPath();
                                    CheckXML(filename);
                                    if (hilfXml) {
                                        AuslesenXMLInformationen entry = new AuslesenXMLInformationen(filename);
                                        if(entry.nicht) {
                                            mylist.add(entry);
                                            xmlDateienZaehlen++;
                                            Platform.runLater(() -> myProgress.progressSetzen(xmlDateienZaehlen, directoryListing.length));
                                        }
                                    }
                                }
                                System.out.println(falscheXml);
                                if (falscheXml != 0) {
                                    Platform.runLater(() -> {
                                        Alert ale = new Alert(Alert.AlertType.CONFIRMATION);
                                        ale.setContentText("Es sind " + falscheXml + "Falsche XML-Dateien aufgetaucht trotzdem weiter");
                                        ale.showAndWait();
                                        if (ale.getResult() == ButtonType.CANCEL) {
                                        } else if (ale.getResult() == ButtonType.OK) {

                                            UserInterface.getInstance().isCheck = new ArrayList<>();
                                            istan = !istan;
                                            Platform.runLater(() -> {
                                                Main.bo.BoxErstellen();
                                                UserInterface.getInstance().Sichtbar(istan);
                                            });
                                        }
                                    });
                                } else {
                                    UserInterface.getInstance().isCheck = new ArrayList<>();
                                    istan = !istan;
                                    Platform.runLater(() -> {
                                        Main.bo.BoxErstellen();
                                        UserInterface.getInstance().Sichtbar(istan);
                                    });
                                }
                            } else {
                                Platform.runLater(() -> {
                                    myProgress.close();
                                    Warnung("Es wurden keine XML_Dateien gefunden");
                                });
                                UserInterface.getInstance().okAuslesen.setDisable(true);
                            }
                        } else {
                            Platform.runLater(() -> {
                                myProgress.close();
                                Warnung("Eingabe Feld is Leer ");
                            });
                        }
                        Platform.runLater(myProgress::close);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }).start());
    }
//    generirt den output der statistik nach den in ischeck ausgewählten checkboxen elementen
    public void Output(Button btn,TextField out, TextField statistik) {
        btn.setOnAction(
                e -> {
                    System.out.println(o + " " + s);
                    new Thread(() -> {
                        try {
                            Platform.runLater(this::AlProgress);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            UserInterface.getInstance().isCheck = new ArrayList<>();
                            for (int i = 0; i < Main.bo.arrBox.size(); i++) {
                                if (Main.bo.arrBox.get(i).isSelected()) {
                                    UserInterface.getInstance().isCheck.add(Main.bo.arrBox.get(i));
                                }
                            }
                            System.out.println("jetzt");
                            if ((out.getText() != null) || (statistik.getText() != null)) {
                                if (o) {
                                        FunkOutput(out);
                                    }
                                else {
                                        Warnung("Die Datei von Output ist keine csv datei");
                                    }
                                }
                                if (s) {
                                        FunkStatistik(statistik);
                            } else {
                                Warnung("keien Datei mit csv endung gefunden ");
                            }
                            Platform.runLater(() -> {
                                myProgress.close();
                                opOuDa = new OpenOutputDatei(out.getText(), statistik.getText());
                                UserInterface.getInstance().outputFileTextField.clear();
                                UserInterface.getInstance().statistikTextFilde.clear();
                                UserInterface.getInstance().okSchreiben.setDisable(true);
                            });
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        System.out.println("ganz");
                    }).start();
                });
    }

//    Schreiben des CSV output in den angegeben Datei-Pfard
    private void FunkOutput(TextField filename) {
        if (filename != null) {
            out = filename.getText();
            try {
                FileWriter writer ;
                writer = new FileWriter(out);
                String[] hilf = new String[mylist.get(0).dictionary.keySet().toArray().length];
                mylist.get(0).dictionary.keySet().toArray(hilf);
                for (String aHilf : hilf) {
                    writer.append(aHilf).append(";");
                }
                writer.append("\n");
                for (AuslesenXMLInformationen aMylist : mylist) {
                    for (String name : aMylist.dictionary.keySet()) {
                        writer.append(aMylist.dictionary.get(name)).append(";");
                    }
                    outZaehlen++;
                    Platform.runLater(() -> myProgress.progressSetzen(outZaehlen, (xmlDateienZaehlen + UserInterface.getInstance().isCheck.size())));
                    writer.append("\n");
                }
                System.out.println(mylist.size() +" " +outZaehlen);
                writer.flush();
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            Warnung("Ein oder mehrere Pfade sind nicht Korrekt");
        }
    }

//    Schreiben der Statistik in den angegebenen Datie-Pfard
    private void FunkStatistik(TextField filename) {
        if (filename != null) {
            statistik = filename.getText();
            try {
                FileWriter writer = new FileWriter(statistik);
                writer.append("XML Dateien").append(Integer.toString(xmlDateienZaehlen)).append("\n");
                writer.append("\n");
                if(!fehlerhafteXMLPfade.isEmpty()){
                    writer.append("Nicht lesbare XML-Dateien").append("\n");
                    for (String aFehlerhafteXMLPfade : fehlerhafteXMLPfade) {
                        writer.append(aFehlerhafteXMLPfade).append("\n");
                    }
                    writer.append("\n");
                }
                if (!UserInterface.getInstance().isCheck.isEmpty()) {

                    for (int i = 0; i < UserInterface.getInstance().isCheck.size(); i++) {
                        if (StatistikInhalteZaehlen.getInstenceOF().name.contains(UserInterface.getInstance().isCheck.get(i).getText())) {
                            MapSchreiben(StatistikInhalteZaehlen.getInstenceOF().auZa.get(StatistikInhalteZaehlen.getInstenceOF().name.indexOf(UserInterface.getInstance().isCheck.get(i).getText())), writer, UserInterface.getInstance().isCheck.get(i).getText());
                            Platform.runLater(() -> myProgress.progressSetzen(outZaehlen, (xmlDateienZaehlen + UserInterface.getInstance().isCheck.size())));
                            outZaehlen++;
                        }
                    }
                }
                writer.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void MapSchreiben(HashMap<String, Integer> map, FileWriter write, String titel) {
        try {
            write.append("\n").append(titel).append("\n");
            for (Entry<String, Integer> pair : map.entrySet()) {
                write.append(pair.getKey()).append(" : ").append(";").append(pair.getValue().toString()).append("\n");
                write.flush();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void Warnung(String titel) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Information Dialog");
        alert.setContentText(titel);
        alert.showAndWait();
    }

    private void AlProgress() {
        myProgress = new Fortschritsanzeige();
    }

    public static EventHandling getInstanceOf() {
        return instanceOf;
    }

//    checkt ob die angegebende datei ein xml ist und gibs sie ensprechend zru bearbeitung frei oder auch nicht
    private void CheckXML(String filname) {
        Document docu = Op(filname);
        try {
            NodeList Nvalue = docu.getElementsByTagName("gmd:fileIdentifier");
            if (Nvalue.getLength() >= 1) {
                hilfXml = true;
            } else {
                hilfXml = false;
                falscheXml++;
            }
        }catch (Exception e){
            hilfXml = false;
            falscheXml++;
            fehlerhafteXMLPfade.add(filname);
        }
    }
//    einleden der xml dateien aus dem angegebenden ordner
    private Document Op(String filename) {
        try {
            InputStream inputStream = new FileInputStream(filename);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(is);
        } catch (Exception e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            return null;
        }
    }
}








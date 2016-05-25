import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
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
	private static final FileChooser fileChooser = new FileChooser();
	private static final DirectoryChooser Chooser = new DirectoryChooser();
	private static final ArrayList<AuslesenXMLInformationen> mylist = new ArrayList<>();
	private static final ArrayList<String> fehlerhafteXMLPfade = new ArrayList<>();
	private static String out;
	private static String statistik;
	private static String folder;
	private static int xmlDateienZaehlen;
	private static int outZaehlen;
	private static boolean istan = true;
	private static ProgessDialog myProgress;
	private static OpenOutputDatei opOuDa;
	private static boolean o = false;
	private static boolean s = false;
	private static File fileHilf;
	private static File fileHilf1;
	private static int falscheXml;
	private static boolean hilfXml;
	public EventHandling() {
		FileSystemView view = FileSystemView.getFileSystemView();
		fileHilf = view.getDefaultDirectory();
		fileHilf1 = view.getDefaultDirectory();
	}

	public static boolean istan() {
		return istan;
	}
// auswahl der ordner in denen sich die xml dateien befinden

	// setzt alles auf Programm start wenn der zurück Button gedrück wird
	public static void Back(Button back) {
		back.setOnAction(
			e -> {
				mylist.clear();
				fehlerhafteXMLPfade.clear();
				StatistikInhalteZaehlen.getInstenceOF().getAuZa().clear();
				StatistikInhalteZaehlen.getInstenceOF().getName().clear();
				UserInterface.getInstance().getFilename().clear();
				xmlDateienZaehlen = 0;
				falscheXml = 0;
				istan = !istan;
				UserInterface.getInstance().Sichtbar(istan);
				UserInterface.getInstance().getOkAuslesen().setDisable(true);
			});
	}

	public static void FolderAuswahl(Button xmlFolder, TextField filename) {
		xmlFolder.setOnAction(
			e -> {
				Chooser.setInitialDirectory(fileHilf1);
				File file = Chooser.showDialog(new Stage());
				if (file != null) {
					fileHilf1 = file.getParentFile();
					filename.setText(file.toString());
					UserInterface.getInstance().getOkAuslesen().setDisable(false);
				}
			});
	}

	//    Auswahl der CSV Datei pfade überprüfung ob die neu generirt werden müssen oder überschrieben
	public static void FileAuswahl(Button btn, TextField filename) {
		btn.setOnAction(
			e -> {
				fileChooser.setInitialDirectory(fileHilf);
				fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
				File file = fileChooser.showSaveDialog(new Stage());
				if (file != null && file.getName().toLowerCase().endsWith(".csv")) {
					filename.setText(file.toString());
					fileHilf = file.getParentFile();
					if (btn.equals(UserInterface.getInstance().getOutputFileButton())) {
						o = true;
					} else if (btn.equals(UserInterface.getInstance().getStatistikButton())) {
						s = true;
					}
				}
				if (o || s) {
					UserInterface.getInstance().getOkSchreiben().setDisable(false);
				}
			});
	}

	//    einlesen aller xml dateien die sich im ordner befinden
	public static void XmlDateieneinlesen(Button btn, TextField text1) {
		btn.setOnAction(
			e -> new Thread(() -> {
				try {
					Platform.runLater(EventHandling::AlProgress);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
//					if (!text1.getText().isEmpty()) {
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
//									if (entry.isNicht()) {
										mylist.add(entry);
										xmlDateienZaehlen++;
									double progress = (double) xmlDateienZaehlen / directoryListing.length;
									Platform.runLater(() -> myProgress.setProgress(progress));
//									}
								}

							}
							Platform.runLater(myProgress::closeMe);
							System.out.println(falscheXml);
							if (falscheXml != 0) {
								Platform.runLater(() -> {
									Alert ale = new Alert(Alert.AlertType.CONFIRMATION);
									ale.setContentText("Es sind " + falscheXml + " falsche XML-Dateien aufgetaucht.\nTrotzdem weiter?");
									ale.showAndWait();
									if (ale.getResult() == ButtonType.OK) {
										UserInterface.getInstance().getIsCheck();
										istan = !istan;
										Platform.runLater(() -> {
											Main.getBo().BoxErstellen();
											UserInterface.getInstance().Sichtbar(istan);
											myProgress.closeMe();
										});
									}
									myProgress.closeMe();
								});
							} else {
								UserInterface.getInstance().getIsCheck();
								istan = !istan;
								Platform.runLater(() -> {
									Main.getBo().BoxErstellen();
									UserInterface.getInstance().Sichtbar(istan);

									myProgress.closeMe();
								});
							}
						} else {
							Platform.runLater(() -> {
								myProgress.closeMe();
								Warnung("Es wurden keine XML_Dateien gefunden");
							});
							UserInterface.getInstance().getOkAuslesen().setDisable(true);
						}
//					} else {
//						Platform.runLater(() -> {
//					)		myProgress.closeMe();
//							Warnung("Eingabe Feld is Leer ");
//						});
//					}
					Platform.runLater(myProgress::closeMe);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}).start());
	}

	//    generirt den output der statistik nach den in ischeck ausgewählten checkboxen elementen
	public static void Output(Button btn, TextField out, TextField statistik) {
		btn.setOnAction(
			e -> {
				System.out.println(o + " " + s);
				new Thread(() -> {
					try {
						Platform.runLater(EventHandling::AlProgress);
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						UserInterface.getInstance().getIsCheck();
						for (int i = 0; i < Main.getBo().getArrBox().size(); i++) {
							if (Main.getBo().getArrBox().get(i).isSelected()) {
								UserInterface.getInstance().getIsCheck().add(Main.getBo().getArrBox().get(i));
							}
						}
						System.out.println("jetzt");
						if ((out.getText() != null) || (statistik.getText() != null)) {
							if (o) {
								FunkOutput(out);
							}
//							else {
//								Platform.runLater(() -> Warnung("Die Datei von Output ist keine csv datei"));
//							}
						}
						if (s) {
							FunkStatistik(statistik);
						}
//						else {
//							Warnung("keien Datei mit csv endung gefunden ");
//						}
						Platform.runLater(() -> {
							myProgress.closeMe();
							opOuDa = new OpenOutputDatei(out.getText(), statistik.getText());
							UserInterface.getInstance().getOutputFileTextField().clear();
							UserInterface.getInstance().getStatistikTextFilde().clear();
							UserInterface.getInstance().getOkSchreiben().setDisable(true);
						});
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					System.out.println("ganz");
				}).start();
			});
	}

	//    Schreiben des CSV output in den angegeben Datei-Pfard
	private static void FunkOutput(TextField filename) {
		if (filename != null) {
			out = filename.getText();
			try {
				FileWriter writer;
				writer = new FileWriter(out);
				String[] hilf = new String[mylist.get(0).getDictionary().keySet().toArray().length];
				mylist.get(0).getDictionary().keySet().toArray(hilf);
				for (String aHilf : hilf) {
					writer.append(aHilf).append(";");
				}
				writer.append("\n");
				for (AuslesenXMLInformationen aMylist : mylist) {
					for (String name : aMylist.getDictionary().keySet()) {
						writer.append(aMylist.getDictionary().get(name)).append(";");
					}
					outZaehlen++;
					double progress = (double) outZaehlen / (xmlDateienZaehlen + UserInterface.getInstance().getIsCheck().size());
					Platform.runLater(() -> myProgress.setProgress(progress));
					writer.append("\n");
				}
				System.out.println(mylist.size() + " " + outZaehlen);
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
	private static void FunkStatistik(TextField filename) {
		if (filename != null) {
			statistik = filename.getText();
			try {
				FileWriter writer = new FileWriter(statistik);
				writer.append("XML Dateien").append(Integer.toString(xmlDateienZaehlen)).append("\n");
				writer.append("\n");
				if (!fehlerhafteXMLPfade.isEmpty()) {
					writer.append("Nicht lesbare XML-Dateien").append("\n");
					for (String aFehlerhafteXMLPfade : fehlerhafteXMLPfade) {
						writer.append(aFehlerhafteXMLPfade).append("\n");
					}
					writer.append("\n");
				}
				if (!UserInterface.getInstance().getIsCheck().isEmpty()) {

					for (int i = 0; i < UserInterface.getInstance().getIsCheck().size(); i++) {
						if (StatistikInhalteZaehlen.getInstenceOF().getName().contains(UserInterface.getInstance().getIsCheck().get(i).getText())) {
							MapSchreiben(StatistikInhalteZaehlen.getInstenceOF().getAuZa().get(StatistikInhalteZaehlen.getInstenceOF().getName().indexOf(UserInterface.getInstance().getIsCheck().get(i).getText())), writer, UserInterface.getInstance().getIsCheck().get(i).getText());
							double progress = (double) outZaehlen / (xmlDateienZaehlen + UserInterface.getInstance().getIsCheck().size());
							Platform.runLater(() -> myProgress.setProgress(progress));
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

	private static void MapSchreiben(HashMap<String, Integer> map, FileWriter write, String titel) {
		try {
			write.append("\n").append(titel).append("\n");
			for (Entry<String, Integer> pair : map.entrySet()) {
				write.append(pair.getKey()).append(" : ").append(";").append(pair.getValue().toString()).append("\n");
				write.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void Warnung(String titel) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Information Dialog");
		alert.setContentText(titel);
		alert.showAndWait();
	}

	private static void AlProgress() {
		myProgress = new ProgessDialog();

	}

	//    checkt ob die angegebende datei ein xml ist und gibs sie ensprechend zur bearbeitung frei oder auch nicht
	private static void CheckXML(String filname) {
		Document docu = Op(filname);
		try {
			assert docu != null;
			NodeList Nvalue = docu.getElementsByTagName("gmd:fileIdentifier");
			if (Nvalue.getLength() >= 1) {
				hilfXml = true;
			} else {
				hilfXml = false;
				falscheXml++;
			}
		} catch (Exception e) {
			hilfXml = false;
			falscheXml++;
			fehlerhafteXMLPfade.add(filname);
		}
	}

	//    einlesen der xml dateien aus dem angegebenden ordner
	private static Document Op(String filename) {
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
			e.printStackTrace();
			return null;
		}
	}
}








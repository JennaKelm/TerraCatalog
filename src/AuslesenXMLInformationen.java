import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

class AuslesenXMLInformationen {

	private final LinkedHashMap<String, String> dictionary = new LinkedHashMap<>();
	private final ArrayList<String> hilf = new ArrayList<>();
	//private boolean nicht = true;
	private final String[] arr1 = {"Name", "Amt", "position", "Tele-", "Faxnummer", "STRASSE-", "STADT-", "PLZ-", "LAND-", "E-MAIL-", "Role-"};
	private final LinkedHashMap<String, String> mapVorlage = new LinkedHashMap<>();
	private final LinkedHashMap<String, String> map = new LinkedHashMap<>();
	private Document XmlDocument = null;

	public LinkedHashMap<String, String> getDictionary() {
		return dictionary;
	}

	//public boolean isNicht() {
	//	return nicht;
	//}

	public AuslesenXMLInformationen(String filename) {

		for (int i = 0; i < arr1.length; i++) {
			String[] arr2 = {"gmd:individualName", "gmd:organisationName", "gmd:positionName", "gmd:voice", "gmd:facsimile", "gmd:deliveryPoint", "gmd:city", "gmd:postalCode", "gmd:country", "gmd:electronicMailAddress", "gmd:role"};
			mapVorlage.put(arr2[i], arr1[i]);
		}
		XmlDocument = OpenXml(filename);
		String filename1 = filename;
		dictionary.put("Dateiname", filename);
		Test("gmd:title", "Titel");
		AdressDaten("gmd:contact", "gmd:CI_ResponsibleParty", "Metadatenkontakt");
		AdressDaten("gmd:identificationInfo", "gmd:pointOfContact", "Zuständigkeit");
		AdressDaten("gmd:distributor", "gmd:distributorContact", "Vertrieb");
		Test3("gmd:keyword", "gco:CharacterString", "Schlüsselwörter");
		Test("gmd:fileIdentifier", "ID");
		GeoPunkte("gmd:northBoundLatitude", "Nord");
		GeoPunkte("gmd:eastBoundLongitude", "Ost");
		GeoPunkte("gmd:westBoundLongitude", "West");
		GeoPunkte("gmd:southBoundLatitude", "Süd");
		Datum();
		Dateizugriff();
		Test("gmd:abstract", "Zusammenfassung");
		Test3("gmd:linkage", "gmd:URL", "Url Dienst/Anwendung");
		Art();
		Test("gmd:metadataStandardName", "ISO");
		TopicCategory();
		getSicherheitsstatus(filename);
		Test("gmd:statement", "Herkunft");
	}

	private Document OpenXml(String filename) {
		try {
			InputStream inputStream = new FileInputStream(filename);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
				.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			System.out.println(doc);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// tcext reader
	private void getSicherheitsstatus(String filename) {
		dictionary.put("Status", "null");
		filename = filename.toLowerCase();
		File fi = new File(filename.replace(".xml", ".tcext"));
		if (fi.exists()) {
			ArrayList<String> hilf = new ArrayList<>();
			try (BufferedReader br = new BufferedReader(new FileReader(
				filename.replace(".xml", ".tcext")))) {
				String line = br.readLine().toLowerCase();
				while (line != null) {
					if (line.contains("status")) {
						String[] test = line.split("=");
						String Status = test[1];
						hilf.add(Status);
						StatistikInhalteZaehlen.getInstenceOF().DatenZaehlen(hilf, "Status");
						dictionary.remove("Status");
						dictionary.put("Status", Status);
					}
					line = br.readLine();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Ermittelt Titel ,Id
	// ,Inhalt,Md,Statement
	private void Test(String tag1, String Titel) {
		dictionary.put(Titel, "null");
		NodeList Nvalue = XmlDocument.getElementsByTagName(tag1);
		for (int o = 0; o < Nvalue.getLength(); o++) {
			Node Nvalue2 = Nvalue.item(o);
			if (Nvalue2 != null) {
				Element E_Value = (Element) Nvalue2;
				NodeList Nvalue3 = E_Value.getElementsByTagName("gco:CharacterString");
				Element E_Value2 = (Element) Nvalue3.item(0);
				String Value = ((NodeList) E_Value2).item(0).getNodeValue();
				if (Value != null) {
					DatenZusammenstellen(hilf, Value);
				}
			}
		}
		StringErstellen(hilf, Titel, dictionary);
	}

	private void GeoPunkte(String tag1, String Titel) {
		dictionary.put(Titel, "null");
		NodeList Nvalue = XmlDocument.getElementsByTagName(tag1);
		for (int i = 0; i < Nvalue.getLength(); i++) {
			if (Nvalue.item(i) != null) {
				String hi = Nvalue.item(i).getTextContent();
				hilf.add(hi.replace(".", ","));
			}
		}
		StringErstellen(hilf, Titel, dictionary);
	}

	// Ermittelt  Uri
	private void Dateizugriff() {
		dictionary.put("Dateizugriff", "null");
		NodeList Nvalue = XmlDocument.getElementsByTagName("gmd:dataSetURI");
		Element E_value = (Element) Nvalue.item(0);
		if (E_value != null) {
			NodeList Nvalue2 = E_value.getElementsByTagName("gco:CharacterString");
			for (int d = 0; d < Nvalue2.getLength(); d++) {
				String Value = Nvalue2.item(d).getFirstChild().getNodeValue();
				if (Value != null) {
					DatenZusammenstellen(hilf, Value);
				}
			}
			StringErstellen(hilf, "Dateizugriff", dictionary);
		}
	}

	// Ermittelt  Schlüsselwörter,url
	private void Test3(String tag1, String tag2, String Titel) {
		dictionary.put(Titel, "null");
		NodeList Nvalue = XmlDocument.getElementsByTagName(tag1);
		for (int d = 0; d < Nvalue.getLength(); d++) {
			Element E_value = (Element) Nvalue.item(d);
			if (E_value != null) {
				NodeList Nvalue2 = E_value.getElementsByTagName(tag2);
				String Value = Nvalue2.item(0).getTextContent();
				if (Value != null) {
					DatenZusammenstellen(hilf, Value);
				}
			}
		}
		StringErstellen(hilf, Titel, dictionary);
	}


	// Ermittelt  art
	private void Art() {
		dictionary.put("Art", "null");
		NodeList Nart = XmlDocument.getElementsByTagName("gmd:MD_ScopeCode");
		NodeList Nart1 = XmlDocument.getElementsByTagName("MD_ScopeCode");
		NodeList Nart3 = null;
		if (Nart.getLength() > 0) {
			Nart3 = Nart;
		} else if (Nart1.getLength() > 0) {
			Nart3 = Nart1;
		}
		if (Nart3.getLength() > 0) {
			Node Nart2 = Nart3.item(0);
			Element E_art2 = (Element) Nart2;
			String Value = E_art2.getAttribute("codeListValue");
			if (Value != null) {
				hilf.add(Value);
			}
		}
		StringErstellen(hilf, "Art", dictionary);
	}

	private void AdressDaten(String tag1, String tag2, String name) {

		NodeList no3 = XmlDocument.getElementsByTagName(tag1);
		if (no3.getLength() == 0) {
			for (int i = 0; i < mapVorlage.size(); i++) {
				map.put(arr1[i] + name, "null");
			}
		} else {
			for (int i = 0; i < mapVorlage.size(); i++) {
				map.put(arr1[i] + name, "null");
			}
			for (int i = 0; i < no3.getLength(); i++) {
				Element na = (Element) no3.item(i);
				NodeList na1 = na.getElementsByTagName(tag2);
				if (na1.getLength() == 0) {
					for (int k = 0; k < mapVorlage.size(); k++) {
						map.put(arr1[k] + name, "null");
					}
				} else {
					for (int j = 0; j < na1.getLength(); j++) {
						writeDoc(na1.item(j), name);
					}
				}
			}
		}
		for (int i = 0; i < map.size(); i++) {
			String titel = map.keySet().toArray()[i].toString();
			String daten = map.get(titel).replace("\n", "").replace(";", ",").replace("[", "").replace("]", "").trim();
			hilf.add(map.get(titel));
			StatistikInhalteZaehlen.getInstenceOF().DatenZaehlen(hilf, titel);
			dictionary.put(titel, daten);
			hilf.clear();
		}
		map.clear();
	}

	private void writeDoc(Node node, String name) {
		short type = node.getNodeType();
		switch (type) {
			case Node.ELEMENT_NODE: {
				NodeList children = node.getChildNodes();
				if (children != null) {
					int length = children.getLength();
					for (int i = 0; i < length; i++) {
						writeDoc(children.item(i), name);
					}
				}
				break;
			}
			case Node.TEXT_NODE: {
				MapFuellen(map, mapVorlage, node.getParentNode().getParentNode().getNodeName(), node.getNodeValue(), name);
				break;
			}
		}
	}

	private void MapFuellen(LinkedHashMap<String, String> map, LinkedHashMap<String, String> mapV, String value, String rein, String name) {
		if (mapV.containsKey(value)) {
			if (map.containsKey(mapV.get(value)) && map.get(mapV.get(value)).equals("null")) {
				map.put(mapV.get(value) + name, map.get(mapV.get(value)) + "," + rein);
			} else {
				map.remove(mapV.get(value));
				map.put(mapV.get(value) + name, rein);
			}
		}
	}

	private void Datum() {

		dictionary.put("Creation-Datum", null);
		dictionary.put("Revision-Datum", null);
		dictionary.put("Publication-Datum", null);

		NodeList no34 = XmlDocument.getElementsByTagName("gmd:dateType");
		for (int i = 0; i < no34.getLength(); i++) {
			try {
				if (no34.item(i) != null) {
					Element ele = (Element) no34.item(i).getFirstChild();
					if (ele != null) {
						String er = ele.getAttribute("codeListValue");
						switch (er) {
							case "creation": {
								NodeList nod = no34.item(i).getParentNode().getParentNode().getChildNodes();
								for (int j = 0; j < nod.getLength(); j++) {
									hilf.add(nod.item(j).getTextContent());
								}
								StringErstellen(hilf, "Creation-Datum", dictionary);
								break;
							}
							case "revision": {
								NodeList nod = no34.item(i).getParentNode().getParentNode().getChildNodes();
								for (int j = 0; j < nod.getLength(); j++) {
									hilf.add(nod.item(j).getTextContent());
								}
								StringErstellen(hilf, "Revision-Datum", dictionary);
								break;
							}
							case "publication": {
								NodeList nod = no34.item(i).getParentNode().getParentNode().getChildNodes();
								for (int j = 0; j < nod.getLength(); j++) {
									hilf.add(nod.item(j).getTextContent());
								}
								StringErstellen(hilf, "Publication-Datum", dictionary);
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Kein datum aus gegeben ");
			}
		}
	}

	// Ermittel TopicCategory
	private void TopicCategory() {
		dictionary.put("Thematik", "null");
		NodeList NTca = XmlDocument.getElementsByTagName("gmd:topicCategory");
		for (int i = 0; i < NTca.getLength(); i++) {
			Element E_Tca = (Element) NTca.item(i);
			if (E_Tca != null) {
				NodeList NTca2 = E_Tca
					.getElementsByTagName("gmd:MD_TopicCategoryCode");
				for (int e = 0; e < NTca2.getLength(); e++) {
					String Value = NTca2.item(e).getTextContent();
					if (Value != null) {
						hilf.add(Value);
					}
				}
			}
		}
		StringErstellen(hilf, "Thematik", dictionary);
	}


	// Abspeicher der Ermittelten Daten im Array um doppelte zu vermeiden
	private void DatenZusammenstellen(ArrayList<String> arr, String Value) {
		arr.add(Value);
	}

	// sezt die Daten aus dem Array in einem String zusammen damit dieser in der
	// hashmap abgelegt werden kann zur weiter verarbeitung
	private void StringErstellen(ArrayList<String> arr, String Titel,
	                             LinkedHashMap<String, String> map) {
		String hilf1 = "";
		StatistikInhalteZaehlen.getInstenceOF().DatenZaehlen(arr, Titel);
		if (!arr.isEmpty()) {
			for (int i = 0; i < arr.size(); i++) {
				hilf1 = arr.toString();
			}
			map.remove(Titel);
			String daten = hilf1.replace("\n", "").replace(";", ",").replace("[", "").replace("]", "").trim();
			map.put(Titel, daten);
			arr.clear();
		}
	}
}

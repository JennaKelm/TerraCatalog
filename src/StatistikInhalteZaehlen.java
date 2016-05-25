import java.util.ArrayList;
import java.util.HashMap;

public class StatistikInhalteZaehlen {
	private static StatistikInhalteZaehlen instenceOF;


	private  ArrayList<String> name = new ArrayList<>();
	private  ArrayList<HashMap<String, Integer>> auZa = new ArrayList<>();
	public ArrayList<HashMap<String, Integer>> getAuZa() {
		return auZa;
	}
	public ArrayList<String> getName() {
		return name;
	}

	public StatistikInhalteZaehlen() {
		instenceOF = this;
	}

	public static StatistikInhalteZaehlen getInstenceOF() {
		return instenceOF;
	}

	public void DatenZaehlen(ArrayList<String> arr, String titel) {
		if (name.contains(titel)) {
			MapFuellen(auZa.get(name.indexOf(titel)), arr);
		} else {
			name.add(titel);
			auZa.add(new HashMap<>());
			MapFuellen(auZa.get(name.indexOf(titel)), arr);
		}
	}

	private void MapFuellen(HashMap<String, Integer> map, ArrayList<String> arr) {
		for (String anArr : arr) {
			if (map.containsKey(anArr)) {
				map.put(anArr, (map.get(anArr) + 1));
			} else {
				map.put(anArr, 1);
			}
		}
	}
}

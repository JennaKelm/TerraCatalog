import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class CheckboxenFuerStatistikAuswahl {
	private final ArrayList<CheckBox> arrBox = new ArrayList<>();
	private final GridPane box = new GridPane();
	private boolean an = true;

	public CheckboxenFuerStatistikAuswahl() {
		box.setHgap(2);
		box.setVgap(2);
	}

	public ArrayList<CheckBox> getArrBox() {
		return arrBox;
	}

	public GridPane getBox() {
		return box;
	}

	public void BoxErstellen() {
		if (!Main.getAu().name.isEmpty()) {
			box.getChildren().clear();
			arrBox.clear();
			for (int i = 0; i < Main.getAu().name.size(); i++) {
				arrBox.add(new CheckBox(Main.getAu().name.get(i)));
				box.add(arrBox.get(i), 0, i);
			}
			alle();
		}
	}

	private void alle() {
		Button al = new Button("Auswahl alle");
		box.add(al, 0, Main.getAu().name.size() + 1);
		al.setOnAction(event -> {
			if (an) {
				for (CheckBox anArrBox : arrBox) {
					anArrBox.setSelected(true);
				}
				an = !an;
			} else {
				for (CheckBox anArrBox : arrBox) {
					anArrBox.setSelected(false);
				}
				an = !an;
			}
		});
	}
}

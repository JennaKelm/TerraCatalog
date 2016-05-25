import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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
		if (!Main.getAu().getName().isEmpty()) {
			box.getChildren().clear();
			arrBox.clear();
			alle();
			for (int i = 0; i < Main.getAu().getName().size(); i++) {
				arrBox.add(new CheckBox(Main.getAu().getName().get(i)));
				box.add(arrBox.get(i), 0, i + 1);
			}
		}
	}

	private void alle() {
		Button al = new Button("Auswahl alle");
		al.setPadding(new Insets(5d, 5d, 5d, 5d));
		VBox vbox = new VBox(10d);
		vbox.setPadding(new Insets(0d, 0d, 10d, 0d));
		vbox.getChildren().add(al);
		box.add(vbox, 0, 0);
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

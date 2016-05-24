


import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;

public class CheckboxenFuerStatistikAuswahl {
     final ArrayList<CheckBox> arrBox = new ArrayList<>();
    final GridPane box = new GridPane();
    private Boolean an = true;

    public CheckboxenFuerStatistikAuswahl() {
        box.setHgap(2);
        box.setVgap(2);
        Scene secen = new Scene(box, 150, 500);
    }

    public void BoxErstellen() {
        if (!Main.au.name.isEmpty()) {
            box.getChildren().clear();
            arrBox.clear();
            for (int i = 0; i < Main.au.name.size(); i++) {
                arrBox.add(new CheckBox(Main.au.name.get(i)));
                box.add(arrBox.get(i), 0, i);
            }
            alle();
        }
    }

    private void alle(){
        Button al = new Button("Auswahl alle");
        box.add(al, 0, Main.au.name.size() + 1);
        al.setOnAction(event -> {
            if(an) {
                for (CheckBox anArrBox : arrBox) {
                    anArrBox.setSelected(true);
                }
                an = !an;
            }else {
                for (CheckBox anArrBox : arrBox) {
                    anArrBox.setSelected(false);
                }
                an = !an;
            }
        });
    }
}

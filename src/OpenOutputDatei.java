import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.awt.*;
import java.io.File;
import java.io.IOException;


class OpenOutputDatei {
    private final File out;
    private final File stat;

    public OpenOutputDatei(String output, String stat) {
        this.out = new File(output);
        this.stat = new File(stat);
        Frage();
    }

    private void Frage() {
        Alert al = new Alert(Alert.AlertType.CONFIRMATION);
        al.setContentText("Sollen die geschriebenen Dateien geöffnet werden ");
        al.showAndWait();
        if (al.getResult() == ButtonType.OK) {
            open();
        }
    }

    private void open() {
        try {
            if (out.exists()) {
                Desktop.getDesktop().open(out);
                System.out.println("Öffne output: " + out);
            }
            if (stat.exists()) {
                Desktop.getDesktop().open(stat);
                System.out.println("Öffne Stst: " + stat);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

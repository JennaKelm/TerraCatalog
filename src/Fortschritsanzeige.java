


import javafx.scene.control.Alert;
        import javafx.scene.control.ProgressIndicator;


class Fortschritsanzeige extends Alert {
    private static ProgressIndicator pro;

    public Fortschritsanzeige() {
        super(AlertType.INFORMATION);
        init();
    }

    private void init() {
        pro = new ProgressIndicator();
        setTitle("Dauer");
        setContentText("Einen Moment Bitte");
        setGraphic(pro);
        show();
    }

    public void progressSetzen(double update, int in) {
        pro.setProgress(update / in);
    }
}
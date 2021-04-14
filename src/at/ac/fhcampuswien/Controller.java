package at.ac.fhcampuswien;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller {
    //Objekte aus der FXML Datei importieren (wichtig @FMXL!, sonst wird es nicht erkannt)
    @FXML
    public TextField server;
    @FXML
    public TextField startPort;
    @FXML
    public TextField endPort;
    @FXML
    protected ComboBox popUp;
    @FXML
    protected CheckBox predef;
    @FXML
    protected CheckBox individual;
    @FXML
    protected TextArea yourLog;
    @FXML
    protected Button scan;
    @FXML
    protected Button reset;

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    @FXML
    public void initialize() { //ComboBox Initialisierung + nichts beim Start fokusieren
        popUp.getItems().removeAll(popUp.getItems());
        popUp.getItems().addAll("System Ports", "User Ports", "Dynamic Ports");
        popUp.getSelectionModel().select("Select Options");
        server.setFocusTraversable(false);
        predef.setFocusTraversable(false);
        individual.setFocusTraversable(false);
        yourLog.setFocusTraversable(false);
        reset.setFocusTraversable(false);
        scan.setFocusTraversable(false);
    }

    // nur eine Checkbox kann angechecked werden
    public void handlePreDef(ActionEvent event) { //PreDef Checkbox ausgewählt = Individual kann nicht ausgewählt werden
        if (predef.isSelected()) {
            individual.setSelected(false);
            popUp.setVisible(true);
            startPort.setVisible(false);
            endPort.setVisible(false);
            popUp.getSelectionModel().select("Select Options");
        }
    }

    public void handleIndividual(ActionEvent event) { //Individual Checkbox ausgewählt = PreDef kann nicht ausgewählt werden
        if (individual.isSelected()) {
            predef.setSelected(false);
            popUp.setVisible(false);
            startPort.setVisible(true);
            endPort.setVisible(true);
            startPort.setFocusTraversable(false);
            endPort.setFocusTraversable(false);
        }
    }

    //wir definieren erstmal in SceneBuilder, dass OnAction vom Button scan, als 'scanClicked' benannt wurde
    //wir benutzten die Methode und sagen was passieren soll wenn OnAction aktiviert wird
    public void scanClicked(ActionEvent event) throws InterruptedException { //Interrupted wegen join
        String hostname = server.getText();
        yourLog.clear();

        if (individual.isSelected()) {
            popUp.setValue("");
            yourLog.appendText(getCurrentTimeStamp() + "\n" + "Scan started" + "\n");
            int start = Integer.parseInt(startPort.getText()); //konvertieren in int
            int end = Integer.parseInt(endPort.getText());
            ScanThread st = new ScanThread(yourLog, hostname, start, end);
            st.start();
            st.join();
            yourLog.appendText(getCurrentTimeStamp() + "\n" + "Scan finished");

        }
        String option = (String) popUp.getValue();
        switch (option) {
            case ("System Ports"):
                yourLog.appendText(getCurrentTimeStamp() + "\n" + "Scan started" + "\n");
                for (int i = 0; i < 32; i++) {
                    ScanThread st = new ScanThread(yourLog, hostname, i * 32, ((i + 1) * 32) - 1);
                    st.start();
                    if (i == 31) {
                        st.join();
                        yourLog.appendText(getCurrentTimeStamp() + "\n" + "Scan finished");
                    }
                }
                break;
            case ("User Ports"):
                yourLog.appendText(getCurrentTimeStamp() + "\n" + "Scan started" + "\n");
                for (int i = 1; i < 23; i++) {
                    if (i == 1) {
                        ScanThread st = new ScanThread(yourLog, hostname, (i * 2137) - 1113, ((i) * 2137) - 1);
                        st.start();
                    } else {
                        ScanThread st = new ScanThread(yourLog, hostname, (i * 2137), ((i + 1) * 2137) - 1);
                        st.start();
                        if (i == 22) {
                            st.join();
                            yourLog.appendText(getCurrentTimeStamp() + "\n" + "Scan finished");
                        }
                    }
                }
                break;
            case ("Dynamic Ports"):
                yourLog.appendText(getCurrentTimeStamp() + "\n" + "Scan started" + "\n");
                for (int i = 39; i < 51; i++) {
                    if (i == 39) {
                        ScanThread st = new ScanThread(yourLog, hostname, (i * 1285) - 963, ((i) * 1285) - 1);
                        st.start();
                    } else if (i == 50) {
                        ScanThread st = new ScanThread(yourLog, hostname, (i * 1285), ((i + 1) * 1285));
                        st.start();
                        st.join();
                        yourLog.appendText(getCurrentTimeStamp() + "\n" + "Scan finished");
                    } else {
                        ScanThread st = new ScanThread(yourLog, hostname, (i * 1285), ((i + 1) * 1285) - 1);
                        st.start();
                    }
                }
                break;
        }
        try {
            InetAddress inetAddress = InetAddress.getByName(hostname); //wir holen hier die IP-Adresse

            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss"); //Time-Stamp
            Date currenttime = new Date();

            BufferedWriter writer = new BufferedWriter(new FileWriter(format.format(currenttime) + " - Scan.txt")); //File wird erstellt
            writer.write(inetAddress + "\n" + yourLog.getText()); //Text wird rein geschrieben
            writer.close();
        } catch (IOException e) {

        }
    }

    public void resetClicked(ActionEvent event) { //alles wird auf default gesetzt
        server.clear();
        startPort.clear();
        endPort.clear();
        yourLog.clear();
    }

}
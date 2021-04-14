package at.ac.fhcampuswien;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ScanThread extends Thread { //allgemeine vordefinierte Java Class Thread
    @FXML
    private final TextArea yourLog;
    String server;
    int start;
    int end;

    public ScanThread(TextArea yourLog, String server, int start, int end) { //Konstruktor
        this.yourLog = yourLog;
        this.server = server;
        this.start = start;
        this.end = end;
    }

    public void run () { //wird von der Class geerbt - Override Funktion
        try {
            InetAddress inetAddress = InetAddress.getByName(server); //Class
            String hostName = inetAddress.getHostName();
            for (int port = start; port <= end; port++) {
                try {
                    Socket socket = new Socket(); //Socket ist eine Verbindung zu einer IP Adresse mit jeweiligen Port.
                    socket.connect(new InetSocketAddress(server,port),100); //ms
                    String text = Controller.getCurrentTimeStamp()+ "\n" + hostName + " is listening on port " + port + "\n";
                    yourLog.appendText(text);
                    socket.close();
                }
                catch (IOException e) {
                   //empty catch block
                }
            }
        }
        catch (IOException e) {
            yourLog.setText("");
        }
    }
}

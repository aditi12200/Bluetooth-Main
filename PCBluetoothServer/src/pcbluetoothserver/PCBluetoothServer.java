/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbluetoothserver;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
/**
 *
 * @author dev
 */

public class PCBluetoothServer {
static final String serverUUID = "d7d5d1847e5f11eabc550242ac130003";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws BluetoothStateException, IOException {
        // TODO code application logic here
        LocalDevice localDevice = null;
    try {
        localDevice = LocalDevice.getLocalDevice();
    } catch (BluetoothStateException ex) {
        Logger.getLogger(PCBluetoothServer.class.getName()).log(Level.SEVERE, null, ex);
    }

localDevice.setDiscoverable(DiscoveryAgent.GIAC); // Advertising the service

String url = "btspp://localhost:" + serverUUID + ";name=BlueToothServer";
StreamConnectionNotifier server = (StreamConnectionNotifier) Connector.open(url);

StreamConnection connection = server.acceptAndOpen(); // Wait until client connects
//=== At this point, two devices should be connected ===//
System.out.println("Connected");
DataInputStream dis = connection.openDataInputStream();

int c;
while (true) {
    c = dis.readInt();
    if((char)c=='~'){
     break;
    }
    System.out.print((char)c);
}

connection.close();
    }
    
}

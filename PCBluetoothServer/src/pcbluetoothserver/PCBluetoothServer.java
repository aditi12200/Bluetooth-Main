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
import java.awt.AWTException; 
import java.awt.Robot; 
import java.awt.event.KeyEvent; 
import java.lang.reflect.Field;
/**
 *
 * @author dev
 */

public class PCBluetoothServer {
static final String serverUUID = "d7d5d1847e5f11eabc550242ac130003";

    /**
     * @param args the command line arguments
     */
public static int getLowerKeyCode(int c) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
    String variableName = "VK_" + Character.toUpperCase((char)c);

            Class clazz = KeyEvent.class;
            Field field = clazz.getField( variableName );
            int keyCode = field.getInt(null);
            return keyCode;
}
    public static void main(String[] args) throws BluetoothStateException, IOException, AWTException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        // TODO code application logic here
        Robot robot=new Robot();
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

int c=45;

while (true) {
    c = dis.readInt();
//    System.out.println(c);
    if((char)c=='~'){
     break;
    }
    System.out.print((char)c);
    if(c>=97 && c<=122){
        c=getLowerKeyCode(c);
    }
    robot.keyPress(c);
    robot.keyRelease(c);
}
connection.close();
    }
   
}








//    try {
////        Thread.sleep(5000);
//    } catch (InterruptedException ex) {
//        Logger.getLogger(PCBluetoothServer.class.getName()).log(Level.SEVERE, null, ex);
//    }
//robot.keyPress(KeyEvent.VK_SHIFT);
//        robot.delay(100);
//
//robot.keyPress(KeyEvent.VK_A);
//    robot.delay(100);
//    robot.keyRelease(KeyEvent.VK_A);
//    robot.delay(100);
//    robot.keyRelease(KeyEvent.VK_SHIFT);
//            robot.delay(100);
// try
//        {
//            String letter="a";
//            boolean upperCase = Character.isUpperCase( letter.charAt(0) );
//            String variableName = "VK_" + letter.toUpperCase();
//
//            Class clazz = KeyEvent.class;
//            Field field = clazz.getField( variableName );
//            int keyCode = field.getInt(null);
////            Thread.sleep(6000);
////            robot.delay(1000);
//
//            if (upperCase) robot.keyPress( KeyEvent.VK_SHIFT );
//
//            robot.keyPress( keyCode );
//            robot.keyRelease( keyCode );
//
//            if (upperCase) robot.keyRelease( KeyEvent.VK_SHIFT );
//        }
//        catch(Exception e)
//        {
//            System.out.println(e);
//        }
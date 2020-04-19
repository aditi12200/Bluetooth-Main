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
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author dev
 */

public class PCBluetoothServer {
static final String serverUUID = "d7d5d1847e5f11eabc550242ac130003";

    /**
     * @param args the command line arguments
     */
static Map<Integer,String> special_chars;
static{
    special_chars=new HashMap<>();
    special_chars.put(41,"0"); //0 and )
    special_chars.put(33,"1"); //1 and !
    special_chars.put(64,"2"); //2 and @
    special_chars.put(35,"3"); //3 and #
    special_chars.put(36,"4"); //4 and $
    special_chars.put(37,"5"); //5 and %
    special_chars.put(94,"6"); //6 and ^
    special_chars.put(38,"7"); //7 and &
    special_chars.put(42,"8"); //8 and *
    special_chars.put(40,"9"); //9 and (
    special_chars.put(34,"DEAD_ACUTE"); //' and "
    special_chars.put(43,"EQUALS");//"= and +"
    special_chars.put(95,"MINUS"); // - and _
    special_chars.put(60, "COMMA");// , and <
    special_chars.put(62, "PERIOD");// . and >
    special_chars.put(63, "SLASH"); // / and ?
    special_chars.put(58, "SEMICOLON");//; and :
    special_chars.put(124, "BACK_SLASH");//\ and |
    special_chars.put(123, "OPEN_BRACKET");//[ and {
    special_chars.put(125, "CLOSE_BRACKET");//] and }


}

public static void printSpecialChar(int c,Robot robot) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
    String variableName = "VK_" + special_chars.get(c);
//    boolean upperCase = Character.isUpperCase((char)c);
    Class clazz = KeyEvent.class;
    Field field = clazz.getField( variableName );
    int keyCode = field.getInt(null);
//    System.out.println(keyCode);
    robot.keyPress( KeyEvent.VK_SHIFT );
    robot.keyPress( keyCode );
    robot.keyRelease( keyCode );
    robot.keyRelease( KeyEvent.VK_SHIFT );

}

public static void printAlphabet(int c,Robot robot) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
    String variableName = "VK_" + Character.toUpperCase((char)c);
    boolean upperCase = Character.isUpperCase((char)c);
    Class clazz = KeyEvent.class;
    Field field = clazz.getField( variableName );
    int keyCode = field.getInt(null);
    if (upperCase) robot.keyPress( KeyEvent.VK_SHIFT );
    robot.keyPress( keyCode );
    robot.keyRelease( keyCode );
    if (upperCase) robot.keyRelease( KeyEvent.VK_SHIFT );
   
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
//
localDevice.setDiscoverable(DiscoveryAgent.GIAC); // Advertising the service

String url = "btspp://localhost:" + serverUUID + ";name=BlueToothServer";
StreamConnectionNotifier server = (StreamConnectionNotifier) Connector.open(url);
while(true){
 System.out.println("New Session started");
StreamConnection connection = server.acceptAndOpen(); // Wait until client connects
//=== At this point, two devices should be connected ===//
System.out.println("Connected");
DataInputStream dis = connection.openDataInputStream();

int c;

while (true) {
    try{
    c = dis.readInt();
       
//   System.out.print((char)c);
   if(c==39){
        robot.keyPress(KeyEvent.VK_DEAD_ACUTE);
        robot.keyRelease(KeyEvent.VK_DEAD_ACUTE);
        continue;
   }
   if(special_chars.containsKey(c)){
       try{
       printSpecialChar(c,robot);
       }catch(Exception e){}
       continue;
   }else if((c>=97 && c<=122 )|| (c>=65 && c<=90)){
        printAlphabet(c,robot);
        continue;
    }else{
       try{
        robot.keyPress(c);
        robot.keyRelease(c);
       }catch(Exception e){}
   }
    }catch(Exception e){
//        e.printStackTrace();
        break;
    }
}
System.out.println("Connection lost");
connection.close();
}

    }
}
//
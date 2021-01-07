package com.port.serial;
import com.fazecast.jSerialComm.*;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 * Hello world!
 *
 */
public class App 
{ 
	public static SerialPort userPort;
	static InputStream in;

	public static void main(String args[]) throws InterruptedException, IOException {

        final SerialPort comPort = SerialPort.getCommPorts()[1];
        System.out.println(comPort.getDescriptivePortName());
        System.out.println( comPort.getPortDescription());
        System.out.println( comPort.getBaudRate());
        comPort.setBaudRate(300);
        System.out.println( comPort.getBaudRate());
        comPort.openPort();

        comPort.addDataListener(new SerialPortDataListener() {

            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

          
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }
                byte[] newData = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(newData, newData.length);
                System.out.println("Read " + numRead + " bytes.");
            }
        });
         
 
        
        File file;
        byte[] filecontent = Files.readAllBytes(Paths.get("gcode.txt"));
        int sendDataSize = filecontent.length;
        
 
      
            comPort.writeBytes(filecontent, sendDataSize);
    
            System.exit(0);
	}
  
}

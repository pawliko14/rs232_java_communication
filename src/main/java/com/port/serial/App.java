package com.port.serial;
import com.fazecast.jSerialComm.*;
import gui.form.mainForm;
import machine.transmission.info.machine_info;

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
    private static String value ;

	public static SerialPort userPort;
	static InputStream in;
	private static String  fileName = "output.txt";
    private static FileOutputStream fos = null;
    private static DataOutputStream dos = null;
    private static char[] finishingString;
    private  SerialPort comPort;

    public String getValue()
    {
        return value;
    }


    public void CLosePortCOM()
    {
        comPort.closePort();
    }

    public enum com {

        COM1(1), COM2(2),COM3(3),COM(4);

       public final int id;

       private com(final int id)
       {
           this.id = id;
        }
        public int getCOM()
        {
            return id;
        }

    }

    public void run(final StringValueClass interestingText)
    {
        System.out.println("connected to listener?");

        try {
            fos = new FileOutputStream(fileName);
            dos = new DataOutputStream(fos);
        }catch (Exception e){
            System.out.println("cannot open: " + fileName);
        }

        try{
            finishingString = new char[3];
        }catch (Exception e) {
            System.out.println("Expection in finishingString creation");
        }

        comPort = SerialPort.getCommPorts()[com.COM2.getCOM()];




        printCOMInformation2(comPort);
        //   setCOMParameters(comPort);
        //  printCOMInformation2(comPort);


        comPort.openPort();

        comPort.addDataListener(new SerialPortDataListener() {

            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }


            int iterator = 0;
            int max_iterator = 3;
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }
                byte[] newData = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(newData, newData.length);
                try {
                    value = new String(newData, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    System.out.println("something went wrong");
                }
                System.out.println("Read " + numRead + " bytes.");
                System.out.println("value: " + value);

                interestingText.setText(value);  // <- Obsrver Pattern



                try{
                    dos.writeChar(value.charAt(0));
                    finishingString[iterator] = value.charAt(0);

                    iterator++;
                    if(iterator == max_iterator)
                        iterator = 0;

//                    if(value.charAt(0) == '\0' || value.charAt(0) == 0x00)
//                    {
//                        dos.writeChar('\n');
//                    }

                    for(int i = 0 ; i < finishingString.length; i++) {
                        System.out.println("cur iter: :" + iterator);
                        System.out.println("Iterator[" + i + "] : " + finishingString[i]);
                    }


                    /* file finished */

                    if(finishingString[0] == 'M' && finishingString[1] == '3' && finishingString[2] == '0'){
                        System.out.println("FILE READ FINISHED");
                        System.exit(1);
                    }



                } catch (FileNotFoundException fnfe) {
                    System.out.println("File not found" + fnfe);
                }
                catch (IOException ioe) {
                    System.out.println("Error while writing to file" + ioe);
                }

            }
        });
    }



//	public static void main(String args[]) throws InterruptedException, IOException {
//
//    mainForm form = new mainForm();
//
//
//
//	    try {
//            fos = new FileOutputStream(fileName);
//            dos = new DataOutputStream(fos);
//        }catch (Exception e){
//            System.out.println("cannot open: " + fileName);
//        }
//
//	    try{
//	        finishingString = new char[3];
//        }catch (Exception e) {
//            System.out.println("Expection in finishingString creation");
//        }
//
//        final SerialPort comPort = SerialPort.getCommPorts()[com.COM2.getCOM()];
//
//
//
//
//        printCOMInformation2(comPort);
//     //   setCOMParameters(comPort);
//      //  printCOMInformation2(comPort);
//
//
//        comPort.openPort();
//
//        comPort.addDataListener(new SerialPortDataListener() {
//
//            public int getListeningEvents() {
//                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
//            }
//
//
//            int iterator = 0;
//            int max_iterator = 3;
//            public void serialEvent(SerialPortEvent event) {
//                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
//                    return;
//                }
//                byte[] newData = new byte[comPort.bytesAvailable()];
//                int numRead = comPort.readBytes(newData, newData.length);
//                try {
//                    value = new String(newData, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    System.out.println("something went wrong");
//                }
//                System.out.println("Read " + numRead + " bytes.");
//                System.out.println("value: " + value);
//
//
//                try{
//                    dos.writeChar(value.charAt(0));
//                    finishingString[iterator] = value.charAt(0);
//
//                    iterator++;
//                    if(iterator == max_iterator)
//                        iterator = 0;
//
////                    if(value.charAt(0) == '\0' || value.charAt(0) == 0x00)
////                    {
////                        dos.writeChar('\n');
////                    }
//
//                    for(int i = 0 ; i < finishingString.length; i++) {
//                        System.out.println("cur iter: :" + iterator);
//                        System.out.println("Iterator[" + i + "] : " + finishingString[i]);
//                    }
//
//
//                    /* file finished */
//
//                    if(finishingString[0] == 'M' && finishingString[1] == '3' && finishingString[2] == '0'){
//                        System.out.println("FILE READ FINISHED");
//                        System.exit(1);
//                    }
//
//
//
//                } catch (FileNotFoundException fnfe) {
//                    System.out.println("File not found" + fnfe);
//                }
//                catch (IOException ioe) {
//                    System.out.println("Error while writing to file" + ioe);
//                }
//
//            }
//        });
//
//

//
//	}

	private static void printCOMInformation2(SerialPort comPort)
    {
        System.out.println("COM settings : ");
        System.out.println("Port Name: " + comPort.getDescriptivePortName());
        System.out.println("Port desc: " +  comPort.getPortDescription());
        System.out.println("Baud: " + comPort.getBaudRate());
        System.out.println("FLow: " + comPort.getFlowControlSettings());
        System.out.println("CTS: " + comPort.getCTS());
        System.out.println("DCD: " + comPort.getDCD());
        System.out.println("Parity: " + comPort.getParity());
        System.out.println("NumDataBit: " + comPort.getNumDataBits());
        System.out.println("NumBitStops: " + comPort.getNumStopBits());


    }

    private static void setCOMParameters(SerialPort comPort)
    {
        comPort.setBaudRate(Integer.parseInt(machine_info.getBaudRate()));
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED);
        comPort.setParity(Integer.parseInt(machine_info.getParity()));
        comPort.setNumStopBits(Integer.parseInt(machine_info.getStopBits()));
        comPort.setNumDataBits(Integer.parseInt(machine_info.getDataBits()));
    }

    public void SendDataToCNC(String file) throws IOException {

        SerialPort comPort = SerialPort.getCommPorts()[com.COM2.getCOM()];
        printCOMInformation2(comPort);

        try {
            comPort.openPort();
        }
        catch (Exception e)
        {
            System.out.print("Cannot open port: "+ e);
        }

      //  Scanner scan = new Scanner(System.in);
     //   byte[] filecontent = Files.readAllBytes(Paths.get("gcode.txt"));
        byte[] filecontent = Files.readAllBytes(Paths.get(file));

        int sendDataSize = filecontent.length;

     //   System.out.print("Press any key to send data . . . ");
      //  scan.nextLine();
try {
    comPort.writeBytes(filecontent, sendDataSize);
}
catch (Exception ex)
{
    System.out.println("blad przy wysylaniu " + ex);
}
        System.out.println("sent");

        comPort.closePort();
    }
}

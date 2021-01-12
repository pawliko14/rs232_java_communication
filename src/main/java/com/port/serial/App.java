package com.port.serial;
import com.fazecast.jSerialComm.*;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App
{

    private static String value;

	public static SerialPort userPort;
	static InputStream in;
	private static String  fileName = "output.txt";
    private static FileOutputStream fos = null;
    private static DataOutputStream dos = null;
    private static char[] finishingString;
    private static SerialPort comPort;

    public String getValue()
    {
        return value;
    }

    private String SelectedCOMPort;

    private List<SerialPort> COMPortList;

    public List<SerialPort> getAvailableCOMPorts()
    {
        return COMPortList;
    }

    public App()
    {
        COMPortList = new ArrayList<SerialPort>();
        // for tests :
        SerialPort[] serialPorts = SerialPort.getCommPorts();
       System.out.println("Number of serial port available:{}" +  serialPorts.length);
       int index_of_port = 0;
        for (int portNo = 1; portNo < serialPorts.length; portNo++) {

            System.out.println("SerialPort[{}]:[{},{}]"+ portNo + 1);
            System.out.println( serialPorts[portNo].getSystemPortName());
            System.out.println("COM shortened name: " + serialPorts[portNo].getSystemPortName() + " its index : " + index_of_port);

            COMPortList.add(serialPorts[portNo]);

            if(serialPorts[portNo].getSystemPortName().equals("COM4"))
            {
                System.out.println("TAKEN PORT: " + serialPorts[portNo].getSystemPortName());
                SelectedCOMPort =  serialPorts[portNo].getSystemPortName();

            }

            index_of_port++;
        }



        comPort = SerialPort.getCommPorts()[index_of_port];
        setCOMParameters();
    }

    public String getSelectedCOMPort() {
        return SelectedCOMPort;
    }



    public void CLosePortCOM() {
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



        printCOMInformation2();
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


                    for(int i = 0 ; i < finishingString.length; i++) {
                        System.out.println("cur iter: :" + iterator);
                        System.out.println("Iterator[" + i + "] : " + finishingString[i]);
                    }


                    if(finishingString[0] == 'M' && finishingString[1] == '3' && finishingString[2] == '0'){
                        System.out.println("FILE READ FINISHED");
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

	public void printCOMInformation2()
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

    public static void setCOMParameters()
    {
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED);
        comPort.setComPortParameters(300,7,SerialPort.ONE_STOP_BIT,SerialPort.EVEN_PARITY);

    }

    public void SendDataToCNC(String file) throws IOException {

        try {
            comPort.openPort();
        }
        catch (Exception e)
        {
            System.out.print("Cannot open port: "+ e);
        }

     //   byte[] filecontent = Files.readAllBytes(Paths.get("gcode.txt"));  // <- testing purpose
        byte[] filecontent = Files.readAllBytes(Paths.get(file));

        int sendDataSize = filecontent.length;
        try {
            comPort.writeBytes(filecontent, sendDataSize);
        }
        catch (Exception ex) {
            System.out.println("blad przy wysylaniu " + ex);
        }
                System.out.println("sent");
                comPort.closePort();

            }
}

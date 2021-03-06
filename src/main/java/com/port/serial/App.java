package com.port.serial;
import com.fazecast.jSerialComm.*;
import machine.transmission.info.machine_info;


import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
        try {

            COMPortList = new ArrayList<SerialPort>();
            // for tests :
            SerialPort[] serialPorts = SerialPort.getCommPorts();

            if (serialPorts.length == 0) {
                JOptionPane.showMessageDialog(null, "BRAK DOSTEPNYCH PORTOW COM, lista dostepny, program nie moze wystartowac: " + serialPorts.length);
                System.exit(0);
            }


            System.out.println("Number of serial port available:{}" + serialPorts.length);

            for(int i = 0 ; i < serialPorts.length;i++) {
                System.out.println("serial " + i + " : " + serialPorts[i].getSystemPortName());
            }
            int index_of_port = 0;
            for (int portNo = 1; portNo < serialPorts.length; portNo++) {

                System.out.println("SerialPort[{}]:[{},{}]" + portNo + 1);
                System.out.println(serialPorts[portNo].getSystemPortName());
                System.out.println("COM shortened name: " + serialPorts[portNo].getSystemPortName() + " its index : " + index_of_port);

                // invalid COM port, do not push to the list
                if (!serialPorts[portNo].getSystemPortName().equals("CON")) {
                    COMPortList.add(serialPorts[portNo]);
                } else {
                    System.out.println("CON port exisits, index : " + portNo);
                }

                if (serialPorts[portNo].getSystemPortName().equals("COM2")) {
                    System.out.println("TAKEN PORT: " + serialPorts[portNo].getSystemPortName());
                    SelectedCOMPort = serialPorts[portNo].getSystemPortName();

                }
                index_of_port++;
            }


            comPort = SerialPort.getCommPorts()[index_of_port];
            setCOMParameters();
        }
        catch (Exception exc){
            JOptionPane.showMessageDialog(null, "BRAK DOSTEPNYCH PORTOW COM, lista dostepny, program nie moze wystartowac: " + exc);

        }
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
          setCOMParameters();
          printCOMInformation2();


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
        System.out.println("CTS: " + comPort.getCTS());
        System.out.println("DCD: " + comPort.getDCD());
        System.out.println("Parity: " + comPort.getParity());
        System.out.println("NumDataBit: " + comPort.getNumDataBits());
        System.out.println("NumBitStops: " + comPort.getNumStopBits());
        System.out.println("FlowControlSettings: " + comPort.getFlowControlSettings() );



    }

    public static void setCOMParameters()
    {
       // comPort.setFlowControl(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED);
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_RTS_ENABLED);



        try {

            comPort.setFlowControl(machine_info.getXonxof().getValue());


            comPort.setComPortParameters(
                    Integer.parseInt(machine_info.getBaudRate()),
                    Integer.parseInt(machine_info.getDataBits()),
                    SerialPort.ONE_STOP_BIT,
                    machine_info.getParity().getValue());



        }catch(Exception e)
        {
            System.out.println("something went wrong, 221 setting comPort: " + e);
        }

    }

    public void SendDataToCNC(String file, JTextArea fileTextArea) throws IOException, BadLocationException {

        try {
            comPort.openPort();
            printCOMInformation2();

        }
        catch (Exception e)
        {
            System.out.print("Cannot open port: "+ e);
        }

     //  byte[] filecontent = Files.readAllBytes(Paths.get("gcode.txt"));  // <- testing purpose
        byte[] filecontent = Files.readAllBytes(Paths.get(file));

        int sendDataSize = filecontent.length;
        System.out.println("size of data BEFORE  send to cnc: " + sendDataSize);
        System.out.println("filecontent BEFORE  send to cnc: " + filecontent.length);


        System.out.println("available bytes  BEFORE : " + comPort.bytesAvailable());
        System.out.println("bytes awaiting to write BEFORE: " + comPort.bytesAwaitingWrite());

         int bytesAwait = sendDataSize;
        try {

                //working properly
                comPort.writeBytes(filecontent, sendDataSize);

                // write bytes in the loop with check



//
//                String firstSelectedChar = null;
//                for(int i = 0 ; i < filecontent.length; i++)
//                {
//                    byte   filecontent_shrinken = filecontent[i];
//                    comPort.writeBytes(new byte[]{filecontent_shrinken}, 1);
//                    System.out.println("added: "+ i + " -> " +  (char)filecontent_shrinken);
//
//                    String text = fileTextArea.getText();
//                    if(1 >=1) {
//                        firstSelectedChar = text.substring(i , i+ 1 );
//                        try {
//                            fileTextArea.getHighlighter().addHighlight(i,i+1, new DefaultHighlighter.DefaultHighlightPainter(Color.cyan));
//                        } catch (BadLocationException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    String finalFirstSelectedChar = firstSelectedChar;
//                    new Thread(() -> {
//                               fileTextArea.append(finalFirstSelectedChar);
//                               fileTextArea.revalidate();
//                    }).start();
//                }










                System.out.println("available bytes : " + comPort.bytesAvailable());
                System.out.println("buffed ready buffered size : " + comPort.getDeviceReadBufferSize());
                System.out.println("device writ buffer size : " + comPort.getDeviceWriteBufferSize());
                System.out.println("bytes awaiting to write : " + comPort.bytesAwaitingWrite());
                System.out.println("filecontent While  send to cnc: " + filecontent.length);

          //  }

            System.out.println("sent");
        }
        catch (Exception ex) {
            System.out.println("blad przy wysylaniu " + ex);
            JOptionPane.showMessageDialog(null,"BLad przy wysylaniu : " + ex);
        }
                comPort.closePort();

            }
}

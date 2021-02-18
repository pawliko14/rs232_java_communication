import com.fazecast.jSerialComm.SerialPort;
import com.port.serial.App;
import gui.form.mainForm;



import javax.swing.*;
import java.util.ArrayList;

public class main {

    private static ArrayList<SerialPort> COMPortList;
    private static String SelectedCOMPort;

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

        App transmission = new App();

        JFrame frame = new JFrame("RS232 CNC -> COMPUTER -> CNC");
        frame.getContentPane().add(new mainForm(transmission).getter());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


      //  checkExistingComPorts();
    }

    private static void checkExistingComPorts() {
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


            COMPortList.forEach(System.out::println);

         //   comPort = SerialPort.getCommPorts()[index_of_port];
          //  setCOMParameters();
        }
        catch (Exception exc){
            JOptionPane.showMessageDialog(null, "BRAK DOSTEPNYCH PORTOW COM, lista dostepny, program nie moze wystartowac: " + exc);

        }

    }


}

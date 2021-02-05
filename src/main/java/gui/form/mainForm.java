package gui.form;

import ComPortSettings.ComPortSetting;
import ConstantFile.ConstantFile;
import com.fazecast.jSerialComm.SerialPort;
import com.port.serial.App;
import com.port.serial.StringValueClass;
import machine.transmission.info.machine_info;

import javax.print.attribute.standard.JobPriority;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;



public  class mainForm extends JPanel {
    private JPanel panelMain;
    private JTextField ustawieniaTextField;
    private JPanel panelSettings;
    private JPanel panelSEND;
    private JPanel panelRECEIVE;
    private JPanel panelSettingsValues;
  //  private  JTextField BaudRateValue;
    private JTextField baudRateTextField;
  //  private JTextField ParityValue;
    private JComboBox DataBitsValue;
    private JTextField DataBits;
    private JTextField StopBits;
    private JTextField TimeOut;
    private JTextField StopBitsValue;
    private JComboBox TimeOutValue;
    private JTextField XONXOF;
    private JComboBox XONXOFValue;
    private JButton Select_fileButton;
    private JTextField SelectedFileText;
    private JTextField SelectedFile;
    private JTextArea FileTextArea;
    private JTextArea SavingArea;
    private JButton SaveData;
    private JButton SendButton;
    private JButton ChangeMachineTransmission;
    private JButton SaveMachineTransmissionButton;
    private JButton Transmission;
    private JButton AbortTransmissionButton;
    private JTextField COMTextField;
    private JTextField SelectedCOMPort;
    private JComboBox COMcomboBox;
    private JComboBox ParityValue;
    private JComboBox BaudRateValue;
    private JComboBox StopBItsValue;
    private JTextArea Text_from_file;
    private JTextPane SelectedFile_text;
    private JFileChooser fc;
    private String chooseFile_path;
    private String chooseFile_name;
    private File chooseFile;
    private String value;
    private StringValueClass bean;

    private ComPortSetting ComSetting;


    public JPanel getter(){
        return this.panelMain;
    }
    public mainForm(final App transmission) {

        // add GeneralComPort settings
        ComSetting = new ComPortSetting();

        InitializeCOmboBoxes(transmission.getAvailableCOMPorts());
        Initialize();
        InitializeTransmissionInfo();

        Select_fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc = new JFileChooser();
                fc.setDialogTitle("Choose Directory");
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setAcceptAllFileFilterUsed(false);
                fc.updateUI();
                fc.showOpenDialog(null);

                chooseFile_path = fc.getSelectedFile().getAbsolutePath();
                chooseFile_name = fc.getSelectedFile().getName();

                SelectedFile.setText(chooseFile_name);
                try {
                    chooseFile = fc.getSelectedFile();
                } catch (Exception exc) {
                    System.out.println("something went wrong with file!" + exc);
                }

                try {
                    printValueFromChoosenFileToTextPane(chooseFile);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        SendButton.addActionListener(e -> {

            String data = FileTextArea.getText().trim();

            if (!data.equals("")) {

                App test = new App(SelectedCOMPort.getText());
                test.printCOMInformation2();
                SelectedCOMPort.setText(test.getSelectedCOMPort());

                try {
                    test.SendDataToCNC(chooseFile_path, FileTextArea);
                } catch (IOException | BadLocationException ioException) {
                    ioException.printStackTrace();
                }

                JOptionPane.showMessageDialog(null, "Wyslano");




            } else {
                JOptionPane.showMessageDialog(null, "Nie mozna wyslac pustego!");
            }

        });
        SaveData.addActionListener(e -> {

            save_taken_data_into_file();
            SavingArea.setText("");

            try {
                transmission.CLosePortCOM();
            }catch (Exception exc ){
                JOptionPane.showMessageDialog(null," Something went wrong while data saving!" + exc);
            }

            if(!Transmission.isEnabled())
                Transmission.setEnabled(true);


        });
        ChangeMachineTransmission.addActionListener(e -> {
            SaveMachineTransmissionButton.setEnabled(true);
            SaveMachineTransmissionButton.setVisible(true);
            setTransmissionVisibility(true);
        });

        SaveMachineTransmissionButton.addActionListener(e -> {


            SaveMachineTransmissionButton.setEnabled(false);
            SaveMachineTransmissionButton.setVisible(false);

            try {
                setTransmissionVisibility(false);
            }
            catch(Exception ee) {
                JOptionPane.showMessageDialog(null,"Cannot Set transmission settings : " + ee);
            }

            try {
                doTransmissionValueChange();
                updateINIFile();
                JOptionPane.showMessageDialog(null, "changes saved");
            }catch( Exception exc) {
                JOptionPane.showMessageDialog(null,"something went wrong on value change : " + exc);
            }

    //        UpdateConstantFile();


        });
        Transmission.addActionListener(e -> {

            if(!AbortTransmissionButton.isEnabled()) {
                AbortTransmissionButton.setEnabled(true);
                Transmission.setEnabled(false);
            }

            bean  = new StringValueClass();
            bean.addPropertyChangeListener(lambda ->
                    SavingArea.append((String) lambda.getNewValue())
                    );


            transmission.run(bean);
            value = transmission.getValue();


        });
        AbortTransmissionButton.addActionListener(e -> {
            try {
                transmission.CLosePortCOM();
                Transmission.setEnabled(true);

            } catch(Exception exc) {
                JOptionPane.showMessageDialog(null,"Cannot Close COM Port");
            }

            try {
                if(AbortTransmissionButton.isEnabled()) {
                    AbortTransmissionButton.setEnabled(false);
                }
            }
            catch(Exception exc2) {
                JOptionPane.showMessageDialog(null,"Cannot enabled AbortTransmission");
            }

            SavingArea.setText("");
            JOptionPane.showMessageDialog(null,"Transmission stopped");
        });

        COMcomboBox.addActionListener(e -> {

            SerialPort SelectedItem =   getCOMbyCOMPort(COMcomboBox.getSelectedItem().toString());
            System.out.println(SelectedItem.getBaudRate() + ", " + SelectedItem.getParity());

            SelectedCOMPort.setText( COMcomboBox.getSelectedItem().toString());





        });
    }

    private void updateINIFile() throws IOException, URISyntaxException {

        machine_info.setBaudRate(BaudRateValue.getSelectedItem().toString());
        machine_info.setParity((ComPortSetting.Parities) ParityValue.getSelectedItem());
        machine_info.setDataBits(DataBitsValue.getSelectedItem().toString());
        machine_info.setStopBits(StopBItsValue.getSelectedItem().toString());
        machine_info.setXonxof((ComPortSetting.FlowControll)  XONXOFValue.getSelectedItem()) ;

        ConstantFile.getInstance().changeWini("machine1", ConstantFile.iniElements.BAUDRATE,machine_info.getBaudRate());
        ConstantFile.getInstance().changeWini("machine1", ConstantFile.iniElements.PARITY, String.valueOf(machine_info.getParity()));
        ConstantFile.getInstance().changeWini("machine1", ConstantFile.iniElements.DATABITS,machine_info.getDataBits());
        ConstantFile.getInstance().changeWini("machine1", ConstantFile.iniElements.STOPBITS,machine_info.getStopBits());
        ConstantFile.getInstance().changeWini("machine1", ConstantFile.iniElements.FLOWCONTROLL, String.valueOf(machine_info.getXonxof()));



    }

    private void Initialize() {
        setTransmissionVisibility(false);
    }

    private List<SerialPort> COMPortList;
    private SerialPort getCOMbyCOMPort(String selectedItem) {

        COMPortList = new ArrayList<>();

        SerialPort SerialPortToReturn = null;

        SerialPort[] serialPorts = SerialPort.getCommPorts();
        System.out.println("Number of serial port available:{}" +  serialPorts.length);
        int index_of_port = 0;
        for (int portNo = 1; portNo < serialPorts.length; portNo++) {
            if(serialPorts[portNo].getSystemPortName().equals(selectedItem)) {
                SerialPortToReturn = serialPorts[portNo];
                break;
            }
            index_of_port++;
        }

        return SerialPortToReturn;
    }




    private void InitializeCOmboBoxes(List<SerialPort> availableCOMPorts) {


        // first comboBox -> COM port chooser
        try {
            availableCOMPorts.forEach(e -> COMcomboBox.addItem(e.getSystemPortName()));
        }catch(Exception e) {
            System.out.println("Error in COMcomboBox initliazie: " + e);
        }

        // 2nd ComboBox -> Baud Rade chooser
        try{
            ComSetting.getBaudRateList().forEach(e -> BaudRateValue.addItem(e));
         //   BaudRateValue.setSelectedIndex(ComSetting.getBaudRateList().indexOf("9600"));
            BaudRateValue.setSelectedItem(ConstantFile.getInstance().getBAUDRATE());


        }catch (Exception e){
            System.out.println("Error in BaudRate Setting");
        }

        //3rd ComBox -> Parity
        try{
            ComSetting.getParityList().forEach(e -> ParityValue.addItem(e));
            ParityValue.setSelectedItem(ConstantFile.getInstance().getPARITY());


        }catch (Exception e){
            System.out.println("Error in Parity");
        }

        //4th ComBox -> DataBits
        try{
            ComSetting.getDataBitsList().forEach(e -> DataBitsValue.addItem(e));
            DataBitsValue.setSelectedItem(ConstantFile.getInstance().getDATABITS());

        }catch (Exception e){
            System.out.println("Error in DataBits");
        }

        //5th ComBox -> StopBits
        try{
            ComSetting.getStopBitsList().forEach(e -> StopBItsValue.addItem(e));
            StopBItsValue.setSelectedItem(ConstantFile.getInstance().getSTOPBITS());

        }catch (Exception e){
            System.out.println("Error in StopBits");
        }

        //6th ComBox -> TimeOutValue
        try{
            TimeOutValue.addItem("Not Implemented");
        }catch (Exception e){
            System.out.println("error in TImeOutValue setting");
        }

        //7th ComBox -> Flow COntroll
        try{
            ComSetting.getFlowcontrollList().forEach(e -> XONXOFValue.addItem(e));
            XONXOFValue.setSelectedItem(ConstantFile.getInstance().getFLOWCONTROLL());

        }catch (Exception e){
            System.out.println("Error in FLowControll setting");
        }


    }





    private void setTransmissionVisibility(Boolean trueFalse)
    {
        BaudRateValue.setEnabled(trueFalse);
        ParityValue.setEnabled(trueFalse);
        DataBitsValue.setEnabled(trueFalse);
        StopBItsValue.setEnabled(trueFalse);
        XONXOFValue.setEnabled(trueFalse);
    }

    private void doTransmissionValueChange() {

        machine_info.setBaudRate(BaudRateValue.getSelectedItem().toString());
        machine_info.setParity((ComPortSetting.Parities) ParityValue.getSelectedItem());
        machine_info.setDataBits(DataBitsValue.getSelectedItem().toString());
        machine_info.setStopBits(StopBItsValue.getSelectedItem().toString());
        machine_info.setXonxof((ComPortSetting.FlowControll)  XONXOFValue.getSelectedItem()) ;

    }

    private void InitializeTransmissionInfo() {

        if( COMcomboBox.getSelectedItem().toString() != null ||  !COMcomboBox.getSelectedItem().toString().equals(""))
               SelectedCOMPort.setText( COMcomboBox.getSelectedItem().toString());
        else {
            SelectedCOMPort.setText( "err");
        }

        machine_info.setBaudRate(BaudRateValue.getSelectedItem().toString());
        machine_info.setParity((ComPortSetting.Parities)ParityValue.getSelectedItem());
        machine_info.setDataBits(DataBitsValue.getSelectedItem().toString());
        machine_info.setStopBits(StopBItsValue.getSelectedItem().toString());
        machine_info.setXonxof((ComPortSetting.FlowControll) XONXOFValue.getSelectedItem());
    }

    private void save_taken_data_into_file() {

        BufferedWriter writer = null;
        File fileToSave = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            fileToSave = fileChooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
        }

        try
        {
            assert fileToSave != null;
            writer = new BufferedWriter( new FileWriter(   fileToSave));
            checkIfAreaIsEmpty(SavingArea);
            writer.write(String.valueOf(SavingArea.getText()));  // temporary, change to 2nd one
            JOptionPane.showMessageDialog(null,"File saved!");
        }
        catch ( IOException e) {
            JOptionPane.showMessageDialog(null,"Nie mozna zapisac pustego pliku!");
        }
        finally
        {
            try {
                if ( writer != null)
                    writer.close( );
            }
            catch ( IOException e) {
                JOptionPane.showMessageDialog(null,"Cannot close file! :" + e);
            }
        }
    }

    private void checkIfAreaIsEmpty(JTextArea savingArea) {

        String data = savingArea.getText().trim();

        if(!data.equals("")) {
            JOptionPane.showMessageDialog(null,"Panel zawiera dane");
        }
        else {
            JOptionPane.showMessageDialog(null,"Nie mozna wyslac pustego!");
        }
    }

    private void printValueFromChoosenFileToTextPane(File choosenFile) throws IOException {

        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    new FileInputStream(choosenFile)));
            FileTextArea.read(input, "READING FILE");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

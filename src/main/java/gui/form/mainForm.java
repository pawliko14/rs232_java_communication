package gui.form;

import com.port.serial.App;
import com.port.serial.StringValueClass;
import machine.transmission.info.machine_info;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;


public  class mainForm      {
    private JPanel panelMain;
    private JTextField ustawieniaTextField;
    private JPanel panelSettings;
    private JPanel panelSEND;
    private JPanel panelRECEIVE;
    private JPanel panelSettingsValues;
    private  JTextField BaudRateValue;
    private JTextField baudRateTextField;
    private JTextField ParityValue;
    private JTextField DataBitsValue;
    private JTextField DataBits;
    private JTextField StopBits;
    private JTextField TimeOut;
    private JTextField StopBitsValue;
    private JTextField TimeOutValue;
    private JTextField XONXOF;
    private JTextField XONXOFValue;
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
    private JTextArea Text_from_file;
    private JTextPane SelectedFile_text;
    private JFileChooser fc;

    private String choosenFile_path;
    private String choosenFile_name;
    private File choosenFile;

    private String value;

    private StringValueClass bean;

    private App transmission;

    public mainForm() {

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

                choosenFile_path = fc.getSelectedFile().getAbsolutePath();
                choosenFile_name = fc.getSelectedFile().getName();

                SelectedFile.setText(choosenFile_name);
                try {
                    choosenFile = fc.getSelectedFile();
                } catch (Exception exc) {
                    System.out.println("something went wrong with file!" + exc);
                }

                try {
                    printValueFromChoosenFileToTextPane(choosenFile);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        SendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String data = FileTextArea.getText().trim();

                if (!data.equals("")) {

                    App test = new App();
                    test.printCOMInformation2();
                    SelectedCOMPort.setText(test.getSelectedCOMPort());
             //       test.setCOMParameters();

                    try {
                        test.SendDataToCNC(choosenFile_path );
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(null, "Wyslano");




                } else {
                    JOptionPane.showMessageDialog(null, "Nie mozna wyslac pustego!");
                }

            }
        });
        SaveData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                save_taken_data_into_file();

                SavingArea.setText("");

                try {
                    transmission.CLosePortCOM();
                }catch (Exception exc ){
                    JOptionPane.showMessageDialog(null," Something went wrong while data saving!" + exc);
                }

                if(!Transmission.isEnabled())
                    Transmission.setEnabled(true);


            }
        });
        ChangeMachineTransmission.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveMachineTransmissionButton.setEnabled(true);
                SaveMachineTransmissionButton.setVisible(true);
                setTransmissionVisibility(true);
            }
        });
        SaveMachineTransmissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                SaveMachineTransmissionButton.setEnabled(false);
                SaveMachineTransmissionButton.setVisible(false);
                setTransmissionVisibility(false);


                doTransmissionValueChange();
                JOptionPane.showMessageDialog(null, "changes saved");

            }
        });
        Transmission.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(!AbortTransmissionButton.isEnabled())
                {
                    AbortTransmissionButton.setEnabled(true);
                    Transmission.setEnabled(false);
                }


                // HERE SHOULD NOT BE ADDEED AS NEW
                // NIE MOZNA USUNAC ZE STOSU
                 transmission = new App();

                // CAREFULL!!!! lambda expression, not fully checked!
                bean  = new StringValueClass();

                bean.addPropertyChangeListener(lambda ->
                        SavingArea.append((String) lambda.getNewValue())
                        );

                transmission.run(bean);


                ///

                value = transmission.getValue();


            }
        });
        AbortTransmissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    transmission.CLosePortCOM();
                    Transmission.setEnabled(true);

                } catch(Exception exc)
                {
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
                JOptionPane.showMessageDialog(null,"Transmission stopped, COM port is released");
            }
        });
    }

    private void setTransmissionVisibility(Boolean trueFalse)
    {
        BaudRateValue.setEditable(trueFalse);
        ParityValue.setEditable(trueFalse);
        DataBitsValue.setEditable(trueFalse);
        StopBitsValue.setEditable(trueFalse);
        TimeOutValue.setEditable(trueFalse);
        XONXOFValue.setEditable(trueFalse);
    }

    private void doTransmissionValueChange() {

        machine_info.setBaudRate(BaudRateValue.getText());
        machine_info.setParity(ParityValue.getText());
        machine_info.setDataBits(DataBitsValue.getText());
        machine_info.setStopBits(StopBitsValue.getText());
        machine_info.setTimeout(TimeOutValue.getText());
        machine_info.setXonxof(XONXOFValue.getText());

    }

    private void InitializeTransmissionInfo() {

        BaudRateValue.setText(machine_info.getBaudRate());
        ParityValue.setText(machine_info.getParity());
        DataBitsValue.setText(machine_info.getDataBits());
        StopBitsValue.setText(machine_info.getStopBits());
        TimeOutValue.setText(machine_info.getTimeout());
        XONXOFValue.setText(machine_info.getXonxof());
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




    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");


        JFrame frame = new JFrame("App");
        frame.setContentPane(new mainForm().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}

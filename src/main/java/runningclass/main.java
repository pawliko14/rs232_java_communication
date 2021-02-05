package runningclass;

import ConstantFile.ConstantFile;
import com.port.serial.App;
import gui.form.mainForm;



import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class main {

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException, URISyntaxException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

        // checking Wini Settings
        ConstantFile.getInstance().show();


        App transmission = new App();

        JFrame frame = new JFrame("RS232 CNC -> COMPUTER -> CNC");
        frame.getContentPane().add(new mainForm(transmission).getter());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

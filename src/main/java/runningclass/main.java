package runningclass;

import com.port.serial.App;
import gui.form.mainForm;

import javax.swing.*;

public class main {

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

        App transmission = new App();

        JFrame frame = new JFrame("App");
        frame.getContentPane().add(new mainForm(transmission).getter());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

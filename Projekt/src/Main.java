import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args)  {
        APK kalkulator = new APK();
    JFrame frame = new JFrame("Calculator");
        frame.setContentPane(kalkulator.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(500,200));
        frame.setResizable(false);
}
}
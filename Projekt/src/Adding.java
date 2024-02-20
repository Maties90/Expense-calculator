import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Objects;

public class Adding {

    private JButton dodajButton;
    public JPanel Panel1;
    public JPanel mainPanel;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JComboBox<String> comboBox1;
    private JFrame frame;
    private Connection connection;

    String url="jdbc:sqlite:/E:/java/demo2/Projekt/database.db";

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTODatabase() {
        String categories = Objects.requireNonNull(comboBox1.getSelectedItem()).toString();
        double amount = Double.parseDouble(textField2.getText());
        String date = textField3.getText();
        String description = textField4.getText();

        try {
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Expenses (Amount, Categories, Description, Data) VALUES (?, ?, ?, ?)");
            insertStatement.setDouble(1, amount);
            insertStatement.setString(2, categories);
            insertStatement.setString(3, description);
            insertStatement.setString(4, date);
            insertStatement.executeUpdate();
            insertStatement.close();

            JOptionPane.showMessageDialog(frame, "Dane zostały dodane do bazy danych.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Adding() {

        dodajButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                connectToDatabase();
                addTODatabase();
            }
        });
    }
}
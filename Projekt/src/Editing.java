import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Editing {
    private static final String url ="jdbc:sqlite:/E:/java/demo2/Projekt/database.db";
    public JPanel panel1;
    private JComboBox categoriesComboBox;
    private JTextField amountTextField;
    private JTextField dataTextField;
    private JTextField descriptionTextField;
    private JButton edytujButton;
    private JComboBox idComboBox;
    private static void getIdsFromDatabase(JComboBox<Integer> idComboBox) {
        try {
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();

            String query = "SELECT Id FROM Expenses";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("Id");
                idComboBox.addItem(id);
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Wystąpił błąd przy pobieraniu identyfikatorów: " + e.getMessage());
        }
    }

public Editing() {
        getIdsFromDatabase(idComboBox);
    edytujButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            Integer selectedId = (Integer) idComboBox.getSelectedItem();
            String newAmount = amountTextField.getText();
            String newCategories = (String) categoriesComboBox.getSelectedItem();
            String newDescription = descriptionTextField.getText();
            String newData = dataTextField.getText();

            if (selectedId != null && !newAmount.isEmpty() && newCategories != null
                    && !newDescription.isEmpty() && !newData.isEmpty()) {
                try {
                    Connection connection = DriverManager.getConnection(url);
                    Statement statement = connection.createStatement();

                    String checkRowQuery = "SELECT * FROM Expenses WHERE Id = " + selectedId;
                    ResultSet resultSet = statement.executeQuery(checkRowQuery);

                    if (resultSet.next()) {

                        String updateRowQuery = "UPDATE Expenses SET " +
                                "Amount = '" + newAmount + "', " +
                                "Categories = '" + newCategories + "', " +
                                "Description = '" + newDescription + "', " +
                                "Data = '" + newData + "' " +
                                "WHERE Id = " + selectedId;
                        statement.executeUpdate(updateRowQuery);

                        System.out.println("Wiersz został zaktualizowany.");
                        JOptionPane.showMessageDialog(null, "Wiersz o numerze identyfikacyjnym " + selectedId + " został zaktualizowany.");
                    } else {
                        System.out.println("Nie znaleziono wiersza o podanym numerze identyfikacyjnym.");
                        JOptionPane.showMessageDialog(null, "Nie znaleziono wiersza o podanym numerze identyfikacyjnym.");
                    }

                    statement.close();
                    connection.close();
                } catch (SQLException x) {
                    System.out.println("Wystąpił błąd przy aktualizacji wiersza: " + x.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Wprowadź poprawne wartości we wszystkich polach.");
            }
        }
    });
}
}

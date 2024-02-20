import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class APK {
    private static final String url ="jdbc:sqlite:/E:/java/demo2/Projekt/database.db";
    public JPanel panel1;
    private JPanel mainPanel;
    private JButton button1;
    private JButton deleteButton;
    private JButton TABELAButton;
    private JTable table1;
    private JButton statisticButton;
    private JButton editingButton;
    private JButton Summary;
    private Connection connection;

    public void createDatabase() {
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();

            String createExpensesTable = "CREATE TABLE IF NOT EXISTS Expenses (Id INTEGER PRIMARY KEY AUTOINCREMENT, Amount REAL, Categories TEXT, Description TEXT, Data TEXT)";
            statement.execute(createExpensesTable);
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadDataFromDatabase() {
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Expenses";
            ResultSet resultSet = statement.executeQuery(query);

            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new Object[]{"Id", "Amount", "Categories", "Description", "Data"});

            while (resultSet.next()) {
                int id = resultSet.getInt("Id");
                String amount = resultSet.getString("Amount");
                String categories = resultSet.getString("Categories");
                String description = resultSet.getString("Description");
                String data = resultSet.getString("Data");
                tableModel.addRow(new Object[]{id, amount, categories, description, data});
            }

            table1.setModel(tableModel);

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void generateSummary() {
        try {
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            String query = "SELECT Categories, SUM(Amount) AS TotalAmount FROM Expenses GROUP BY Categories";
            ResultSet resultSet = statement.executeQuery(query);

            double totalExpenses = 0.0;

            StringBuilder summaryBuilder = new StringBuilder();
            summaryBuilder.append("Podsumowanie:\n\n");

            while (resultSet.next()) {
                String category = resultSet.getString("Categories");
                double amount = resultSet.getDouble("TotalAmount");
                totalExpenses += amount;
                summaryBuilder.append(category).append(": ").append(amount).append("\n");
            }

            resultSet.close();
            statement.close();
            connection.close();

            summaryBuilder.append("\nCałkowite wydatki: ").append(totalExpenses);

            JOptionPane.showMessageDialog(mainPanel, summaryBuilder.toString(), "Summary", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void generateChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try {
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            String query = "SELECT Categories, SUM(Amount) AS TotalAmount FROM Expenses GROUP BY Categories";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String category = resultSet.getString("Categories");
                double amount = resultSet.getDouble("TotalAmount");
                dataset.setValue(category, amount);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createPieChart("Wydatki", dataset, true, true, false);

        ChartFrame frame = new ChartFrame("Wydatki", chart);
        frame.pack();
        frame.setVisible(true);
    }

    public void DeleteDataFromDatabase() {
        String idInput = JOptionPane.showInputDialog(null, "Podaj numer identyfikacyjny:");

        if (idInput != null && !idInput.isEmpty()) {
            try {
                int id = Integer.parseInt(idInput);

                try {
                    Connection connection = DriverManager.getConnection(url);
                    Statement statement = connection.createStatement();

                    String deleteRowQuery = "DELETE FROM Expenses WHERE Id = " + id;
                    int rowsAffected = statement.executeUpdate(deleteRowQuery);

                    if (rowsAffected > 0) {
                        System.out.println("Wiersz został usunięty.");
                        JOptionPane.showMessageDialog(null, "Wiersz o numerze identyfikacyjnym " + id + " został usunięty.");
                    } else {
                        System.out.println("Nie znaleziono wiersza o podanym numerze identyfikacyjnym.");
                        JOptionPane.showMessageDialog(null, "Nie znaleziono wiersza o podanym numerze identyfikacyjnym.");
                    }

                    statement.close();
                    connection.close();
                } catch (SQLException x) {
                    System.out.println("Wystąpił błąd przy usuwaniu wiersza: " + x.getMessage());
                }
            } catch (NumberFormatException z) {
                JOptionPane.showMessageDialog(null, "Nieprawidłowy numer identyfikacyjny.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nie podano numeru identyfikacyjnego.");
        }
    }
    
    public void resetIdInTable() {
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();

            String createTempTableQuery = "CREATE TABLE TempExpenses AS SELECT * FROM Expenses";
            statement.executeUpdate(createTempTableQuery);

            String dropTableQuery = "DROP TABLE Expenses";
            statement.executeUpdate(dropTableQuery);

            String createTableQuery = "CREATE TABLE Expenses (Id INTEGER PRIMARY KEY AUTOINCREMENT, Amount TEXT, Categories TEXT, Description TEXT, Data TEXT)";
            statement.executeUpdate(createTableQuery);

            String copyDataQuery = "INSERT INTO Expenses (Amount, Categories, Description, Data) SELECT Amount, Categories, Description, Data FROM TempExpenses";
            statement.executeUpdate(copyDataQuery);

            String dropTempTableQuery = "DROP TABLE TempExpenses";
            statement.executeUpdate(dropTempTableQuery);

            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }



    public APK() {
        createDatabase();
        Summary.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSummary();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteDataFromDatabase();
                resetIdInTable();
            }

            });
        editingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Editing form1 = new Editing();
                JFrame f = new JFrame();
                f.setContentPane(form1.panel1);
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.pack();
                f.setVisible(true);
                f.setResizable(false);
            }
        });
        TABELAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDataFromDatabase();
            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Adding form1 = new Adding();
                JFrame f = new JFrame();
                f.setContentPane(form1.mainPanel);
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.pack();
                f.setVisible(true);
                f.setResizable(false);
            }
        });
        statisticButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                generateChart();
            }
        });
    }
    }

package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:wandshop.db" );
            createTables();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ошибка подключения к базе данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            connection = null; 
        }
    }

    private void createTables() throws SQLException {
        if (connection == null) {
            throw new SQLException("Подключение к базе данных не установлено");
        }
        String createWandsTable = "CREATE TABLE IF NOT EXISTS Wands (" +
                "wand_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "core TEXT NOT NULL, " +
                "wood TEXT NOT NULL, " +
                "status TEXT NOT NULL)";
        String createCustomersTable = "CREATE TABLE IF NOT EXISTS Customers (" +
                "customer_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT NOT NULL, " +
                "last_name TEXT NOT NULL)";
        String createPurchasesTable = "CREATE TABLE IF NOT EXISTS Purchases (" +
                "purchase_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "wand_id INTEGER, " +
                "customer_id INTEGER, " +
                "purchase_date TEXT, " +
                "FOREIGN KEY (wand_id) REFERENCES Wands(wand_id), " +
                "FOREIGN KEY (customer_id) REFERENCES Customers(customer_id))";
        String createSuppliesTable = "CREATE TABLE IF NOT EXISTS Supplies (" +
                "supply_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "component_type TEXT NOT NULL, " +
                "component_name TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "supply_date TEXT NOT NULL)";

        Statement statement = connection.createStatement();
        statement.execute(createWandsTable);
        statement.execute(createCustomersTable);
        statement.execute(createPurchasesTable);
        statement.execute(createSuppliesTable);
        statement.close();
    }

    public void addWand(String core, String wood) throws SQLException {
        if (connection == null) {
            throw new SQLException("Подключение к базе данных не установлено");
        }
        String sql = "INSERT INTO Wands (core, wood, status) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, core);
        ps.setString(2, wood);
        ps.setString(3, "В наличии");
        ps.executeUpdate();
        ps.close();
    }

    public void addCustomer(String firstName, String lastName) throws SQLException {
        if (connection == null) {
            throw new SQLException("Подключение к базе данных не установлено");
        }
        String sql = "INSERT INTO Customers (first_name, last_name) VALUES (?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ps.executeUpdate();
        ps.close();
    }

    public void addPurchase(int wandId, int customerId, String purchaseDate) throws SQLException {
        if (connection == null) {
            throw new SQLException("Подключение к базе данных не установлено");
        }
        String updateWand = "UPDATE Wands SET status = ? WHERE wand_id = ?";
        PreparedStatement ps1 = connection.prepareStatement(updateWand);
        ps1.setString(1, "Продана");
        ps1.setInt(2, wandId);
        ps1.executeUpdate();
        ps1.close();

        String sql = "INSERT INTO Purchases (wand_id, customer_id, purchase_date) VALUES (?, ?, ?)";
        PreparedStatement ps2 = connection.prepareStatement(sql);
        ps2.setInt(1, wandId);
        ps2.setInt(2, customerId);
        ps2.setString(3, purchaseDate);
        ps2.executeUpdate();
        ps2.close();
    }

    public void addSupply(String componentType, String componentName, int quantity, String supplyDate) throws SQLException {
        if (connection == null) {
            throw new SQLException("Подключение к базе данных не установлено");
        }
        String sql = "INSERT INTO Supplies (component_type, component_name, quantity, supply_date) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, componentType);
        ps.setString(2, componentName);
        ps.setInt(3, quantity);
        ps.setString(4, supplyDate);
        ps.executeUpdate();
        ps.close();
    }

    public List<Wand> getAllWands() throws SQLException {
        if (connection == null) {
            throw new SQLException("Подключение к базе данных не установлено");
        }
        List<Wand> wands = new ArrayList<>();
        String sql = "SELECT * FROM Wands";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            wands.add(new Wand(
                    rs.getInt("wand_id"),
                    rs.getString("core"),
                    rs.getString("wood"),
                    rs.getString("status")
            ));
        }
        rs.close();
        statement.close();
        return wands;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        if (connection == null) {
            throw new SQLException("Подключение к базе данных не установлено");
        }
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            customers.add(new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name")
            ));
        }
        rs.close();
        statement.close();
        return customers;
    }

    public List<Purchase> getAllPurchases() throws SQLException {
        if (connection == null) {
            throw new SQLException("Подключение к базе данных не установлено");
        }
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM Purchases";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            purchases.add(new Purchase(
                    rs.getInt("purchase_id"),
                    rs.getInt("wand_id"),
                    rs.getInt("customer_id"),
                    rs.getString("purchase_date")
            ));
        }
        rs.close();
        statement.close();
        return purchases;
    }

    public List<Supply> getAllSupplies() throws SQLException {
        if (connection == null) {
            throw new SQLException("Подключение к базе данных не установлено");
        }
        List<Supply> supplies = new ArrayList<>();
        String sql = "SELECT * FROM Supplies";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            supplies.add(new Supply(
                    rs.getInt("supply_id"),
                    rs.getString("component_type"),
                    rs.getString("component_name"),
                    rs.getInt("quantity"),
                    rs.getString("supply_date")
            ));
        }
        rs.close();
        statement.close();
        return supplies;
    }

    public void clearAllData() throws SQLException {
        if (connection == null) {
            throw new SQLException("Подключение к базе данных не установлено");
        }
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM Purchases");
        statement.execute("DELETE FROM Wands");
        statement.execute("DELETE FROM Customers");
        statement.execute("DELETE FROM Supplies");
        statement.close();
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
package controller;

import java.sql.*;
import java.util.ArrayList;
import other.Product;
import other.Slot;
import user.User;
import user.Customer;
import user.Manager;
import user.Restocker;
import exceptions.*;

public class MySQL_DATABASE {

    private static Connection connection = null;

    private static final String URL = "jdbc:mysql://localhost:3306/vending_machine";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@Bunhou2006";

    public static Connection getConnection() throws DatabaseConnectionException {
        if (connection == null) {
            try {
                // Test if MySQL driver is available
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connection successful!");
            } catch (ClassNotFoundException e) {
                throw new DatabaseConnectionException("MySQL JDBC Driver not found. Please add the MySQL connector to your classpath.", e);
            } catch (SQLException e) {
                throw new DatabaseConnectionException("Failed to connect to database: " + e.getMessage() + ". Please check if MySQL server is running and credentials are correct.", e);
            }
        }
        return connection;
    }

    public static void closeConnection() throws DatabaseConnectionException {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
                connection = null;
                System.out.println("Connection closed!");
            } catch (SQLException e) {
                throw new DatabaseConnectionException("Failed to close database connection: " + e.getMessage(), e);
            }
        }
    }

    public static ResultSet executeQuery(String query) throws DatabaseConnectionException {
        if (query == null || query.trim().isEmpty()) {
            throw new DatabaseConnectionException("Query cannot be null or empty");
        }
        
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Failed to execute query: " + e.getMessage() + "\nQuery: " + query, e);
        }
    }

    public static int executeUpdate(String query) throws DatabaseConnectionException {
        if (query == null || query.trim().isEmpty()) {
            throw new DatabaseConnectionException("Query cannot be null or empty");
        }
        
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            int result = statement.executeUpdate(query);
            return result;
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Failed to execute update: " + e.getMessage() + "\nQuery: " + query, e);
        }
    }

    // Load users from database
    public static ArrayList<User> loadUsers() throws DatabaseConnectionException {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT u.*, c.email, m.salary as manager_salary, r.salary as restocker_salary " +
                      "FROM users u " +
                      "LEFT JOIN customers c ON u.user_id = c.user_id " +
                      "LEFT JOIN managers m ON u.user_id = m.user_id " +
                      "LEFT JOIN restockers r ON u.user_id = r.user_id";
        
        try {
            ResultSet rs = executeQuery(query);
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String fullName = rs.getString("full_name");
                String phone = rs.getString("phone");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String userType = rs.getString("user_type");
                
                User user;
                try {
                    switch (userType) {
                        case "CUSTOMER":
                            String email = rs.getString("email");
                            user = new Customer(userId, fullName, phone, username, password, email);
                            break;
                        case "MANAGER":
                            float managerSalary = rs.getFloat("manager_salary");
                            user = new Manager(userId, fullName, phone, username, password, managerSalary);
                            break;
                        case "RESTOCKER":
                            float restockerSalary = rs.getFloat("restocker_salary");
                            user = new Restocker(userId, fullName, phone, username, password, restockerSalary);
                            break;
                        default:
                            continue; // Skip unknown user types
                    }
                    
                    // Set additional user properties
                    user.setActive(rs.getBoolean("active"));
                    user.setBalance(rs.getDouble("balance"));
                    user.setPremium(rs.getBoolean("premium"));
                    user.setItemsBought(rs.getInt("items_bought"));
                    
                    users.add(user);
                } catch (InvalidInputException e) {
                    System.out.println("Skipping invalid user data from database: " + e.getMessage());
                    // Continue with next user
                    continue;
                }
            }
            rs.close();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error processing user data: " + e.getMessage(), e);
        }
        return users;
    }

    // Load products from database
    public static ArrayList<Product> loadProducts() throws DatabaseConnectionException {
        ArrayList<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        
        try {
            ResultSet rs = executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                double price = rs.getDouble("price");
                
                Product product = new Product(name, category, price);
                product.setId(id);
                products.add(product);
            }
            rs.close();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error processing product data: " + e.getMessage(), e);
        }
        return products;
    }

    // Load slots for a specific machine from database
    public static ArrayList<Slot> loadSlots(int machineId) throws DatabaseConnectionException {
        if (machineId <= 0) {
            throw new DatabaseConnectionException("Invalid machine ID: " + machineId);
        }
        
        ArrayList<Slot> slots = new ArrayList<>();
        String query = "SELECT s.*, p.name as product_name, p.category, p.price " +
                      "FROM slots s " +
                      "LEFT JOIN products p ON s.product_id = p.product_id " +
                      "WHERE s.machine_id = " + machineId;
        
        try {
            ResultSet rs = executeQuery(query);
            while (rs.next()) {
                String slotId = rs.getString("slot_id");
                int quantity = rs.getInt("quantity");
                
                Product product = null;
                if (rs.getString("product_name") != null) {
                    String productName = rs.getString("product_name");
                    String category = rs.getString("category");
                    double price = rs.getDouble("price");
                    product = new Product(productName, category, price);
                }
                
                Slot slot = new Slot(slotId, product, quantity);
                slots.add(slot);
            }
            rs.close();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error processing slot data: " + e.getMessage(), e);
        }
        return slots;
    }

    // Get vending machine locations
    public static ArrayList<String> getMachineLocations() throws DatabaseConnectionException {
        ArrayList<String> locations = new ArrayList<>();
        String query = "SELECT location FROM vending_machines WHERE status = 'ACTIVE'";
        
        try {
            ResultSet rs = executeQuery(query);
            while (rs.next()) {
                String location = rs.getString("location");
                if (location != null && !location.trim().isEmpty()) {
                    locations.add(location);
                }
            }
            rs.close();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error processing machine location data: " + e.getMessage(), e);
        }
        return locations;
    }

    public static void main(String[] args) {
        try {
            connection = getConnection();
            System.out.println("Testing database connection...");
            
            ResultSet result = executeQuery("SELECT * FROM employees;");
            if (result != null) {
                try {
                    while (result.next()) {
                        System.out.println(result.getString(1));
                    }
                } catch (SQLException e) {
                    System.out.println("Error processing results: " + e.getMessage());
                } finally {
                    try {
                        result.close();
                    } catch (SQLException e) {
                        System.out.println("Error closing result set: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Query returned no results");
            }
        } catch (DatabaseConnectionException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            try {
                closeConnection();
            } catch (DatabaseConnectionException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

}
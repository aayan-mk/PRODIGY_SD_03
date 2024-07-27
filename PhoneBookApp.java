package phonebook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PhoneBookApp {

    private JFrame frame;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextArea displayArea;
    private Connection connection;

    public PhoneBookApp() {
        initialize();
        connectToDatabase();
    }

    private void initialize() {
        frame = new JFrame("Phone Book");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        // Set background color
        frame.getContentPane().setBackground(new Color(230, 230, 250));

        // Create input panel with a border and padding
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Contact Information"));
        inputPanel.setBackground(new Color(240, 248, 255));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);

        // Create buttons with custom styles
        JButton addButton = createButton("Add Contact");
        addButton.addActionListener(new AddContactAction());
        inputPanel.add(addButton);

        JButton viewButton = createButton("View Contacts");
        viewButton.addActionListener(new ViewContactsAction());
        inputPanel.add(viewButton);

        JButton deleteButton = createButton("Delete Contact");
        deleteButton.addActionListener(new DeleteContactAction());
        inputPanel.add(deleteButton);

        JButton editButton = createButton("Edit Contact");
        editButton.addActionListener(new EditContactAction());
        inputPanel.add(editButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 14));
        displayArea.setBackground(new Color(255, 255, 255));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(100, 149, 237)); // Cornflower blue
        button.setForeground(Color.BLACK); // Set font color to black
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/PhoneBookDB";
            String user = "root"; // Change to your MySQL username
            String password = "Admin@123"; // Change to your MySQL password
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error connecting to the database.");
        }
    }

    private class AddContactAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (connection != null) { // Check if connection is not null
                String name = nameField.getText();
                String phone = phoneField.getText();
                if (!name.isEmpty() && !phone.isEmpty()) { // Check if both fields have data
                    try {
                        String query = "INSERT INTO Contacts (name, phone_number) VALUES (?, ?)";
                        PreparedStatement pstmt = connection.prepareStatement(query);
                        pstmt.setString(1, name);
                        pstmt.setString(2, phone);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Contact added!");
                        nameField.setText("");
                        phoneField.setText("");
                        displayContacts();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error adding contact.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter both name and phone.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Error: Database connection is not established.");
            }
        }
    }

    private class ViewContactsAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (connection != null) { // Check if connection is not null
                displayContacts();
            } else {
                JOptionPane.showMessageDialog(frame, "Error: Database connection is not established.");
            }
        }
    }

    private class DeleteContactAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (connection != null) { // Check if connection is not null
                String name = nameField.getText();
                try {
                    String query = "DELETE FROM Contacts WHERE name = ?";
                    PreparedStatement pstmt = connection.prepareStatement(query);
                    pstmt.setString(1, name);
                    int rowsDeleted = pstmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(frame, "Contact deleted!");
                        nameField.setText("");
                        phoneField.setText("");
                        displayContacts();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Contact not found: " + name);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error deleting contact.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Error: Database connection is not established.");
            }
        }
    }

    private class EditContactAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (connection != null) { // Check if connection is not null
                String name = nameField.getText();
                String phone = phoneField.getText();
                if (!name.isEmpty() && !phone.isEmpty()) { // Check if both fields have data
                    try {
                        String query = "UPDATE Contacts SET phone_number = ? WHERE name = ?";
                        PreparedStatement pstmt = connection.prepareStatement(query);
                        pstmt.setString(1, phone);
                        pstmt.setString(2, name);
                        int rowsUpdated = pstmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(frame, "Contact updated!");
                            nameField.setText("");
                            phoneField.setText("");
                            displayContacts();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Contact not found: " + name);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error updating contact.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter both name and phone.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Error: Database connection is not established.");
            }
        }
    }

    private void displayContacts() {
        displayArea.setText(""); // Clear previous display
        try {
            String query = "SELECT * FROM Contacts";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            boolean hasContacts = false;
            while (rs.next()) {
                hasContacts = true;
                String name = rs.getString("name");
                String phone = rs.getString("phone_number");
                displayArea.append(name + ": " + phone + "\n");
            }
            if (!hasContacts) {
                displayArea.setText("No contacts found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error retrieving contacts.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhoneBookApp());
    }
}
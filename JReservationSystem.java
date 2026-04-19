import javax.swing.border.TitledBorder;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class JReservationSystem extends JPanel implements RoomObserver {
    
    private BookingManager bookingManager;
    private JTextField nameField;
    private JComboBox<String> roomTypeCombo;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextArea reservationsArea;
    private JPanel usersPanel;
    private JLabel[] roomStatusLabels;
    private JPanel roomButtonsPanel;
    
    public JReservationSystem() {
        // Get Singleton instance
        bookingManager = BookingManager.getInstance();
        bookingManager.addObserver(this);
        
        initializeRooms();
        setupUI();
    }
    
    private void initializeRooms() {
        // Using Factory Pattern to create 40 rooms (101-140)
        for (int i = 0; i < 40; i++) {
            int roomNum = 101 + i;
            String type;
            
            // Assign room types
            if (i < 15) {
                type = "Single";
            } else if (i < 28) {
                type = "Deluxe";
            } else {
                type = "Suite";
            }
            
            // Use RoomFactory to create rooms
            Room room = JRoomFactory.createRoom(roomNum, type);
            bookingManager.addRoom(room);
        }
    }
    
    private void setupUI() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Left Panel - Users
        JPanel leftPanel = createLeftPanel();
        leftPanel.setOpaque(false);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.2;
        gbc.weighty = 1;
        add(leftPanel, gbc);
        
        // Center Panel - Rooms
        JPanel centerPanel = createCenterPanel();
        centerPanel.setOpaque(false);
        gbc.gridx = 1;
        gbc.weightx = 1.5;
        add(centerPanel, gbc);
        
        // Right Panel - Reservations
        JPanel rightPanel = createRightPanel();
        rightPanel.setOpaque(false);
        gbc.gridx = 2;
        gbc.weightx = 1;
        add(rightPanel, gbc);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;
        
        // Users List Card
        JPanel usersCard = createStyledCard("Current Guests");
        usersCard.setLayout(new BorderLayout());
        
        usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
        usersPanel.setOpaque(false);
        
        updateUsersDisplay();
        
        JScrollPane userScroll = new JScrollPane(usersPanel);
        userScroll.setOpaque(false);
        userScroll.getViewport().setOpaque(false);
        userScroll.setBorder(BorderFactory.createEmptyBorder());
        userScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        usersCard.add(userScroll, BorderLayout.CENTER);
        
        gbc.gridy = 0;
        gbc.weighty = 0.6;
        panel.add(usersCard, gbc);
        
        // Add User Form Card
        JPanel addUserCard = createStyledCard("Add New User");
        addUserCard.setLayout(new GridBagLayout());
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(8, 10, 8, 10);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.gridx = 0;
        cardGbc.weightx = 1;
        
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setForeground(new Color(44, 62, 80));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cardGbc.gridy = 0;
        addUserCard.add(nameLabel, cardGbc);
        
        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        cardGbc.gridy = 1;
        addUserCard.add(nameField, cardGbc);
        
        JLabel typeLabel = new JLabel("Room Type:");
        typeLabel.setForeground(new Color(44, 62, 80));
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cardGbc.gridy = 2;
        addUserCard.add(typeLabel, cardGbc);
        
        roomTypeCombo = new JComboBox<>(new String[]{"Single", "Deluxe", "Suite"});
        roomTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomTypeCombo.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        cardGbc.gridy = 3;
        addUserCard.add(roomTypeCombo, cardGbc);
        
        JButton addUserBtn = createStyledButton("+ Add User", new Color(212, 175, 55));
        addUserBtn.addActionListener(e -> addNewUser());
        cardGbc.gridy = 4;
        cardGbc.insets = new Insets(15, 10, 10, 10);
        addUserCard.add(addUserBtn, cardGbc);
        
        gbc.gridy = 1;
        gbc.weighty = 0.4;
        panel.add(addUserCard, gbc);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1;
        
        // Rooms Table Card
        JPanel roomsCard = createStyledCard("Available Rooms");
        roomsCard.setLayout(new BorderLayout());
        
        // Create table with 40 rooms
        String[] columns = {"Room No.", "Type", "Price (Tk)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(40);
        roomTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roomTable.setSelectionBackground(new Color(212, 175, 55, 100));
        roomTable.setShowGrid(false);
        roomTable.setIntercellSpacing(new Dimension(5, 5));
        
        // Custom cell renderer for colors
        roomTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                           boolean isSelected, boolean hasFocus, 
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String type = (String) tableModel.getValueAt(row, 1);
                String status = (String) tableModel.getValueAt(row, 3);
                
                // Color coding based on room type
                if (type.equals("Single")) {
                    c.setBackground(new Color(245, 222, 179, 200)); // Beige
                } else if (type.equals("Deluxe")) {
                    c.setBackground(new Color(64, 224, 208, 200)); // Soft Teal
                } else {
                    c.setBackground(new Color(255, 215, 0, 200)); // Light Gold
                }
                
                // Status color
                if (status.equals("Available")) {
                    c.setForeground(new Color(0, 150, 0));
                } else {
                    c.setForeground(new Color(200, 0, 0));
                }
                
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        
        refreshRoomTable();
        
        JScrollPane tableScroll = new JScrollPane(roomTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        roomsCard.add(tableScroll, BorderLayout.CENTER);
        
        gbc.gridy = 0;
        gbc.weighty = 0.7;
        panel.add(roomsCard, gbc);
        
        // Bottom section with Book Button and Prices
        JPanel bottomCard = createStyledCard("Booking & Pricing");
        bottomCard.setLayout(new GridBagLayout());
        GridBagConstraints bottomGbc = new GridBagConstraints();
        bottomGbc.insets = new Insets(10, 10, 10, 10);
        bottomGbc.fill = GridBagConstraints.HORIZONTAL;
        bottomGbc.gridx = 0;
        bottomGbc.weightx = 1;
        
        JButton bookButton = createStyledButton("📖 Book Selected Room", new Color(0, 150, 136));
        bookButton.addActionListener(e -> bookRoom());
        bottomGbc.gridy = 0;
        bottomCard.add(bookButton, bottomGbc);
        
        // Price List
        JPanel pricePanel = new JPanel(new GridLayout(3, 1, 5, 5));
        pricePanel.setOpaque(false);
        pricePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(212, 175, 55, 100)),
            "Room Prices (per night)",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(212, 175, 55)
        ));
        
        pricePanel.add(createPriceLabel("🏠 Single", "Tk 4,000"));
        pricePanel.add(createPriceLabel("✨ Deluxe", "Tk 11,500"));
        pricePanel.add(createPriceLabel("👑 Suite", "Tk 20,000"));
        
        bottomGbc.gridy = 1;
        bottomCard.add(pricePanel, bottomGbc);
        
        gbc.gridy = 1;
        gbc.weighty = 0.3;
        panel.add(bottomCard, gbc);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1;
        
        // Reservations Card
        JPanel reservationsCard = createStyledCard("Reservations");
        reservationsCard.setLayout(new BorderLayout());
        
        reservationsArea = new JTextArea();
        reservationsArea.setEditable(false);
        reservationsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reservationsArea.setBackground(new Color(255, 255, 255, 220));
        reservationsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        updateReservationsDisplay();
        
        JScrollPane reservationScroll = new JScrollPane(reservationsArea);
        reservationScroll.setBorder(BorderFactory.createEmptyBorder());
        reservationsCard.add(reservationScroll, BorderLayout.CENTER);
        
        gbc.gridy = 0;
        gbc.weighty = 0.8;
        panel.add(reservationsCard, gbc);
        
        // View Reservations Button
        JPanel buttonCard = createStyledCard("Actions");
        buttonCard.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JButton viewButton = createStyledButton("👁️ View All Reservations", new Color(76, 175, 80));
        viewButton.addActionListener(e -> {
            updateReservationsDisplay();
            JOptionPane.showMessageDialog(this, 
                "Total Reservations: " + bookingManager.getReservations().size(),
                "Reservation Count",
                JOptionPane.INFORMATION_MESSAGE);
        });
        buttonCard.add(viewButton);
        
        gbc.gridy = 1;
        gbc.weighty = 0.2;
        panel.add(buttonCard, gbc);
        
        return panel;
    }
    
    private void refreshRoomTable() {
        tableModel.setRowCount(0);
        for (Room room : bookingManager.getRooms()) {
            String status = room.isAvailable() ? "Available" : "Booked";
            tableModel.addRow(new Object[]{
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPrice(),
                status
            });
        }
    }
    
    private void updateUsersDisplay() {
        usersPanel.removeAll();
        
        List<Reservation> reservations = bookingManager.getReservations();
        if (reservations.isEmpty()) {
            JLabel emptyLabel = new JLabel("No guests yet");
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            usersPanel.add(emptyLabel);
        } else {
            for (Reservation res : reservations) {
                JPanel userCard = new JPanel(new BorderLayout());
                userCard.setOpaque(false);
                userCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(212, 175, 55, 100), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                
                JLabel userLabel = new JLabel("👤 " + res.getGuestName() + " → Room " + res.getRoom().getRoomNumber());
                userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                userLabel.setForeground(new Color(44, 62, 80));
                userCard.add(userLabel, BorderLayout.CENTER);
                
                JLabel typeLabel = new JLabel(res.getRoom().getRoomType() + " - Tk " + res.getTotalPrice());
                typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                typeLabel.setForeground(new Color(212, 175, 55));
                userCard.add(typeLabel, BorderLayout.EAST);
                
                usersPanel.add(userCard);
                usersPanel.add(Box.createVerticalStrut(8));
            }
        }
        
        usersPanel.revalidate();
        usersPanel.repaint();
    }
    
    private void updateReservationsDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════\n");
        sb.append("     CURRENT RESERVATIONS       \n");
        sb.append("═══════════════════════════════\n\n");
        
        List<Reservation> reservations = bookingManager.getReservations();
        if (reservations.isEmpty()) {
            sb.append("   No reservations yet.\n");
            sb.append("   Book a room to get started!\n");
        } else {
            for (int i = 0; i < reservations.size(); i++) {
                Reservation res = reservations.get(i);
                sb.append(String.format("%d. %s\n", i + 1, res.toString()));
                sb.append("   ├─ Room: ").append(res.getRoom().getRoomNumber()).append("\n");
                sb.append("   ├─ Type: ").append(res.getRoom().getRoomType()).append("\n");
                sb.append("   └─ Price: Tk ").append(res.getTotalPrice()).append("\n\n");
            }
        }
        
        sb.append("═══════════════════════════════\n");
        sb.append("Total: ").append(reservations.size()).append(" reservation(s)");
        
        reservationsArea.setText(sb.toString());
    }
    
    private void addNewUser() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String roomType = (String) roomTypeCombo.getSelectedItem();
        
        // Create User object using your User class
        User newUser = new User(name, roomType);
        
        JOptionPane.showMessageDialog(this, 
            "✅ User Added Successfully!\n\n" +
            "Name: " + newUser.getName() + "\n" +
            "Preferred Room: " + newUser.getRoomType() + "\n\n" +
            "Please select a room from the table and click 'Book Room'",
            "User Added",
            JOptionPane.INFORMATION_MESSAGE);
        
        nameField.setText("");
    }
    
    private void bookRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a room from the table first!",
                "No Room Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int roomNum = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 3);
        
        if (!status.equals("Available")) {
            JOptionPane.showMessageDialog(this, 
                "Room " + roomNum + " is already booked!",
                "Room Unavailable",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String guestName = JOptionPane.showInputDialog(this, 
            "Enter guest name for Room " + roomNum + ":",
            "Book Room",
            JOptionPane.QUESTION_MESSAGE);
        
        if (guestName == null || guestName.trim().isEmpty()) {
            return;
        }
        
        // Find and book the room
        Room selectedRoom = null;
        for (Room room : bookingManager.getRooms()) {
            if (room.getRoomNumber() == roomNum) {
                selectedRoom = room;
                break;
            }
        }
        
        if (selectedRoom != null && selectedRoom.isAvailable()) {
            boolean success = bookingManager.bookRoom(selectedRoom, guestName.trim());
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "🎉 Booking Confirmed!\n\n" +
                    "Guest: " + guestName + "\n" +
                    "Room: " + roomNum + " (" + selectedRoom.getRoomType() + ")\n" +
                    "Price: Tk " + selectedRoom.getPrice() + "/night\n\n" +
                    "Thank you for choosing us!",
                    "Booking Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                refreshRoomTable();
                updateUsersDisplay();
                updateReservationsDisplay();
            }
        }
    }
    
    // Observer Pattern Implementation
    @Override
    public void onRoomBooked(Room room) {
        System.out.println("[Observer] Room " + room.getRoomNumber() + " was booked by " + room.getGuestName());
        SwingUtilities.invokeLater(() -> {
            refreshRoomTable();
            updateUsersDisplay();
            updateReservationsDisplay();
        });
    }
    
    @Override
    public void onRoomAvailable(Room room) {
        System.out.println("[Observer] Room " + room.getRoomNumber() + " is now available");
        SwingUtilities.invokeLater(() -> {
            refreshRoomTable();
            updateUsersDisplay();
            updateReservationsDisplay();
        });
    }
    
    // UI Helper Methods
    private JPanel createStyledCard(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(212, 175, 55, 150), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(212, 175, 55));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        panel.setLayout(new BorderLayout());
        panel.add(titleLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JLabel createPriceLabel(String type, String price) {
        JLabel label = new JLabel(type + ": " + price);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(44, 62, 80));
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
}
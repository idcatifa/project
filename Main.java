import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class Main extends JFrame implements Observer {
    
    private JPanel usersPanel;
    private JPanel roomsPanel;
    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;
    private JComboBox<String> roomTypeCombo;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField nightsField;
    private JLabel totalCostLabel;
    private RoomFacade hotelFacade;
    private ReservationCaretaker caretaker;
    private String currentFilter = "ALL";
    private JButton allFilterBtn;
    private JButton singleFilterBtn;
    private JButton deluxeFilterBtn;
    private JButton suiteFilterBtn;
    
    public Main() {
        hotelFacade = new RoomFacade();
        caretaker = new ReservationCaretaker();
        hotelFacade.attachObserver(this);
        initializeRooms();
        setupUI();
    }
    
    private void initializeRooms() {
        List<String> roomTypes = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            if (i < 14) roomTypes.add("Single");
            else if (i < 27) roomTypes.add("Deluxe");
            else roomTypes.add("Suite");
        }
        Collections.shuffle(roomTypes);
        
        for (int i = 0; i < 40; i++) {
            int roomNum = 101 + i;
            String type = roomTypes.get(i);
            RoomFactoryPattern factory;
            
            if (type.equals("Single")) factory = new SingleRoomFactory();
            else if (type.equals("Deluxe")) factory = new DeluxeRoomFactory();
            else factory = new SuiteRoomFactory();
            
            Room room = factory.createRoom(roomNum);
            BookingManager.getInstance().addRoom(room);
        }
    }
    
    private void setupUI() {
        setTitle("Hotel Reservation System - 11 Design Patterns");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(25, 35, 50));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title Section
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("🏨 HOTEL RESERVATION SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(212, 175, 55));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel badgePanel = new JPanel(new FlowLayout());
        badgePanel.setOpaque(false);
        String[] patterns = {"SINGLETON", "FACTORY", "STRATEGY", "COMMAND", "ITERATOR", 
                             "ADAPTER", "DECORATOR", "OBSERVER", "FACADE", "MEMENTO", "COMPOSITE"};
        for (String p : patterns) {
            JLabel badge = new JLabel(" 🔷 " + p + " ");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badge.setForeground(new Color(212, 175, 55));
            badge.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1));
            badge.setBackground(new Color(0,0,0,80));
            badge.setOpaque(true);
            badgePanel.add(badge);
        }
        titlePanel.add(badgePanel, BorderLayout.SOUTH);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Content Panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(createUsersPanel());
        contentPanel.add(createRoomsPanel());
        contentPanel.add(createReservationsPanel());
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        setVisible(true);
    }
    
    private JPanel createUsersPanel() {
        JPanel panel = createStyledPanel("👥 CURRENT GUESTS");
        panel.setPreferredSize(new Dimension(350, 0));
        
        usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
        usersPanel.setBackground(new Color(245, 245, 245));
        updateUsersDisplay();
        
        JScrollPane scrollPane = new JScrollPane(usersPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(330, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add User Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(250, 250, 250));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(212, 175, 55), 2),
            "➕ Add New User",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(212, 175, 55)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;
        
        // First Name
        gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:*"), gbc);
        gbc.gridy = 1;
        firstNameField = new JTextField();
        firstNameField.setPreferredSize(new Dimension(200, 28));
        formPanel.add(firstNameField, gbc);
        
        // Last Name
        gbc.gridy = 2;
        formPanel.add(new JLabel("Last Name:*"), gbc);
        gbc.gridy = 3;
        lastNameField = new JTextField();
        lastNameField.setPreferredSize(new Dimension(200, 28));
        formPanel.add(lastNameField, gbc);
        
        // Phone Number
        gbc.gridy = 4;
        formPanel.add(new JLabel("Phone Number:* (+880 + 10 digits)"), gbc);
        gbc.gridy = 5;
        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        phonePanel.setBackground(new Color(250, 250, 250));
        phonePanel.add(new JLabel("+880 "), BorderLayout.WEST);
        phoneField = new JTextField();
        phoneField.setToolTipText("Enter 10 digits (e.g., 1712345678)");
        phonePanel.add(phoneField, BorderLayout.CENTER);
        formPanel.add(phonePanel, gbc);
        
        // Email (Optional)
        gbc.gridy = 6;
        formPanel.add(new JLabel("Email (Optional):"), gbc);
        gbc.gridy = 7;
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 28));
        formPanel.add(emailField, gbc);
        
        // Address (NEW)
        gbc.gridy = 8;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridy = 9;
        addressField = new JTextField();
        addressField.setPreferredSize(new Dimension(200, 28));
        addressField.setToolTipText("Enter your full address");
        formPanel.add(addressField, gbc);
        
        // Room Type
        gbc.gridy = 10;
        formPanel.add(new JLabel("Room Type:*"), gbc);
        gbc.gridy = 11;
        roomTypeCombo = new JComboBox<>(new String[]{"Single", "Deluxe", "Suite"});
        roomTypeCombo.addActionListener(e -> calculateTotalCost());
        formPanel.add(roomTypeCombo, gbc);
        
        // Number of Nights
        gbc.gridy = 12;
        formPanel.add(new JLabel("Number of Nights:*"), gbc);
        gbc.gridy = 13;
        nightsField = new JTextField("1");
        nightsField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculateTotalCost();
            }
        });
        formPanel.add(nightsField, gbc);
        
        // Total Cost Display
        gbc.gridy = 14;
        totalCostLabel = new JLabel("Total Cost: Tk 0", SwingConstants.CENTER);
        totalCostLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalCostLabel.setForeground(new Color(212, 175, 55));
        totalCostLabel.setBackground(new Color(50, 50, 60));
        totalCostLabel.setOpaque(true);
        totalCostLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        formPanel.add(totalCostLabel, gbc);
        
        JButton addBtn = new JButton("➕ ADD USER");
        addBtn.setBackground(new Color(212, 175, 55));
        addBtn.setForeground(new Color(40, 30, 10));
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.addActionListener(e -> addUser());
        gbc.gridy = 15;
        formPanel.add(addBtn, gbc);
        
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private void calculateTotalCost() {
        try {
            String selectedType = (String) roomTypeCombo.getSelectedItem();
            int nights = Integer.parseInt(nightsField.getText().trim());
            int pricePerNight = selectedType.equals("Single") ? 4000 : (selectedType.equals("Deluxe") ? 11500 : 20000);
            totalCostLabel.setText("Total: Tk " + (pricePerNight * nights) + " (" + pricePerNight + " x " + nights + " nights)");
        } catch (NumberFormatException e) {
            totalCostLabel.setText("Total: Tk 0");
        }
    }
    
    private boolean validatePhoneNumber(String phone) {
        return phone != null && phone.trim().length() == 10 && phone.matches("\\d+");
    }
    
    private boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true;
        return Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email);
    }
    
    private void addUser() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        String selectedType = (String) roomTypeCombo.getSelectedItem();
        
        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter First Name and Last Name!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (phone.isEmpty() || !validatePhoneNumber(phone)) {
            JOptionPane.showMessageDialog(this, "Invalid Phone Number!\nMust be exactly 10 digits after +880", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!validateEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid Email Format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int nights;
        try {
            nights = Integer.parseInt(nightsField.getText().trim());
            if (nights < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid number of nights!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int pricePerNight = selectedType.equals("Single") ? 4000 : (selectedType.equals("Deluxe") ? 11500 : 20000);
        int totalCost = pricePerNight * nights;
        
        StringBuilder available = new StringBuilder();
        available.append("═══════════════════════════════════\n");
        available.append("     USER INFORMATION\n");
        available.append("═══════════════════════════════════\n");
        available.append("👤 Name: " + firstName + " " + lastName + "\n");
        available.append("📞 Phone: +880" + phone + "\n");
        if (!email.isEmpty()) available.append("📧 Email: " + email + "\n");
        if (!address.isEmpty()) available.append("🏠 Address: " + address + "\n");
        available.append("🛏️ Room Type: " + selectedType + "\n");
        available.append("🌙 Nights: " + nights + "\n");
        available.append("💰 Price per night: Tk " + pricePerNight + "\n");
        available.append("💵 Total Cost: Tk " + totalCost + "\n");
        available.append("═══════════════════════════════════\n\n");
        available.append("📋 Available " + selectedType + " rooms:\n");
        
        for (Room room : BookingManager.getInstance().getRooms()) {
            if (room.isAvailable() && room.getRoomType().equals(selectedType)) {
                available.append("   🏠 Room " + room.getRoomNumber() + " - Tk " + (int)room.getPrice() + "/night\n");
            }
        }
        
        JOptionPane.showMessageDialog(this, available.toString(), "✅ User Added", JOptionPane.INFORMATION_MESSAGE);
        
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        nightsField.setText("1");
        calculateTotalCost();
    }
    
    private JPanel createRoomsPanel() {
        JPanel panel = createStyledPanel("🛏️ AVAILABLE ROOMS");
        panel.setPreferredSize(new Dimension(500, 0));
        
        // FILTER BUTTONS
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        filterPanel.setBackground(new Color(250, 250, 250));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        allFilterBtn = createFilterButton("ALL", new Color(52, 152, 219));
        singleFilterBtn = createFilterButton("SINGLE", new Color(245, 222, 179));
        deluxeFilterBtn = createFilterButton("DELUXE", new Color(64, 224, 208));
        suiteFilterBtn = createFilterButton("SUITE", new Color(255, 215, 0));
        
        allFilterBtn.addActionListener(e -> setFilter("ALL"));
        singleFilterBtn.addActionListener(e -> setFilter("Single"));
        deluxeFilterBtn.addActionListener(e -> setFilter("Deluxe"));
        suiteFilterBtn.addActionListener(e -> setFilter("Suite"));
        
        filterPanel.add(allFilterBtn);
        filterPanel.add(singleFilterBtn);
        filterPanel.add(deluxeFilterBtn);
        filterPanel.add(suiteFilterBtn);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Rooms Grid
        roomsPanel = new JPanel(new GridLayout(8, 5, 8, 8));
        roomsPanel.setBackground(new Color(245, 245, 245));
        roomsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        updateRoomsGrid();
        
        JScrollPane roomScroll = new JScrollPane(roomsPanel);
        roomScroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(roomScroll, BorderLayout.CENTER);
        
        // Bottom Panel with Prices
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(250, 250, 250));
        
        JPanel pricePanel = new JPanel(new GridLayout(1, 3, 10, 0));
        pricePanel.add(createPriceCard("SINGLE", "Tk 4,000", new Color(245, 222, 179)));
        pricePanel.add(createPriceCard("DELUXE", "Tk 11,500", new Color(64, 224, 208)));
        pricePanel.add(createPriceCard("SUITE", "Tk 20,000", new Color(255, 215, 0)));
        
        JButton bookBtn = new JButton("📖 BOOK SELECTED ROOM");
        bookBtn.setBackground(new Color(46, 204, 113));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookBtn.addActionListener(e -> bookRoom());
        
        JButton undoBtn = new JButton("↩️ UNDO");
        undoBtn.setBackground(new Color(241, 196, 15));
        undoBtn.setForeground(new Color(40, 30, 10));
        undoBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        undoBtn.addActionListener(e -> undoLast());
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.add(bookBtn);
        buttonPanel.add(undoBtn);
        
        bottomPanel.add(pricePanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private JButton createFilterButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(new Color(44, 62, 80));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void setFilter(String filter) {
        currentFilter = filter;
        resetFilterButtons();
        if (filter.equals("ALL")) allFilterBtn.setBackground(new Color(41, 128, 185));
        else if (filter.equals("Single")) singleFilterBtn.setBackground(new Color(210, 180, 140));
        else if (filter.equals("Deluxe")) deluxeFilterBtn.setBackground(new Color(0, 180, 160));
        else if (filter.equals("Suite")) suiteFilterBtn.setBackground(new Color(218, 165, 32));
        updateRoomsGrid();
    }
    
    private void resetFilterButtons() {
        allFilterBtn.setBackground(new Color(52, 152, 219));
        singleFilterBtn.setBackground(new Color(245, 222, 179));
        deluxeFilterBtn.setBackground(new Color(64, 224, 208));
        suiteFilterBtn.setBackground(new Color(255, 215, 0));
    }
    
    private void updateRoomsGrid() {
        roomsPanel.removeAll();
        
        for (Room room : BookingManager.getInstance().getRooms()) {
            if (currentFilter.equals("ALL") || room.getRoomType().equals(currentFilter)) {
                roomsPanel.add(createRoomCard(room));
            }
        }
        
        roomsPanel.revalidate();
        roomsPanel.repaint();
    }
    
    private JPanel createReservationsPanel() {
        JPanel panel = createStyledPanel("📋 RESERVATIONS LIST");
        panel.setPreferredSize(new Dimension(400, 0));
        
        // Create table for reservations
        String[] columns = {"#", "Guest Name", "Phone", "Room", "Type", "Nights", "Total"};
        reservationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setRowHeight(30);
        reservationsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reservationsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        reservationsTable.getTableHeader().setBackground(new Color(212, 175, 55));
        reservationsTable.getTableHeader().setForeground(new Color(44, 62, 80));
        
        // Set column widths
        reservationsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        reservationsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        reservationsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        reservationsTable.getColumnModel().getColumn(3).setPreferredWidth(40);
        reservationsTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        reservationsTable.getColumnModel().getColumn(5).setPreferredWidth(40);
        reservationsTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        
        updateReservationsTable();
        
        JScrollPane tableScroll = new JScrollPane(reservationsTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(tableScroll, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton viewBtn = new JButton("👁️ VIEW DETAILS");
        viewBtn.setBackground(new Color(52, 152, 219));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        viewBtn.addActionListener(e -> viewReservationDetails());
        
        JButton deleteBtn = new JButton("🗑️ DELETE");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        deleteBtn.addActionListener(e -> deleteReservation());
        
        buttonPanel.add(viewBtn);
        buttonPanel.add(deleteBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private void updateReservationsTable() {
        reservationsTableModel.setRowCount(0);
        List<Reservation> reservations = BookingManager.getInstance().getReservations();
        
        int count = 1;
        for (Reservation res : reservations) {
            // Calculate nights and total (need to store nights - using placeholder)
            int nights = 1; // You can store nights in Reservation class
            int total = (int)res.getTotalPrice();
            
            reservationsTableModel.addRow(new Object[]{
                count++,
                res.getGuestName(),
                "N/A", // Phone number needs to be stored in Reservation
                res.getRoom().getRoomNumber(),
                res.getRoom().getRoomType(),
                nights,
                "Tk " + total
            });
        }
    }
    
    private void viewReservationDetails() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to view!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String guestName = (String) reservationsTableModel.getValueAt(selectedRow, 1);
        int roomNum = (int) reservationsTableModel.getValueAt(selectedRow, 3);
        String roomType = (String) reservationsTableModel.getValueAt(selectedRow, 4);
        String total = (String) reservationsTableModel.getValueAt(selectedRow, 6);
        
        JOptionPane.showMessageDialog(this,
            "📋 RESERVATION DETAILS\n\n" +
            "👤 Guest: " + guestName + "\n" +
            "🏠 Room: " + roomNum + " (" + roomType + ")\n" +
            "💰 " + total + "\n\n" +
            "💡 Tip: Contact guest for check-in details",
            "Reservation Details",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void bookRoom() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add user information first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String fullName = firstName + " " + lastName;
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        
        String roomNumStr = JOptionPane.showInputDialog(this, 
            "Enter room number to book (101-140):\n\n👤 Guest: " + fullName + "\n📞 Phone: +880" + phone,
            "Book Room",
            JOptionPane.QUESTION_MESSAGE);
        
        if (roomNumStr == null) return;
        
        try {
            int roomNum = Integer.parseInt(roomNumStr);
            int nights = Integer.parseInt(nightsField.getText().trim());
            
            Room selectedRoom = null;
            for (Room room : BookingManager.getInstance().getRooms()) {
                if (room.getRoomNumber() == roomNum) {
                    selectedRoom = room;
                    break;
                }
            }
            
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(this, "Invalid room number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!selectedRoom.isAvailable()) {
                JOptionPane.showMessageDialog(this, "Room " + roomNum + " is already booked!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int totalCost = (int)selectedRoom.getPrice() * nights;
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "📋 CONFIRM BOOKING:\n\n" +
                "👤 Guest: " + fullName + "\n" +
                "📞 Phone: +880" + phone + "\n" +
                "🏠 Address: " + (address.isEmpty() ? "Not provided" : address) + "\n" +
                "🏠 Room: " + roomNum + " (" + selectedRoom.getRoomType() + ")\n" +
                "🌙 Nights: " + nights + "\n" +
                "💰 Total Cost: Tk " + totalCost + "\n\n" +
                "Confirm booking?",
                "Confirm Reservation",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                caretaker.saveState(BookingManager.getInstance().getReservations());
                selectedRoom.bookRoom(fullName);
                BookingManager.getInstance().addReservation(new Reservation(fullName, selectedRoom));
                
                JOptionPane.showMessageDialog(this, "🎉 BOOKING CONFIRMED!\n\nRoom " + roomNum + " booked for " + fullName, "Success", JOptionPane.INFORMATION_MESSAGE);
                
                updateAllDisplays();
                
                firstNameField.setText("");
                lastNameField.setText("");
                phoneField.setText("");
                emailField.setText("");
                addressField.setText("");
                nightsField.setText("1");
                calculateTotalCost();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void undoLast() {
        ReservationMemento memento = caretaker.undo();
        if (memento != null) {
            for (Room room : BookingManager.getInstance().getRooms()) room.releaseRoom();
            BookingManager.getInstance().getReservations().clear();
            for (Reservation res : memento.getSavedReservations()) {
                for (Room room : BookingManager.getInstance().getRooms()) {
                    if (room.getRoomNumber() == res.getRoom().getRoomNumber()) {
                        room.bookRoom(res.getGuestName());
                        BookingManager.getInstance().addReservation(res);
                        break;
                    }
                }
            }
            updateAllDisplays();
            JOptionPane.showMessageDialog(this, "✅ Undone successfully!", "Undo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to undo!", "Undo", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void deleteReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to delete!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String guestName = (String) reservationsTableModel.getValueAt(selectedRow, 1);
        int roomNum = (int) reservationsTableModel.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete reservation for " + guestName + " (Room " + roomNum + ")?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            caretaker.saveState(BookingManager.getInstance().getReservations());
            
            for (Reservation res : BookingManager.getInstance().getReservations()) {
                if (res.getGuestName().equals(guestName) && res.getRoom().getRoomNumber() == roomNum) {
                    for (Room room : BookingManager.getInstance().getRooms()) {
                        if (room.getRoomNumber() == roomNum) {
                            room.releaseRoom();
                            break;
                        }
                    }
                    BookingManager.getInstance().removeReservation(res);
                    break;
                }
            }
            updateAllDisplays();
            JOptionPane.showMessageDialog(this, "✅ Reservation deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void updateUsersDisplay() {
        usersPanel.removeAll();
        List<Reservation> reservations = BookingManager.getInstance().getReservations();
        
        if (reservations.isEmpty()) {
            JLabel empty = new JLabel("No guests yet", SwingConstants.CENTER);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            usersPanel.add(empty);
        } else {
            int count = 1;
            for (Reservation res : reservations) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(212, 175, 55, 100), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                JLabel label = new JLabel(count + ". " + res.getGuestName() + " → Room " + res.getRoom().getRoomNumber());
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                card.add(label, BorderLayout.CENTER);
                usersPanel.add(card);
                usersPanel.add(Box.createVerticalStrut(5));
                count++;
            }
        }
        usersPanel.revalidate();
        usersPanel.repaint();
    }
    
    private void updateAllDisplays() {
        updateRoomsGrid();
        updateUsersDisplay();
        updateReservationsTable();
    }
    
    private JPanel createRoomCard(Room room) {
        Color color;
        if (room.getRoomType().equals("Single")) color = new Color(245, 222, 179);
        else if (room.getRoomType().equals("Deluxe")) color = new Color(64, 224, 208);
        else color = new Color(255, 215, 0);
        
        if (!room.isAvailable()) color = color.darker();
        
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(3, 3));
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1));
        
        JLabel numLabel = new JLabel(String.valueOf(room.getRoomNumber()), SwingConstants.CENTER);
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel statusLabel = new JLabel(room.isAvailable() ? "AVAIL" : "BOOKED", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        statusLabel.setForeground(room.isAvailable() ? new Color(0, 150, 0) : Color.RED);
        
        JLabel priceLabel = new JLabel("Tk " + (int)room.getPrice(), SwingConstants.CENTER);
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        
        card.add(numLabel, BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.SOUTH);
        card.add(priceLabel, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(212, 175, 55), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setLayout(new BorderLayout(8, 8));
        
        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setForeground(new Color(212, 175, 55));
        panel.add(header, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createPriceCard(String title, String price, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        card.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
        card.add(new JLabel(price, SwingConstants.CENTER), BorderLayout.CENTER);
        return card;
    }
    
    public void update(String message) {
        System.out.println("Notification: " + message);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}
            new Main();
        });
    }
}
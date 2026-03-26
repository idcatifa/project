import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        ReservationSystem system = ReservationSystem.getInstance();

        JFrame frame = new JFrame("Hotel Reservation System");
        frame.setSize(1250, 680);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Color mainBeige = new Color(245, 222, 179);
        Color softBeige = new Color(255, 239, 213);

        JPanel mainPanel = new JPanel(new GridLayout(1,3,20,20));
        mainPanel.setBackground(mainBeige);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        Font titleFont = new Font("Serif", Font.BOLD, 22);

        JPanel userPanel = new JPanel(new BorderLayout(10,10));
        userPanel.setBackground(softBeige);

        JLabel userTitle = new JLabel("Users", JLabel.CENTER);
        userTitle.setFont(titleFont);

        DefaultListModel<String> userModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userModel);

        JTextField userField = new JTextField();
        JComboBox<String> availableRoomsBox = new JComboBox<>();

        JButton addUserBtn = new JButton("Add User");
        JButton editUserBtn = new JButton("Edit User");
        JButton deleteUserBtn = new JButton("Delete User");
        JButton bookBtn = new JButton("Book Room");

        JPanel userBottom = new JPanel(new GridLayout(8,1,5,5));
        userBottom.setBackground(softBeige);

        userBottom.add(new JLabel("Enter User Name:"));
        userBottom.add(userField);
        userBottom.add(addUserBtn);
        userBottom.add(editUserBtn);
        userBottom.add(deleteUserBtn);
        userBottom.add(new JLabel("Book Room (Available Only):"));
        userBottom.add(availableRoomsBox);
        userBottom.add(bookBtn);

        userPanel.add(userTitle, BorderLayout.NORTH);
        userPanel.add(new JScrollPane(userList), BorderLayout.CENTER);
        userPanel.add(userBottom, BorderLayout.SOUTH);

        JPanel roomPanel = new JPanel(new BorderLayout(10,10));
        roomPanel.setBackground(softBeige);

        JLabel roomTitle = new JLabel("Rooms", JLabel.CENTER);
        roomTitle.setFont(titleFont);

        DefaultListModel<String> roomModel = new DefaultListModel<>();
        JList<String> roomList = new JList<>(roomModel);

        JTextField roomNumberField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Single", "Deluxe", "Suite"});
        JButton addRoomBtn = new JButton("Add Room");

        JPanel roomBottom = new JPanel(new GridLayout(4,5));
        roomBottom.setBackground(softBeige);

        roomBottom.setLayout(new GridLayout(4,1,5,5));
        roomBottom.add(new JLabel("Room Number:"));
        roomBottom.add(roomNumberField);
        roomBottom.add(typeBox);
        roomBottom.add(addRoomBtn);

        roomPanel.add(roomTitle, BorderLayout.NORTH);
        roomPanel.add(new JScrollPane(roomList), BorderLayout.CENTER);
        roomPanel.add(roomBottom, BorderLayout.SOUTH);

        JPanel resPanel = new JPanel(new BorderLayout(10,10));
        resPanel.setBackground(softBeige);

        JLabel resTitle = new JLabel("Reservations", JLabel.CENTER);
        resTitle.setFont(titleFont);

        DefaultListModel<String> resModel = new DefaultListModel<>();
        JList<String> resList = new JList<>(resModel);

        resPanel.add(resTitle, BorderLayout.NORTH);
        resPanel.add(new JScrollPane(resList), BorderLayout.CENTER);

        Runnable refresh = () -> {

            // USERS with numbering
            userModel.clear();
            int count = 1;
            for (User u : system.getUsers()) {
                userModel.addElement(count + ". " + u.getName());
                count++;
            }

            // ROOMS + Available Rooms Dropdown
            roomModel.clear();
            availableRoomsBox.removeAllItems();

            for (Room r : system.getRooms()) {

                roomModel.addElement(
                        "Room " + r.getRoomNumber() +
                        " | " + r.getType() +
                        " | " + r.getStatus()
                );

                if (r.isAvailable()) {
                    availableRoomsBox.addItem(
                            "Room " + r.getRoomNumber() + " (" + r.getType() + ")"
                    );
                }
            }

            resModel.clear();
            for (User u : system.getUsers()) {
                if (u.getRoom() != null) {
                    resModel.addElement(
                            u.getName() +
                            " | Room " +
                            u.getRoom().getRoomNumber() +
                            " | BOOKED"
                    );
                }
            }
        };

        refresh.run();


        // ADD USER
        addUserBtn.addActionListener(e -> {
            if (!userField.getText().trim().isEmpty()) {
                system.addUser(userField.getText());
                userField.setText("");
                refresh.run();
            }
        });

        // EDIT USER (Professional Dialog Version)
        editUserBtn.addActionListener(e -> {

            int index = userList.getSelectedIndex();

            if (index >= 0) {

                User selectedUser = system.getUsers().get(index);

                String newName = JOptionPane.showInputDialog(
                        frame,
                        "Enter new name for: " + selectedUser.getName(),
                        selectedUser.getName()
                );

                if (newName != null && !newName.trim().isEmpty()) {
                    selectedUser.setName(newName);
                    refresh.run();
                }

            } else {
                JOptionPane.showMessageDialog(frame, "Please select a user to edit.");
            }
        });

        // DELETE USER with Confirmation
        deleteUserBtn.addActionListener(e -> {

            int index = userList.getSelectedIndex();

            if (index >= 0) {

                int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to delete this user?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    system.deleteUser(system.getUsers().get(index));
                    refresh.run();
                }

            } else {
                JOptionPane.showMessageDialog(frame, "Select a user first.");
            }
        });

        // ADD ROOM (Prevent Duplicate)
        addRoomBtn.addActionListener(e -> {

            try {
                int number = Integer.parseInt(roomNumberField.getText());
                String type = typeBox.getSelectedItem().toString();

                boolean added = system.addRoom(number, type);

                if (!added) {
                    JOptionPane.showMessageDialog(frame, "Room already exists!");
                }

                roomNumberField.setText("");
                refresh.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Enter valid room number!");
            }
        });

        // BOOK ROOM
        bookBtn.addActionListener(e -> {

            int userIndex = userList.getSelectedIndex();
            String selectedRoomText = (String) availableRoomsBox.getSelectedItem();

            if (userIndex >= 0 && selectedRoomText != null) {

                int roomNumber = Integer.parseInt(
                        selectedRoomText.replaceAll("[^0-9]", "")
                );

                for (Room r : system.getRooms()) {
                    if (r.getRoomNumber() == roomNumber) {
                        system.bookRoom(system.getUsers().get(userIndex), r);
                        break;
                    }
                }

                refresh.run();

            } else {
                JOptionPane.showMessageDialog(frame, "Select user and room first.");
            }
        });

        mainPanel.add(userPanel);
        mainPanel.add(roomPanel);
        mainPanel.add(resPanel);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
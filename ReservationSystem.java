import java.util.ArrayList;
import java.util.List;

public class ReservationSystem {

    private static ReservationSystem instance;

    private List<User> users;
    private List<Room> rooms;

    private ReservationSystem() {
        users = new ArrayList<>();
        rooms = new ArrayList<>();

        // Default Rooms
        rooms.add(new Room(101, "Single"));
        rooms.add(new Room(102, "Deluxe"));
        rooms.add(new Room(103, "Suite"));
    }

    public static ReservationSystem getInstance() {
        if (instance == null) {
            instance = new ReservationSystem();
        }
        return instance;
    }


    public List<User> getUsers() {
        return users;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void addUser(String name) {
        if (!name.isEmpty())
            users.add(new User(name));
    }

    public void deleteUser(User user) {
        if (user.getRoom() != null)
            user.getRoom().freeRoom();
        users.remove(user);
    }

    public boolean addRoom(int number, String type) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == number) {
                return false;
            }
        }
        rooms.add(new Room(number, type));
        return true;
    }

    public void bookRoom(User user, Room room) {
        if (room.isAvailable()) {
            if (user.getRoom() != null) {
                user.getRoom().freeRoom();
            }
            room.bookRoom();
            user.assignRoom(room);
        }
    }
}
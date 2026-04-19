//singleton pattern



import java.util.*;

// SINGLETON PATTERN - Only one instance exists
public class BookingManager {
    private static BookingManager instance;
    private List<Reservation> reservations;
    private List<Room> rooms;
    
    private BookingManager() {
        reservations = new ArrayList<>();
        rooms = new ArrayList<>();
    }
    
    public static synchronized BookingManager getInstance() {
        if (instance == null) {
            instance = new BookingManager();
        }
        return instance;
    }
    
    public void addRoom(Room room) { rooms.add(room); }
    public List<Room> getRooms() { return rooms; }
    public void addReservation(Reservation res) { reservations.add(res); }
    public List<Reservation> getReservations() { return reservations; }
    public void removeReservation(Reservation res) { reservations.remove(res); }
}
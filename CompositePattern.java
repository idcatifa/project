import java.util.*;

// COMPOSITE PATTERN - Group rooms into floors
interface RoomComponent {
    String getDescription();
    double getTotalPrice();
    int getTotalRooms();
}

class SingleRoom implements RoomComponent {
    private Room room;
    public SingleRoom(Room room) { this.room = room; }
    public String getDescription() { return room.getDetails(); }
    public double getTotalPrice() { return room.getPrice(); }
    public int getTotalRooms() { return 1; }
}

class FloorComposite implements RoomComponent {
    private String floorName;
    private List<RoomComponent> rooms = new ArrayList<>();
    
    public FloorComposite(String floorName) { this.floorName = floorName; }
    public void addRoom(RoomComponent room) { rooms.add(room); }
    public void removeRoom(RoomComponent room) { rooms.remove(room); }
    
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Floor: ").append(floorName).append("\n");
        for (RoomComponent room : rooms) {
            sb.append("  └─ ").append(room.getDescription()).append("\n");
        }
        return sb.toString();
    }
    
    public double getTotalPrice() {
        double total = 0;
        for (RoomComponent room : rooms) total += room.getTotalPrice();
        return total;
    }
    
    public int getTotalRooms() {
        int total = 0;
        for (RoomComponent room : rooms) total += room.getTotalRooms();
        return total;
    }
}
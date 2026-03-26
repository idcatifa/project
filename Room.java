public class Room {
    private int roomNumber;
    private String type;
    private boolean available;

    public Room(int roomNumber, String type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.available = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void bookRoom() {
        available = false;
    }

    public void freeRoom() {
        available = true;
    }

    public String getStatus() {
        return available ? "Available" : "BOOKED";
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " | " + type + " | " + getStatus();
    }
}
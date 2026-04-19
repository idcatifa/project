public class Room {
    private int roomNumber;
    private String roomType;
    private double price;
    private boolean isAvailable;
    private String guestName;

    public Room(int roomNumber, String roomType, double price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isAvailable = true;
        this.guestName = "";
    }

    public int getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return isAvailable; }
    public String getGuestName() { return guestName; }

    public void bookRoom(String guestName) {
        if (isAvailable) {
            this.isAvailable = false;
            this.guestName = guestName;
        }
    }

    public void releaseRoom() {
        this.isAvailable = true;
        this.guestName = "";
    }
    
    @Override
    public String toString() {
        return roomNumber + " (" + roomType + ") - " + (isAvailable ? "Available" : "Booked by " + guestName);
    }
}
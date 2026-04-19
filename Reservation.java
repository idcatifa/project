import java.util.Date;

public class Reservation {
    private String guestName;
    private Room room;
    private Date bookingDate;
    private double totalPrice;

    public Reservation(String guestName, Room room) {
        this.guestName = guestName;
        this.room = room;
        this.bookingDate = new Date();
        this.totalPrice = room.getPrice();
    }

    public String getGuestName() { return guestName; }
    public Room getRoom() { return room; }
    public Date getBookingDate() { return bookingDate; }
    public double getTotalPrice() { return totalPrice; }

    @Override
    public String toString() {
        return guestName + " → Room " + room.getRoomNumber() + 
               " (" + room.getRoomType() + ") - Tk " + totalPrice;
    }
}
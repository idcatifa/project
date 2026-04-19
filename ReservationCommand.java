// COMMAND PATTERN - Encapsulates booking requests
public interface ReservationCommand {
    void execute();
    void undo();
}

class BookRoomCommand implements ReservationCommand {
    private BookingManager manager;
    private Room room;
    private String guestName;
    private Reservation createdReservation;
    
    public BookRoomCommand(BookingManager manager, Room room, String guestName) {
        this.manager = manager;
        this.room = room;
        this.guestName = guestName;
    }
    
    public void execute() {
        if (room.isAvailable()) {
            room.bookRoom(guestName);
            createdReservation = new Reservation(guestName, room);
            manager.addReservation(createdReservation);
        }
    }
    
    public void undo() {
        if (createdReservation != null) {
            room.releaseRoom();
            manager.removeReservation(createdReservation);
        }
    }
}

class CancelReservationCommand implements ReservationCommand {
    private BookingManager manager;
    private Reservation reservation;
    private Room room;
    
    public CancelReservationCommand(BookingManager manager, Reservation reservation, Room room) {
        this.manager = manager;
        this.reservation = reservation;
        this.room = room;
    }
    
    public void execute() {
        manager.removeReservation(reservation);
        room.releaseRoom();
    }
    
    public void undo() {
        manager.addReservation(reservation);
        room.bookRoom(reservation.getGuestName());
    }
}
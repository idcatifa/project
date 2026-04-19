import java.util.*;

// FACADE PATTERN - Simplified interface for complex booking process
public class RoomFacade {
    private BookingManager manager;
    private PricingStrategy pricingStrategy;
    private ReservationSubject subject;
    private List<ReservationCommand> commandHistory;
    
    public RoomFacade() {
        this.manager = BookingManager.getInstance();
        this.pricingStrategy = new RegularPricing();
        this.subject = new ReservationSubject();
        this.commandHistory = new ArrayList<>();
    }
    
    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
    }
    
    public void attachObserver(Observer observer) {
        subject.attach(observer);
    }
    
    public boolean bookRoomSimple(int roomNumber, String guestName, int nights) {
        for (Room room : manager.getRooms()) {
            if (room.getRoomNumber() == roomNumber && room.isAvailable()) {
                double totalPrice = pricingStrategy.calculatePrice(room.getPrice(), nights);
                ReservationCommand command = new BookRoomCommand(manager, room, guestName);
                command.execute();
                commandHistory.add(command);
                subject.notifyObservers("Room " + roomNumber + " booked by " + guestName);
                return true;
            }
        }
        return false;
    }
    
    public boolean cancelLastBooking() {
        if (!commandHistory.isEmpty()) {
            ReservationCommand lastCommand = commandHistory.remove(commandHistory.size() - 1);
            lastCommand.undo();
            subject.notifyObservers("Last booking cancelled");
            return true;
        }
        return false;
    }
    
    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        for (Room room : manager.getRooms()) {
            if (room.isAvailable()) available.add(room);
        }
        return available;
    }
    
    public List<Reservation> getAllReservations() {
        return manager.getReservations();
    }
}
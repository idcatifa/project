import java.util.*;

// MEMENTO PATTERN - Save and restore reservation states
class ReservationMemento {
    private final List<Reservation> savedReservations;
    private final Date timestamp;
    
    public ReservationMemento(List<Reservation> reservations) {
        this.savedReservations = new ArrayList<>(reservations);
        this.timestamp = new Date();
    }
    
    public List<Reservation> getSavedReservations() { return savedReservations; }
    public Date getTimestamp() { return timestamp; }
}

class ReservationCaretaker {
    private Stack<ReservationMemento> history = new Stack<>();
    
    public void saveState(List<Reservation> reservations) {
        history.push(new ReservationMemento(reservations));
    }
    
    public ReservationMemento undo() {
        if (!history.isEmpty()) {
            return history.pop();
        }
        return null;
    }
    
    public boolean canUndo() { return !history.isEmpty(); }
}
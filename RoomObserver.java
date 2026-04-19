import java.util.*;

// OBSERVER PATTERN - Notify when reservations change
interface Observer {
    void update(String message);
}

interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(String message);
}

class ReservationSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void attach(Observer observer) { observers.add(observer); }
    public void detach(Observer observer) { observers.remove(observer); }
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
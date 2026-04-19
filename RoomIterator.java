import java.util.*;

// ITERATOR PATTERN - Traverse rooms without exposing internal structure
public interface RoomIterator {
    boolean hasNext();
    Room next();
}

class RoomCollection implements Iterable<Room> {
    private List<Room> rooms;
    
    public RoomCollection(List<Room> rooms) { this.rooms = rooms; }
    
    public Iterator<Room> iterator() {
        return new Iterator<Room>() {
            private int index = 0;
            public boolean hasNext() { return index < rooms.size(); }
            public Room next() { return rooms.get(index++); }
        };
    }
    
    public RoomIterator getAvailableRoomsIterator() {
        return new AvailableRoomIterator();
    }
    
    private class AvailableRoomIterator implements RoomIterator {
        private int index = 0;
        public boolean hasNext() {
            while (index < rooms.size() && !rooms.get(index).isAvailable()) index++;
            return index < rooms.size();
        }
        public Room next() { return rooms.get(index++); }
    }
}
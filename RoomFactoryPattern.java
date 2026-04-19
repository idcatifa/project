//factory Pattern

public interface RoomFactoryPattern {
    Room createRoom(int roomNumber);
}

class SingleRoomFactory implements RoomFactoryPattern {
    public Room createRoom(int roomNumber) {
        return new Room(roomNumber, "Single", 4000);
    }
}

class DeluxeRoomFactory implements RoomFactoryPattern {
    public Room createRoom(int roomNumber) {
        return new Room(roomNumber, "Deluxe", 11500);
    }
}

class SuiteRoomFactory implements RoomFactoryPattern {
    public Room createRoom(int roomNumber) {
        return new Room(roomNumber, "Suite", 20000);
    }
}
// Factory Pattern

public class RoomFactory {

    public static Room createRoom(int number, String type) {

        if (type.equalsIgnoreCase("Single")) {
            return new Room(number, "Single");

        } else if (type.equalsIgnoreCase("Deluxe")) {
            return new Room(number, "Deluxe");

        } else if (type.equalsIgnoreCase("Suite")) {
            return new Room(number, "Suite");

        } else {
            System.out.println("Invalid type! Defaulting to Single.");
            return new Room(number, "Single");
        }
    }
}
public class User {
    private String name;
    private Room room;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void assignRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return name;
    }
}
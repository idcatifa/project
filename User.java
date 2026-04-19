public class User {
    private String name;
    private String roomType;
    private String phoneNumber;
    
    public User(String name, String roomType) {
        this.name = name;
        this.roomType = roomType;
        this.phoneNumber = "";
    }
    
    public User(String name, String roomType, String phoneNumber) {
        this.name = name;
        this.roomType = roomType;
        this.phoneNumber = phoneNumber;
    }
    
    public String getName() { return name; }
    public String getRoomType() { return roomType; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setName(String name) { this.name = name; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
}
import java.util.*;

// ADAPTER PATTERN - Converts external data to our system
interface ExternalBookingSystem {
    String getExternalBookings();
}

class LegacySystem implements ExternalBookingSystem {
    public String getExternalBookings() {
        return "John Doe,101|Jane Smith,105|Bob Wilson,110";
    }
}

public class ReservationAdapter {
    public static List<Reservation> convertExternalReservations(ExternalBookingSystem external, List<Room> rooms) {
        String data = external.getExternalBookings();
        List<Reservation> converted = new ArrayList<>();
        String[] bookings = data.split("\\|");
        
        for (String booking : bookings) {
            String[] parts = booking.split(",");
            if (parts.length >= 2) {
                String guestName = parts[0];
                int roomNum = Integer.parseInt(parts[1]);
                
                for (Room room : rooms) {
                    if (room.getRoomNumber() == roomNum && room.isAvailable()) {
                        room.bookRoom(guestName);
                        converted.add(new Reservation(guestName, room));
                        break;
                    }
                }
            }
        }
        return converted;
    }
}
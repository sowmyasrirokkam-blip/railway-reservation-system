import java.util.ArrayList;
import java.util.List;

public class Train {
    private String trainNo, name, source, destination, departureTime;
    private int totalSeats;
    private List<Integer> bookedSeats = new ArrayList<>();  // seat numbers already taken

    public Train(String trainNo, String name, String source,
                 String destination, String departureTime, int totalSeats) {
        this.trainNo = trainNo;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return totalSeats - bookedSeats.size();
    }

    // gives the lowest free seat number and marks it as booked
    public int bookNextSeat() throws NoSeatsAvailableException {
        for (int seat = 1; seat <= totalSeats; seat++) {
            if (!bookedSeats.contains(seat)) {
                bookedSeats.add(seat);
                return seat;
            }
        }
        throw new NoSeatsAvailableException("No seats available on train " + trainNo);
    }

    // used on cancellation, or when loading saved tickets from file
    public void freeSeat(int seat) { bookedSeats.remove(Integer.valueOf(seat)); }
    public void markSeatBooked(int seat) { if (!bookedSeats.contains(seat)) bookedSeats.add(seat); }

    public String getTrainNo() { return trainNo; }
    public String getName() { return name; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public int getTotalSeats() { return totalSeats; }
}
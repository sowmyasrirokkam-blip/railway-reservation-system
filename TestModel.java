public class TestModel {
    public static void main(String[] args) throws Exception {
        Train t = new Train("12627", "Karnataka Express", "Bangalore", "Delhi", "19:20", 2);
        System.out.println("Available: " + t.getAvailableSeats());   // 2
        System.out.println("Seat: " + t.bookNextSeat());              // 1
        System.out.println("Seat: " + t.bookNextSeat());              // 2
        System.out.println("Available: " + t.getAvailableSeats());   // 0
        t.bookNextSeat();   // should throw NoSeatsAvailableException
    }
}
public class TestService {
    public static void main(String[] args) throws Exception {
        ReservationService s = new ReservationService();

        System.out.println("Seats before: " + s.getAvailableSeats("12627"));   // 5

        Ticket t1 = s.bookTicket("12627", "Ravi", 20, "Male", "Sleeper");
        //Ticket t2 = s.bookTicket("12627", "Sita", 22, "Female", "AC");
        System.out.println(s.generateTicketText(t1));
        System.out.println("Seats after booking 2: " + s.getAvailableSeats("12627"));   // 3

        s.cancelTicket(t1.getTicketNo());
        System.out.println("Seats after cancelling 1: " + s.getAvailableSeats("12627")); // 4

        Ticket t3 = s.bookTicket("12627", "Arun", 25, "Male", "Sleeper");
        System.out.println("New ticket got seat: " + t3.getSeatNo());   // 1 (the freed seat is reused)

        try {
            s.cancelTicket(t1.getTicketNo());   // already cancelled
        } catch (TicketNotFoundException e) {
            System.out.println("Error caught: " + e.getMessage());
        }
    }
}
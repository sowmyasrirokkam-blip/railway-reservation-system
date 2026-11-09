public class TestPersist {
    public static void main(String[] args) {
        ReservationService s = new ReservationService();
        System.out.println("Tickets loaded from file: " + s.getBookingHistory().size());
        System.out.println("Seats left on 12627: " + s.getAvailableSeats("12627"));
        for (Ticket t : s.getBookingHistory()) {
            System.out.println(t.getTicketNo() + " " + t.getPassenger().getName()
                    + " seat " + t.getSeatNo() + " " + t.getStatus());
        }
    }
}
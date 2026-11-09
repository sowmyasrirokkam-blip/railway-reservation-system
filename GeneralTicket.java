public class GeneralTicket extends Ticket {
    public GeneralTicket(String ticketNo, String trainNo, Passenger p, int seatNo) {
        super(ticketNo, trainNo, p, seatNo);
    }
    public double calculateFare() { return 150; }
    public String getClassType() { return "General"; }
}
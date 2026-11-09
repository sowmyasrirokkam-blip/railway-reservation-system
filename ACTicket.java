public class ACTicket extends Ticket {
    public ACTicket(String ticketNo, String trainNo, Passenger p, int seatNo) {
        super(ticketNo, trainNo, p, seatNo);
    }
    public double calculateFare() { return 1200; }
    public String getClassType() { return "AC"; }
}
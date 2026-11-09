public class SleeperTicket extends Ticket {
    public SleeperTicket(String ticketNo, String trainNo, Passenger p, int seatNo) {
        super(ticketNo, trainNo, p, seatNo);
    }
    public double calculateFare() { return 500; }
    public String getClassType() { return "Sleeper"; }
}
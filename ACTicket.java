public class ACTicket extends Ticket {
    private String className;
    private double fare;

    public ACTicket(String ticketNo, String trainNo, Passenger p, int seatNo,
                    String className, double fare) {
        super(ticketNo, trainNo, p, seatNo);
        this.className = className;
        this.fare = fare;
    }
    public double calculateFare() { return fare; }
    public String getClassType() { return className; }
}

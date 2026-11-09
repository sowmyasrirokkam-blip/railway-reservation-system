public abstract class Ticket {
    protected String ticketNo, trainNo, status;   // status is "BOOKED" or "CANCELLED"
    protected Passenger passenger;
    protected int seatNo;

    public Ticket(String ticketNo, String trainNo, Passenger passenger, int seatNo) {
        this.ticketNo = ticketNo;
        this.trainNo = trainNo;
        this.passenger = passenger;
        this.seatNo = seatNo;
        this.status = "BOOKED";
    }

    public abstract double calculateFare();   // each subclass decides its own fare
    public abstract String getClassType();
    public double getFare() { return calculateFare(); }

    public String getTicketNo() { return ticketNo; }
    public String getTrainNo() { return trainNo; }
    public Passenger getPassenger() { return passenger; }
    public int getSeatNo() { return seatNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
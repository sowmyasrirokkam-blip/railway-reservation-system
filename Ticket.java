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
    public static final String[] CLASSES = {"General", "Sleeper", "3rd AC", "2nd AC", "1st AC"};

    // one place that decides which Ticket subclass to create
    public static Ticket create(String classType, String ticketNo, String trainNo,
                                Passenger p, int seatNo) {
        switch (classType) {
            case "General": return new GeneralTicket(ticketNo, trainNo, p, seatNo);
            case "3rd AC":
            case "AC":      return new ACTicket(ticketNo, trainNo, p, seatNo, "3rd AC", 1200);
            case "2nd AC":  return new ACTicket(ticketNo, trainNo, p, seatNo, "2nd AC", 1800);
            case "1st AC":  return new ACTicket(ticketNo, trainNo, p, seatNo, "1st AC", 3200);
            default:        return new SleeperTicket(ticketNo, trainNo, p, seatNo);
        }
    }

    public static double fareOf(String classType) {
        return create(classType, "x", "x", null, 0).calculateFare();
    }
}

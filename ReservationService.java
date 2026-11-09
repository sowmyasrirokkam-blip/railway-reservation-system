import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReservationService {

    // Collections: trains stored by train number, tickets stored in a list
    private Map<String, Train> trains = new LinkedHashMap<>();
    private List<Ticket> tickets = new ArrayList<>();
    private int ticketCounter = 1000;   // next ticket will be TKT1001

    public ReservationService() {
        for (Train t : FileManager.loadTrains()) {
            addTrain(t);
        }
        tickets = FileManager.loadTickets();
        for (Ticket t : tickets) {
            // keep the counter ahead of the highest saved ticket number
            int num = Integer.parseInt(t.getTicketNo().substring(3));
            if (num > ticketCounter) ticketCounter = num;
            // re-mark seats of active tickets as taken
            if (t.getStatus().equals("BOOKED")) {
                Train tr = trains.get(t.getTrainNo());
                if (tr != null) tr.markSeatBooked(t.getSeatNo());
            }
        }
    }

    /*private void loadSampleTrains() {
        addTrain(new Train("12627", "Karnataka Express", "Bangalore", "Delhi", "19:20", 5));
        addTrain(new Train("12951", "Mumbai Rajdhani", "Mumbai", "Delhi", "16:35", 5));
        addTrain(new Train("12841", "Coromandel Express", "Kolkata", "Chennai", "14:50", 5));
        addTrain(new Train("12723", "Telangana Express", "Hyderabad", "Delhi", "06:25", 5));
    }*/

    public void addTrain(Train t) {
        trains.put(t.getTrainNo(), t);
    }

    // ---------- 1. Show trains ----------
    public List<Train> getAllTrains() {
        return new ArrayList<>(trains.values());
    }

    public int getAvailableSeats(String trainNo) {
        return findTrain(trainNo).getAvailableSeats();
    }

    // ---------- 2. Book a ticket ----------
    public Ticket bookTicket(String trainNo, String name, int age,
                             String gender, String classType)
            throws NoSeatsAvailableException {

        // validation (stops bad input)
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (age < 1 || age > 120) {
            throw new IllegalArgumentException("Age must be between 1 and 120");
        }

        Train train = findTrain(trainNo);
        int seat = train.bookNextSeat();   // throws NoSeatsAvailableException if full

        ticketCounter++;
        String ticketNo = "TKT" + ticketCounter;
        Passenger p = new Passenger(name.trim().replace(",", " "), age, gender);
        Ticket ticket;
        if (classType.equals("AC")) {
            ticket = new ACTicket(ticketNo, trainNo, p, seat);
        } else {
            ticket = new SleeperTicket(ticketNo, trainNo, p, seat);
        }
        tickets.add(ticket);
        FileManager.saveTickets(tickets);
        FileManager.saveTicketText(ticketNo, generateTicketText(ticket));
        return ticket;
    }

    // ---------- 3. Cancel a ticket ----------
    public void cancelTicket(String ticketNo) throws TicketNotFoundException {
        Ticket ticket = null;
        for (Ticket t : tickets) {
            if (t.getTicketNo().equalsIgnoreCase(ticketNo.trim())) {
                ticket = t;
                break;
            }
        }
        if (ticket == null) {
            throw new TicketNotFoundException("Ticket " + ticketNo + " not found");
        }
        if (ticket.getStatus().equals("CANCELLED")) {
            throw new TicketNotFoundException("Ticket " + ticketNo + " is already cancelled");
        }

        ticket.setStatus("CANCELLED");
        findTrain(ticket.getTrainNo()).freeSeat(ticket.getSeatNo());  
        FileManager.saveTickets(tickets); // seat becomes available again
    }

    // ---------- 4. History and printable ticket ----------
    public List<Ticket> getBookingHistory() {
        return tickets;
    }

    public String generateTicketText(Ticket t) {
        Train tr = findTrain(t.getTrainNo());
        return "===== RAILWAY TICKET =====\n"
             + "Ticket No : " + t.getTicketNo() + "\n"
             + "Train     : " + tr.getTrainNo() + " - " + tr.getName() + "\n"
             + "From      : " + tr.getSource() + "\n"
             + "To        : " + tr.getDestination() + "\n"
             + "Departure : " + tr.getDepartureTime() + "\n"
             + "Passenger : " + t.getPassenger().getName()
                             + " (" + t.getPassenger().getAge() + ", "
                             + t.getPassenger().getGender() + ")\n"
             + "Class     : " + t.getClassType() + "\n"
             + "Seat No   : " + t.getSeatNo() + "\n"
             + "Fare      : Rs. " + t.calculateFare() + "\n"
             + "Status    : " + t.getStatus() + "\n"
             + "==========================";
    }

    // helper: find a train or complain
    private Train findTrain(String trainNo) {
        Train t = trains.get(trainNo);
        if (t == null) {
            throw new IllegalArgumentException("Train " + trainNo + " not found");
        }
        return t;
    }
}
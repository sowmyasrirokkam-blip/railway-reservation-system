import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DIR = "data";
    private static final String TRAINS_FILE = DIR + "/trains.csv";
    private static final String TICKETS_FILE = DIR + "/tickets.csv";

    // ---------- Trains ----------
    public static List<Train> loadTrains() {
        new File(DIR).mkdirs();                       // create data folder if missing
        if (!new File(TRAINS_FILE).exists()) {
            createDefaultTrains();
        }
        List<Train> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TRAINS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                list.add(new Train(p[0], p[1], p[2], p[3], p[4], Integer.parseInt(p[5].trim())));
            }
        } catch (IOException e) {
            System.out.println("Could not read trains: " + e.getMessage());
        }
        return list;
    }

    private static void createDefaultTrains() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRAINS_FILE))) {
            pw.println("12627,Karnataka Express,Bangalore,Delhi,19:20,5");
            pw.println("12951,Mumbai Rajdhani,Mumbai,Delhi,16:35,5");
            pw.println("12841,Coromandel Express,Kolkata,Chennai,14:50,5");
            pw.println("12723,Telangana Express,Hyderabad,Delhi,06:25,5");
            pw.println("12245,Duronto Express,Howrah,Bangalore,08:10,5");
            pw.println("12009,Shatabdi Express,Mumbai,Ahmedabad,06:25,5");
        } catch (IOException e) {
            System.out.println("Could not create trains file: " + e.getMessage());
        }
    }

    // ---------- Tickets ----------
    // line format: ticketNo,trainNo,name,age,gender,seatNo,classType,status
    public static void saveTickets(List<Ticket> tickets) {
        new File(DIR).mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(TICKETS_FILE))) {
            for (Ticket t : tickets) {
                pw.println(t.getTicketNo() + "," + t.getTrainNo() + ","
                        + t.getPassenger().getName() + "," + t.getPassenger().getAge() + ","
                        + t.getPassenger().getGender() + "," + t.getSeatNo() + ","
                        + t.getClassType() + "," + t.getStatus());
            }
        } catch (IOException e) {
            System.out.println("Could not save tickets: " + e.getMessage());
        }
    }

    public static List<Ticket> loadTickets() {
        List<Ticket> list = new ArrayList<>();
        File f = new File(TICKETS_FILE);
        if (!f.exists()) return list;                 // first run: no tickets yet
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                Passenger pass = new Passenger(p[2], Integer.parseInt(p[3]), p[4]);
                int seat = Integer.parseInt(p[5]);
                Ticket t = Ticket.create(p[6], p[0], p[1], pass, seat);
                t.setStatus(p[7]);
                list.add(t);
            }
        } catch (IOException e) {
            System.out.println("Could not read tickets: " + e.getMessage());
        }
        return list;
    }

    // saves a printable ticket as data/tickets/TKT1001.txt
    public static void saveTicketText(String ticketNo, String text) {
        new File(DIR + "/tickets").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(DIR + "/tickets/" + ticketNo + ".txt"))) {
            pw.println(text);
        } catch (IOException e) {
            System.out.println("Could not save ticket text: " + e.getMessage());
        }
    }
}

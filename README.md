# Railway Ticket Reservation and Cancellation System

A desktop application built with Java and JavaFX that simulates railway reservation.

## Features
- View trains with source, destination, departure time and live available seats
- Register passenger details and book tickets (unique ticket numbers like TKT1001)
- Seat capacity check: booking is blocked when a train is full
- Cancel tickets; the seat becomes available again and is reused
- Booking history with status (BOOKED / CANCELLED)
- Printable ticket saved as a .txt file
- Data stored in CSV files (no database), so it persists after closing

## Java Concepts Used
- Classes and objects: Train, Passenger, Ticket
- Inheritance: Person -> Passenger; abstract Ticket -> SleeperTicket, ACTicket
- Collections: Map (trains), List (tickets)
- Exception handling: NoSeatsAvailableException, TicketNotFoundException
- File handling: FileManager reads and writes CSV files
- JavaFX: TabPane, TableView, forms, alert dialogs

## How to Run
1. Install JDK 17+ and the JavaFX SDK
2. Open the folder in VS Code
3. Set the JavaFX path in .vscode/launch.json
4. Run MainApp.java (F5)

## Team
- Member 1 - model classes and service
- Member 2 - file handling
- Member 3 - JavaFX interface
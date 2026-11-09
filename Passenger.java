public class Passenger extends Person {
    private String gender;

    public Passenger(String name, int age, String gender) {
        super(name, age);   // calls the Person constructor
        this.gender = gender;
    }

    public String getGender() { return gender; }
}

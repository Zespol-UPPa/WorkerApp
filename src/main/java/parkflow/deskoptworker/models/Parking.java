package parkflow.deskoptworker.models;


public class Parking {
    private int id;
    private String name;
    private String address;
    private int totalSpaces;
    private int availableSpaces;

    private int freeMinutes;
    private double ratePerMinute;
    private double reservationFee;

    public Parking(int id, String name, String address, int totalSpaces, int availableSpaces,
                   int freeMinutes, double ratePerMinute, double reservationFee) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.totalSpaces = totalSpaces;
        this.availableSpaces = availableSpaces;
        this.freeMinutes = freeMinutes;
        this.ratePerMinute = ratePerMinute;
        this.reservationFee = reservationFee;
    }

    // Konstruktor bez cennika (na wypadek gdyby był osobno)
    public Parking(int id, String name, String address, int totalSpaces, int availableSpaces) {
        this(id, name, address, totalSpaces, availableSpaces, 15, 0.10, 5.0); // domyślne wartości
    }

    // Gettery i settery
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getTotalSpaces() { return totalSpaces; }
    public void setTotalSpaces(int totalSpaces) { this.totalSpaces = totalSpaces; }

    public int getAvailableSpaces() { return availableSpaces; }
    public void setAvailableSpaces(int availableSpaces) { this.availableSpaces = availableSpaces; }

    public int getFreeMinutes() { return freeMinutes; }
    public void setFreeMinutes(int freeMinutes) { this.freeMinutes = freeMinutes; }

    public double getRatePerMinute() { return ratePerMinute; }
    public void setRatePerMinute(double ratePerMinute) { this.ratePerMinute = ratePerMinute; }

    public double getReservationFee() { return reservationFee; }
    public void setReservationFee(double reservationFee) { this.reservationFee = reservationFee; }
}

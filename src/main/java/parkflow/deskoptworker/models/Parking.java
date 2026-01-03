package parkflow.deskoptworker.models;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}

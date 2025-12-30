package parkflow.deskoptworker.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Reservation {
    // Gettery i settery
    private int reservationId;
    private LocalDateTime validUntil;
    private String statusReservation; // "upcoming", "active", "completed", "cancelled"
    private int spotId;
    private int parkingId;
    private int refAccountId;

    // Dane dołączone z innych tabel (dla wyświetlania)
    private String customerName;
    private String licensePlate;
    private String parkingSpotCode;
    private String parkingLocationName;
    private double reservationFee; // z Parking_pricing

    public Reservation() {}

    public Reservation(int reservationId, LocalDateTime validUntil, String statusReservation,
                       int spotId, int parkingId, int refAccountId) {
        this.reservationId = reservationId;
        this.validUntil = validUntil;
        this.statusReservation = statusReservation;
        this.spotId = spotId;
        this.parkingId = parkingId;
        this.refAccountId = refAccountId;
    }

    public String getFormattedId() {
        return String.format("#R%03d", reservationId);
    }

    public String getFormattedDate() {
        if (validUntil == null) return "-";
        return validUntil.toLocalDate().toString();
    }

    public String getFormattedCost() {
        return String.format("%.2f $", reservationFee);
    }
}
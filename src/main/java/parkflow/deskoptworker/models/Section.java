package parkflow.deskoptworker.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a parking section in the Add Parking form
 */
@Setter
@Getter
public class Section {
    // Getters and Setters
    private String prefix;          // A, B, C, etc.
    private int numberOfSpots;      // Number of spots in section
    private int floorLevel;         // Which floor
    private boolean reservable;     // Can be reserved?

    public Section(String prefix, int numberOfSpots, int floorLevel, boolean reservable) {
        this.prefix = prefix;
        this.numberOfSpots = numberOfSpots;
        this.floorLevel = floorLevel;
        this.reservable = reservable;
    }

    @Override
    public String toString() {
        return String.format("Section %s: %d spots on floor %d (reservable: %s)",
                prefix, numberOfSpots, floorLevel, reservable);
    }
}
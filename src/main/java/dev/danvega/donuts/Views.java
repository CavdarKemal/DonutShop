package dev.danvega.donuts;

/**
 * JSON View definitions for controlling serialization of Donut objects.
 * Views are hierarchical - each extends the previous one to include more data.
 */
public class Views {

    /**
     * Summary view: Minimal information for quick listings
     * Includes: type, price
     */
    public interface Summary {}

    /**
     * Public view: Information suitable for public API consumers
     * Includes: Summary + glaze, toppings, isVegan
     */
    public interface Public extends Summary {}

    /**
     * Internal view: Additional details for internal use
     * Includes: Public + calories, bakedAt
     */
    public interface Internal extends Public {}

    /**
     * Admin view: Complete information for administrative purposes
     * Includes: All fields (no restrictions)
     */
    public interface Admin extends Internal {}
}

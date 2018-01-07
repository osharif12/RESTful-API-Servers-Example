package hotelapp;
/** A class that represents a hotel. Stores hotelId, name, address, and averageRating.
 * Implements Comparable - the hotels are compared based on the hotel names. If the names are the same, hotels
 * are compared based on the hotelId.
 * @author okarpenko
 */
public class Hotel implements Comparable<Hotel>{

    private String hId;
    private String name;
    private Address address;
    private double averageRating;

    /**
     * Constructor
     * @param hId - the id of the hotel
     * @param name - the name of the hotel
     * address should be set to null.
     */
    public Hotel(String hId, String name) {
        this.hId = hId;
        this.name = name;
        this.address = null;
    }

    /**
     * Constructor
     * @param hId - the id of the hotel
     * @param name - the name of the hotel
     * @param address - the address of the hotel
     */
    public Hotel(String hId, String name, Address address) {
        this.hId = hId;
        this.name = name;
        this.address = address;
    }

    public String getHotelId(){
        return hId;
    }

    public String getName(){
        return name;
    }

    public Address getAddress(){
        return address;
    }

    public void setAverageRating(double value){
        averageRating = value;
    }

    /** Compare hotels based on the name (alphabetically). May use compareTo method in class String.
     * If the names are the same, compare based on the hotel ids. */
    @Override
    public int compareTo(Hotel o) {
        if(this.name.equals(o.name)){
            return this.hId.compareTo(o.hId);
        }
        else{
            return this.name.compareTo(o.name);
        }
    }

    /**
     * Returns the string representation of the hotel in the following format:
     * hotelName: hotelID
     * streetAddress
     * city, state
     *
     * Example: Travelodge Central San Francisco: 40682
     1707 Market St
     San Francisco, CA
     *
     * Does not include information about the reviews.
     */
    public String toString() {
        String res = "";
        res += name + ": " + hId + System.lineSeparator() + address;

        return res;
    }

}

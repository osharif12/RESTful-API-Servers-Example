package hotelapp;

public class TouristAttraction {
    // FILL IN CODE: add instance variables to store
    // name, rating, address, id
    private String name;
    private double rating;
    private String address;
    private String id;

    /** Constructor for TouristAttraction
     *
     * @param id
     * @param name
     * @param rating
     * @param address
     */
    public TouristAttraction(String id, String name, double rating, String address) {
        // FILL IN CODE
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.address = address;
    }

    // FILL IN CODE: add getters as needed
    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    public String getId(){
        return id;
    }

    public double getRating(){
        return rating;
    }

    /** toString() method
     * @return a String representing this
     * TouristAttraction
     */
    @Override
    public String toString() {
        // FILL IN CODE
        return name + "; " + address;
    }
}

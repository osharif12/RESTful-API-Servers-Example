package hotelapp;

import com.sun.org.apache.regexp.internal.RE;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Class HotelData - a data structure that stores information about hotels and
 * hotel reviews. Allows to quickly lookup a Hotel given the hotel id. (use TreeMap).
 * Allows to efficiently find hotel reviews for a given hotelID (use a TreeMap,
 * where for each hotelId, the value is a TreeSet). Reviews for a
 * given hotel id are sorted by date from most recent to oldest;
 * if the dates are the same, the reviews are sorted by user nickname,
 * and the user nicknames are the same, by the reviewId.
 * You may NOT modify the signatures of methods in the starter code.
 *
 */
public class HotelData {

    // FILL IN CODE:
    // Add two data structures to store hotel data:
    // The first map should allow to lookup a hotel given the hotel id. (use TreeMap).
    // The second map should allow to efficiently find all hotel reviews for a given hotelID (use a TreeMap,
    // where for each hotelId, the value is a TreeSet of Review-s).

    private TreeMap<String, Hotel> hotelsMap;
    private TreeMap<String, TreeSet<Review>> reviewsMap;

    /**
     * Default constructor.
     */
    public HotelData() {
        hotelsMap = new TreeMap<String, Hotel>();
        reviewsMap = new TreeMap<String, TreeSet<Review>>();
    }

    /**
     * Create a Hotel given the parameters, and add it to the appropriate data
     * structure.
     *
     * @param hotelId
     *            - the id of the hotel
     * @param hotelName
     *            - the name of the hotel
     * @param city
     *            - the city where the hotel is located
     * @param state
     *            - the state where the hotel is located.
     * @param streetAddress
     *            - the building number and the street
     * @param lat latitude
     * @param lon longitude
     */
    public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress, double lat,
                         double lon) {
        Address address = new Address(city, state, streetAddress, lat, lon);
        Hotel hotel = new Hotel(hotelId, hotelName, address);

        hotelsMap.put(hotelId, hotel);
    }

    /**
     * Add a new hotel review. Add it to the map (to the TreeSet of reviews for a given key=hotelId).
     *
     * @param hotelId
     *            - the id of the hotel reviewed
     * @param reviewId
     *            - the id of the review
     * @param rating
     *            - integer rating 1-5.
     * @param reviewTitle
     *            - the title of the review
     * @param review
     *            - text of the review
     * @param isRecom
     *            - whether the user recommends it or not
     * @param date
     *            - date of the review in the format yyyy-MM-ddThh:mm:ss, e.g. "2016-06-29T17:50:37"
     * @param username
     *            - the nickname of the user writing the review.
     * @return true if successful, false if unsuccessful because of invalid hotelId, invalid date
     *         or rating. Needs to catch and handle the following exceptions:
     *         ParseException if the date is invalid
     *         InvalidRatingException if the rating is out of range.
     */
    public boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review,
                             boolean isRecom, String date, String username) {
        try{
            Review review1 = new Review(hotelId, reviewId, rating, reviewTitle, review, isRecom, date, username);

            if(reviewsMap.containsKey(hotelId)){ // copy treeset, add to it, remove previous treeset, insert new object
                TreeSet<Review> temp = reviewsMap.remove(hotelId);
                temp.add(review1);
                reviewsMap.put(hotelId, temp);
                return true;
            }
            else{
                TreeSet<Review> temp = new TreeSet<Review>();
                temp.add(review1);
                reviewsMap.put(hotelId, temp);
                return true;
            }
        }
        catch(java.text.ParseException e){
            e.printStackTrace();
        }
        catch(InvalidRatingException e){
            System.out.print("Invalid range");
        }

        return false; // change it as needed
    }

    /**
     * Returns a string representing information about the hotel with the given
     * id, including all the reviews for this hotel separated by --------------------
     * Format of the string:
     * HotelName: hotelId
     * streetAddress
     * city, state
     * --------------------
     * Review by username on date
     * Rating: rating
     * ReviewTitle
     * ReviewText
     * --------------------
     * Review by username on date
     * Rating: rating
     * ReviewTitle
     * ReviewText ...
     *
     * @param hotelId
     * @return - output string.
     */
    public String toString(String hotelId) {
        StringBuilder sb = new StringBuilder();

        if(hotelsMap.containsKey(hotelId)){
            Hotel hotel = hotelsMap.get(hotelId);

            sb.append(hotel.toString());

            if(reviewsMap.get(hotelId) != null){
                TreeSet<Review> reviews = reviewsMap.get(hotelId);
                Iterator<Review> iterator = reviews.iterator();

                while(iterator.hasNext()){
                    Review temp = iterator.next();
                    sb.append(System.lineSeparator() + "--------------------" + System.lineSeparator() + temp.toString());
                }
                sb.append(System.lineSeparator());
            }
            else{
                sb.append(System.lineSeparator());
            }

            return sb.toString();
        }
        else{
            return sb.toString();
        }
    }

    /**
     * Return a list of hotel ids, in alphabetical order of hotelIds
     *
     * @return
     */
    public List<String> getHotels() {
        List<String> hotelIdList = new ArrayList<String>();

        Set<String> hotels = hotelsMap.keySet();
        for(String hotel: hotels){
            hotelIdList.add(hotel);
        }

        return hotelIdList;
    }

    /**
     * Return the average rating for the given hotelId.
     *
     * @param hotelId-
     *            the id of the hotel
     * @return average rating or 0 if no ratings for the hotel
     */
    public double getRating(String hotelId) {
        TreeSet<Review> reviews = reviewsMap.get(hotelId);
        if(reviews == null){
            return 0;
        }

        Iterator<Review> iterator = reviews.iterator();
        double sum = 0;
        int count = 0;

        while(iterator.hasNext()){
            Review temp = iterator.next();
            sum += temp.getRating();
            count++;
        }

        return sum/count;
    }


    /**
     * Read the given json file with information about the hotels (check hotels.json to see the expected format)
     * and load it into the appropriate data structure(s).
     * Do not hardcode the name of the file! the could should work on any json file in the same format.
     * You may use JSONSimple library for parsing a JSON file.
     *
     */
    public void loadHotelInfo(String jsonFilename) {
        // FILL IN CODE (use JSONParser class from JSON Simple library)
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader(jsonFilename));
            JSONArray arr = (JSONArray) obj.get("sr");

            Iterator<JSONObject> iterator = arr.iterator();
            while (iterator.hasNext()) {
                JSONObject res = iterator.next();

                JSONObject ll = (JSONObject) res.get("ll");
                String lat = (String)ll.get("lat");
                String lng = (String)ll.get("lng");
                double dLat = Double.parseDouble(lat);
                double dLng = Double.parseDouble(lng);

                Address address = new Address((String)res.get("ci"), (String)res.get("pr"), (String)res.get("ad"), dLat, dLng);
                Hotel hotel = new Hotel((String)res.get("id"), (String)res.get("f"), address);
                hotelsMap.put((String)res.get("id"), hotel);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file: " + jsonFilename);
        } catch (ParseException e) {
            System.out.println("Can not parse a given json file. ");
        } catch (IOException e) {
            System.out.println("General IO Exception in readJSON");
        }

    }

    /**
     * Find all review files in the given path (including in subfolders and subsubfolders etc),
     * read them, parse them using JSONSimple library, and
     * load review info to the TreeMap that contains a TreeSet of Review-s for each hotel id (you should
     * have defined this instance variable above)
     * @param path
     */
    public void loadReviews(Path path) {

        try (DirectoryStream<Path> filesList = Files.newDirectoryStream(path)) {
            for (Path file : filesList) {
                File f = file.toFile();
                String filename = file.toString();

                if(f.isDirectory()){
                    loadReviews(file);
                }
                else if (filename.contains(".json") && filename.contains("review"))
                    helperLoadRev(filename);
            }
        }
        catch (IOException e){
            System.out.println("Could not print the contents of the following folder: " + path);
        }

        // Calculate average review ratings for each hotel
        Set<String> hotels = hotelsMap.keySet(); // set of hotel ids

        for(String hotelId: hotels){
            double avgRating = getRating(hotelId);
            Hotel hTemp = hotelsMap.get(hotelId);
            hTemp.setAverageRating(avgRating);
        }

    }

    public void helperLoadRev(String filename) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader(filename));
            JSONObject obj1 = (JSONObject) obj.get("reviewDetails");
            JSONObject obj2 = (JSONObject) obj1.get("reviewCollection");

            JSONArray arr = (JSONArray) obj2.get("review");
            Iterator<JSONObject> iterator = arr.iterator();

            while (iterator.hasNext()) {
                JSONObject res = iterator.next();

                String isRec = (String) res.get("isRecommended");
                boolean rec;
                if (isRec.equals("YES"))
                    rec = true;
                else
                    rec = false;

                long value = (long)res.get("ratingOverall");
                boolean add = addReview((String) res.get("hotelId"), (String) res.get("reviewId"), (int)value, (String) res.get("title"),
                        (String) res.get("reviewText"), rec, (String) res.get("reviewSubmissionTime"), (String) res.get("userNickname"));
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Could not find file: " + filename);
        } catch (ParseException e) {
            System.out.println("Can not parse a given json file. ");
        } catch (IOException e) {
            System.out.println("General IO Exception in readJSON");
        }
    }

    /**
     * Save the string representation of the hotel data to the file specified by
     * filename in the following format (see "expectedOutput" in the test folder):
     * an empty line
     * A line of 20 asterisks ********************
     * on the next line information for each hotel, printed
     * in the format described in the toString method of this class.
     *
     * The hotels in the file should be sorted by hotel ids
     *
     * @param filename
     *            - Path specifying where to save the output.
     */
    public void printToFile(Path filename) {

        try (PrintWriter pw = new PrintWriter(filename.toString())) {
            List<String> listOfHotels = getHotels();

            for(String hotelId: listOfHotels){
                pw.println(System.lineSeparator() + "********************");
                pw.print(toString(hotelId));    // prints all hotel names and hotel reviews underneath it
            }

            pw.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void setAvgHotelReview(String hotelId){
        // Calculate average review ratings for each hotel

        Hotel hTemp = hotelsMap.get(hotelId); // Get the hotel
        double avgRating = getRating(hotelId); // find the average rating of that hotel
        hTemp.setAverageRating(avgRating); // set average rating for hotel

    }

    public Hotel getHotel(String hotelId){
        Hotel ret = hotelsMap.get(hotelId);

        return ret;
    }

    public boolean hotelExists(String hotelId){
        return hotelsMap.containsKey(hotelId);
    }

    public String getHotelName(String hotelId){
        Hotel ret = hotelsMap.get(hotelId);

        return ret.getName();
    }

    public String getHotelAddress(String hotelId){
        Hotel ret = hotelsMap.get(hotelId);
        Address address = ret.getAddress();

        return address.getStreetAddress();
    }

    public String getHotelCity(String hotelId){
        Hotel ret = hotelsMap.get(hotelId);
        Address address = ret.getAddress();

        return address.getCity();
    }

    public String getHotelState(String hotelId){
        Hotel ret = hotelsMap.get(hotelId);
        Address address = ret.getAddress();

        return address.getState();
    }

    public double getHotelLat(String hotelId){
        Hotel ret = hotelsMap.get(hotelId);
        Address address = ret.getAddress();

        return address.getLat();
    }

    public double getHotelLon(String hotelId){
        Hotel ret = hotelsMap.get(hotelId);
        Address address = ret.getAddress();

        return address.getLon();
    }

    public TreeSet<Review> getReviews(String hotelId, int number){
        TreeSet<Review> reviews = reviewsMap.get(hotelId);
        TreeSet<Review> ret = new TreeSet<>();
        int count = 0;

        for(Review review: reviews){
            if(count == number)
                break;

            ret.add(review);
            count++;
        }

        return ret;
    }
}
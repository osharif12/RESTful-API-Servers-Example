package webservers;

import hotelapp.Review;
import hotelapp.ThreadSafeHotelData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class ReviewHandler handles get requests that contain "/reviews"
 */
public class ReviewHandler {
    private String path;
    private ThreadSafeHotelData hdata;

    /**
     * Constructor that takes in path and ThreadsafeHotelData as parameters
     * @param path
     * @param hdata
     */
    public ReviewHandler(String path, ThreadSafeHotelData hdata){
        this.path = path;
        this.hdata = hdata;
    }

    /**
     * Method that returns a json object that specific data for this get request
     * @return json object
     */
    public JSONObject processRequest(){
        String hotelId = returnHotelId();   // parses the hotelId out of the path using regex
        //System.out.println(hotelId);
        String num = returnNum();
        JSONObject object = null;

        if(hotelId == null || num == null || !hdata.hotelExists(hotelId)){ // if hotelId is not valid, return json object that states invalid
            object = invalidJSON();
        }
        else{   // if hotelId is valid and num are valid
            int num1 = Integer.valueOf(num);
            object = validJSON(hotelId, num1);
        }

        return object;
    }

    /**
     * Method that returns a hotel id from the path using regex
     * @return String
     */
    public String returnHotelId(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(hotelId=)(.*)&";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(path);

        while(matcher.find()){
            builder.append(matcher.group(2));
        }

        return builder.toString().trim();
    }

    /**
     * Method that returns a number(# of reviews) from the path using regex
     * @return String
     */
    public String returnNum(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(num=)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(path);

        while(matcher.find()){
            builder.append(matcher.group(2));
        }

        return builder.toString();
    }

    /**
     * Helper function for processRequest method, will return json object for valid parameters
     * @param hotelId
     * @return JSONobject
     */
    public JSONObject validJSON(String hotelId, int num){

        TreeSet<Review> reviews = hdata.getReviews(hotelId, num);

        JSONObject object = new JSONObject();   // Create json object
        object.put("success", true);
        object.put("hotelId", hotelId);

        JSONArray array = new JSONArray();      // create json array, build it, then add it to main json object

        for(Review rev: reviews){
            JSONObject obj2 = new JSONObject();

            obj2.put("reviewId", rev.getReviewID());
            obj2.put("title", rev.getReviewTitle());
            obj2.put("user", rev.getUserName());
            obj2.put("reviewText", rev.getReview());

            Date date = rev.getDate();
            int month = date.getMonth();
            StringBuilder a = new StringBuilder();
            String monthString = String.valueOf(month);
            if(monthString.length() == 1){
                a.append("0");
                a.append(monthString);
            }
            String dateStr = date.toString();
            //System.out.println(dateStr);

            int year = date.getYear();
            String yearString = String.valueOf(year);
            yearString = yearString.substring(0, 2);

            String day = dateStr.substring(8, 10);
            String dateFinal = a.toString() + ":" + day + ":" + yearString;

            obj2.put("date", dateFinal);
            array.add(obj2);
        }

        object.put("reviews", array);

        return object;  // return json object
    }

    /**
     * Helper function for processRequest method, will return json object for invalid parameters
     * @return JSONobject
     */
    public JSONObject invalidJSON(){
        JSONObject object = new JSONObject();

        object.put("success", false);
        object.put("hotelId", "invalid");

        return object;
    }
}

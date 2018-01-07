package webservers;

import hotelapp.ThreadSafeHotelData;
import org.json.simple.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class HotelHandler handles get requests that contain "/hotelInfo"
 */
public class HotelHandler {
    private String path;
    private ThreadSafeHotelData hdata;

    /**
     * Constructor that takes in path and ThreadsafeHotelData as parameters
     * @param path
     * @param hdata
     */
    public HotelHandler(String path, ThreadSafeHotelData hdata){
        this.path = path;
        this.hdata = hdata;
        //System.out.println(path);
    }

    /**
     * Method that returns a json object that specific data for this get request
     * @return json object
     */
    public JSONObject processRequest(){
        String hotelId = returnHotelId();   // parses the hotelId out of the path using regex
        JSONObject object = null;

        if(hotelId == null || !hdata.hotelExists(hotelId)){ // if hotelId is not valid, return json object that states invalid
            object = invalidJSON();
        }
        else{   // if hotelId is valid
            object = validJSON(hotelId);
        }

        return object;
    }

    /**
     * Method that returns a hotel id from the path using regex
     * @return String
     */
    public String returnHotelId(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(hotelId=)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(path);

        while(matcher.find()){
            builder.append(matcher.group(2));
        }

        return builder.toString().trim();
    }

    /**
     * Helper function for processRequest method, will return json object for valid parameters
     * @param hotelId
     * @return JSONobject
     */
    public JSONObject validJSON(String hotelId){

        JSONObject object = new JSONObject();
        object.put("success", true);
        object.put("hotelId", hotelId);

        object.put("name", hdata.getHotelName(hotelId));
        object.put("addr", hdata.getHotelAddress(hotelId));
        object.put("city", hdata.getHotelCity(hotelId));
        object.put("state", hdata.getHotelState(hotelId));
        object.put("lat", hdata.getHotelLat(hotelId));
        object.put("lng", hdata.getHotelLon(hotelId));

        return object;
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

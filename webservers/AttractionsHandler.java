package webservers;

import hotelapp.ThreadSafeHotelData;
import org.json.simple.JSONObject;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class AttractionHandler handles get requests that contain "/attractions"
 */
public class AttractionsHandler {
    private String path1;       // the path from the request
    private ThreadSafeHotelData hdata;
    private static final String host = "maps.googleapis.com";
    private static final String path = "/maps/api/place/textsearch/json";
    private static final String key = "&key=AIzaSyAqwmpELzGSWrgrFNTilS0srCZ-neOz9u0";

    /**
     * Constructor that takes in path and ThreadsafeHotelData as parameters
     * @param path1
     * @param hdata
     */
    public AttractionsHandler(String path1, ThreadSafeHotelData hdata){
        this.path1 = path1;
        this.hdata = hdata;
    }

    /**
     * Method that returns a string that specific data for this get request
     * @return string
     */
    public String processRequest(){
        String hotelId = returnHotelId();   // parses the hotelId out of the path using regex
        String radius = returnRadius();

        String object = "";

        if(hotelId == null || radius == null || !hdata.hotelExists(hotelId)){ // if hotelId is not valid, return json object that states invalid
            object = invalidJSON();
        }
        else{   // if hotelId is valid and num are valid
            int radiusMeters = Integer.valueOf(radius)*1609;

            String city = hdata.getHotelCity(hotelId);
            double lat = hdata.getHotelLat(hotelId);
            double lon = hdata.getHotelLon(hotelId);

            boolean hasWhiteSpace = containsWhiteSpace(city);

            StringBuilder builder = new StringBuilder();
            if(hasWhiteSpace){     // replaces whitespace characters in city name with "%20"
                String[] c = city.split(" ");

                for(int i = 0; i < c.length; i++){
                    builder.append(c[i]);
                    if(i != c.length - 1)
                        builder.append("%20");
                }
                city = builder.toString();
            }

            String query = "?query=tourist%20attractions+in+" + city + "&location=" + lat + "," + lon + "&radius=" + radiusMeters + key;

            object = validJSON(query);
            //out.println(jsonResponse);
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
        Matcher matcher = p.matcher(path1);

        while(matcher.find()){
            builder.append(matcher.group(2));
        }

        return builder.toString().trim();
    }

    /**
     * Method that returns a radius from the path using regex
     * @return String
     */
    public String returnRadius(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(radius=)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(path1);

        while(matcher.find()){
            builder.append(matcher.group(2));
        }

        return builder.toString();
    }

    /**
     * Helper function for processRequest method, will return a string for valid parameters
     * @param query
     * @return String
     */
    public String validJSON(String query){
        String ret = "";
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(host, 443);

            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String request = getRequest(host, path + query);
            out.println(request);   // send the get request
            out.flush();

            String line;
            StringBuffer sb = new StringBuffer();

            line = in.readLine();
            while(!line.contains("Connection: close")){ // don't read in the header
                line = in.readLine();
            }

            while ((line = in.readLine()) != null) {
                //System.out.println(line);   // print out json file from Google api server
                sb.append(line + System.lineSeparator());
            }

            ret = sb.toString();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally {
            try {
                // close the streams and the socket
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                out.println("An exception occured while trying to close the streams or the socket: " + e);
            }
        }

        return ret;
    }

    /**
     * Helper function for processRequest method, will return a string for invalid parameters
     * @return string
     */
    public String invalidJSON(){
        JSONObject object = new JSONObject();

        object.put("success", false);
        object.put("hotelId", "invalid");
        object.put("radius", "invalid");

        return object.toString();
    }

    /**
     * Method returns a string representing a get request
     * @param host
     * @param pathResourceQuery
     * @return request
     */
    private static String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator() + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
        return request;
    }

    /**
     * Returns a boolean depending on whether string in parameter has whitespaces
     * @param word
     * @return boolean
     */
    public boolean containsWhiteSpace(String word){
        boolean hasSpace = false;

        for(int i = 0; i < word.length(); i++){
            if(word.charAt(i) == ' '){
                hasSpace = true;
            }
        }
        return hasSpace;
    }
}

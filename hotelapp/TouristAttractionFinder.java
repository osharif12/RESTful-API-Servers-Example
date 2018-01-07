package hotelapp;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class TouristAttractionFinder {

    private static final String host = "maps.googleapis.com";
    private static final String path = "/maps/api/place/textsearch/json";

    private ThreadSafeHotelData hdata;
    private TreeMap<String, List<TouristAttraction>> attractions; // hotelID, nearby list of tourist attractions
    private TreeMap<String, Hotel> hotels;                  // hotelID, hotel

    // FILL IN CODE: add data structures to store attractions
    // Alternatively, you can store these data structures in ThreadSafeHotelData

    /** Constructor for TouristAttractionFinder
     *
     * @param hdata
     */
    public TouristAttractionFinder(ThreadSafeHotelData hdata) {
        // FILL IN CODE
        this.hdata = hdata;
        attractions = new TreeMap<>();
        hotels = new TreeMap<>();
    }


    /**
     * Creates a secure socket to communicate with googleapi's server that
     * provides Places API, sends a GET request (to find attractions close to
     * the hotel within a given radius), and gets a response as a string.
     * Removes headers from the response string and parses the remaining json to
     * get Attractions info. Adds attractions to the ThreadSafeHotelData.
     *
     * @return A String of the response.
     */
    public void fetchAttractions(int radiusInMiles) {
        // FILL IN CODE
        // This method should call getRequest method

        int radMeters = radiusInMiles*1609;

        URL url;
        PrintWriter out = null;
        BufferedReader in = null;
        SSLSocket socket = null;


        List<String> hotelIds = hdata.getHotels(); // all Hotel id's, for each hotel id get hotel info and do Get request

        for(String hotelId: hotelIds) { // for each hotel in json file, find nearest attractions within radius
            Hotel temp = hdata.getHotel(hotelId);
            Address address = temp.getAddress();
            String city = address.getCity().trim();

            boolean hasWhiteSpace = containsWhiteSpace(city);
            System.out.println(hasWhiteSpace);

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

            double lat = address.getLat();
            double lon = address.getLon();

            List<TouristAttraction> a = new ArrayList<TouristAttraction>();
            attractions.put(hotelId, a);    // put the hotelID with empty arraylist in data structure
            hotels.put(hotelId, temp); // puts name of hotel with id in a treemap

            String query = "?query=tourist%20attractions+in+" + city + "&location=" + lat + "," + lon + "&radius=" + radMeters + "&key=AIzaSyAqwmpELzGSWrgrFNTilS0srCZ-neOz9u0";

            try {
                url = new URL("https://" + host + path);
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
                    System.out.println(line);   // print out json file from Google api server
                    sb.append(line + System.lineSeparator());
                }

                line = sb.toString();
                String outputfile = "src/jsonOutput1.json";
                printToFile(outputfile, line); // takes the json file from google and outputs it in jsonOutput

                jsonParser(outputfile, hotelId);
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
                    System.out.println("An exception occured while trying to close the streams or the socket: " + e);
                }
            } // end of try-catch-finally block (within for-each loop)
        } // end of for each loop, iterating over hotels

    }

    /**
     * GetRequest is function that takes host and path to make getrequest to send
     * @param host
     * @param pathResourceQuery
     * @return a string representation of the request
     */
    private static String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator() + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
        return request;
    }

    /**
     * This function prints the input stream in json format to json file
     * @param outputFile
     * @param jsonString
     */
    public void printToFile(String outputFile, String jsonString) {
        Path outputPath = Paths.get(outputFile);

        try (PrintWriter pw = new PrintWriter(outputPath.toString())) {
            pw.println(jsonString);
            pw.flush();
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    /**
     * This function parses a given json file and puts in in the proper data structure corresponding
     * to hotel id
     * @param filename
     * @param hotelId
     */
    public void jsonParser(String filename, String hotelId){

        JSONParser parser = new JSONParser();

        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader(filename));

            JSONArray arr = (JSONArray) obj.get("results");
            Iterator<JSONObject> iterator = arr.iterator();

            while (iterator.hasNext()) {
                JSONObject res = iterator.next();

                String id = (String) res.get("id");
                String name = (String) res.get("name");

                Number r = (Number) res.get("rating");
                if(r == null)
                    r = 0.0;
                double rating = r.doubleValue();

                //double rating = (double) res.get("rating");
                String address = (String) res.get("formatted_address");

                TouristAttraction temp = new TouristAttraction(id, name, rating, address); // create touristattraction object

                List<TouristAttraction> touristList = attractions.remove(hotelId); // gets a list of touristAttractions, deletes the attractions object, makes another
                touristList.add(temp);
                attractions.put(hotelId, touristList);
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
     * checks if string has whitespace
     * @param word
     * @return
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

    /** Print attractions near the hotels to a file.
     * The format is described in the lab description.
     *
     * @param filename
     */
    public void printAttractions(Path filename) {
        try (PrintWriter pw = new PrintWriter(filename.toString())) {

            Set<String> hotelIds = attractions.keySet(); // get all hotel Id's

            for(String hId: hotelIds){
                Hotel hotel = hotels.get(hId);   // get corresponding hotel for hotel id
                List<TouristAttraction> tList = attractions.get(hId);   // get corresponding list of tourist attractions for hotelId

                pw.println("Attractions near " + hotel.getName() + ", " + hId);
                for(TouristAttraction tAttraction: tList){
                    pw.println(tAttraction.toString() + System.lineSeparator());
                }
                pw.println("++++++++++++++++++++");
                pw.flush();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    // FILL IN CODE: add other helper methods as needed

}

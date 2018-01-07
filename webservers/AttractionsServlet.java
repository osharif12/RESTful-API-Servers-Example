package webservers;

import java.io.*;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import hotelapp.ThreadSafeHotelData;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;

/**
 * This class is a servlet that handles get requests that contain "/attractions".
 */
public class AttractionsServlet extends HttpServlet {
    private ThreadSafeHotelData hdata;
    private static final String host = "maps.googleapis.com";
    private static final String path = "/maps/api/place/textsearch/json";
    private static final String key = "&key=AIzaSyAqwmpELzGSWrgrFNTilS0srCZ-neOz9u0";

    public AttractionsServlet(ThreadSafeHotelData hdata){
        this.hdata = hdata;
    }

    /**
     * This doGet method processes the get request that the client sends with parameters hotelId and radius
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter(); // going to print entire json file on the page

        String hotelId = request.getParameter("hotelId");
        String radius = request.getParameter("radius");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        radius = StringEscapeUtils.escapeHtml4(radius);

        if(hotelId == null || radius == null || !hdata.hotelExists(hotelId)){
            JSONObject object = invalidJSON();
            out.println(object);
        }
        else{
            int radiusInMiles = Integer.valueOf(radius);
            int radMeters = radiusInMiles*1609;

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

            String query = "?query=tourist%20attractions+in+" + city + "&location=" + lat + "," + lon + "&radius=" + radMeters + key;

            String jsonResponse = validJSON(query);
            out.println(jsonResponse);
        }

    }

    /**
     * Method returns a string representing a json file that has specific data.
     * @param query
     * @return string representing json file
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
     * Method returns string representing json file that marks get request as invalid
     * @return string representing json file
     */
    public JSONObject invalidJSON(){
        JSONObject object = new JSONObject();

        object.put("success", false);
        object.put("hotelId", "invalid");
        object.put("radius", "invalid");

        return object;
    }

    /**
     * Method that takes host and path to create a get request from
     * @param host
     * @param pathResourceQuery
     * @return String
     */
    private static String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator() + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
        return request;
    }

    /**
     * Checks if a string has any whitespace
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

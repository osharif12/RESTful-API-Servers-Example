package webservers;

import java.io.IOException;
import java.io.PrintWriter;
import hotelapp.ThreadSafeHotelData;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;


/**
 * This class is a servlet that handles get requests that contain "/hotelInfo".
 */
public class HotelInfoServlet extends HttpServlet{
    private ThreadSafeHotelData hdata;

    public HotelInfoServlet(ThreadSafeHotelData hdata){
        this.hdata = hdata;
    }

    /**
     * This doGet method processes the get request that the client sends with parameters hotelId
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //System.out.println();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter(); // going to print entire json file on the page

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);

        if(hotelId == null || !hdata.hotelExists(hotelId)){ // if hotelId is invalid
            JSONObject object = invalidJSON();
            out.println(object);
        }
        else{ // if hotelId is valid
            JSONObject object = validJSON(hotelId);
            out.println(object);
        }

    }

    /**
     * Method returns a JSONObject representing a json file that has specific data.
     * @param hotelId
     * @return string representing json file
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
     * Method returns string representing json file that marks get request as invalid
     * @return string representing json file
     */
    public JSONObject invalidJSON(){
        JSONObject object = new JSONObject();

        object.put("success", false);
        object.put("hotelId", "invalid");

        return object;
    }

}

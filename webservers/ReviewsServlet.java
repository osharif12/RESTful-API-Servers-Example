package webservers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import hotelapp.Review;
import hotelapp.ThreadSafeHotelData;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This class is a servlet that handles get requests that contain "/reviews".
 */
public class ReviewsServlet extends HttpServlet{
    private ThreadSafeHotelData hdata;

    public ReviewsServlet(ThreadSafeHotelData hdata){
        this.hdata = hdata;
    }

    /**
     * This doGet method processes the get request that the client sends with parameters hotelId and num
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

        // The two parameters hotelId and num for reviews API endpoint
        String hotelId = request.getParameter("hotelId");
        String num = request.getParameter("num");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        num = StringEscapeUtils.escapeHtml4(num);

        if(hotelId == null || num == null || !hdata.hotelExists(hotelId)){ // if hotelId and num invalid
            JSONObject object = invalidJSON();
            out.println(object);
        }
        else{ // if hotelId is valid
            int num1 = Integer.valueOf(num);

            JSONObject object = validJSON(hotelId, num1);
            out.println(object);
        }

    }

    /**
     * Method returns a JSONObject representing a json file that has specific data.
     * @param hotelId
     * @return string representing json file
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

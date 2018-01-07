package webservers;

import hotelapp.HotelDataBuilder;
import hotelapp.ThreadSafeHotelData;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;
import java.nio.file.*;

/**
 * This class is a webserver that uses jetty/servlets
 */
public class JettyServer {
    public static final int PORT = 7050;
    public static final int THREADS = 4;

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);

        ServletHandler handler = new ServletHandler();

        // Load all the hotel info and reviews from the file
        ThreadSafeHotelData hData = new ThreadSafeHotelData();
        HotelDataBuilder builder = new HotelDataBuilder(hData, THREADS);
        //builder.loadHotelInfo("omar"+ File.separator + "input" + File.separator + "hotels.json");
        //builder.loadReviews(Paths.get("omar"+ File.separator + "input" + File.separator + "reviews"));
        builder.loadHotelInfo("input" + File.separator + "hotels.json");
        builder.loadReviews(Paths.get("input" + File.separator + "reviews"));

        // Each servlet has a ThreadSafeHotelData object passed to it
        handler.addServletWithMapping(new ServletHolder(new HotelInfoServlet(hData)), "/hotelInfo");
        handler.addServletWithMapping(new ServletHolder(new ReviewsServlet(hData)), "/reviews");
        handler.addServletWithMapping(new ServletHolder(new AttractionsServlet(hData)), "/attractions");

        server.setHandler(handler);
        server.start();
        server.join();
    }
}

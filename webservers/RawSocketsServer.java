package webservers;

import hotelapp.HotelDataBuilder;
import hotelapp.ThreadSafeHotelData;
import org.json.simple.JSONObject;

import java.nio.file.Paths;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;

/**
 * Class RawSocketsServer uses raw sockets to provide restful API's to communicate with clients
 */
public class RawSocketsServer {
    public static final int PORT = 7000;
    public static final int THREADS = 4;
    private volatile boolean isShutdown = false;

    private static Requests requests;
    private static ThreadSafeHotelData hData;
    private static HotelDataBuilder builder;

    public static void main(String[] args) throws IOException {
        requests = new Requests();
        hData = new ThreadSafeHotelData();
        builder = new HotelDataBuilder(hData, THREADS);

        builder.loadHotelInfo("input" + File.separator + "hotels.json");
        builder.loadReviews(Paths.get("input" + File.separator + "reviews"));

        new RawSocketsServer().welcomingServer();

    }

    /**
     * This is a runnable method that creates a welcoming socket to accept client request. It then calls the
     * class Client to send the request to the server.
     */
    public void welcomingServer() {
        final ExecutorService threads = Executors.newFixedThreadPool(THREADS);

        Runnable serverTask = new Runnable() {

            @Override
            public void run() {
                try {
                    ServerSocket welcomingSocket = new ServerSocket(PORT);
                    while (!isShutdown) {
                        Socket clientSocket = welcomingSocket.accept();
                        threads.submit(new Client(clientSocket));
                    }
                    if (isShutdown) {
                        welcomingSocket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    /**
     * This class Client implements runnable and has a runnable method which takes client connection socket and sends
     * get request to the server
     */
    private class Client implements Runnable {
        private final Socket connectionSocket;
        StringBuilder builder = new StringBuilder();    // appends the entire request
        String input = "";  // string that will represent final request which includes all lines for parsing

        private Client(Socket connectionSocket) {
            this.connectionSocket = connectionSocket;
        }

        /**
         * Runnable method that opens input/output streams, takes in response, parses response, and sends back proper
         * response.
         */
        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                PrintWriter out = new PrintWriter(connectionSocket.getOutputStream());
                boolean counter = true;
                while (counter) {
                    input = reader.readLine();
                    builder.append(input + System.lineSeparator());

                    if(input == null || input.length() < 3)
                        counter = false;
                } // end of while loop

                input = builder.toString();     // this is whole get request sent by client

                HttpRequest r1 = new HttpRequest(input); // whatever type of request the client sends, parse it, and save it in treemap in Requests class
                String p1 = r1.getPath();
                requests.addToRequestsMap(p1, r1);

                //System.out.println("\nThe get request that is read in: ");
                //System.out.println(input);

                if(!input.contains("favicon") && input.startsWith("GET")){ // and starts with GET?
                    HttpRequest parser = new HttpRequest(input);    // send the input to HttpRequest class
                    String path = parser.getPath(); // this will return the path from the first line of get request
                    //System.out.println(path);

                    if(path.contains("/hotelInfo")){
                        // then create HotelHandler object, passing hdata and path(with parameters)
                        // have HotelHandler object return an object(jsonObject) to send back to the socket through printwriter

                        HotelHandler handler = new HotelHandler(path, hData);
                        JSONObject object = handler.processRequest();
                        out.print(httpRequest());
                        out.print(object);
                    }
                    else if(path.contains("/reviews")){
                        ReviewHandler handler = new ReviewHandler(path, hData);
                        JSONObject object = handler.processRequest();

                        out.print(httpRequest());
                        out.print(object);
                    }
                    else if(path.contains("/attractions")){
                        AttractionsHandler handler = new AttractionsHandler(path, hData);
                        String object = handler.processRequest();

                        out.print(httpRequest());
                        out.print(object);
                    }
                    else{   // if the client is sending a GET request but not asking for hotel info, reviews, or attractions
                        // method not found 404
                        out.print(methodNotFound());
                    }
                }
                else{   // if the client is sending another type of request like a POST request
                    // method not allowed 405
                    out.print(methodNotAllowed());
                }

                out.close();
                reader.close();

            } catch (IOException e) {
                System.out.println(e);
            } finally {
                try {
                    if (connectionSocket != null)
                        connectionSocket.close();
                } catch (IOException e) {
                    System.out.println("Can't close the socket : " + e);
                }
            }
        } // end of run method
    }   // end of Client class

    /**
     * This method returns a string specifying an http response, it has the protocol version and status code
     * (200) for okay
     * @return http response
     */
    public String httpRequest(){
        return "HTTP/1.1 200" + System.lineSeparator() +
                "Content-Type: application/json" + System.lineSeparator() +
                "Connection: close" + System.lineSeparator() +
                System.lineSeparator();
    }

    /**
     * This method returns a string specifying an http response, in particular a method not found response. This is
     * specified with status code 404
     * @return string method not found response
     */
    public String methodNotFound(){
        return "HTTP/1.1 404" + System.lineSeparator() +
                "Connection: close" + System.lineSeparator() +
                System.lineSeparator();
    }

    /**
     * This method returns a string specifying an http response, in particular a method not allowed response. This is
     * specified with status code 405
     * @return string method not allowed response
     */
    public String methodNotAllowed(){
        return "HTTP/1.1 405" + System.lineSeparator() +
                "Connection: close" + System.lineSeparator() +
                System.lineSeparator();
    }
}

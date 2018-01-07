package hotelapp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/** Class HotelDataBuilder. Loads hotel info from input files to ThreadSafeHotelData (using multithreading). */
public class HotelDataBuilder {

    private ThreadSafeHotelData hdata; // the "big" ThreadSafeHotelData that will contain all hotel and reviews info
    private ExecutorService executor;
    // FILL IN CODE: add other instance variables as needed

    /** Constructor for class HotelDataBuilder.
     *  @param data */
    public HotelDataBuilder(ThreadSafeHotelData data) {
        hdata = data;
        executor = Executors.newFixedThreadPool(1);
    }

    /** Constructor for class HotelDataBuilder that takes ThreadSafeHotelData and
     * the number of threads to create as a parameter.
     * @param data
     * @param numThreads
     */
    public HotelDataBuilder(ThreadSafeHotelData data, int numThreads) {
        hdata = data;
        executor = Executors.newFixedThreadPool(numThreads);
    }


    /**
     * Read the json file with information about the hotels and load it into the
     * appropriate data structure(s).
     * @param jsonFilename
     */
    public void loadHotelInfo(String jsonFilename) {
        // FILL IN CODE (from lab 1)

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

                hdata.addHotel((String)res.get("id"), (String)res.get("f"), (String)res.get("ci"), (String)res.get("pr"), (String)res.get("ad"),
                        dLat, dLng);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file: " + jsonFilename);
        } catch (ParseException e) {
            System.out.println("Can not parse a given json file. ");
        } catch (IOException e) {
            System.out.println("General IO Exception in readJSON");
        }

    }

    /** Loads reviews from json files. Recursively processes subfolders.
     *  Each json file with reviews should be processed concurrently (you need to create a new runnable job for each
     *  json file that you encounter)
     *  @param dir
     */
    public void loadReviews(Path dir) {

        try (DirectoryStream<Path> filesList = Files.newDirectoryStream(dir)) {
            for (Path file : filesList) {
                File f = file.toFile();
                String filename = file.toString();

                if(f.isDirectory()){
                    loadReviews(file); // if folder, keep looking for json file
                }
                else if (filename.contains(".json") && filename.contains("review")) { // if json file, create new runnable job that will parse file, create reviews, and add to hdata
                    executor.submit(new jsonRunnable(filename));  // creating new runnable job for each json file
                    //jsonRunnable temp = new jsonRunnable(filename);
                    //executor.submit(temp);
                }
            }
        }
        catch (IOException e){
            System.out.println("Could not print the contents of the following folder: " + dir);
        }

        // Calculate average review ratings for each hotel
        List<String> hotels = hdata.getHotels(); // returns a list of Hotel Id's
        for(String id: hotels){
            hdata.setAvgHotelReview(id); // This one function finds hotel given id, calculates avg reviews for hotel, and sets it
        }
    }

    /** Prints all hotel info to the file. Calls hdata's printToFile method. */
    public void printToFile(Path filename) {
        executor.shutdown(); // Initiates an orderly shutdown, no new tasks accepted but previous ones will be completed
        try{
            executor.awaitTermination(1, TimeUnit.MINUTES); // Blocks until all tasks have completed execution after a shutdown request,
        }															// or the timeout occurs, or the current thread is interrupted
        catch (InterruptedException e){
            e.printStackTrace();
        }

        hdata.printToFile(filename);
    }

    /**
     * A class that implements runnable interface. This class parses a given json file and adds review into proper data structure.
     */
    public class jsonRunnable implements Runnable{
        String filename;

        public jsonRunnable(String file){
            filename = file;
        } // constructor that passes name of file to runnable object

        @Override
        public void run() {
            // parse json review file, add reviews to threadSafeHotelData
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

                    boolean add = hdata.addReview((String) res.get("hotelId"), (String) res.get("reviewId"), (int)value, (String) res.get("title"), // adds review to threadsafe hotel data
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
    }

    // FILL IN CODE: add an inner class and other methods as needed
    // Note: You need to have an inner class that implements Runnable and parses each json file with reviews

}
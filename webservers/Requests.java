package webservers;

import java.util.TreeMap;

/**
 * Class Requests has a TreeMap which stores every request sent by the client, the treemap key is the path
 * and the object is an HttpRequest class where the most common fields of the request header can be accessed.
 */

public class Requests {
    private TreeMap<String, HttpRequest> requestsMap;  // key is path, object is Requests object

    /**
     * Constructor which doesn't take in any values
     */
    public Requests(){
        requestsMap = new TreeMap<>();
    }

    /**
     * Adds to the treeset of requests, takes in path and HttpRequest object
     * @param path
     * @param object
     */
    public void addToRequestsMap(String path, HttpRequest object){
        requestsMap.put(path, object);
    }

}

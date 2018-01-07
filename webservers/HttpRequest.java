package webservers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class HttpRequest captures the entire request sent by the client to the server and parses it
 */
public class HttpRequest {

    private String host;
    private String userAgent;
    private String accept;
    private String acceptCharSet;
    private String acceptEncoding;
    private String acceptLanguage;
    private String cookie;
    private String connection;
    private String cacheControl;
    private String upgrade;

    private String request; // entire request sent

    /**
     * Constructor which takes string representing entire client rquest as parameter
     * @param request
     */
    public HttpRequest(String request){ // Constructor which gets all the info sent by client socket to welcoming server
        this.request = request;

        host = getHost();
        userAgent = getUserAgent();
        accept = getAccept();
        acceptLanguage = getAcceptLanguage();
        acceptEncoding = getAcceptEncoding();
        acceptCharSet = getAcceptCharset();
        cookie = getConnection();
        connection = getConnection();
        upgrade = getUpgradeRequests();
        cacheControl = getCacheControl();
    }

    /**
     * Method that gets the path from client response and returns it as a string
     * @return String
     */
    public String getPath(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(GET)(.*?)HTTP";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }

    public String getHost(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(Host:)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }

    public String getUserAgent(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(User-Agent:)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }

    public String getAccept(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(Accept:)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }

    public String getAcceptLanguage(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(Accept-Language:)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }

    public String getAcceptEncoding(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(Accept-Encoding:)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }
    public String getAcceptCharset(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(Accept-Charset:)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }

    public String getCookie(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(Cookie:)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }

    public String getConnection(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(Connection:)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }

    public String getUpgradeRequests(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(Upgrade-Insecure-Requests:)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }

    public String getCacheControl(){
        StringBuilder builder = new StringBuilder();
        String regex = ".*?(Cache-Control:)(.*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(request);

        while(matcher.find()){
            builder.append(matcher.group(2));   // gets the path in the first line of getRequest
        }

        return builder.toString().trim();   // returns string without trailing whitespace
    }

}

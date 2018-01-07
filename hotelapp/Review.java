package hotelapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** The class stores information about one hotel review.
 *  Stores the id of the review, the id of the corresponding hotel, the rating,
 *  the title of the review, the text of the review, the date when the review was posted in
 *  the following format: yyyy-MM-ddThh:mm:ss
 *  Also stores the nickname of the user who submitted this review,
 *  and whether the user recommends the hotel to others or not.
 *  Implements Comparable - reviews should be compared based on the date
 *  (more recent review is considered "less" that the older one).
 *  If the dates are the same, compares reviews based on the user nicknames alphabetically.
 *  If the user nicknames are the same, compares based on the review id.
 * @author okarpenko
 *
 */
public class Review implements Comparable<Review> {

    public static final double MINREVIEW = 1;
    public static final double MAXREVIEW = 5;

    // FILL IN CODE: add instance variables:
    // the id of the review, the id of the corresponding hotel, the rating,
    // the title of the review, the text of the review, the date when the review was posted, whether it is recommended or not

    private String reviewId;
    private String hotelId;
    private int rating;
    private String reviewTitle;
    private String review;
    private boolean isRecom;
    private Date date;
    private String username;

    /**
     * Default constructor.
     */
    public Review() {
        reviewId = "";
        hotelId = "";
        rating = 1;
        reviewTitle = "";
        review = "";
        username = "";
    }

    /**
     * Constructor
     *
     * @param hotelId
     *            - the id of the hotel that is being reviewed
     * @param reviewId
     *            = the id of the review
     * By default, the hotel is recommended.
     */
    public Review(String hotelId, String reviewId) {
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.isRecom = true;
    }

    /**
     * Constructor
     *
     * @param hotelId
     *            - id of the hotel that is being reviewed
     * @param reviewId
     *            - id of the review
     * @param rating
     *            - integer rating from 1 to 5
     * @param reviewTitle
     *            - the title of the review
     * @param review
     *            - text of the review.
     * @param isRecom
     *            - boolean, whether the user recommends it or not
     * @param date
     *            - date of the review in the format yyyy-MM-ddThh:mm:ss
     * @param username
     *            - the nickname of the user writing the review. If empty, save it as  "Anonymous"
     * @throws ParseException
     *             - If date is not valid.
     * @throws InvalidRatingException
     * 			   - If the rating is out of the correct range from MINREVIEW TO MAXREVIEW
     */
    public Review(String hotelId, String reviewId, int rating, String reviewTitle, String review, boolean isRecom,
                  String date, String username) throws ParseException, InvalidRatingException {
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.reviewTitle = reviewTitle;
        this.review = review;
        this.isRecom = isRecom;
        this.username = username;

        if(rating < MINREVIEW || rating > MAXREVIEW)
            throw new InvalidRatingException("");
        else
            this.rating = rating;

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.date = format.parse(date);
    }

    // FILL IN CODE: add getters for all instance variables
    public String getHotelD(){
        return hotelId;
    }

    public String getReviewID(){
        return reviewId;
    }

    public int getRating(){
        return rating;
    }

    public String getReviewTitle(){ return reviewTitle; }

    public String getReview(){
        return review;
    }

    public boolean getIsRecom(){
        return isRecom;
    }

    public Date getDate(){
        return date;
    }

    public String getUserName(){
        if(username == null || username.isEmpty())
            username = "Anonymous";

        return username;
    }

    /** Compares this review with the review passed as a parameter based on
     *  the dates (more recent date is "less" than older date).
     *  If the dates are equal, it compares reviews based on the user nicknames, alphabetically.
     *  If user nicknames are the same, it compares based on the review ids.
     *  Note that we only care about comparing reviews for the same hotel id.
     *  @param other review to compare this one with
     *  @return
     *  	-1 if this review is "less than" the argument,
     *       0 if equal
     *  	 1 if this review is "greater" than the other one
     */
    @Override
    public int compareTo(Review other) {
        if(this.date.equals(other.date)){
            if(this.username.equals(other.username)){
                return this.reviewId.compareTo(other.reviewId);
            }
            else{
                return this.username.compareTo(other.username);
            }
        }
        else{
            if(this.date.after(other.date))
                return -1;
            else
                return 1;
        }
    }

    /** Return a string representation of this review. Use StringBuilder for efficiency.
     * @return A string in the following format:
    Review by username on date
    Rating: rating
    reviewTitle
    textOfReview
     * Example:
    Review by Ben on Tue Aug 16 18:38:29 PDT 2016
    Rating: 2
    Very bad experience
    Awaken by noises from top floor at 5AM. Lots of mosquitos too.
     * If the username is null or empty, print "Anonymous" instead of the username
     */
    public String toString() {
        StringBuilder sb= new StringBuilder(); // date.toString() returns date in 'Tue Aug 16 18:38:29 PDT 2016' format

        if(username.equals(null) || username.equals(""))
            username = "Anonymous";

        sb.append("Review by " + username + " on " + date.toString() + System.lineSeparator()
                + "Rating: " + rating + System.lineSeparator()
                + reviewTitle + System.lineSeparator() + review);

        return sb.toString();
    }
}
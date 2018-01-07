package hotelapp;

import java.nio.file.Path;
import java.util.*;

import concurrent.ReentrantReadWriteLock;

/**
 * Class ThreadSafeHotelData - extends class HotelData.
 * Thread-safe, uses ReentrantReadWriteLock to synchronize access to all data structures.
 */
public class ThreadSafeHotelData extends HotelData {

	private ReentrantReadWriteLock lock = null;

	/**
	 * Default constructor.
	 */
	public ThreadSafeHotelData() {
		// FILL IN CODE: call parent's constructor and initialize the lock
		super();
		lock = new ReentrantReadWriteLock();
	}


	/**
	 * Overrides addHotel method from HotelData class to make it thread-safe; uses the lock.
	 * Create a Hotel given the parameters, and add it to the appropriate data
	 * structure(s).
	 *
	 * @param hotelId
	 *            - the id of the hotel
	 * @param hotelName
	 *            - the name of the hotel
	 * @param city
	 *            - the city where the hotel is located
	 * @param state
	 *            - the state where the hotel is located.
	 * @param streetAddress
	 *            - the building number and the street
	 * @param lat
	 * @param lon
	 */
	@Override
	public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress, double lat,
						 double lon) {

		lock.lockWrite();
		try{
			super.addHotel(hotelId, hotelName, city, state, streetAddress, lat, lon);
		}
		finally{
			lock.unlockWrite();
		}
	}

	/**
	 * Overrides addReview method from HotelData class to make it thread-safe; uses the lock.
	 *
	 * @param hotelId
	 *            - the id of the hotel reviewed
	 * @param reviewId
	 *            - the id of the review
	 * @param rating
	 *            - integer rating 1-5.
	 * @param reviewTitle
	 *            - the title of the review
	 * @param review
	 *            - text of the review
	 * @param isRecom
	 *            - whether the user recommends it or not
	 * @param date
	 *            - date of the review in the format yyyy-MM-dd, e.g.
	 *            2016-08-29.
	 * @param username
	 *            - the nickname of the user writing the review.
	 * @return true if successful, false if unsuccessful because of invalid date
	 *         or rating. Needs to catch and handle the following exceptions:
	 *         ParseException if the date is invalid InvalidRatingException if
	 *         the rating is out of range
	 */
	@Override
	public boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review,
							 boolean isRecom, String date, String username) {
		boolean reviewAdded;

		lock.lockWrite();
		try {
			reviewAdded = super.addReview(hotelId, reviewId, rating, reviewTitle, review, isRecom, date, username);
		}
		finally {
			lock.unlockWrite();
		}

		return reviewAdded;
	}

	/** Overrides toString method of class HotelData to make it thread-safe.
	 * Returns a string representing information about the hotel with the given
	 * id, including all the reviews for this hotel separated by
	 * -------------------- Format of the string: HoteName: hotelId
	 * streetAddress city, state -------------------- Review by username: rating
	 * ReviewTitle ReviewText -------------------- Review by username: rating
	 * ReviewTitle ReviewText ...
	 *
	 * @param hotelId
	 * @return - output string.
	 */
	@Override
	public String toString(String hotelId) {
		// FILL IN CODE
		StringBuilder sb = new StringBuilder();

		lock.lockRead();
		try{
			sb.append(super.toString(hotelId));
		}
		finally {
			lock.unlockRead();
		}

		return sb.toString();
	}

	/**
	 * Overrides the method printToFile of the parent class to make it thread-safe.
	 * Save the string representation of the hotel data to the file specified by
	 * filename in the following format: an empty line A line of 20 asterisks
	 * ******************** on the next line information for each hotel, printed
	 * in the format described in the toString method of this class.
	 *
	 * The hotels should be sorted by hotel ids
	 *
	 * @param filename
	 *            - Path specifying where to save the output.
	 */
	@Override
	public void printToFile(Path filename) {
		// FILL IN CODE
		lock.lockRead(); // read operation since you are reading from data structures
		try{
			super.printToFile(filename);
		}
		finally {
			lock.unlockRead();
		}
	}

	/**
	 * Overrides a method of the parent class to make it thread-safe.
	 * Return an alphabetized list of the ids of all hotels
	 *
	 * @return
	 */
	@Override
	public List<String> getHotels() {
		List<String> ret;

		lock.lockRead();
		try{
			ret = super.getHotels();
		}
		finally {
			lock.unlockRead();
		}

		return ret;
	}

	/**
	 * Return the average rating for the given hotelId (threadsafe version).
	 *
	 * @param hotelId-
	 *            the id of the hotel
	 * @return average rating or 0 if no ratings for the hotel
	 */
	@Override
	public double getRating(String hotelId) {
		double value;

		lock.lockRead();
		try{
			value = super.getRating(hotelId);
		}
		finally {
			lock.unlockRead();
		}
		return value;
	}

	/**
	 * Sets the avg review for each hotel (threadsafe version)
	 * @param hotelId- id of hotel
	 */
	@Override
	public void setAvgHotelReview(String hotelId){
		// Calculate average review ratings for each hotel
		lock.lockWrite();
		try{
			super.setAvgHotelReview(hotelId);
		}
		finally {
			lock.unlockWrite();
		}
	}

	@Override
	public Hotel getHotel(String hotelId){
		Hotel ret;

		lock.lockRead();
		try{
			ret = super.getHotel(hotelId);
		}
		finally {
			lock.unlockRead();
		}

		return ret;
	}

	@Override
	public boolean hotelExists(String hotelId){
		boolean exists;

		lock.lockRead();
		try {
			exists = super.hotelExists(hotelId);
		}
		finally {
			lock.unlockRead();
		}
		return exists;
	}

	@Override
	public String getHotelName(String hotelId){
		String name;
		lock.lockRead();
		try {
			name = super.getHotelName(hotelId);
		}
		finally {
			lock.unlockRead();
		}

		return name;
	}

	@Override
	public String getHotelAddress(String hotelId){
		String address;
		lock.lockRead();
		try {
			address = super.getHotelAddress(hotelId);
		}
		finally {
			lock.unlockRead();
		}

		return address;
	}

	@Override
	public String getHotelCity(String hotelId){
		String city;

		lock.lockRead();
		try {
			city = super.getHotelCity(hotelId);
		}
		finally {
			lock.unlockRead();
		}

		return city;
	}

	@Override
	public String getHotelState(String hotelId){
		String state;

		lock.lockRead();
		try {
			state = super.getHotelState(hotelId);
		}
		finally {
			lock.unlockRead();
		}
		return state;
	}

	@Override
	public double getHotelLat(String hotelId){
		double lat;
		lock.lockRead();
		try {
			lat = super.getHotelLat(hotelId);
		}
		finally {
			lock.unlockRead();
		}

		return lat;
	}

	@Override
	public double getHotelLon(String hotelId){
		double lon;
		lock.lockRead();
		try {
			lon = super.getHotelLon(hotelId);
		}
		finally {
			lock.unlockRead();
		}

		return lon;
	}

	@Override
	public TreeSet<Review> getReviews(String hotelId, int number){
		TreeSet<Review> ret;

		lock.lockRead();
		try{
			ret = super.getReviews(hotelId, number);
		}
		finally {
			lock.unlockRead();
		}
		return ret;
	}

	// Override other methods as needed
}
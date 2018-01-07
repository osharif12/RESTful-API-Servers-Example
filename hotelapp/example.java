package hotelapp;

/**
 * Created by Omar Sharif on 10/31/2017.
 */
public class example {
    public static void main(String[] args){
        ThreadSafeHotelData hdata = new ThreadSafeHotelData();
        HotelDataBuilder builder = new HotelDataBuilder(hdata);

        System.out.println("WORKING.");
    }
}

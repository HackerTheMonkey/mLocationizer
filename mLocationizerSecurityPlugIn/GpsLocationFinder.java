package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class GpsLocationFinder 
{
	private Context context = null;
	public static final String PROVIDER_DOES_NOT_EXIST = "Provider_Does_Not_Exist";
	public static final String LAST_KNOWN_LOCATION_UNAVAILABLE = "Last_Known_Location_Unavailable";
	public static final String NO_PERMISSION_TO_RETRIEVE_LOCATION = "No_Permission_To_Retrieve_Location";
	/**
	 * 
	 * @param context
	 */
	public GpsLocationFinder(Context context) 
	{
		this.context = context;
	}	
	/**
	 * This method will retrieve the last known location information from the GPS location provider location store, and will return back
	 * the location in the form of Longitude and Latitude in a comma sepearted String, respectively.
	 * @return<br>
	 * <ul>
	 * <li><b>A String object that represent a comma separated list that the first element is the Longitude and the second is the
	 * Latitude of the last known location</b>, or</li>
	 * <li><b>GpsLocationFinder.PROVIDER_DOES_NOT_EXIST</b> if location provider is null or doesn't exist.</li>
	 * <li><b>GpsLocationFinder.LAST_KNOWN_LOCATION_UNAVAILABLE</b> If the retrieved location is null which implies that the last known location
	 * store for that specific location provider is empty</li>
	 * <li><b>GpsLocationFinder.NO_PERMISSION_TO_RETRIEVE_LOCATION</b>, when the security permission to access the GPS
	 * information on the device is not set properly and the application does not have the appropriate permissions set.</li>
	 * </ul>
	 */
	public String getCurrentLocationLongitudeLatitude()
	{
		LocationManager gpsLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);		
        Location lastLocation;
        try
        {
        	lastLocation = gpsLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        catch(IllegalArgumentException e)
        {
        	return GpsLocationFinder.PROVIDER_DOES_NOT_EXIST;
        }
        catch(SecurityException e)
        {
        	return GpsLocationFinder.NO_PERMISSION_TO_RETRIEVE_LOCATION;
        }
        if (lastLocation != null)
        {          
            Double longitude = lastLocation.getLongitude();
            Double latitude = lastLocation.getLatitude();
            String location = longitude.toString() + "," + latitude.toString();
            return location;
        }
        else
        {
        	return GpsLocationFinder.LAST_KNOWN_LOCATION_UNAVAILABLE;
        }
	}	
	/**
	 * This method will return the address information that corresponds to the passed in Longitude and Latitude
	 * @param longitude
	 * @param latitude
	 * @return<br>
	 * <ul>
	 * <li><b>A String object representing the translated address information</b></li>
	 * <li><b>GeoCoderTranslator.LOCATION_UNAVAILABLE</b> when there is no information in the database that correspond to the passed Longitude and Latitude arguments</li>
	 * <li><b>GeoCoderTranslator.NETWORK_CONNECTION_ERROR</b> when encountering network connection problems</li>
	 * <li><b>GeoCoderTranslator.ILLEGAL_LONGITUDE_LATITUDE_ARGS</b> when the passed in Longitude and Latitude arguments are in the wrong format</li>
	 * </ul>
	 */
	public String getCurrentAddressInformation(String longitude, String latitude)
	{
		double lng = Double.parseDouble(longitude);
		double lat = Double.parseDouble(latitude);
		GeoCoderTranslator geoCoderTranslator = new GeoCoderTranslator(context);
		String addressInformation = geoCoderTranslator.getTranslatedAddress(lng, lat);		
		return addressInformation;
	}
}
package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class GeoCoderTranslator 
{	
	private Context context = null;	
	public final static String NETWORK_CONNECTION_ERROR = "Network_Connection_Error";
	public final static String LOCATION_UNAVAILABLE = "Location_Unavailable";	
	public final static String ILLEGAL_LONGITUDE_LATITUDE_ARGS = "Illegal_Longitude_Latitude_Args";
	
	public GeoCoderTranslator(Context context)
	{
		this.context = context;
	}
	/**
	 * This method will accept two parameters of data type double, those parameters represent the Longitude and Latitude to be translated
	 * or converted into a meaningfull address information such as street address, building number, country or post code, etc...	
	 * @param longitude : The double value of the Longitude information.
	 * @param latitude : The double value of the Latitude information.
	 * @return This method will return<br>
	 * <ul>
	 * <li><b>A String object that contains the address information</b>, or</li>
	 * <li><b>GeoCoderTranslator.LOCATION_UNAVAILABLE</b>, where there is no address information held in the GeoCoder database regarding
	 * the passed in Longitude and Latitude arguments.</li>
	 * <li><b>GeoCoderTranslator.NETWORK_CONNECTION_ERROR</b>, when encountering a network IO problem when trying to connect to the database
	 * and getting the translated address information.</li>
	 * <li><b>GeoCoderTranslator.ILLEGAL_LONGITUDE_LATITUDE_ARGS</b>, when the entering the Longitude and Latitude in a wrong format such
	 * as if latitude is less than -90 or greater than 90 or if longitude is less than -180 or greater than 180</li>
	 * </ul>
	 */
	public String getTranslatedAddress(double longitude, double latitude)
	{
		StringBuilder translatedAddress = new StringBuilder();
		String addressString = LOCATION_UNAVAILABLE;				
		Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
		try
		{
			List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
			if (addresses.size() > 0)
			{				
				Address address = addresses.get(0);
				translatedAddress.append(address.getAddressLine(0)).append("\n");
				translatedAddress.append(address.getLocality()).append("\n");
				translatedAddress.append(address.getPostalCode()).append("\n");
				translatedAddress.append(address.getCountryCode());
				addressString = translatedAddress.toString();
			}
			else
			{
				addressString = GeoCoderTranslator.LOCATION_UNAVAILABLE;				
			}
		}
		catch(IOException e)
		{			
			addressString = GeoCoderTranslator.NETWORK_CONNECTION_ERROR;
			return addressString;			
		}
		catch(IllegalArgumentException e)
		{
			addressString = GeoCoderTranslator.ILLEGAL_LONGITUDE_LATITUDE_ARGS;
			return addressString;
		}		
		return addressString;
	}
}
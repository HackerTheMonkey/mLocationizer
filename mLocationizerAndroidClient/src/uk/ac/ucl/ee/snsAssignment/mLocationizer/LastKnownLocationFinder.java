package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.content.Context;
import android.location.LocationManager;

public class LastKnownLocationFinder
{
	private Context context = null;
	public static final String NO_LOCATION_INFORMATION = "No Location Information";
	public static final String UNKNOWN_PROVIDER = "Unknown_Provider";
	public static final String GPS_LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
	public static final String BTS_LOCATION_PROVIDER = "Bts_Location_Provider";
	/**
	 * 
	 * @param context
	 */
	public LastKnownLocationFinder(Context context)
	{
		this.context = context;
	}
	/**
	 * This method will find the last known location of the mobile device giving first priority to the GPS, but if the GPS is not available
	 * then it will rely on the mobile network to get the location information.
	 * @return A string array that contains the Longitude, Latitude, Address Information and the Location Provider Type, either BTS or GPS.
	 * and in case of errors, this method will return an user-friendly error description of why the location-related information is not available
	 * <ul>
	 * <li><b>Longitude & Latitude</b>, they will be either normal values or LastKnownLocationFinder.NO_LOCATION_INFORMATION</li>
	 * <li><b>Address Information</b>, it will be either normal values or LastKnownLocationFinder.NO_LOCATION_INFORMATION</li>
	 * <li><b>Provider Type</b>, it will be either LastKnownLocationFinder.GPS_LOCATION_PROVIDER, LastKnownLocationFinder.BTS_LOCATION_PROVIDER or LastKnownLocationFinder.UNKNOWN_PROVIDER</li>
	 * </ul>
	 */
	public String[] findLastKnownLocation()
	{
		String lastKnownLocation[] = {NO_LOCATION_INFORMATION, NO_LOCATION_INFORMATION, NO_LOCATION_INFORMATION , UNKNOWN_PROVIDER}; // Longitude, Latitude, Address Information Provider Type (GPS|BTS)
		String longitude, latitude, providerType;
		
		GpsLocationFinder gpsLocationFinder = new GpsLocationFinder(context);
		String lngLat = gpsLocationFinder.getCurrentLocationLongitudeLatitude();
		
		if (lngLat != GpsLocationFinder.LAST_KNOWN_LOCATION_UNAVAILABLE && lngLat != GpsLocationFinder.PROVIDER_DOES_NOT_EXIST && lngLat != GpsLocationFinder.NO_PERMISSION_TO_RETRIEVE_LOCATION)
		{
			String tempArray[] = lngLat.split(",");
			longitude = tempArray[0];
			latitude = tempArray[1];
			providerType = GPS_LOCATION_PROVIDER;
			lastKnownLocation[0] = longitude;
			lastKnownLocation[1] = latitude;
			
			String addressInformation = gpsLocationFinder.getCurrentAddressInformation(longitude, latitude);
			if (addressInformation.equals(GeoCoderTranslator.ILLEGAL_LONGITUDE_LATITUDE_ARGS) || addressInformation.equals(GeoCoderTranslator.LOCATION_UNAVAILABLE))
			{
				addressInformation = "Location Unavailable";
			}
			else if (addressInformation.equals(GeoCoderTranslator.NETWORK_CONNECTION_ERROR))
			{
				addressInformation = "Location information not available due to a network connection error";
			}
			lastKnownLocation[2] = addressInformation;
			
			lastKnownLocation[3] = providerType;
			return lastKnownLocation;
		}
		
		NetworkInformationRetriever networkInformationRetriever = new NetworkInformationRetriever(context);
		String[] cellLocation = networkInformationRetriever.getCellLocation();
		if (cellLocation[0] != null && cellLocation[0] != NetworkInformationRetriever.NETWORK_SIGNAL_UNAVAILABLE)
		{
			lastKnownLocation[0] = cellLocation[0];
			lastKnownLocation[1] = cellLocation[1];
			
			String addressInformation = networkInformationRetriever.getCellLocationAddress(Double.parseDouble(cellLocation[0]), Double.parseDouble(cellLocation[1]));
			if (addressInformation.equals(GeoCoderTranslator.ILLEGAL_LONGITUDE_LATITUDE_ARGS) || addressInformation.equals(GeoCoderTranslator.LOCATION_UNAVAILABLE))
			{
				addressInformation = "Address Information Unavailable";
			}
			else if (addressInformation.equals(GeoCoderTranslator.NETWORK_CONNECTION_ERROR))
			{
				addressInformation = "Address information not available due to a network connection error";
			}
			lastKnownLocation[2] = addressInformation;
						
			lastKnownLocation[3] = BTS_LOCATION_PROVIDER;
			return lastKnownLocation;
		}
		return lastKnownLocation;
	}
}
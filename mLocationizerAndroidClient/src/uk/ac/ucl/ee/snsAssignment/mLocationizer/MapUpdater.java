package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.widget.TextView;

public class MapUpdater 
{
	public static void updateWithNewLocation(Location location, Context context, MapController mapController, MyPositionOverlay myPositionOverlay)
	{
		Activity activity = (Activity) context;
    	TextView myLocationText;
    	myLocationText = (TextView) activity.findViewById(R.id.myLocationTextView);
    	
    	if (location != null)
    	{
    		Double lat = location.getLatitude();
    		Double lng = location.getLongitude();
    		
    		/*
    		 * Using the Map Controller, center the map
    		 * around the new obtained location
    		 */
    		Double geoLat = lat * 1E6;
    		Double geoLng = lng * 1E6;
    		GeoPoint geoPoint = new GeoPoint(geoLat.intValue(), geoLng.intValue());
    		mapController.animateTo(geoPoint);
    		
    		/*
    		 * Update the MyLocationOverlay with the new location
    		 * information
    		 */
    		myPositionOverlay.setLocation(location);
    		
    		/*
    		 * Set the text of the Text Views to the address corresponding 
    		 * to the newly obtained location information
    		 */
    		GeoCoderTranslator geoCoderTranslator = new GeoCoderTranslator(context);
    		String addressInformation = geoCoderTranslator.getTranslatedAddress(lng, lat);
    		
    		if (addressInformation.equals(GeoCoderTranslator.ILLEGAL_LONGITUDE_LATITUDE_ARGS) || addressInformation.equals(GeoCoderTranslator.LOCATION_UNAVAILABLE))
			{
				addressInformation = "Location Unavailable";
			}
			else if (addressInformation.equals(GeoCoderTranslator.NETWORK_CONNECTION_ERROR))
			{
				addressInformation = "Location information not available due to a network connection error";
			}
    		
    		myLocationText.setText(addressInformation);
    	} 
	}
}

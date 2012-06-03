package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.content.Context;
import com.google.android.maps.*;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GpsLocationChangeListener implements LocationListener {

	
	private Context context = null;
	private MapController mapController = null;
	private MyPositionOverlay myPositionOverlay;
	
	public GpsLocationChangeListener(Context context, MapController mapController, MyPositionOverlay myPositionOverlay)
	{
		this.context = context;
		this.mapController = mapController;
		this.myPositionOverlay = myPositionOverlay;
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{	
		MapUpdater.updateWithNewLocation(location, context, mapController, myPositionOverlay);				
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}	
}

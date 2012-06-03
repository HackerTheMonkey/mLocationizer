package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class ShowMyLocationSubActivity extends MapActivity 
{
	private MapController mapController = null;
	private MyPositionOverlay myPositionOverlay = null;
	private GpsLocationChangeListener gpsLocationChangeListener = null;
	private LocationManager locationManager = null;
	private TelephonyManager telephonyManager = null;
	private BtsHandoverDetector btsHandoverDetecter = null;
	String providerType = null;
	/**
	 * 
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_my_location_menu_layout);								
				
		telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		/*
		 * We need to construct the map and instantiate the map controller that control this map view
		 */
		MapView mapView = (MapView)findViewById(R.id.myLocationMapView);
		mapController = mapView.getController();
		mapView.setStreetView(true);
		mapView.setSatellite(true);
		mapView.setTraffic(true);
		mapController.setZoom(18);
		myPositionOverlay = new MyPositionOverlay();
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(myPositionOverlay);
		mapView.setBuiltInZoomControls(true);
		/*
		 * Get a handle on the Location text view
		 */
		TextView myLocationTextView = (TextView) findViewById(R.id.myLocationTextView);
		/*
		 * We need to get the LastKnownLocation of the device and depending on the
		 * provider type and the obtained information we will register for location change and update 
		 * the MapView accordingly.
		 */
		LastKnownLocationFinder lastKnownLocationFinder = new LastKnownLocationFinder(this);
		String lastKnownLocationArray[] = lastKnownLocationFinder.findLastKnownLocation();
		String longitude = lastKnownLocationArray[0];
		String latitude = lastKnownLocationArray[1];
		String addressInformation = lastKnownLocationArray[2];
		providerType = lastKnownLocationArray[3];
		
		/*
		 * Update the Map with the location information as well as displaying
		 * the address information on the TextView, we will do this only when we get a normal vlaues for 
		 * Longitude and Latitude, otherwise, nothing will be done and the map will not need to be updated
		 */
		if (longitude != LastKnownLocationFinder.NO_LOCATION_INFORMATION && latitude != LastKnownLocationFinder.NO_LOCATION_INFORMATION)
		{
			Double geoLongitude = Double.parseDouble(longitude) * 1E6;
			Double geoLatitude = Double.parseDouble(latitude) * 1E6;
			GeoPoint geoPoint = new GeoPoint(geoLatitude.intValue(), geoLongitude.intValue());
			mapController.animateTo(geoPoint);
			Location location = new Location(LocationManager.GPS_PROVIDER);
			location.setLongitude(Double.parseDouble(longitude));
			location.setLatitude(Double.parseDouble(latitude));
			myPositionOverlay.setLocation(location);
			myLocationTextView.setText(addressInformation);
		}
		else
		{
			/*
			 * Update the TextView to indicate that the location information are not available
			 * and load the map with the default location.
			 */			
			myLocationTextView.setText("No Location Information available.");
		}
		/*
		 * Register a listener for GPS location updates
		 */
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gpsLocationChangeListener = new GpsLocationChangeListener(this, mapController, myPositionOverlay);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationChangeListener);			
		/*
		 * Register a listener for BTS location updates
		 */
		btsHandoverDetecter = new BtsHandoverDetector(this, mapController, myPositionOverlay);
		telephonyManager.listen(btsHandoverDetecter,PhoneStateListener.LISTEN_CELL_LOCATION);		
	}

	/**
	 * This method should return true if the map is supposed to display 
	 * traffic routes information, otherwise it returns false 
	 */
	@Override
	protected boolean isRouteDisplayed() 
	{
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * This method will be called when the Activity will be dismissed by the user, here we will
	 * include the code needed to remove any location change updates requested whether via GPS or
	 * via BTS.
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		locationManager.removeUpdates(gpsLocationChangeListener);
		telephonyManager.listen(btsHandoverDetecter, PhoneStateListener.LISTEN_NONE);
	}
}

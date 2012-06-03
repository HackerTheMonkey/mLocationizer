package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import com.google.android.maps.MapController;
import android.content.Context;
import android.location.Location;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;

public class BtsHandoverDetector extends PhoneStateListener
{
	private Context context = null;
	private MapController mapController = null;
	private MyPositionOverlay myPositionOverlay = null;
	public BtsHandoverDetector(Context context, MapController mapController, MyPositionOverlay myPositionOverlay)
	{
		super();
		this.context = context;
		this.mapController = mapController;
		this.myPositionOverlay = myPositionOverlay;
	}
	/**
	 * This method will be called whenever a handover between the BTSs is detected
	 * which implies that the location of the mobile service BTS has been changed, as such
	 * the location information need to be updated on the map accordingly.
	 */
	public void  onCellLocationChanged  (CellLocation location)
	{
		NetworkInformationRetriever networkInformationRetriever = new NetworkInformationRetriever(context);
		String cellLocationArray[] = networkInformationRetriever.getCellLocation();
		Location cellLocation = new Location(LastKnownLocationFinder.BTS_LOCATION_PROVIDER);
		if (cellLocationArray[0] != null && cellLocationArray[1] != null)
		{
			cellLocation.setLongitude(Double.parseDouble(cellLocationArray[0]));
			cellLocation.setLatitude(Double.parseDouble(cellLocationArray[1]));
			MapUpdater.updateWithNewLocation(cellLocation, context, mapController, myPositionOverlay);
		}
	}	
}

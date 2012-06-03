package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class LocationUpdateBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) 
	{
		Toast.makeText(context, "STARTS", Toast.LENGTH_LONG).show();
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		LocationUpdateManager locationUpdateManager = new LocationUpdateManager(context);
		telephonyManager.listen(locationUpdateManager, PhoneStateListener.LISTEN_CELL_LOCATION);
		/*
		 * update the location information.
		 */
		locationUpdateManager.initiateLocatioUpdate();
	}

}

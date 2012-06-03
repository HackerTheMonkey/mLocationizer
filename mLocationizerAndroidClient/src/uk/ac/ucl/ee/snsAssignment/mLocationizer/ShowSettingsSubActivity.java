package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class ShowSettingsSubActivity extends Activity 
{
	public final static String SERVERIPADDRESS = "SERVERIPADDRESS";
	public final static String PORTNUMBER = "PORTNUMBER";
	public final static String UPDATELOCATION = "UPDATELOCATION";
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		EditText enterServerIPAddressEditText = (EditText)findViewById(R.id.enterServerIPAddressEditText);
		EditText enterPortNumberEditText = (EditText)findViewById(R.id.enterPortNumberEditText);
		RadioButton makeLocationAvailableRadioButton = (RadioButton)findViewById(R.id.makeLocationAvailableRadioButton);
		RadioButton makeLocationUnAvailableRadioButton = (RadioButton)findViewById(R.id.makeLocationUnAvailableRadioButton);
		/*
		 * We need to set the values of the settings GUI controls to the values
		 * storred in the shared prefrences related to them.
		 */
		SharedPreferences serverIPAddressSttings = getSharedPreferences(SERVERIPADDRESS, 0);
		SharedPreferences updateLocationSettings = getSharedPreferences(UPDATELOCATION, 0);
		SharedPreferences portNumberSettings = getSharedPreferences(PORTNUMBER, 0);
		
		String serverIPAddress = serverIPAddressSttings.getString(SERVERIPADDRESS, MainActivity.DEFAULT_SERVER_IP_ADDRESS);
		boolean updateLocation = updateLocationSettings.getBoolean(UPDATELOCATION, MainActivity.DEFAULT_UPDATE_LOCATION);
		int portNumber = portNumberSettings.getInt(PORTNUMBER, MainActivity.DEFAULT_SERVER_PORT_NUMBER);
		/*
		 * Update the GUI controls with the values obtained from the Shared Prefrences
		 */
		enterServerIPAddressEditText.setText(serverIPAddress);
		enterPortNumberEditText.setText(Integer.toString(portNumber));
		makeLocationAvailableRadioButton.setChecked(updateLocation);
		makeLocationUnAvailableRadioButton.setChecked(!updateLocation);
		/*
		 * Register OnClickListener for the Save Settings button
		 */
		Button saveSettingsButton = (Button)findViewById(R.id.saveSettingsButton);
		SaveSettingsButtonOnClickListener saveSettingsButtonOnClickListener = new SaveSettingsButtonOnClickListener(this);
		saveSettingsButton.setOnClickListener(saveSettingsButtonOnClickListener);
		
	}
}

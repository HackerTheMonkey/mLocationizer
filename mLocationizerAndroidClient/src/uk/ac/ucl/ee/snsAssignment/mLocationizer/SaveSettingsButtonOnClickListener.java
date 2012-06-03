/**
 * 
 */
package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * @author hasanein
 *
 */
public class SaveSettingsButtonOnClickListener implements OnClickListener 
{
	private Activity activity = null;
	
	public SaveSettingsButtonOnClickListener(Activity activity)
	{
		this.activity = activity;
	}
	
	@Override
	public void onClick(View v) 
	{			
		/*
		 * Show the progress bar when the user clicks on the button
		 */
		ProgressDialog progressDialog = new ProgressDialog(activity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage("Please wait: the changes you have made are being saved");
		progressDialog.setCancelable(false);
		progressDialog.show();
		/*
		 * Get the values of the GUI controls to be saved to the Shared Prefrences
		 */
		EditText enterServerIPAddressEditText = (EditText)activity.findViewById(R.id.enterServerIPAddressEditText);
		EditText enterPortNumberEditText = (EditText)activity.findViewById(R.id.enterPortNumberEditText);
		RadioButton makeLocationAvailableRadioButton = (RadioButton)activity.findViewById(R.id.makeLocationAvailableRadioButton);		
		/*
		 * Save the values to the shared prefrences		
		 */
		SharedPreferences serverIPAddressSttings = activity.getSharedPreferences(ShowSettingsSubActivity.SERVERIPADDRESS, 0);
		SharedPreferences updateLocationSettings = activity.getSharedPreferences(ShowSettingsSubActivity.UPDATELOCATION, 0);
		SharedPreferences portNumberSettings = activity.getSharedPreferences(ShowSettingsSubActivity.PORTNUMBER, 0);
		
		SharedPreferences.Editor serverIPAddressEditor = serverIPAddressSttings.edit();
		SharedPreferences.Editor serverPortNumberEditor = portNumberSettings.edit();
		SharedPreferences.Editor updateLocationEditor = updateLocationSettings.edit();		
		
		serverIPAddressEditor.putString(ShowSettingsSubActivity.SERVERIPADDRESS, enterServerIPAddressEditText.getText().toString());
		serverPortNumberEditor.putInt(ShowSettingsSubActivity.PORTNUMBER,Integer.parseInt(enterPortNumberEditText.getText().toString()));
		updateLocationEditor.putBoolean(ShowSettingsSubActivity.UPDATELOCATION, makeLocationAvailableRadioButton.isChecked());
		serverIPAddressEditor.commit();
		serverPortNumberEditor.commit();
		updateLocationEditor.commit();
		/*
		 * Start a new Thread, pass the ProgressDialog to it in order to dismiss it.
		 */
		ProgressDialogDismisser progressDialogDismisser = new ProgressDialogDismisser(progressDialog,activity);
		Thread thread = new Thread(progressDialogDismisser);
		thread.start();				
	}
}

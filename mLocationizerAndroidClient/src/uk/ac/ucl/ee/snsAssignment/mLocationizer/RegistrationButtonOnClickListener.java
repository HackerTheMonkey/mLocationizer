package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class RegistrationButtonOnClickListener implements OnClickListener 
{
	private Activity activity = null;
	private SharedPreferences settings = null;
	
	public RegistrationButtonOnClickListener(Activity activity, SharedPreferences settings)
	{
		this.activity = activity;
		this.settings = settings;
	}
	
	@Override
	public void onClick(View arg0) 
	{		
		/*
		 * Get the registration details from the GUI
		 */
		String passwordMismatchMessage = "Your entered passwords are not matching, please enter them again";
		String firstName, secondName, dateOfBirth, emailAddress, password, confirmPassword, imei, imsi, msisdn;
		firstName = ((EditText)activity.findViewById(R.id.firstNameEditText)).getText().toString();
		secondName = ((EditText)activity.findViewById(R.id.secondNameEditText)).getText().toString();
		dateOfBirth = ((EditText)activity.findViewById(R.id.dateOfBirthEditText)).getText().toString();
		emailAddress = ((EditText)activity.findViewById(R.id.emailEditText)).getText().toString();
		password = ((EditText)activity.findViewById(R.id.passwordEditText)).getText().toString();
		confirmPassword = ((EditText)activity.findViewById(R.id.confrimPasswordEditText)).getText().toString();
		imei = (new NetworkInformationRetriever(activity)).getImei();		
		msisdn = (new NetworkInformationRetriever(activity)).getMsisdn();
		imsi = (new NetworkInformationRetriever(activity)).getImsi();
		/*
		 * Check if the passwords entered are match, if they don't match inform the user and exit
		 * the method.
		 */
		if (!password.equals(confirmPassword))
		{
			Toast.makeText(activity, passwordMismatchMessage, Toast.LENGTH_LONG).show();
			// Clear the password and confirm password fields.
			((EditText)activity.findViewById(R.id.passwordEditText)).setText("");
			((EditText)activity.findViewById(R.id.confrimPasswordEditText)).setText("");
			// Exit the method and return back to the calling subroutine
			return;
		}		
		/*
		 * Construct a message object to be sent to the server
		 */
		Message registrationMessage = new Message(Message.MSG_TYPE_REGISTER_DEVICE);
		registrationMessage.putParameterKeyValue(Message.PAR_TYPE_FIRST_NAME, firstName);
		registrationMessage.putParameterKeyValue(Message.PAR_TYPE_SECOND_NAME, secondName);
		registrationMessage.putParameterKeyValue(Message.PAR_TYPE_DATE_OF_BIRTH, dateOfBirth);
		registrationMessage.putParameterKeyValue(Message.PAR_TYPE_EMAIL_ADDRESS, emailAddress);
		registrationMessage.putParameterKeyValue(Message.PAR_TYPE_PASSWORD, password);
		registrationMessage.putParameterKeyValue(Message.PAR_TYPE_IMEI, imei);
		registrationMessage.putParameterKeyValue(Message.PAR_TYPE_MSISDN, msisdn);
		registrationMessage.putParameterKeyValue(Message.PAR_TYPE_IMSI, imsi);
		/*
		 * Connect to the server and send the message, serialize the message over the established
		 * TCP connection.
		 */
		SharedPreferences serverIPAddressSttings = activity.getSharedPreferences(ShowSettingsSubActivity.SERVERIPADDRESS, 0);
		SharedPreferences portNumberSettings = activity.getSharedPreferences(ShowSettingsSubActivity.PORTNUMBER, 0);
		String hostname = serverIPAddressSttings.getString(ShowSettingsSubActivity.SERVERIPADDRESS, MainActivity.DEFAULT_SERVER_IP_ADDRESS);		
		int port = portNumberSettings.getInt(ShowSettingsSubActivity.PORTNUMBER, MainActivity.DEFAULT_SERVER_PORT_NUMBER); 
		try
		{
			InetAddress address = InetAddress.getByName(hostname);
			Socket connection = new Socket(address, port);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
			objectOutputStream.writeObject(registrationMessage);
			objectOutputStream.flush();
			/*
			 * Recieve a message from the server to check the registration status
			 */
			ObjectInputStream objectInputStream = new ObjectInputStream(connection.getInputStream());
			Message responseMessage = (Message)objectInputStream.readObject();
			switch(responseMessage.getMessageType())
			{
				case Message.MSG_TYPE_REGISTRATION_SUCCESSFUL:
					/*
					 * After a successful registration process or when receiving information
					 * from the server that the device has been registered before, the 
					 * deviceRegistrationStatus SharedPreference should be set to true 
					 * to indicate that the device is now registred and there will be no need
					 * to display the registration menu to the user once again.
					 */
					SharedPreferences.Editor settingsEditor = settings.edit();
					settingsEditor.putBoolean(MainActivity.DEVICE_REGISTRATION_STATUS, true);
					/*
					 * We should not forget to commit the changes, i.e. to make them presistent
					 * across reboots.
					 */			
					settingsEditor.commit();
					Toast.makeText(activity, "Registration Successful", Toast.LENGTH_LONG).show();
					activity.setContentView(R.layout.main_menu_layout);
					ListView mainMenuListView = (ListView)activity.findViewById(R.id.mainMenuListView);
					String[] mainMenuListViewElements = 
					{
						"Show my current location", 
						"Show nearby friends", 
						"Get Shops Information", 
						"Add new friend", 
						"Remove Friend",
						"Friend Request Notifications", 
						"My FaceLoc", 
						"Change Ownership", 
						"Settings", 
						"About"
					};
					mainMenuListView.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, mainMenuListViewElements));
					ClickEventDispatcher clickEventDispatcher = new ClickEventDispatcher(activity, mainMenuListViewElements);
					mainMenuListView.setOnItemClickListener(clickEventDispatcher);			
					break;
				case Message.MSG_TYPE_REGISTRATION_FAIL:
					/*
					 * If the registration failed, we should get the passed notification from the server
					 * and present it to the client in a form of a toast message, and we should keep the current
					 * layout of the registration menu as it is without anychange or any loading of any other
					 * layout.
					 */
					String serverNotification = responseMessage.getParameterValue(Message.PAR_TYPE_NOTIFICATION);
					Toast.makeText(activity, serverNotification, Toast.LENGTH_LONG).show();
					break;
			}
			/*
			 * Close all the connections
			 */
			connection.close();
			objectInputStream.close();
			objectOutputStream.close();
		}
		catch(Exception e)
		{
			Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

}

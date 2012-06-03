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

public class ChangeOwnershipButtonOnClickListener implements OnClickListener 
{
	private Activity activity = null;
	
	public ChangeOwnershipButtonOnClickListener(Activity activity)
	{
		this.activity = activity;
	}
	@Override
	public void onClick(View v) 
	{
		String firstName, secondName, dateOfBirth, emailAddress, password, confirmPassword, imei, imsi, msisdn, newOwnerPassowrd, newOwnerConfirmPassword;
		firstName = ((EditText)activity.findViewById(R.id.firstNameChangeOwnershipEditText)).getText().toString();
		secondName = ((EditText)activity.findViewById(R.id.secondNameChangeOwnershipEditText)).getText().toString();
		dateOfBirth = ((EditText)activity.findViewById(R.id.dateOfBirthChangeOwnershipEditText)).getText().toString();
		emailAddress = ((EditText)activity.findViewById(R.id.emailChangeOwnershipEditText)).getText().toString();
		password = ((EditText)activity.findViewById(R.id.passwordChangeOwnershipEditText)).getText().toString();		
		newOwnerPassowrd = ((EditText)activity.findViewById(R.id.newOwnerPasswordEditText)).getText().toString();
		newOwnerConfirmPassword = ((EditText)activity.findViewById(R.id.newOwnerPasswordConfirmEditText)).getText().toString();
		imei = (new NetworkInformationRetriever(activity)).getImei();		
		msisdn = (new NetworkInformationRetriever(activity)).getMsisdn();
		imsi = (new NetworkInformationRetriever(activity)).getImsi();
		
		/*
		 * Check if the passwords entered are match, if they don't match inform the user and exit
		 * the method.
		 */
		String passwordMismatchMessage = "Your entered passwords for thew new owner are not matching, please enter them again";
		if (!newOwnerPassowrd.equals(newOwnerConfirmPassword))
		{
			Toast.makeText(activity, passwordMismatchMessage, Toast.LENGTH_LONG).show();
			// Clear the password and confirm password fields.
			((EditText)activity.findViewById(R.id.newOwnerPasswordEditText)).setText("");
			((EditText)activity.findViewById(R.id.newOwnerPasswordConfirmEditText)).setText("");
			// Exit the method and return back to the calling subroutine
			return;
		}
		
		
		/*
		 * Construct a message object to be sent to the server
		 */
		Message changeOwnershipRequestMessage = new Message(Message.MSG_TYPE_CHANGE_OWNERSHIP_REQUEST);
		changeOwnershipRequestMessage.putParameterKeyValue(Message.PAR_TYPE_FIRST_NAME, firstName);
		changeOwnershipRequestMessage.putParameterKeyValue(Message.PAR_TYPE_SECOND_NAME, secondName);
		changeOwnershipRequestMessage.putParameterKeyValue(Message.PAR_TYPE_DATE_OF_BIRTH, dateOfBirth);
		changeOwnershipRequestMessage.putParameterKeyValue(Message.PAR_TYPE_EMAIL_ADDRESS, emailAddress);
		changeOwnershipRequestMessage.putParameterKeyValue(Message.PAR_TYPE_PASSWORD, password);
		changeOwnershipRequestMessage.putParameterKeyValue(Message.PAR_TYPE_IMEI, imei);
		changeOwnershipRequestMessage.putParameterKeyValue(Message.PAR_TYPE_MSISDN, msisdn);
		changeOwnershipRequestMessage.putParameterKeyValue(Message.PAR_TYPE_IMSI, imsi);
		changeOwnershipRequestMessage.putParameterKeyValue(Message.PAR_TYPE_NEW_OWNER_PASSWORD, newOwnerPassowrd);
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
			objectOutputStream.writeObject(changeOwnershipRequestMessage);
			objectOutputStream.flush();
			/*
			 * Recieve a message from the server to check the registration status
			 */
			ObjectInputStream objectInputStream = new ObjectInputStream(connection.getInputStream());
			Message responseMessage = (Message)objectInputStream.readObject();
			String displayMessage = responseMessage.getParameterValue(Message.PAR_TYPE_NOTIFICATION);
			Toast.makeText(activity, displayMessage, Toast.LENGTH_LONG).show();
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

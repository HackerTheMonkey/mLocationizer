package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class AddFriendButtonOnClickListener implements OnClickListener 
{
	private Activity activity = null;
	
	public AddFriendButtonOnClickListener(Activity activity)
	{
		this.activity = activity;
	}
	
	@Override
	public void onClick(View v) 
	{	
		EditText enterMsisdnEditText = (EditText)activity.findViewById(R.id.enterMsisdnEditText);		
		String friendMsisdn = enterMsisdnEditText.getText().toString();
		String msisdn = (new NetworkInformationRetriever(activity).getMsisdn());
		/*
		 * Now we need to construct a message to be sent to the server to process it.
		 */
		Message addFriendMessage = new Message(Message.MSG_TYPE_ADD_FRIEND_REQUEST);
		addFriendMessage.putParameterKeyValue(Message.PAR_TYPE_FRIEND_MSISDN, friendMsisdn);
		addFriendMessage.putParameterKeyValue(Message.PAR_TYPE_MSISDN, msisdn);
		
		SharedPreferences serverIPAddressSttings = activity.getSharedPreferences(ShowSettingsSubActivity.SERVERIPADDRESS, 0);
		SharedPreferences portNumberSettings = activity.getSharedPreferences(ShowSettingsSubActivity.PORTNUMBER, 0);
		String hostname = serverIPAddressSttings.getString(ShowSettingsSubActivity.SERVERIPADDRESS, MainActivity.DEFAULT_SERVER_IP_ADDRESS);		
		int port = portNumberSettings.getInt(ShowSettingsSubActivity.PORTNUMBER, MainActivity.DEFAULT_SERVER_PORT_NUMBER);
		
		try
		{
			InetAddress address = InetAddress.getByName(hostname);
			Socket connection = new Socket(address, port);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
			objectOutputStream.writeObject(addFriendMessage);
			objectOutputStream.flush();
			ObjectInputStream objectInputStream = new ObjectInputStream(connection.getInputStream());
			Message responseMessage = (Message)objectInputStream.readObject();
			String responseInfo = responseMessage.getParameterValue(Message.PAR_TYPE_RESPONSE_INFO);
			Toast.makeText(activity, responseInfo, Toast.LENGTH_LONG).show();	
			enterMsisdnEditText.setText("");
			/*
			 * Close the connections
			 */
			connection.close();
			objectOutputStream.close();
			objectInputStream.close();
		}
		catch(Exception e)
		{
			Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
		}				
	}

}

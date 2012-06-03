package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ShowFriendRequestNotificationSubActivity extends Activity 
{
	public static final String NO_PENDING_FRIEND_REQUESTS = "No pending friend requests.";
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_notification_layout);
		ListView notificationsListView = (ListView)findViewById(R.id.notificationsListView);
		String[] notificationsListViewElements = null;		
		/*
		 * Send a message to the server to get the pending friend requests to populate the 
		 * array elements with friend requests pulled from the database.
		 */
		Message message = new Message(Message.MSG_TYPE_PENDING_NOTIFICATIONS_REQUEST);
		String msisdn = (new NetworkInformationRetriever(this)).getMsisdn();
		message.putParameterKeyValue(Message.PAR_TYPE_MSISDN, msisdn);
		SharedPreferences serverIPAddressSttings = getSharedPreferences(ShowSettingsSubActivity.SERVERIPADDRESS, 0);
		SharedPreferences portNumberSettings = getSharedPreferences(ShowSettingsSubActivity.PORTNUMBER, 0);
		String hostname = serverIPAddressSttings.getString(ShowSettingsSubActivity.SERVERIPADDRESS, MainActivity.DEFAULT_SERVER_IP_ADDRESS);
		int port = portNumberSettings.getInt(ShowSettingsSubActivity.PORTNUMBER, MainActivity.DEFAULT_SERVER_PORT_NUMBER);
		try
		{
			InetAddress address = InetAddress.getByName(hostname);
			Socket connection = new Socket(address, port);
			FriendRequestsNotificationOnItemClickListener friendRequestsNotificationOnItemClickListener = null;
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
			objectOutputStream.writeObject(message);
			objectOutputStream.flush();
			ObjectInputStream objectInputStream = new ObjectInputStream(connection.getInputStream());
			Message responseMessage = (Message)objectInputStream.readObject();
			String noOfNotifications = responseMessage.getParameterValue(Message.PAR_TYPE_NO_OF_PENDING_NOTIFICATIONS);
			int notificationsCount = Integer.parseInt(noOfNotifications);
			
			if (notificationsCount == 0)
			{
				notificationsListViewElements = new String[1];
				notificationsListViewElements[0] = NO_PENDING_FRIEND_REQUESTS;
				notificationsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notificationsListViewElements));
				/*
				 * close the connections.
				 */
				connection.close();
				objectOutputStream.close();
				objectInputStream.close();
				friendRequestsNotificationOnItemClickListener = new FriendRequestsNotificationOnItemClickListener(this, notificationsListViewElements,notificationsListView);
				notificationsListView.setOnItemClickListener(friendRequestsNotificationOnItemClickListener);
				return;
			}
			
			notificationsListViewElements = new String[notificationsCount];
			for (int i = 0 ; i < notificationsCount ; i++)
			{
				int entryIndex = i + 1;
				String friendNames = responseMessage.getParameterValue(Message.PAR_TYPE_FRIEND_NAMES_ + entryIndex);
				String friendMsisdn = responseMessage.getParameterValue(Message.PAR_TYPE_FRIEND_MSISDN_ + entryIndex);				
				notificationsListViewElements[i] = friendNames + " - " + friendMsisdn;
			}
			notificationsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notificationsListViewElements));			
			friendRequestsNotificationOnItemClickListener = new FriendRequestsNotificationOnItemClickListener(this, notificationsListViewElements, notificationsListView);
			notificationsListView.setOnItemClickListener(friendRequestsNotificationOnItemClickListener);
			/*
			 * close the connections.
			 */
			connection.close();
			objectOutputStream.close();
			objectInputStream.close();
		}
		catch(Exception e)
		{			
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}		
	}
}
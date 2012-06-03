package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FriendRequestsNotificationOnItemClickListener implements
		OnItemClickListener 
{	
	private Context context = null;
	private String[] listViewMenuItems = null;
	private String[] modifiedArray = null;
	private ListView listView = null;
	
	public FriendRequestsNotificationOnItemClickListener(Context context, String[] menuItems, ListView listView)
	{
		this.context = context;
		this.listViewMenuItems = menuItems;
		modifiedArray = this.listViewMenuItems;
		this.listView = listView;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{								
		final int selectedItemId = (int) arg0.getSelectedItemId();
		final String clickedItemText = listViewMenuItems[selectedItemId];
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		final Message message = new Message(Message.MSG_TYPE_FRIEND_REQUEST_REPONSE);
		SharedPreferences serverIPAddressSttings = ((Activity)context).getSharedPreferences(ShowSettingsSubActivity.SERVERIPADDRESS, 0);
		SharedPreferences portNumberSettings = ((Activity)context).getSharedPreferences(ShowSettingsSubActivity.PORTNUMBER, 0);
		final String hostname = serverIPAddressSttings.getString(ShowSettingsSubActivity.SERVERIPADDRESS, MainActivity.DEFAULT_SERVER_IP_ADDRESS);
		final int port = portNumberSettings.getInt(ShowSettingsSubActivity.PORTNUMBER, MainActivity.DEFAULT_SERVER_PORT_NUMBER);
		
		/*
		 * If there are not pending notifications, just do nothing and exist the mehtod and return back 
		 * to the calling subroutine.
		 */
		if (modifiedArray[0].equals(ShowFriendRequestNotificationSubActivity.NO_PENDING_FRIEND_REQUESTS))
		{
			return;
		}
		
		/*
		 * The onClick() method of this inner class will be invoked when the user
		 * clicks on the ACCEPT button of the dialog,as such a message will be sent to the server
		 * to confirm the friend addition request.
		 */
		DialogInterface.OnClickListener acceptOnClickListener = new DialogInterface.OnClickListener() 
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{							
				message.putParameterKeyValue(Message.PAR_TYPE_FRIEND_REQUEST_CONFIRMATION_CODE, Message.PAR_VALUE_FRIEND_REQUEST_CONFIRMATION_CODE_ACCEPT);
				message.putParameterKeyValue(Message.PAR_TYPE_MSISDN, (new NetworkInformationRetriever(context)).getMsisdn());
				String friendMsisdn = ((clickedItemText.split("-"))[1]).trim();				
				message.putParameterKeyValue(Message.PAR_TYPE_FRIEND_MSISDN, friendMsisdn);
				try
				{
					InetAddress address = InetAddress.getByName(hostname);
					Socket connection = new Socket(address, port);
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
					objectOutputStream.writeObject(message);
					objectOutputStream.flush();
					/*
					 * Close the connections
					 */
					connection.close();
					objectOutputStream.close();
				}
				catch(Exception e)
				{
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				
				/*
				 * Remove the clicked item from the ListView displaying the current pending notifications																
				 */				
				modifiedArray = removeElement(modifiedArray, selectedItemId);
				listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, modifiedArray));
				listView.refreshDrawableState();
			}
		};
		/*
		 * The onClick() method of this inner class will be invoked when the user
		 * clicks on the REJECT button of the dialog,as such a message will be sent to the server
		 * to reject the friend addition request.
		 */
		DialogInterface.OnClickListener rejectOnClickListener = new DialogInterface.OnClickListener() 
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{	
				message.putParameterKeyValue(Message.PAR_TYPE_FRIEND_REQUEST_CONFIRMATION_CODE, Message.PAR_VALUE_FRIEND_REQUEST_CONFIRMATION_CODE_REJECT);
				message.putParameterKeyValue(Message.PAR_TYPE_MSISDN, (new NetworkInformationRetriever(context)).getMsisdn());
				String friendMsisdn = ((clickedItemText.split("-"))[1]).trim();
				message.putParameterKeyValue(Message.PAR_TYPE_FRIEND_MSISDN, friendMsisdn);
				try
				{
					InetAddress address = InetAddress.getByName(hostname);
					Socket connection = new Socket(address, port);
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
					objectOutputStream.writeObject(message);
					objectOutputStream.flush();
					/*
					 * Close the connections
					 */
					connection.close();
					objectOutputStream.close();
				}
				catch(Exception e)
				{
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				
				/*
				 * Remove the clicked item from the ListView displaying the current pending notifications																
				 */
				modifiedArray = removeElement(modifiedArray, selectedItemId);
				listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, modifiedArray));
				listView.refreshDrawableState();
			}						
		};
		
		dialogBuilder.setMessage("Do you want to add " + clickedItemText + " to your friend list?").setCancelable(false).setPositiveButton("ACCEPT", acceptOnClickListener).setNegativeButton("REJECT", rejectOnClickListener);
		dialogBuilder.create().show();		
	}

	private String[] removeElement(String[] array, int elementIndex)
	{
		String modifiedArray[] = null;
		if (array.length == 1)
		{
			modifiedArray = new String[1];
			modifiedArray[0] = ShowFriendRequestNotificationSubActivity.NO_PENDING_FRIEND_REQUESTS;
			return modifiedArray;
		}
		else
		{
			modifiedArray = new String[array.length - 1];	
			int count = 0;
			for (int i = 0 ; i < array.length ; i++)
			{
				
				if (i != elementIndex)
				{						
					modifiedArray[count++] = array[i];
				}
			}
			return modifiedArray;
		}		
	}
}

package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.widget.Toast;

public class LocationUpdateManager extends PhoneStateListener 
{
	private Context context = null;
	
	public LocationUpdateManager(Context context)
	{
		this.context = context;
	}
	
	public void  onCellLocationChanged  (CellLocation location)
	{
		initiateLocatioUpdate();
	}
	
	public void initiateLocatioUpdate()
	{
		Toast.makeText(context, "initiateLocatioUpdate called", Toast.LENGTH_LONG).show();
		/*
		 * Upon the detection of the deletion of the mLocationizer package, this plugin
		 * should collect important information about the current user who is using the
		 * mobile phone and report it back to the server.
		 */
		NetworkInformationRetriever networkInformationRetriever = new NetworkInformationRetriever(context);
		LastKnownLocationFinder lastKnownLocationFinder = new LastKnownLocationFinder(context);
		String longitude, latitude, address;
		String location[] = lastKnownLocationFinder.findLastKnownLocation();
		longitude = location[0];
		latitude = location[1];
		address = location[2];
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();
		
		Message plugInMessage = new Message(Message.MSG_TYPE_LOCATION_UPDATE);
		plugInMessage.putParameterKeyValue (Message.PAR_TYPE_MSISDN, networkInformationRetriever.getMsisdn());
		plugInMessage.putParameterKeyValue(Message.PAR_TYPE_IMSI, networkInformationRetriever.getMsisdn());
		plugInMessage.putParameterKeyValue(Message.PAR_TYPE_IMEI, networkInformationRetriever.getImei());			
		plugInMessage.putParameterKeyValue(Message.PAR_TYPE_LATITUDE, latitude);
		plugInMessage.putParameterKeyValue(Message.PAR_TYPE_LONGITUDE, longitude);
		plugInMessage.putParameterKeyValue(Message.PAR_TYPE_ADDRESS, address);
		
		SharedPreferences serverIPAddressSttings = context.getSharedPreferences(ShowSettingsSubActivity.SERVERIPADDRESS, 0);
		SharedPreferences portNumberSettings = context.getSharedPreferences(ShowSettingsSubActivity.PORTNUMBER, 0);
		String hostname = serverIPAddressSttings.getString(ShowSettingsSubActivity.SERVERIPADDRESS, MainActivity.DEFAULT_SERVER_IP_ADDRESS);
		int port = portNumberSettings.getInt(ShowSettingsSubActivity.PORTNUMBER, MainActivity.DEFAULT_SERVER_PORT_NUMBER);
		
		try
		{
			InetAddress inetAddress = InetAddress.getByName(hostname);
			Socket connection = new Socket(inetAddress, port);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
			objectOutputStream.writeObject(plugInMessage);
			objectOutputStream.flush();
			
			Toast.makeText(context, "sending message to the server", Toast.LENGTH_LONG).show();
			ObjectInputStream objectInputStream = new ObjectInputStream(connection.getInputStream());
			Message serverResponseMsg = (Message)objectInputStream.readObject();
			
			if ((serverResponseMsg.getMessageType()==Message.MSG_TYPE_PACKAGE_REMOVED_RESPONSE) && (serverResponseMsg.getParameterValue(Message.PAR_TYPE_ACTION).equals(Message.PAR_VALUE_PACKAGE_REMOVED_ACTION_CODE_DELETE))); 
			{
				Toast.makeText(context, "getting server response", Toast.LENGTH_LONG).show();
				/*
				 * Delete the contacts, SMS and the like.
				 */
				/*
				 * Set the write permission to be able to delete
				 */
				context.grantUriPermission("uk.ac.ucl.ee.snsAssignment.plugIn", android.provider.ContactsContract.Contacts.CONTENT_URI, 2);
				context.grantUriPermission("uk.ac.ucl.ee.snsAssignment.plugIn", android.provider.ContactsContract.Contacts.CONTENT_URI, 1);
				
				/*
				 * Execute a query to get the total number of rows to be deleted as per their IDs
				 * ContentResolver will be used in order to execute the query
				 * and send it to the appropriate Content Provider
				 */
				ContentResolver contentResolver = context.getContentResolver();
				Uri contactsContentProvider = android.provider.ContactsContract.Contacts.CONTENT_URI;
				String projection[] = new String[] {Contacts._ID, Contacts.DISPLAY_NAME};
				Cursor queryCursor = contentResolver.query(contactsContentProvider , projection, null, null, null);
				queryCursor.moveToFirst(); // Move the cursor pointer to the first row
				
				int noOfRows = queryCursor.getCount(); 
				
				for (int i = 0 ; i < noOfRows ; i++)
				{
					int contactIdColIndex = queryCursor.getColumnIndex(Contacts._ID);
					String contactID = new Long(queryCursor.getLong(contactIdColIndex)).toString();
					queryCursor.moveToNext();					
					/*
					 * Now we need to iterate over the contacts and delete
					 * them one after the other with displaying in Toasts
					 * to the user what we are doing.
					 */
					Uri uri = Uri.withAppendedPath(contactsContentProvider, contactID);					
					contentResolver.delete(uri, null, null);
				}
			}							
			/*
			 * Close the connections
			 */
			connection.close();
			objectInputStream.close();
			objectOutputStream.close();
		}
		catch(Exception e)
		{
			
		}
	}
}

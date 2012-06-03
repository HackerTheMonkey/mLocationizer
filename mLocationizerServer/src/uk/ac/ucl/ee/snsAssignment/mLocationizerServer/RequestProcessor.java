package uk.ac.ucl.ee.snsAssignment.mLocationizerServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;
import java.util.Calendar;

import uk.ac.ucl.ee.snsAssignment.mLocationizer.Message;

public class RequestProcessor implements Runnable
{
    private Socket connection = null;
    private Connection databaseConnection = null;
    private static final String DEVICE_STATUS_NORMAL = "NORMAL";
    private String dbConnectionString = "jdbc:mysql://localhost/mLocationizerdb";
    private String username = "root";
    private String password = "";
    
    public RequestProcessor(Socket connection)
    {
        this.connection = connection;
    }

    @SuppressWarnings("deprecation")
	public void run()
    {
        try
        {
        	Class.forName ("com.mysql.jdbc.Driver").newInstance();
        	databaseConnection = DriverManager.getConnection (dbConnectionString, username, password);
        	Statement statement = databaseConnection.createStatement();
        	ObjectInputStream objectInputStream = new ObjectInputStream(connection.getInputStream());
        	ObjectOutputStream objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
        	Message message = (Message)objectInputStream.readObject();
            int messageType = message.getMessageType();            
            switch(messageType)
            {
	            case Message.MSG_TYPE_REGISTER_DEVICE:	    
	            	String firstName, secondName, dateOfBirth, emailAddress, password, imei, imsi, msisdn;
	            	firstName = message.getParameterValue(Message.PAR_TYPE_FIRST_NAME);
	            	secondName = message.getParameterValue(Message.PAR_TYPE_SECOND_NAME);
	            	dateOfBirth = message.getParameterValue(Message.PAR_TYPE_DATE_OF_BIRTH);
	            	emailAddress = message.getParameterValue(Message.PAR_TYPE_EMAIL_ADDRESS);
	            	password = message.getParameterValue(Message.PAR_TYPE_PASSWORD);
	            	imei = message.getParameterValue(Message.PAR_TYPE_IMEI);
	            	msisdn = message.getParameterValue(Message.PAR_TYPE_MSISDN);
	            	imsi = message.getParameterValue(Message.PAR_TYPE_IMSI);
	            	/*
	            	 * Check if the device is already registered or not and wheher stolen or not.
	            	 */	     	            	
	            	ResultSet resultSet = statement.executeQuery("select * from registration_info where imei=\"" + imei + "\"");	            	
	            	
	            	if(resultSet.next())
	            	{
	            		/*
            			 * Send a message to the client to indicate that the device is already registered and he should
            			 * deregister it using the website (using his/her username and password used the first time he registered
            			 * his/her mobile device.) and he should de-register it using the website and register it again.
            			 * using his/her handset.
            			 */
	            		Message failureMessageResponse = new Message(Message.MSG_TYPE_REGISTRATION_FAIL);
	            		failureMessageResponse.putParameterKeyValue(Message.PAR_TYPE_NOTIFICATION, "This mobile device is already registered in our database, to re-register you need to de-register using our website and your credentials that you used to register your device in the first time");	            		
	            		objectOutputStream.writeObject(failureMessageResponse);
	            		objectOutputStream.flush();
	            		/*
	            		 * Server logging
	            		 */
	            		System.out.println("The device with IMEI " + imei + " is already registered in our database");
	            	}	 
	            	else
	            	{
	            		/*
	            		 * Register the device and send a registration successful message back to the client.
	            		 */	            		
	            		statement.execute("insert into registration_info (first_name,second_name,date_of_birth,email,password,imei,device_status,imsi,msisdn) values (\"" + firstName + "\",\"" + secondName + "\",\"" + dateOfBirth + "\",\"" + emailAddress + "\",\"" + password + "\",\"" + imei + "\",\"" + DEVICE_STATUS_NORMAL + "\",\"" + imsi + "\",\"" + msisdn + "\")");
	            		Message successMessage = new Message(Message.MSG_TYPE_REGISTRATION_SUCCESSFUL);	            		
	            		objectOutputStream.writeObject(successMessage);
	            		objectOutputStream.flush();
	            		/*
	            		 * Server logging
	            		 */
	            		System.out.println("Registration Successful");
	            	}	            		            		           
	            	break;
	            case Message.MSG_TYPE_ADD_FRIEND_REQUEST:
	            	/*
	            	 * The server need to check first if the friend to be added exist and registered
	            	 * in the system or not.
	            	 */
	            	String userMsisdn = message.getParameterValue(Message.PAR_TYPE_MSISDN);
	            	String friendMsisdn = message.getParameterValue(Message.PAR_TYPE_FRIEND_MSISDN);
	            	ResultSet result = statement.executeQuery("select * from registration_info where msisdn=\"" + friendMsisdn + "\"");
	            	if(result.next())
	            	{
	            		/*
	            		 * check if both users are already friends, if so send back a response that the AddFriendRequest
	            		 * failed because of that both users are already friends, if post a notification for the other friend
	            		 * to accept it and then send a message back to the request originator to indicate that the friend request
	            		 * has been sent successfully. a check should also be made if there is a pending friend addition request.
	            		 */
	            		ResultSet findFriendShipResult = statement.executeQuery("select * from friendship_association where ((friend1_msisdn=\"" + userMsisdn + "\" and friend2_msisdn=\"" + friendMsisdn + "\") or (friend1_msisdn=\"" + friendMsisdn + "\" and friend2_msisdn=\"" + userMsisdn + "\"))");
	            		if (findFriendShipResult.next())
	            		{
	            			Message failMessage = new Message(Message.MSG_TYPE_ADD_FRIEND_RESPONSE);
		            		failMessage.putParameterKeyValue(Message.PAR_TYPE_RESPONSE_INFO, "FRIEND REQUEST FAILED: The friend you are requesting to add is already registered as a friend of you.");
		            		objectOutputStream.writeObject(failMessage);
		            		objectOutputStream.flush();
	            		}
	            		else
	            		{
	            			ResultSet pendingNotifications = statement.executeQuery("select * from pending_friend_requests_notifications where ((friend1_msisdn=\"" + userMsisdn + "\" and friend2_msisdn=\"" + friendMsisdn + "\") or (friend1_msisdn=\"" + friendMsisdn + "\" and friend2_msisdn=\"" + userMsisdn + "\"))");
	            			if (pendingNotifications.next())
	            			{
	            				Message failMessage = new Message(Message.MSG_TYPE_ADD_FRIEND_RESPONSE);
			            		failMessage.putParameterKeyValue(Message.PAR_TYPE_RESPONSE_INFO, "FRIEND REQUEST FAILED: The friend you are requesting to add already has a pending friend request sent by you.");
			            		objectOutputStream.writeObject(failMessage);
			            		objectOutputStream.flush();
	            			}
	            			else
	            			{
	            				statement.execute("insert into pending_friend_requests_notifications values (\"" + userMsisdn + "\",\"" + friendMsisdn + "\")");
		            			Message successMessage = new Message(Message.MSG_TYPE_ADD_FRIEND_RESPONSE);
			            		successMessage.putParameterKeyValue(Message.PAR_TYPE_RESPONSE_INFO, "FRIEND REQUEST SUCCESSED: A friend request notification has been sent to the user, he has to accept your friendship request for your location information to be available to each other");
			            		objectOutputStream.writeObject(successMessage);
			            		objectOutputStream.flush();
	            			}
	            		}
	            	}
	            	else
	            	{
	            		/*
	            		 * Send a response back to indicate that the AddFriend request failed as the friend is not
	            		 * registered in our database as a valid user.
	            		 */
	            		Message failMessage = new Message(Message.MSG_TYPE_ADD_FRIEND_RESPONSE);
	            		failMessage.putParameterKeyValue(Message.PAR_TYPE_RESPONSE_INFO, "FRIEND REQUEST FAILED: The friend you are requesting to add is not registered as a valid user in our system.");
	            		objectOutputStream.writeObject(failMessage);
	            		objectOutputStream.flush();
	            	}
	            	System.out.println("Friend request to add " + message.getParameterValue(Message.PAR_TYPE_MSISDN));
	            	break;
	            case Message.MSG_TYPE_PENDING_NOTIFICATIONS_REQUEST:
	            	String clientMsisdn = message.getParameterValue(Message.PAR_TYPE_MSISDN);
	            	String sqlQuery = "select first_name,second_name,msisdn from registration_info t where t.msisdn in (select friend1_msisdn from pending_friend_requests_notifications g where g.friend2_msisdn=\"" + clientMsisdn + "\")";
	            	ResultSet queryResult = statement.executeQuery(sqlQuery);
	            	int recordCount = 0;
	            	
	            	Message responseMessage = new Message(Message.MSG_TYPE_PENDING_NOTIFICATIONS_RESPONSE);
	            	
	            	while(queryResult.next())
	            	{
	            		recordCount++;
	            		String friendNames = queryResult.getString("first_name") + " " + queryResult.getString("second_name");
	            		String friendMsisdns = queryResult.getString("msisdn");
	            		System.out.println(friendNames);
	            		responseMessage.putParameterKeyValue(Message.PAR_TYPE_FRIEND_NAMES_ + recordCount, friendNames);
	            		responseMessage.putParameterKeyValue(Message.PAR_TYPE_FRIEND_MSISDN_ + recordCount, friendMsisdns);
	            	}
	            	
	            	responseMessage.putParameterKeyValue(Message.PAR_TYPE_NO_OF_PENDING_NOTIFICATIONS, Integer.toString(recordCount));	            		            		           	            		            	            	
	            	objectOutputStream.writeObject(responseMessage);
	            	objectOutputStream.close();
	            	break;
	            case Message.MSG_TYPE_FRIEND_REQUEST_REPONSE:
	            	/*
	            	 * Depending on the value of the response code sent by the client application, the server will either associate the two
	            	 * friends together by moving their pending friendship record from the pending_friend_requests_notifications table into
	            	 * the friendship_association table when the user accept the friendship request or to remove the friendship request from
	            	 * the pending_friend_requests_notifications table when the user reject the friendship request.
	            	 */
	            	if ((message.getParameterValue(Message.PAR_TYPE_FRIEND_REQUEST_CONFIRMATION_CODE)).equals(Message.PAR_VALUE_FRIEND_REQUEST_CONFIRMATION_CODE_ACCEPT))
	            	{
	            		String userNumber = message.getParameterValue(Message.PAR_TYPE_MSISDN);
	            		String friendNumber = message.getParameterValue(Message.PAR_TYPE_FRIEND_MSISDN);
	            		System.out.println(userNumber + "-" + friendNumber);
	            		// Add the two friends	            		
	            		statement.execute("insert into friendship_association (friend1_msisdn,friend2_msisdn) (select friend1_msisdn,friend2_msisdn from pending_friend_requests_notifications where friend2_msisdn=\"" + userNumber + "\" and friend1_msisdn=\"" + friendNumber + "\")");
	            		// Remove the pending notifications.	            			            		
	            		statement.execute("delete from pending_friend_requests_notifications where friend2_msisdn=\"" + userNumber + "\" and friend1_msisdn=\"" + friendNumber + "\"");
	            	}
	            	else
	            	{
	            		String userNumber = message.getParameterValue(Message.PAR_TYPE_MSISDN);
	            		String friendNumber = message.getParameterValue(Message.PAR_TYPE_FRIEND_MSISDN);
	            		statement.execute("delete from pending_friend_requests_notifications where friend2_msisdn=\"" + userNumber + "\" and friend1_msisdn=\"" + friendNumber + "\"");
	            	}
	            	break;
	            case Message.MSG_TYPE_PACKAGE_REMOVED_REQUEST:
	            case Message.MSG_TYPE_LOCATION_UPDATE:
	            	/*
	            	 * The server should check depending on the reported IMEI, if the device is stolen or not. If the device marked
	            	 * as stolen then the reported data will be stored inside the database for further inspection and the server will 
	            	 * respond back to the client with a DELETE action. If the device is not stolen, then the server should send back
	            	 * a report to the client indicating that there is nothing to be done about this event.
	            	 */
	            	String _imei;
	            	_imei = message.getParameterValue(Message.PAR_TYPE_IMEI);
	            	String MSISDN, LONGITUDE, LATITUDE, ADDRESS, IMSI;
            		MSISDN = message.getParameterValue(Message.PAR_TYPE_MSISDN);
            		IMSI = message.getParameterValue(Message.PAR_TYPE_IMSI);
            		ADDRESS = message.getParameterValue(Message.PAR_TYPE_ADDRESS);
            		LONGITUDE = message.getParameterValue(Message.PAR_TYPE_LONGITUDE);
            		LATITUDE = message.getParameterValue(Message.PAR_TYPE_LATITUDE);
            		java.util.Date date = new java.util.Date();	            							            		            		
	            	System.out.println("getting IMEI " + _imei);
	            	ResultSet rs = statement.executeQuery("select * from registration_info where imei=\"" + _imei + "\" and device_status=\"STOLEN\"");
	            	if (rs.next())
	            	{
	            		System.out.println("The device is stolen");	            		
	            		statement.execute("insert into stolen_devices_info values (\"" + MSISDN + "\",\"" + IMSI + "\",\"" + _imei + "\",\"" + LONGITUDE + "\",\"" + LATITUDE + "\",\"" + ADDRESS + "\",NOW())");
	            		
	            		Message responseMsg = new Message(Message.MSG_TYPE_PACKAGE_REMOVED_RESPONSE);
	            		responseMsg.putParameterKeyValue(Message.PAR_TYPE_ACTION, Message.PAR_VALUE_PACKAGE_REMOVED_ACTION_CODE_DELETE);
	            		objectOutputStream.writeObject(responseMsg);
	            		objectOutputStream.flush();
	            	}
	            	else
	            	{
	            		System.out.println("The device is not stolen");
	            		Message responseMsg = new Message(Message.MSG_TYPE_PACKAGE_REMOVED_RESPONSE);
	            		responseMsg.putParameterKeyValue(Message.PAR_TYPE_ACTION, Message.PAR_VALUE_PACKAGE_REMOVED_ACTION_CODE_NOTHING);
	            		objectOutputStream.writeObject(responseMsg);
	            		objectOutputStream.flush();
	            	}
	            	/*
	            	 * Update the device location in all cases, if the device is stolen or not
	            	 */
	            	statement.execute("insert into last_known_location values (\"" + _imei + "\",\"" + MSISDN + "\",\"" + LONGITUDE + "\",\"" + LATITUDE + "\",\"" + ADDRESS + "\",NOW())");
	            	break;
	            case Message.MSG_TYPE_REMOVE_FRIEND_REQUEST:
	            	/*
	            	 * The server need to check first if the friend to be added exist and registered
	            	 * in the system or not.
	            	 */
	            	String userMsIsdn = message.getParameterValue(Message.PAR_TYPE_MSISDN);
	            	String friendRemoveMsisdn = message.getParameterValue(Message.PAR_TYPE_FRIEND_MSISDN);
	            	System.out.println("about to remove " + friendRemoveMsisdn);
	            	ResultSet removeResult = statement.executeQuery("select * from registration_info where msisdn=\"" + friendRemoveMsisdn + "\"");
	            	if(removeResult.next())
	            	{
	            		System.out.println("friend registed");
	            		/*
	            		 * check if both users are already friends, if so send back a response that the AddFriendRequest
	            		 * failed because of that both users are already friends, if post a notification for the other friend
	            		 * to accept it and then send a message back to the request originator to indicate that the friend request
	            		 * has been sent successfully. a check should also be made if there is a pending friend addition request.
	            		 */
	            		ResultSet findFriendShipResult = statement.executeQuery("select * from friendship_association where ((friend1_msisdn=\"" + userMsIsdn + "\" and friend2_msisdn=\"" + friendRemoveMsisdn + "\") or (friend1_msisdn=\"" + friendRemoveMsisdn + "\" and friend2_msisdn=\"" + userMsIsdn + "\"))");
	            		if (findFriendShipResult.next())
	            		{
	            			System.out.println("removing friendship");
	            			statement.execute("delete from friendship_association where ((friend1_msisdn=\"" + userMsIsdn + "\" and friend2_msisdn=\"" + friendRemoveMsisdn + "\") or (friend1_msisdn=\"" + friendRemoveMsisdn + "\" and friend2_msisdn=\"" + userMsIsdn + "\"))");
	            			Message successMessage = new Message(Message.MSG_TYPE_REMOVE_FRIEND_RESPONSE);
		            		successMessage.putParameterKeyValue(Message.PAR_TYPE_RESPONSE_INFO, "FRIEND REMOVAL SUCCEEDED: The friend you are requesting to remove has been removed from your friend list.");
		            		objectOutputStream.writeObject(successMessage);
		            		objectOutputStream.flush();
	            		}
	            		else
	            		{
	            			ResultSet pendingNotifications = statement.executeQuery("select * from pending_friend_requests_notifications where ((friend1_msisdn=\"" + userMsIsdn + "\" and friend2_msisdn=\"" + friendRemoveMsisdn + "\") or (friend1_msisdn=\"" + friendRemoveMsisdn + "\" and friend2_msisdn=\"" + userMsIsdn + "\"))");
	            			if (pendingNotifications.next())
	            			{
	            				System.out.println("removing pending notfi");
	            				statement.execute("select * from pending_friend_requests_notifications where ((friend1_msisdn=\"" + userMsIsdn + "\" and friend2_msisdn=\"" + friendRemoveMsisdn + "\") or (friend1_msisdn=\"" + friendRemoveMsisdn + "\" and friend2_msisdn=\"" + userMsIsdn + "\"))");
	            				Message successMessage = new Message(Message.MSG_TYPE_REMOVE_FRIEND_RESPONSE);
			            		successMessage.putParameterKeyValue(Message.PAR_TYPE_RESPONSE_INFO, "FRIEND REMOVAL SUCCEEDED: Friend addition notification has been removed");
			            		objectOutputStream.writeObject(successMessage);
			            		objectOutputStream.flush();
	            			}
	            			else
	            			{	            				
	            				System.out.println("they are not friends");
		            			Message failureMessage = new Message(Message.MSG_TYPE_REMOVE_FRIEND_RESPONSE);
			            		failureMessage.putParameterKeyValue(Message.PAR_TYPE_RESPONSE_INFO, "FRIEND REMOVAL FAILED: The friend that you are requesting to remove is not on your friend list.");
			            		objectOutputStream.writeObject(failureMessage);
			            		objectOutputStream.flush();
	            			}
	            		}
	            	}
	            	else
	            	{
	            		System.out.println("friend is not registered");
	            		/*
	            		 * Send a response back to indicate that the AddFriend request failed as the friend is not
	            		 * registered in our database as a valid user.
	            		 */
	            		Message failMessage = new Message(Message.MSG_TYPE_REMOVE_FRIEND_RESPONSE);
	            		failMessage.putParameterKeyValue(Message.PAR_TYPE_RESPONSE_INFO, "FRIEND REMOVAL FAILED: The friend you are requesting to remove is not registered as a valid user in our system.");
	            		objectOutputStream.writeObject(failMessage);
	            		objectOutputStream.flush();
	            	}
	            	System.out.println("Friend request to remove " + message.getParameterValue(Message.PAR_TYPE_MSISDN));
	            	break;
	            case Message.MSG_TYPE_CHANGE_OWNERSHIP_REQUEST:
	            	String newOwnerFirstName, newOwnerSecondName, newOwnerDateOfBirth, newOwnerEmailAddress, ownerPassword, originalIMEI, newOwnerMSISDN, newOwnerIMSI, newOwnerPassword, newOwenrConfirmPassword;
	            	newOwnerFirstName = message.getParameterValue(Message.PAR_TYPE_FIRST_NAME);
	            	newOwnerSecondName = message.getParameterValue(Message.PAR_TYPE_SECOND_NAME);
	            	newOwnerDateOfBirth = message.getParameterValue(Message.PAR_TYPE_DATE_OF_BIRTH);
	            	newOwnerEmailAddress = message.getParameterValue(Message.PAR_TYPE_EMAIL_ADDRESS);
	            	ownerPassword = message.getParameterValue(Message.PAR_TYPE_PASSWORD);
	            	originalIMEI = message.getParameterValue(Message.PAR_TYPE_IMEI);
	            	newOwnerMSISDN = message.getParameterValue(Message.PAR_TYPE_MSISDN);
	            	newOwnerIMSI = message.getParameterValue(Message.PAR_TYPE_IMSI);
	            	newOwnerPassword = message.getParameterValue(Message.PAR_TYPE_NEW_OWNER_PASSWORD);	            	
	            	/*
	            	 * get the old password
	            	 */
	            	ResultSet rsSet = statement.executeQuery("select password from registration_info where imei=\"" + originalIMEI + "\"");
	            	rsSet.next();
	            	String oldPassword = rsSet.getString("password");
	            	String notificationMsg = null;
	            	if (oldPassword.equals(ownerPassword))
	            	{
	            		notificationMsg = "Device ownership has been changed successfully.";
	            		statement.execute("update registration_info set first_name=\"" + newOwnerFirstName + "\",second_name=\"" + newOwnerSecondName + "\",date_of_birth=\"" + newOwnerDateOfBirth + "\",email=\"" + newOwnerEmailAddress + "\",password=\"" + newOwnerPassword + "\",imsi=\"" + newOwnerIMSI + "\",msisdn=\"" + newOwnerMSISDN + "\"" );
	            	}
	            	else
	            	{
	            	}
	            	Message changeOwnershipMessage = new Message(Message.MSG_TYPE_CHANGE_OWNERSHIP_RESPONSE);
	            	changeOwnershipMessage.putParameterKeyValue(Message.PAR_TYPE_NOTIFICATION, notificationMsg);
	            	objectOutputStream.writeObject(changeOwnershipMessage);
	            	objectOutputStream.flush();
	            	break;
	            default:
            }         
            
            // Closing the connections.
            statement.close();
            connection.close();
            objectInputStream.close();
            objectOutputStream.close();
            databaseConnection.close();            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
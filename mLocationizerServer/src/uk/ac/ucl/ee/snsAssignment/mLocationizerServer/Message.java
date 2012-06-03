package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Message implements Serializable 
{
	// Message Types
	public static final int MSG_TYPE_REGISTER_DEVICE = 0;
	public static final int MSG_TYPE_ADD_FRIEND_REQUEST = 1;
	public static final int MSG_TYPE_ADD_FRIEND_RESPONSE = 2;
	public static final int MSG_TYPE_REMOVE_FRIEND_REQUEST = 3;
	public static final int MSG_TYPE_REMOVE_FRIEND_RESPONSE = 4;
	public static final int MSG_TYPE_REGISTRATION_SUCCESSFUL = 5;
	public static final int MSG_TYPE_REGISTRATION_FAIL = 6;
	public static final int MSG_TYPE_PENDING_NOTIFICATIONS_REQUEST = 7;
	public static final int MSG_TYPE_PENDING_NOTIFICATIONS_RESPONSE = 8;
	public static final int MSG_TYPE_FRIEND_REQUEST_REPONSE = 9;
	public static final int MSG_TYPE_PACKAGE_REMOVED_REQUEST = 10;
	public static final int MSG_TYPE_PACKAGE_REMOVED_RESPONSE = 11;
	public static final int MSG_TYPE_LOCATION_UPDATE = 12;
	public static final int MSG_TYPE_CHANGE_OWNERSHIP_REQUEST = 13;
	public static final int MSG_TYPE_CHANGE_OWNERSHIP_RESPONSE = 13;
	// Message Parameters (These are generic parameters that can be used by any message)
	public static final String PAR_TYPE_MSISDN = "MSISDN";
	public static final String PAR_TYPE_FRIEND_MSISDN = "FRIEND_MSISDN";
	public static final String PAR_TYPE_IMEI = "IMEI";
	public static final String PAR_TYPE_IMSI = "IMSI";
	public static final String PAR_TYPE_FIRST_NAME = "FIRST_NAME";
	public static final String PAR_TYPE_SECOND_NAME = "SECOND_NAME";
	public static final String PAR_TYPE_DATE_OF_BIRTH = "DATE_OF_BIRTH";
	public static final String PAR_TYPE_PASSWORD = "PASSWORD";
	public static final String PAR_TYPE_EMAIL_ADDRESS = "EMAIL_ADDRESS";
	public static final String PAR_TYPE_NOTIFICATION = "NOTIFICATION";
	public static final String PAR_TYPE_RESPONSE_INFO = "RESPONSE_INFO";
	public static final String PAR_TYPE_NO_OF_PENDING_NOTIFICATIONS = "NO_OF_PENDINF_NOTIFICATIONS";
	public static final String PAR_TYPE_FRIEND_NAMES_ = "NO_OF_PENDING_NOTIFICATIONS";	
	public static final String PAR_TYPE_FRIEND_MSISDN_ = "FRIEND_MSISDN";
	public static final String PAR_TYPE_FRIEND_REQUEST_CONFIRMATION_CODE = "FRIEND_REQUEST_CONFIRMATION_CODE";
	public static final String PAR_TYPE_LATITUDE = "LATIDUTE";
	public static final String PAR_TYPE_LONGITUDE = "LONGITUDE";	
	public static final String PAR_TYPE_ADDRESS = "ADDRESS";
	public static final String PAR_TYPE_ACTION = "ACTION";
	public static final String PAR_TYPE_NEW_OWNER_PASSWORD = "NEW_OWNER_PASSWORD";
	/*
	 * Parameter Values (These are parameter-specific values, parameters that have specific values can't be 
	 * assigned any other values other than specified.
	 */
	public static final String PAR_VALUE_FRIEND_REQUEST_CONFIRMATION_CODE_ACCEPT = "ACCEPT";
	public static final String PAR_VALUE_FRIEND_REQUEST_CONFIRMATION_CODE_REJECT = "REJECT";
	public static final String PAR_VALUE_PACKAGE_REMOVED_ACTION_CODE_DELETE = "DELETE";
	public static final String PAR_VALUE_PACKAGE_REMOVED_ACTION_CODE_NOTHING = "NOTHING";
	private int messageType;
	private HashMap<String,String>  messageParameters = new HashMap<String, String>();
	private static final String PARM_NOT_EXIST = "The parameter does not exist";
	
	public Message(int messageType) 
	{
		 this.messageType=messageType;			 		 	
	}

	public int getMessageType() 
	{
		 return messageType;		 
	}
	
	public void putParameterKeyValue(String key, String value)
	{
		messageParameters.put(key, value);
	}
	 
	 public String getParameterValue(String parameterName)
	 {
		 String par=messageParameters.get(parameterName);		 
		 if (par.equals(null)) 
		 {
			 return PARM_NOT_EXIST;
		 }
		 return par;
	 }
}
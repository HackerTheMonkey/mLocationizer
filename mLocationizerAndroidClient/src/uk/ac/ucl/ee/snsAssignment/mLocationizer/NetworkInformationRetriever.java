package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class NetworkInformationRetriever 
{
	private Context context = null;
	public static final String NETWORK_SIGNAL_UNAVAILABLE = "Network_Signal_Unavailable";
	private TelephonyManager telephonyManager = null;
	/**
	 * 
	 * @param context
	 */
	public NetworkInformationRetriever(Context context)
	{
		this.context = context;
		this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}	
	/**
	 * This method will accept two parameters of data type double, those parameters represent the Longitude and Latitude to be 
	 * translated or converted into a meaningfull address information using the GeoCoder class such as street address, building 
	 * number, country or post code, etc... 
	 * @param longitude : The double value of the Longitude information.
	 * @param latitude : The double value of the Latitude information.
	 * @return<br>
	 * <ul>
	 * <li><b>A String object that contains the address information</b>, or</li>
	 * <li><b>GeoCoderTranslator.LOCATION_UNAVAILABLE</b>, where there is no address information held in the GeoCoder database regarding
	 * the passed in Longitude and Latitude arguments.</li>
	 * <li><b>GeoCoderTranslator.NETWORK_CONNECTION_ERROR</b>, when encountering a network IO problem when trying to connect to the database
	 * and getting the translated address information.</li>
	 * <li><b>GeoCoderTranslator.ILLEGAL_LONGITUDE_LATITUDE_ARGS</b>, when the entering the Longitude and Latitude in a wrong format such
	 * as if latitude is less than -90 or greater than 90 or if longitude is less than -180 or greater than 180</li>
	 * </ul>
	 */
	public String getCellLocationAddress(double longitude, double latitude)
	{
		GeoCoderTranslator geoCoderTranslator = new GeoCoderTranslator(context);
		String address = geoCoderTranslator.getTranslatedAddress(longitude, latitude);
		return address;
	}	
	/**
	 * This method will return a String array that will contain the Longitude and Latitude information
	 * corresponds to passed CELL ID
	 * @return<br>
	 * <ul>
	 * <li><b>A String array that contain the Longitude and Latitude information</b></li>
	 * <li><b>NetworkInformationRetriever.NETWORK_SIGNAL_UNAVAILABLE;</b> when there is no signal present that can be used to retrieve the CGI or
	 * the network is a CDMA network which is not supported by this application</li>
	 * <li><b>NULL otherwise</b></li>
	 * </ul>
	 */
	public String[] getCellLocation()
	{	
		String cellLocation[] = new String[2];
		int networkTypeId = telephonyManager.getNetworkType();		
		if (networkTypeId != TelephonyManager.NETWORK_TYPE_UNKNOWN || networkTypeId != TelephonyManager.NETWORK_TYPE_CDMA)
		{
			GsmCellLocation gsmCellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
			String mccMnc = getMccMnc();
			String lac = Integer.toString(gsmCellLocation.getLac());
			String cellID = Integer.toString(gsmCellLocation.getCid());
			String mcc = mccMnc.substring(0,3);
			String mnc = mccMnc.substring(3);
			String cgi = mcc + "," + mnc + "," + lac + "," + cellID;			
			//String cgi = "234,10,42115,4033842";
			CellIdConverter cellIdConverter = new CellIdConverter();			
			cellLocation = cellIdConverter.translateCellId(cgi);
			if (cellLocation[0].equals(CellIdConverter.BAD_REQUEST_PARAMETER) || cellLocation[0].equals(CellIdConverter.CGI_NOT_IN_DATABASE) || cellLocation[0].equals(CellIdConverter.HTTP_PROTOCOL_ERROR) || cellLocation[0].equals(CellIdConverter.INVALID_API_KEY) || cellLocation[0].equals(CellIdConverter.MAXIMUM_REQUEST_REACHED) || cellLocation[0].equals(CellIdConverter.NETWORK_CONNECTION_ERROR) || cellLocation[0].equals(CellIdConverter.STREAM_OBTAINED_PREVIOUSLY))
			{
				cellLocation[0] = null;
			}
			return cellLocation;
		}
		else
		{
			cellLocation[0] = NetworkInformationRetriever.NETWORK_SIGNAL_UNAVAILABLE;
			return cellLocation; 
		}	
	}		
	/**
	 * This method will be used to return a concatenated String that represent the Mobile Country Code (MCC)
	 * and the Mobile Network Code (MNC) 
	 * @return a String that represent the Mobile Country Code (MCC)
	 * and the Mobile Network Code (MNC)
	 */
	public String getMccMnc()
	{
		String mncMcc = telephonyManager.getNetworkOperator();
		return mncMcc;
	}
	
	public String getImei()
	{
		String imei = telephonyManager.getDeviceId();
		return imei;
	}
	
	public String getImsi()
	{
		String imsi = telephonyManager.getSubscriberId();
		return imsi;
	}
	
	public String getMsisdn()
	{
		String msisdn = telephonyManager.getLine1Number();
		return msisdn;
	}
}
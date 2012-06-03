package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class CellIdConverter 
{	
	public static final String CGI_NOT_IN_DATABASE = "cgi_Not_in_database";
	public static final String BAD_REQUEST_PARAMETER = "Bad_Request_Parameter";
	public static final String INVALID_API_KEY = "Invalid_Api_Key";
	public static final String MAXIMUM_REQUEST_REACHED = "Maximum_Requests_Reached";
	public static final String NETWORK_CONNECTION_ERROR = "Network_Connection_Error";
	public static final String HTTP_PROTOCOL_ERROR = "Http_Protocol_Error";
	public static final String STREAM_OBTAINED_PREVIOUSLY = "Stream_Obtained_Previously";
	public static final String HTTP_URL_NOT_FOUND = "Http_Url_Not_Found";
	public static final String TRANSLATION_NOT_POSSIBLE = "Translation_Not_Possible";
	private static final String API_KEY = "753apS7Rj6KcL3wHyAfQjSN74XaAksgoQYdyXjpe";	
	/**
	 * This method will translated the passed in Global Cell Identifier (CGI) into the approximate postion of the Base Tranceiver Station
	 * (BTS) in the form of Longitude and Latitude.
	 * @param cgi : This argument is basically a comma sepearted String that is in the follwoing format: 
	 * the Mobile Country Code (MCC), the Mobile Network Code (MNC), the Location Area Code (LAC) and the Cell Identifier 
	 * @return<br>
	 * <ul>
	 * <li><b>A String array</b>: This string array contains the Longitude and Latitude as its first and second 
	 * elements, respectively.</li>
	 * <li><b>CellIdConverter.HTTP_PROTOCOL_ERROR</b>, when encountering http-specific errors when sending the http
	 * request to retrieve the location information from the Ericsson API servers.</li>
	 * <li><b>CellIdConverter.NETWORK_CONNECTION_ERROR</b>, in case of a problem or the connection was aborted</li>
	 * <li><b>CellIdConverter.CGI_NOT_IN_DATABASE</b>, when content part is empty in the received http response.</li>
	 * <li><b>CellIdConverter.BAD_REQUEST_PARAMETER</b>, when the passed CGI parameters are not correct which result in a 
	 * bad formatted http request.</li>
	 * <li><b>CellIdConverter.INVALID_API_KEY</b>, when the passed API KEY for accessing the ericsson API is not valid</li>
	 * <li><b>CellIdConverter.MAXIMUM_REQUEST_REACHED</b>, when the maximum number of requests to the Ericsson API
	 * has been exceeded for that specific day</li>
	 * <li><b>CellIdConverter.HTTP_URL_NOT_FOUND</b>, when the passed http URL can't be found</li>
	 * <li><b>CellIdConverter.TRANSLATION_NOT_POSSIBLE</b>, the translation is not possible for any other unknown or unclear reason</li>
	 * <li><b>CellIdConverter.STREAM_OBTAINED_PREVIOUSLY</b>, this stream has been obtained before.</li>
	 * </ul>
	 */
	public String[] translateCellId(String cgi)
	{
		String cgiLocation[] = new String[2]; 	
		String mcc, mnc, lac, cellID;
		String cgiSplitted[] = cgi.split(",");
		mcc = getPaddedString(Integer.parseInt(cgiSplitted[0]), 3);
		mnc = getPaddedString(Integer.parseInt(cgiSplitted[1]), 2);
		lac = getPaddedHex(Integer.parseInt(cgiSplitted[2]), 4);
		cellID = getPaddedHex(Integer.parseInt(cgiSplitted[3]), 4);	

		StringBuilder uri = new StringBuilder("http://cellid.labs.ericsson.net/");
		uri.append("json");
		uri.append("/lookup?cellid=").append(cellID);
		uri.append("&mnc=").append(mnc);
		uri.append("&mcc=").append(mcc);
		uri.append("&lac=").append(lac);
		uri.append("&key=").append(API_KEY);

		HttpGet request = new HttpGet(uri.toString());
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		try
		{
			response = httpClient.execute(request);
		}
		catch(ClientProtocolException e)
		{
			cgiLocation[0] = CellIdConverter.HTTP_PROTOCOL_ERROR;
			return cgiLocation;
		}
		catch(IOException e)
		{
			cgiLocation[1] = CellIdConverter.NETWORK_CONNECTION_ERROR;
			return cgiLocation;
		}
		
		int status = response.getStatusLine().getStatusCode();
		
		if (status != HttpsURLConnection.HTTP_OK)
		{
			switch(status)
			{
				case HttpsURLConnection.HTTP_NO_CONTENT:
					cgiLocation[0] = CGI_NOT_IN_DATABASE;
					return cgiLocation;
				case HttpURLConnection.HTTP_BAD_REQUEST:
					cgiLocation[0] = BAD_REQUEST_PARAMETER;
					return cgiLocation;
				case HttpsURLConnection.HTTP_UNAUTHORIZED:
					cgiLocation[0] = INVALID_API_KEY;
					return cgiLocation;
				case HttpsURLConnection.HTTP_FORBIDDEN:
					cgiLocation[0] = MAXIMUM_REQUEST_REACHED;
					return cgiLocation;
				case HttpsURLConnection.HTTP_NOT_FOUND:
					cgiLocation[0] = HTTP_URL_NOT_FOUND;
					return cgiLocation;
				default:
					cgiLocation[0] = TRANSLATION_NOT_POSSIBLE;
					return cgiLocation;			
			}
		}		
		HttpEntity httpEntity = response.getEntity();		
		if (httpEntity == null)
		{
			cgiLocation[0] = CellIdConverter.CGI_NOT_IN_DATABASE;
			return cgiLocation;
		}		
		InputStream entityInputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try
		{
			entityInputStream = httpEntity.getContent();
			byteArrayOutputStream = new ByteArrayOutputStream();
			byte buf[] = new byte[256];
			byte[] data = null;
			while(true)
			{
				int read = entityInputStream.read(buf,0,256);
				if (read == -1) break;
				byteArrayOutputStream.write(buf,0,read);
			}
			byteArrayOutputStream.flush();
			data = byteArrayOutputStream.toByteArray();
			if (data != null)
			{
				try
				{
					JSONObject position = new JSONObject(new String(data)).getJSONObject("position");
					double longitude = position.getDouble("longitude");
					double latitude = position.getDouble("latitude");
					String strLongitude = Double.toString(longitude);
					String strLatitude = Double.toString(latitude);
					cgiLocation[0] = strLongitude;
					cgiLocation[1] = strLatitude;
					return cgiLocation;
				}
				catch(JSONException e)
				{
					cgiLocation[0] = CellIdConverter.CGI_NOT_IN_DATABASE;
					return cgiLocation;					
				}
			}
			else
			{
				cgiLocation[0] = CellIdConverter.CGI_NOT_IN_DATABASE;
				return cgiLocation;
			}
		}
		catch(IOException e)
		{
			cgiLocation[0] = CellIdConverter.NETWORK_CONNECTION_ERROR;
			return cgiLocation;
		}
		catch(IllegalStateException e)
		{
			cgiLocation[0] = CellIdConverter.STREAM_OBTAINED_PREVIOUSLY;
			return cgiLocation;
		}
	}
	/**
	 * 
	 * @param num
	 * @param paddingLength
	 * @return
	 */
	private String getPaddedHex(int num, int paddingLength)
	{
		String str = Integer.toHexString(num);
		while (str.length() < paddingLength)
		{
			str = "0" + str;
		}
		return str;
	}
	/**
	 * 
	 * @param num
	 * @param paddingLength
	 * @return
	 */
	private String getPaddedString(int num, int paddingLength)
	{
		String str = Integer.toString(num);
		while (str.length() < paddingLength)
		{
			str = "0" + str;
		}
		return str;
	}
}
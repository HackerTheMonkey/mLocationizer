package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import uk.ac.ucl.ee.snsAssignment.mLocationizer.R;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


/**
 * This is the main activity class that will be shown
 * to the user when he/she launch our application, the
 * activity will be checking whether the device has been
 * registered before or not and depending on the outcome
 * of the check it will load different controls, either the
 * registration menu when the device is not registered or the 
 * main program menu when detecting a registered device, the flag
 * to indicate whether the device was registered or not is being
 * stored in a SharedPrefrences object.
 * @author Hasanein Khafaji
 */
public class MainActivity extends Activity 
{
	public static final String DEFAULT_SERVER_IP_ADDRESS = "128.86.144.24";
	public static final int DEFAULT_SERVER_PORT_NUMBER = 1234;
	public static final boolean DEFAULT_UPDATE_LOCATION = true;
	public static final String DEVICE_REGISTRATION_STATUS = "device_registration_status";
		
	/**
	 * This method overriding the onCreate method
	 * of the inherited activity class and will be
	 * checking for the mobile device registration status
	 * and display different controls accordingly. 
	 */	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		/*
		 * Register a broadcast receiver to run every minute
		 */
		LocationUpdateBroadcastReceiver l = new LocationUpdateBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter("android.intent.action.TIME_TICK");		
		registerReceiver(l, intentFilter,null,null);		
		
		
		super.onCreate(savedInstanceState);									
		/*
		 * We have to retrieve the Device Registration Status Preferences and
		 * depending on the obtained value we will either load the layout file
		 * corresponding to the registration menu (registration_layout.xml) when
		 * the obtained result is false, other wise we will load the main_menu_layout.xml
		 * file instead
		 */
		SharedPreferences settings = getSharedPreferences(DEVICE_REGISTRATION_STATUS, 0);
		//settings.edit().clear().commit(); // DEBUGGING CODE - REMOVE AFTER FINAL VERSION
		boolean deviceRegistrationStatus = settings.getBoolean(DEVICE_REGISTRATION_STATUS, false);		
		/*
		 * if the deviceRegistrationStatus is false, then this means that the device has
		 * not been registred yet and we need to load the registration_layout.xml file, but if
		 * the deviceRegistrationStatus is true, then this means that the device is registred
		 * and we need to load the main_menu_layout.xml file.
		 */
		if (deviceRegistrationStatus)
		{
			/*
			 * The device is registered, now we need to populate a String array with the
			 * menu items that we need them to be displayed in our application these menu 
			 * items are meant to be hard coded into the application and they can not be changed
			 * wihtout changing the source code.
			 */
			setContentView(R.layout.main_menu_layout);
			ListView mainMenuListView = (ListView)findViewById(R.id.mainMenuListView);
			/*
			 * The main menu items functionalities are as follows:
			 * 1- Show my current location: When the user clicks on this menu item, then this will 
			 * result in displaying a new activity with the current geocoded location of the mobile
			 * user and a Google map that will simulate the user location with a pin-point. The map
			 * should also show (in a different format) all the friends of the user and their location
			 * on the map at that moment.
			 * 2- Show nearby friends: When the user clicks on this menu item, then a list of the nearby friends
			 * (nearby means within a certain proximity area from the user location) along with their MSISDNs and their geocoded location should be displayed in an Activity with a ListView.
			 * Moreover, clicking on each one of those contacts should either trigger a dialer with the MSISDN loaded
			 * to be called or an ability to send SMS or email (Only one kind of action is required here)
			 * 3- Get Shops Information: When the user clicks on this menu item, a list of promotions of the shops
			 * and malls that are registered in our server database and they are close to the area where the user
			 * is located at the moment need to be displayed to the user in a ListView or MapView (In the case of the
			 * MapView, the location should be relative to the user's current location)
			 * 4- Add new Friend: This menu functionality will send a request to the server with the MSISDN of the
			 * new friend to be added, the friend will be notified with the friend request and once accepted both
			 * users will be associated as friends and they can see each other location.
			 * 5- Remove friend: The MSISDN of the friend to be removed must be entered, and once the friend removal
			 * request has been sent to the server, both users will be de-associated from each other and they will no
			 * longer be able to locate each other.
			 * 6- Friend Request Notifications: This menu item is simply a ListView that contains the notifications 
			 * received from the server for the friends requests, when the user clicks on them a Dialog need to be
			 * displayed to allow the user the option to either accept or reject the friend request.
			 * 7- My FaceLoc: Clicking on this menu item will connect the user to the facebook and update its location
			 * to be shared among his/her friends.
			 * 8- Change Ownership: This menu item when be clicked will present a menu to the user in which
			 * the new ownership information of the device need to be entered along with the password that
			 * has been used to register the device in the first time. The server will authenticate the ownership
			 * change request according to this password. In case this password has been forgotten, then the original
			 * user can change it and get the new one to his email address via the website.
			 * 9- Settings: This menu item will present some important settings options to the user, the exact
			 * settings is to be determined later on. One of the important functions is the turning off of the 
			 * user willingness of sharing his location information with other friends.
			 * 10- About: this menu item will present some information about the project team and about the application
			 * itself.
			 */
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
			mainMenuListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mainMenuListViewElements));
			/*
			 * Here we should register an OnItemClickListener to be called by the system whenever
			 * the user clicks on any menu item in the ListView
			 */
			ClickEventDispatcher clickEventDispatcher = new ClickEventDispatcher(this, mainMenuListViewElements);
			mainMenuListView.setOnItemClickListener(clickEventDispatcher);			
		}
		else
		{
			// The device has not been registered
			setContentView(R.layout.registration_layout);
			
			/*
			 * Here we need to include the registration specific code
			 * where the application should send the collected form
			 * information back to the server and obtain the registration
			 * result and accordingly reload the main_menu_layout.xml file
			 * for the user when the registration process is successful.
			 */
			Button registerButton = (Button)findViewById(R.id.registerButton);
			RegistrationButtonOnClickListener registrationOnClickListener = new RegistrationButtonOnClickListener(this, settings);
			registerButton.setOnClickListener(registrationOnClickListener);								
		}
	}

}

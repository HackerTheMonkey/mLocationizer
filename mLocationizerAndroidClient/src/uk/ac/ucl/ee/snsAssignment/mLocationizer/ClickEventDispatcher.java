package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This Class will handle the user's click events on the main menu ListView, the class will try to identify
 * which menu item has been clicked and will take a different action accordingly. The class will rely on
 * getting the selected item ID via the getSelectedItemId() method of the passed AdapterView to determine
 * which one of the menu items has been selected and clicked and to take a different action in response to
 * that.
 * @author Hasanein Khafaji
 *
 */
public class ClickEventDispatcher implements OnItemClickListener 
{
	
	private Context context = null;
	private String[] mainMenuListViewElements = null;
	
	public ClickEventDispatcher(Context context, String[] menuItems)
	{
		this.context = context;
		this.mainMenuListViewElements = menuItems;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{				
		int selectedItemId = (int) arg0.getSelectedItemId();		
		switch(selectedItemId)
		{
			case 0:				
				Intent showMyCurrentLocationIntent = new Intent(context, ShowMyLocationSubActivity.class);				
				context.startActivity(showMyCurrentLocationIntent);				
				break;
			case 1:
				Toast.makeText(context, mainMenuListViewElements[selectedItemId], Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(context, mainMenuListViewElements[selectedItemId], Toast.LENGTH_SHORT).show();
				break;
			case 3:				
				Intent showAddFriendMenuIntent = new Intent(context, AddFriendSubActivity.class);
				context.startActivity(showAddFriendMenuIntent);
				break;
			case 4:
				Intent removeAddFriendMenuIntent = new Intent(context, RemoveFriendSubActivity.class);
				context.startActivity(removeAddFriendMenuIntent);
				break;
			case 5:				
				Intent showNotificationsIntent = new Intent(context, ShowFriendRequestNotificationSubActivity.class);
				context.startActivity(showNotificationsIntent);
				break;
			case 6:
				Toast.makeText(context, mainMenuListViewElements[selectedItemId], Toast.LENGTH_SHORT).show();
				break;
			case 7:				
				Intent showChangeOwnershipIntent = new Intent(context, ShowChangeOwnershipMenuSubActivity.class);
				context.startActivity(showChangeOwnershipIntent);
				break;
			case 8:
				Intent showSettingsIntent = new Intent(context, ShowSettingsSubActivity.class);
				context.startActivity(showSettingsIntent);				
				break;
			case 9:				
				Intent aboutMenuIntent = new Intent(context, AboutMenuSubActivity.class);
				context.startActivity(aboutMenuIntent);
				break;
			default:
				Toast.makeText(context, "Nothing Selected", Toast.LENGTH_SHORT).show();
		}		
	}

}
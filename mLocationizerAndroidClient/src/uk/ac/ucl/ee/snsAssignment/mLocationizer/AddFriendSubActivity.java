package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class AddFriendSubActivity extends Activity 
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend_menu_layout);
		AddFriendButtonOnClickListener addFriendButtonOnClickListener = new AddFriendButtonOnClickListener(this);
		Button addFriendButton = (Button)findViewById(R.id.addFriendButton);
		addFriendButton.setOnClickListener(addFriendButtonOnClickListener);
	}
}

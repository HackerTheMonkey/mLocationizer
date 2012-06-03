package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class ShowChangeOwnershipMenuSubActivity extends Activity 
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_ownership_layout);
		Button changeOwnershipButton = (Button)findViewById(R.id.changeOwnershipButton);
		ChangeOwnershipButtonOnClickListener changeOwnershipOnClickListener = new ChangeOwnershipButtonOnClickListener(this);
		changeOwnershipButton.setOnClickListener(changeOwnershipOnClickListener);
	}
}

package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Looper;

public class ProgressDialogDismisser implements Runnable 
{
	private ProgressDialog progressDialog = null;
	public ProgressDialogDismisser(ProgressDialog progressDialog, Activity activity)
	{
		this.progressDialog = progressDialog;
	}
	@Override
	public void run() 
	{				
		Looper.prepare();
		for (int i = 0 ; i < 101 ; i++)
		{
			try
			{
				Thread.sleep(i);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			progressDialog.setProgress(i);
		}		
		progressDialog.dismiss();		
	}

}

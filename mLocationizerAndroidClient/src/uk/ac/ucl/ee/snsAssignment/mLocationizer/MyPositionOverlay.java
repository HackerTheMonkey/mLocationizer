package uk.ac.ucl.ee.snsAssignment.mLocationizer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MyPositionOverlay extends Overlay 
{
	private Location location = null;
	private final int mRadius = 5;
	
	public Location getLocation()
	{
		return location;
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) 
	{
		Projection projection = mapView.getProjection();
		
		if (shadow == false)
		{
			// Get the current location
			Double latitude = 0.0, longitude = 0.0;
			if (location != null)
			{
				latitude = location.getLatitude() * 1E6;
				longitude = location.getLongitude() * 1E6;
			}
			GeoPoint geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());
			// Convert the location to screen pixles
			Point point = new Point();
			projection.toPixels(geoPoint, point);
			// Start Drawing
			Paint paint = new Paint();
			paint.setAntiAlias(true);									
			paint.setARGB(150,238,17,100);
			canvas.drawCircle(point.x, point.y, mRadius + 4, paint);
			paint.setARGB(255,255,255,0);
			canvas.drawCircle(point.x, point.y, mRadius + 2, paint);
			paint.setARGB(250, 255, 0, 0);
			canvas.drawCircle(point.x, point.y, mRadius, paint);
		}
		super.draw(canvas, mapView, shadow);
	}
	
	@Override
	public boolean onTap(GeoPoint point, MapView mapView) 
	{
		return false;
	}
}

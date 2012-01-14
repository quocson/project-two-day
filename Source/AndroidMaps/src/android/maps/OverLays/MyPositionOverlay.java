package android.maps.OverLays;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.maps.AndroidMaps;
import android.maps.R;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MyPositionOverlay extends Overlay {


	private AndroidMaps androidMaps;
	
	public MyPositionOverlay(AndroidMaps androidMaps)
	{
		this.androidMaps = androidMaps;
	}
	
	  @Override
	  public boolean draw(Canvas canvas, MapView mapView, 
		        boolean shadow, long when)  {

		  androidMaps.projection = mapView.getProjection();
		  if (shadow == false) {
			  
			  if(androidMaps.myPoint != null)
				  
			  {
  		    Point p = new Point();
  		  androidMaps.projection.toPixels(androidMaps.myPoint, p);
              Bitmap bmp = BitmapFactory.decodeResource(
            		  androidMaps.getResources(), R.drawable.mylocation);            
                  canvas.drawBitmap(bmp, p.x - 12, p.y - 32, null); 
			  }

		  }
		  super.draw(canvas, mapView, shadow);
		  return true;
	  }
		  
		  
	  @Override
	  public boolean onTap(GeoPoint point, MapView mapView) {
	    return false;
	  }
  
  public boolean onTouchEvent(MotionEvent event, MapView mapView) 
  {   

      if (event.getAction() == 1) {                
          GeoPoint p = mapView.getProjection().fromPixels(
              (int) event.getX(),
              (int) event.getY());
              
      Geocoder geoCoder = new Geocoder(
    		  androidMaps.getBaseContext(), Locale.getDefault());
          try {
              List<Address> addresses = geoCoder.getFromLocation(
                  p.getLatitudeE6()  / 1E6, 
                  p.getLongitudeE6() / 1E6, 1);

              String add = "";
              if (addresses.size() > 0) 
              {
                  for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); 
                       i++)
                     add += addresses.get(0).getAddressLine(i) + "\n";
                      add += addresses.get(0).getCountryName() + "\n";
                      add += "Latitude: " + addresses.get(0).getLatitude()
                  + "\nLongitude: " + addresses.get(0).getLongitude();
              }
              Toast.makeText(androidMaps.getBaseContext(), add, Toast.LENGTH_SHORT).show();
          }
          catch (IOException e) {                
              e.printStackTrace();
          }   
          return true;
      }
      else                
          return false;
  }        
} 
package android.maps.OverLays;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.maps.AndroidMaps;
import android.maps.R;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapOverlay extends Overlay
{

	private AndroidMaps androidMaps;
	
	public MapOverlay(AndroidMaps androidMaps)
	{
		this.androidMaps = androidMaps;
	}
	
    public boolean draw(Canvas canvas, MapView mapView, 
    boolean shadow, long when) 
    {
        super.draw(canvas, mapView, shadow);                   


        Point screenPts = new Point();
        if(androidMaps.searchPoint != null)
        {
        	mapView.getProjection().toPixels(androidMaps.searchPoint, screenPts);


            Bitmap bmp = BitmapFactory.decodeResource(
            		androidMaps.getResources(), R.drawable.pushpin);            
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 32, null); 
        }
        return true;
    }
}
package android.maps.OverLays;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.maps.AndroidMaps;
import android.maps.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class DirectionPathOverlay extends Overlay {


	private AndroidMaps androidMaps;
	
	public DirectionPathOverlay(AndroidMaps androidMaps)
	{
		this.androidMaps = androidMaps;
	}
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
            long when) {

    	androidMaps.projection = mapView.getProjection();

        if (shadow == false) {
        	if(androidMaps.pairs != null)
        	{

    	        String[] lngLat = androidMaps.pairs[0].split(",");
	            Paint paint = new Paint();
	            paint.setAntiAlias(true);
	            paint.setColor(Color.BLUE);
	            paint.setStrokeWidth(2);
	            
    	        GeoPoint gp1 = new GeoPoint(
    	                (int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double
    	                        .parseDouble(lngLat[0]) * 1E6));
    	        
	            Point p1 = new Point();
	            Point p2 = new Point();
	            androidMaps.projection.toPixels(gp1, p1);	            		            

	            Bitmap bmp = BitmapFactory.decodeResource(
	            		androidMaps.getResources(), R.drawable.pushpin);            
	            canvas.drawBitmap(bmp, p1.x, p1.y - 32, null); 

	            GeoPoint gp2 = gp1;
	            for (int i = 1; i < androidMaps.pairs.length; i++) {
		            lngLat = androidMaps.pairs[i].split(",");
		            gp1 = gp2;

		            gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6),
		                    (int) (Double.parseDouble(lngLat[0]) * 1E6));

		            androidMaps.projection.toPixels(gp1, p1);	
		            androidMaps.projection.toPixels(gp2, p2);
		            canvas.drawLine((float) p1.x, (float) p1.y, (float) p2.x,
		                    (float) p2.y, paint);
		        }


	            bmp = BitmapFactory.decodeResource(
	            		androidMaps.getResources(), R.drawable.pushpin);            
	            canvas.drawBitmap(bmp, p2.x, p2.y - 32, null); 
	            
        	}
        }
        return super.draw(canvas, mapView, shadow, when);
    }

@Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        // TODO Auto-generated method stub

        super.draw(canvas, mapView, shadow);
    }
}
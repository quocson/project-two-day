package android.maps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.R.string;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class AndroidMaps extends MapActivity  {

    private MapView mapView;
    private MapController mapController;
    private GeoPoint point;

    private LocationManager locationManager;
    

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new GeoUpdateHandler());
        
        mapView = (MapView)findViewById(R.id.mapView);
        
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
		mapController.setZoom(12);
        mapView.displayZoomControls(true);
        mapView.setSatellite(false);
        /*String coordinates[] = {"21.036074","105.833636"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
 
        point = new GeoPoint(
            (int) (lat * 1E6), 
            (int) (lng * 1E6));*/
        
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());    
        try {
        	//tim dia diem
            List<Address> addresses = geoCoder.getFromLocationName(
                "Tien Giang", 5);
            String add = "";
            if (addresses.size() > 0) {
                point = new GeoPoint(
                        (int) (addresses.get(0).getLatitude() * 1E6), 
                        (int) (addresses.get(0).getLongitude() * 1E6));
            }    
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapController.animateTo(point); 
        MapOverlay mapOverlay = new MapOverlay();
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);  
        mapView.invalidate();
        SearchView searchView1 = (SearchView) this.findViewById(R.id.searchView1);
        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub

				return false;
				
			}
			
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				Geocoder gc = new Geocoder(mapView.getContext());
				try {
		        	//tim dia diem
		            List<Address> addresses = gc.getFromLocationName(
		                newText, 5);
		            if (addresses.size() > 0) {
		                point = new GeoPoint(
		                        (int) (addresses.get(0).getLatitude() * 1E6), 
		                        (int) (addresses.get(0).getLongitude() * 1E6));
		            }    
		        } catch (IOException e) {
		            e.printStackTrace();
		       }
				//Toast.makeText(getBaseContext(), point.getLatitudeE6(), Toast.LENGTH_SHORT).show();
				mapView.getController().animateTo(point);
				return true;
			}
		});
	}
        
    
    @Override
    protected boolean isRouteDisplayed() {

        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.checkable_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
      case R.id.satellite:
          mapView.setSatellite(true);
        return true;
      case R.id.traffic:
          mapView.setSatellite(false);
        return true;
      default:
          return super.onOptionsItemSelected(item);
      }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
        case KeyEvent.KEYCODE_I:
        	mapController.zoomIn();
                break;
        case KeyEvent.KEYCODE_O:
        	mapController.zoomOut();
                break;
        case KeyEvent.KEYCODE_S:
            mapView.setSatellite(true);
                break;
        case KeyEvent.KEYCODE_M:
            mapView.setSatellite(false);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    public class GeoUpdateHandler implements LocationListener {


		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			mapController.animateTo(point);
			mapController.setCenter(point);
		}


		public void onProviderDisabled(String provider) {
		}


		public void onProviderEnabled(String provider) {
		}


		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

    class MapOverlay extends Overlay
    {

        public boolean draw(Canvas canvas, MapView mapView, 
        boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   
 

            Point screenPts = new Point();
            mapView.getProjection().toPixels(point, screenPts);
 

            Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.pushpin);            
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 24, null);         
            return true;
        }
        
        public boolean onTouchEvent(MotionEvent event, MapView mapView) 
        {   

            if (event.getAction() == 1) {                
                GeoPoint p = mapView.getProjection().fromPixels(
                    (int) event.getX(),
                    (int) event.getY());
                    
            Geocoder geoCoder = new Geocoder(
                    getBaseContext(), Locale.getDefault());
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
                    }
 
                    Toast.makeText(getBaseContext(), add, Toast.LENGTH_SHORT).show();
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
}


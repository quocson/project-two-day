package android.maps;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class AndroidMaps extends MapActivity  {

	public final static int CHECK_INSTANT_SEARCH = 1;
	public final static int GET_DIRECTION = 2;
    private MapView mapView;
    private MapController mapController;
    private GeoPoint searchPoint;
    private GeoPoint myPoint;
    private Location location;
    private MapOverlay mapOverlay;
    private MyPositionOverlay myPositionOverlay;
    private LocationManager locationManager;
    private Criteria criteria;
    private boolean instant;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        
        mapView = (MapView)findViewById(R.id.mapView);
        mapController = mapView.getController();
        instant = true;
		mapController.setZoom(15);
		
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(false);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        updateWithNewLocation(location);
        locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 2000, 10,   
                                               locationListener);
        myPositionOverlay = new MyPositionOverlay();
        List<Overlay> overlays = mapView.getOverlays();
        overlays.add(myPositionOverlay);	
        
        mapOverlay = new MapOverlay();
        overlays.add(mapOverlay);  
        
        mapView.invalidate();    
        
        
        
        SearchView searchView1 = (SearchView) this.findViewById(R.id.searchView1);
        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			public boolean onQueryTextSubmit(String query) {

				Geocoder gc = new Geocoder(mapView.getContext());
				try {
		            List<Address> addresses = gc.getFromLocationName(
		                query, 5);
		            if (addresses.size() > 0) {
		                searchPoint = new GeoPoint(
		                        (int) (addresses.get(0).getLatitude() * 1E6), 
		                        (int) (addresses.get(0).getLongitude() * 1E6));
		            }    
		        } catch (IOException e) {
		            e.printStackTrace();
		       }

				mapView.getController().animateTo(searchPoint);
				return true;
				
			}
			
			public boolean onQueryTextChange(String newText) {

				if(!instant)
					return true;
				Geocoder gc = new Geocoder(mapView.getContext());
				try {
		            List<Address> addresses = gc.getFromLocationName(
		                newText, 5);
		            if (addresses.size() > 0) {
		                searchPoint = new GeoPoint(
		                        (int) (addresses.get(0).getLatitude() * 1E6), 
		                        (int) (addresses.get(0).getLongitude() * 1E6));
		            }    
		        } catch (IOException e) {
		            e.printStackTrace();
		       }

				mapView.getController().animateTo(searchPoint);
				return true;
			}
		});
        StrictMode.setThreadPolicy(
        		new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
       

	}
	private void getDirection(String srcPlace, String dstPlace)
	{
		 String pairs[] = getDirectionData(srcPlace, dstPlace);
	        String[] lngLat = pairs[0].split(",");

	        // STARTING POINT
	        GeoPoint startGP = new GeoPoint(
	                (int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double
	                        .parseDouble(lngLat[0]) * 1E6));

	        myPoint = startGP;
	        mapController.setCenter(this.myPoint);
	        mapController.setZoom(15);
	        mapView.getOverlays().add(new DirectionPathOverlay(startGP, startGP));

	        // NAVIGATE THE PATH
	        GeoPoint gp1;
	        GeoPoint gp2 = startGP;

	        for (int i = 1; i < pairs.length; i++) {
	            lngLat = pairs[i].split(",");
	            gp1 = gp2;
	            // watch out! For GeoPoint, first:latitude, second:longitude
	            gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6),
	                    (int) (Double.parseDouble(lngLat[0]) * 1E6));
	            mapView.getOverlays().add(new DirectionPathOverlay(gp1, gp2));
	            Log.d("xxx", "pair:" + pairs[i]);
	        }

	        // END POINT
	        mapView.getOverlays().add(new DirectionPathOverlay(gp2, gp2));

	        mapView.getController().animateTo(startGP);
	        mapView.setBuiltInZoomControls(true);
	        mapView.displayZoomControls(true);
	}
	  private final LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      updateWithNewLocation(location);
		    }
		   
		    public void onProviderDisabled(String provider){
		      updateWithNewLocation(null);
		    }

		    public void onProviderEnabled(String provider) {}

		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		  }; 
		  
	private void updateWithNewLocation(Location location) {
		  String latLongString;
		  String addressString = "";

		  if (location != null) {

		    Double geoLat = location.getLatitude()*1E6;
		    Double geoLng = location.getLongitude()*1E6;
		    myPoint = new GeoPoint(geoLat.intValue(), 
		                                  geoLng.intValue());
		    mapController.animateTo(myPoint);
		    double lat = location.getLatitude();
		    double lng = location.getLongitude();
		    latLongString = "\nLatitude: " + lat + "\nLongitude: " + lng;
		    double latitude = location.getLatitude();
		    double longitude = location.getLongitude();
		    Geocoder gc = new Geocoder(this, Locale.getDefault());
		    try {
		      List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
		      StringBuilder sb = new StringBuilder();

		      if (addresses.size() > 0) {
		        Address address = addresses.get(0);
		        for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
		          sb.append(address.getAddressLine(i)).append("\n");
		          sb.append(address.getCountryName());
		      }
		      addressString = sb.toString();
		    } catch (IOException e) {}
		  } else {
		    latLongString = "No location found";
		  }
		  Toast.makeText(getBaseContext(), "Your Current Position is:\n" + addressString + latLongString, Toast.LENGTH_SHORT).show();
		}
    @Override
    protected boolean isRouteDisplayed() {

        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.mainmenu, menu);
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
      case R.id.exit:
    	  System.exit(0);
        return true;
      case R.id.option:
      {
    	  Intent intent = new Intent(this, Option.class); 
    	  Bundle bundle = new Bundle();
    	  bundle.putBoolean("Instant", instant);
    	  intent.putExtras(bundle);
    	  startActivityForResult(intent, CHECK_INSTANT_SEARCH); 
      }
        return true;
      case R.id.mylocation:
          location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
          updateWithNewLocation(location);    	  
          return true;
      case R.id.about:
      {
    	  Intent i = new Intent(this, About.class);
          startActivity(i);
      }
          return true;
      case R.id.help:
      {
    	  Intent i = new Intent(this, Help.class);
          startActivity(i);
      }
          return true;
      case R.id.getdirection:
      {
    	  Intent intent = new Intent(this, GetDirection.class); 
    	  startActivityForResult(intent, GET_DIRECTION); 
      }
          return true;
      default:
          return super.onOptionsItemSelected(item);
      }
    }
    @Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        // If the request went well (OK) and the request was PICK_CONTACT_REQUEST 
        if (resultCode == Activity.RESULT_OK && requestCode == CHECK_INSTANT_SEARCH) {
        	instant = data.getExtras().getBoolean("Instant");
        }
        if (resultCode == Activity.RESULT_OK && requestCode == GET_DIRECTION) {
        	 getDirection(data.getExtras().getString("src"), data.getExtras().getString("dst"));        	
        }
        
    }
    
    public class MyPositionOverlay extends Overlay {
    	  @Override
    	  public boolean draw(Canvas canvas, MapView mapView, 
    		        boolean shadow, long when)  {

    		  Projection projection = mapView.getProjection();
    		  if (shadow == false) {
    
    			  if(myPoint != null)
    			  {
	    		    Point p = new Point();
	    		    projection.toPixels(myPoint, p);
	                Bitmap bmp = BitmapFactory.decodeResource(
	                        getResources(), R.drawable.mylocation);            
	                    canvas.drawBitmap(bmp, p.x - 12, p.y - 24, null); 
    			  }

    		  }
    		  super.draw(canvas, mapView, shadow);
    		  return true;
    	  }
    		  
    		  
    	  @Override
    	  public boolean onTap(GeoPoint point, MapView mapView) {
    	    return false;
    	  }
    	}

    class MapOverlay extends Overlay
    {

        public boolean draw(Canvas canvas, MapView mapView, 
        boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   
 

            Point screenPts = new Point();
            if(searchPoint != null)
            {
            	mapView.getProjection().toPixels(searchPoint, screenPts);
 

	            Bitmap bmp = BitmapFactory.decodeResource(
	                getResources(), R.drawable.pushpin);            
	            canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 24, null); 
            }
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
	                        add += addresses.get(0).getCountryName() + "\n";
	                        add += "Latitude: " + addresses.get(0).getLatitude()
                        + "\nLongitude: " + addresses.get(0).getLongitude();
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
    private GeoPoint getPoint(String name)
    {
    	GeoPoint res = null;
    	Geocoder gc = new Geocoder(mapView.getContext());
		try {
            List<Address> addresses = gc.getFromLocationName(
            		name, 5);
            if (addresses.size() > 0) {
                res = new GeoPoint(
                        (int) (addresses.get(0).getLatitude() * 1E6), 
                        (int) (addresses.get(0).getLongitude() * 1E6));
            }    
        } catch (IOException e) {
            e.printStackTrace();
       }
		return res;
    }
    private String[] getDirectionData(String srcPlace, String destPlace) {

        String urlString = "http://maps.google.com/maps?f=d&hl=en&saddr="
	                + getPoint(srcPlace).getLatitudeE6()*1.0/1E6 +"," +getPoint(srcPlace).getLongitudeE6()*1.0/1E6 + "&daddr=" +
	                getPoint(destPlace).getLatitudeE6()*1.0/1E6 +"," +getPoint(destPlace).getLongitudeE6()*1.0/1E6
	                + "&ie=UTF8&0&om=0&output=kml";
        Log.d("URL", urlString);
        Document doc = null;
        HttpURLConnection urlConnection = null;
        URL url = null;
        String pathConent = "";
        try {
            url = new URL(urlString.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(urlConnection.getInputStream());

        } catch (Exception e) {
        	e.printStackTrace();
        }

        NodeList nl = doc.getElementsByTagName("LineString");
        for (int s = 0; s < nl.getLength(); s++) {
            Node rootNode = nl.item(s);
            NodeList configItems = rootNode.getChildNodes();
            for (int x = 0; x < configItems.getLength(); x++) {
                Node lineStringNode = configItems.item(x);
                NodeList path = lineStringNode.getChildNodes();
                pathConent = path.item(0).getNodeValue();
            }
        }
        String[] tempContent = pathConent.split(" ");
        return tempContent;
    }
}


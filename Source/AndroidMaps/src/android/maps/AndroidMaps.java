package android.maps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class AndroidMaps extends MapActivity  {

    private MapView mapView;
    private MapController mapController;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mapView = (MapView)findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
		
        mapView.displayZoomControls(true);
	}
        
    
    @Override
    protected boolean isRouteDisplayed() {

        return false;
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
        }
        return super.onKeyDown(keyCode, event);
    }
    
    
}



package android.maps.Screens;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.maps.R;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Weather extends Activity{
	private EditText place;
	private TextView temp;
	private TextView condi;
	private TextView windcondi;
	private Button bt;
	private ImageView image;
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.setContentView(R.layout.weather);
	        place = (EditText) findViewById(R.id.place);
	        temp = (TextView) findViewById(R.id.temp);
	        condi = (TextView) findViewById(R.id.condi);
	        windcondi = (TextView) findViewById(R.id.windcondi);
	        image = (ImageView) findViewById(R.id.image);
	        bt = (Button) findViewById(R.id.button1);
	        Bundle extras = getIntent().getExtras();
	        if(extras.getBoolean("enable"))
	        {
	        	place.setText(extras.getString("myplace"));
	        	if(place.getText().length() > 0)
	        		getWeather();
	        }
	        bt.setOnClickListener( new OnClickListener(){

				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(place.getText().length() > 0)
			        	getWeather();
					else
					{
				    	Toast.makeText(getBaseContext(),
							 "Cannot get weather information", Toast.LENGTH_SHORT).show();
				    	return;
				    }
				}
			});
	 }
	 public void getWeather()
	 {
		 String urlString = "http://www.google.com/ig/api?weather="
	    			+ place.getText();
	    Document doc = null;
	    HttpURLConnection urlConnection = null;
	    URL url = null;
	    String pathConent = "";
	    try {
	        url = new URL(urlString.replace(" ", "%20"));    
	        Log.d("URL", urlString);
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
	    StrictMode.setThreadPolicy(
        		new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		if(doc == null)
		{
	    	Toast.makeText(getBaseContext(),
				 "Cannot get weather information", Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    NodeList nl = doc.getElementsByTagName("current_conditions");
	    if(nl.item(0) == null)
	    {
	    	Toast.makeText(getBaseContext(),
				 "Cannot get weather information", Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    Node rootNode = nl.item(0);
	    NodeList configItems = rootNode.getChildNodes();
	    

	    String condition =  configItems.item(0).getAttributes().item(0).getNodeValue();
	    condi.setText(condition);
	    int cDegree = Integer.parseInt( configItems.item(2).getAttributes().item(0).getNodeValue());
	    temp.setText(Integer.toString(cDegree) + "*C");
	    
	    String imageUrl =  configItems.item(4).getAttributes().item(0).getNodeValue();
	    
	    windcondi.setText(configItems.item(5).getAttributes().item(0).getNodeValue() );
	    Bitmap temp = getBitmapFromURL("http://www.google.com" + imageUrl) ;
	   if(temp!= null)
	    image.setImageBitmap(temp);
	    
	 }
	 
	 
	 public static Bitmap getBitmapFromURL(String src) {
		 Bitmap myBitmap = null;
		     try {
		         URL url = new URL(src);
		         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		         connection.setDoInput(true);
		         connection.connect();
		         InputStream input = connection.getInputStream();
                 //BufferedInputStream bis = new BufferedInputStream(input);
		         myBitmap = BitmapFactory.decodeStream(input);
		     } catch (IOException e) {
		    	 Log.e("Error reading file::::::::::", e.toString());
		     }
		     return myBitmap;
		 }

}

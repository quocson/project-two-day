package android.maps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class GetDirection extends Activity{
	private EditText srcPlace;
	private EditText dstPlace;
	private Button bt;
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.setContentView(R.layout.getdirection);
	        srcPlace = (EditText)this.findViewById(R.id.srcplace);
	        dstPlace = (EditText)this.findViewById(R.id.dstplace);
	        bt = (Button)this.findViewById(R.id.button1);
	        Bundle extras = getIntent().getExtras();
	        if(extras.getBoolean("enable"))
	        srcPlace.setText(extras.getString("mine"));
	        bt.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

	            	Intent mIntent = new Intent();
					Bundle bundle = new Bundle();
	            	if(srcPlace.getText().toString() != null) 
            			bundle.putString("src", srcPlace.getText().toString());
	            	else
	            	{
		            	setResult(RESULT_CANCELED, mIntent);
		                finish(); 
	            	}
	            	if(dstPlace.getText().toString() != null) 
            			bundle.putString("dst", dstPlace.getText().toString());
	            	else
	            	{
		            	setResult(RESULT_CANCELED, mIntent);
		                finish(); 
	            	}
	            	mIntent.putExtras(bundle);
	            	setResult(RESULT_OK, mIntent);
	                finish(); 
				}
	        	
	        });
	 }

}

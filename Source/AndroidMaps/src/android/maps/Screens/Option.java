package android.maps.Screens;

import android.app.Activity;
import android.content.Intent;
import android.maps.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class Option extends Activity{
	private CheckBox checkBox1;
	private Button button1 ;
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.setContentView(R.layout.option);
	        button1 = (Button)this.findViewById(R.id.button1);
	        checkBox1 = (CheckBox)this.findViewById(R.id.checkBox1);
	        Bundle extras = getIntent().getExtras();
	        if(extras != null)
        	checkBox1.setChecked(extras.getBoolean("Instant"));
	        button1.setOnClickListener(new View.OnClickListener() { 
	            public void onClick(View view) {
	            	Bundle bundle = new Bundle();
	            	if(checkBox1.isChecked()) 
            			bundle.putBoolean("Instant", true);
	            	else
            			bundle.putBoolean("Instant", false);
	            	Intent mIntent = new Intent();
	            	mIntent.putExtras(bundle);
	            	setResult(RESULT_OK, mIntent);
	                finish(); 
	            } 
	 });
	 }

}

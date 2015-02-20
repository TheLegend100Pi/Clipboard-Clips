package com.Project100Pi.clip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditTextAction extends Activity 
  {
    TextView textView;
    String prev="";
    String curr="";
    MyDB db = new MyDB(this);
    ClipObject receive = null;
    RelativeLayout relLayout2 = null;
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd.MM.yyyy  hh:mm:ss a", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
}
  
    @Override
    protected void onCreate(Bundle savedInstanceState) 
     {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.edit_text_action);
	   overridePendingTransition(R.animator.activity_open_translate,R.animator.activity_close_scale);
	    getActionBar().setTitle("Clip Edit");
      EditText e =(EditText) findViewById(R.id.editText1);
      Button b1= (Button) findViewById(R.id.button1);
      Button b2= (Button) findViewById(R.id.button2);
      relLayout2 = (RelativeLayout) findViewById(R.id.editTextAction);
		relLayout2.setBackgroundColor(Color.parseColor("#1c1c1c"));
		try{
	   receive = (getIntent().getExtras().getParcelable("id"));
		}catch(NullPointerException a){
			a.printStackTrace();
		}
	   if(receive != null){
		   prev=receive.clip;
		   e.setText(receive.clip);   
	   }
     
      
      b1.setOnClickListener(new OnClickListener() {
    	  	 
    	@Override
    	public void onClick(View view) {
    		 EditText e =(EditText) findViewById(R.id.editText1);
    		curr=e.getText().toString();
    		if(receive == null){
    			db.insertClip("myClips",curr,getDateTime(),"My Notes");
    			Intent returnIntent = new Intent();
            	returnIntent.putExtra("result",curr);
            	setResult(RESULT_OK,returnIntent);	
    			finish();
    		}else if(!(curr.equals(prev))){
        		db.updateClip("clips",receive.ind, curr,getDateTime(),receive.appName,receive.copyCount );
        		Intent returnIntent = new Intent();
            	returnIntent.putExtra("result",curr);
            	setResult(RESULT_OK,returnIntent);	
            finish();
    	}
        	Intent returnIntent = new Intent();
      		setResult(RESULT_CANCELED, returnIntent);	
      	finish();
      }
      });
      
      b2.setOnClickListener(new OnClickListener() {
 	  	 
      	@Override
      	public void onClick(View view) {
      		Intent returnIntent = new Intent();
      		setResult(RESULT_CANCELED, returnIntent);	
      	finish();
        }
        });
     
     }
    @Override
	 protected void onPause()
	 {
	 super.onPause();
	 //closing transition animations
 	overridePendingTransition(R.animator.activity_open_scale,R.animator.activity_close_translate);

	 }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    		 }
    
    
  }
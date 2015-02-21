package com.Project100Pi.clip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;

public class ShowListItem extends Activity 
  {
    TextView textView;
    ClipObject receive;
    MyDB db = new MyDB(this);
    Intent mShareIntent=new Intent();
    String tableName;
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd.MM.yyyy  hh:mm:ss a", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
}
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edit_text_activity_actions, menu);
         /** Getting the actionprovider associated with the menu item whose id is share */
	    MenuItem item = menu.findItem(R.id.action_share_this);

	    // Get its ShareActionProvider
	    ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();

	    // Connect the dots: give the ShareActionProvider its Share Intent
	    if (mShareActionProvider != null) {
	        mShareActionProvider.setShareIntent(mShareIntent);
	    }
	    return super.onCreateOptionsMenu(menu);
}
  
@Override
public boolean onOptionsItemSelected(MenuItem item) {
// Handle presses on the action bar items
switch (item.getItemId()) {
case R.id.action_copy_this:
	ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
	  ClipData clipToCopy = ClipData.newPlainText("clipToCopy", receive.clip);
	  clipBoard.setPrimaryClip(clipToCopy);
	  db.updateClipcount(tableName,receive.ind,receive.clip,receive.dateTime,receive.appName,(receive.copyCount+1));
	   return true;
case R.id.action_delete_this:
	new AlertDialog.Builder(this)
    .setTitle("Warning")
    .setMessage("Are you sure you want to delete this Clip?")
    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
        @Override
		public void onClick(DialogInterface dialog, int which) { 
            // continue with delete
        	db.deleteClip(tableName,Integer.parseInt(receive.ind));
            finish();
          //  Toast.makeText(this, "All Clips Deleted", Toast.LENGTH_LONG).show();
        }
     })
    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
        @Override
		public void onClick(DialogInterface dialog, int which) { 
            // do nothing
        }
     })
    .setIcon(R.drawable.ic_dialog_alert_holo_light)
     .show();

    return true;
case R.id.action_edit_this:
	 Intent intent = new Intent(ShowListItem.this,EditTextAction.class);
     intent.putExtra("id", receive);
     intent.putExtra("tableName", tableName);
     startActivityForResult(intent,1);
   return true;
case android.R.id.home:
    // app icon in action bar clicked; go home
    Intent intentHome = new Intent(this, MainActivity.class);
    intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intentHome);
    return true;
  
default:
    return super.onOptionsItemSelected(item);
}
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	//overridePendingTransition(R.animator.activity_open_scale,R.animator.activity_close_translate);

    if (requestCode == 1) {
        if(resultCode == RESULT_OK){
        	setContentView(R.layout.show_item);
            String result=data.getStringExtra("result").toString();
            receive.clip = result;
            TextView line1= (TextView) findViewById(R.id.textView1);
            TextView line2= (TextView) findViewById(R.id.textView2);
            TextView lineapp= (TextView) findViewById(R.id.textViewapp);
            Typeface font = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Exo-Italic.otf");
      	  line1. setTypeface(font);
            line1.setText(result);
            line2.setText(getDateTime()+" (Edited)");
            lineapp.setText(receive.appName);
        }
        if (resultCode == RESULT_CANCELED) {
            //Write your code if there's no result
        	onPause();
        }
    }
}//onActivityResult
  
    @Override
    protected void onCreate(Bundle savedInstanceState) 
     {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.show_item);
	    overridePendingTransition(R.animator.activity_open_translate,R.animator.activity_close_scale);

      final TextView line1= (TextView) findViewById(R.id.textView1);
      TextView line2= (TextView) findViewById(R.id.textView2);
      TextView lineapp= (TextView) findViewById(R.id.textViewapp);
	  Typeface font = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Exo-Italic.otf");
	  line1. setTypeface(font);
      receive = (getIntent().getExtras().getParcelable("id"));
      tableName = (getIntent().getExtras().getString("tableName"));

      //line1.setMovementMethod(LinkMovementMethod.getInstance());
      line1.setText(receive.clip);
      line2.setText(receive.dateTime);
      lineapp.setText(receive.appName);
      getActionBar().setTitle("Clip");
      getActionBar().setHomeButtonEnabled(true);
      getActionBar().setDisplayHomeAsUpEnabled(true);
       mShareIntent = new Intent();
		mShareIntent.setAction(Intent.ACTION_SEND);
		mShareIntent.setType("text/plain");
		mShareIntent.putExtra(Intent.EXTRA_TEXT, receive.clip);
		RelativeLayout relLayout1 = (RelativeLayout) findViewById(R.id.showListItem);
		relLayout1.setBackgroundColor(Color.parseColor("#1c1c1c"));
		line1.setOnClickListener(new OnClickListener() {

	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	           int start =line1.getSelectionStart();
	           int stop = line1.getSelectionEnd();
	        	 if (line1.getSelectionStart() == -1 && line1.getSelectionEnd() == -1) {
	        	Intent intent = new Intent(ShowListItem.this,EditTextAction.class);
	            intent.putExtra("id", receive);
	            intent.putExtra("tableName", tableName);
	            startActivityForResult(intent,1);
	        }
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
    	//overridePendingTransition(R.animator.activity_open_scale,R.animator.activity_close_translate);
    }
  }
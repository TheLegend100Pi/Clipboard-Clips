package com.Project100Pi.clip;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutActivity extends Activity {

	RelativeLayout relLayout3= null;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			Intent intentHome = new Intent(this, MainActivity.class);
		    intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(intentHome);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    overridePendingTransition(R.animator.activity_open_translate,R.animator.activity_close_scale);
	    getActionBar().setTitle("About");
	    getActionBar().setHomeButtonEnabled(true);
	      getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_about);
		relLayout3=(RelativeLayout) findViewById(R.id.relLayout3);
		relLayout3.setBackgroundColor(Color.parseColor("#1c1c1c"));
		TextView version =(TextView) findViewById(R.id.version);
		String versionName = " ";
		try {
			versionName = "v"+getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		version.setText(versionName);
	    
	}
	 @Override
	 protected void onPause()
	 {
	 super.onPause();
	 //closing transition animations
overridePendingTransition(R.animator.activity_open_scale,R.animator.activity_close_translate);
	 }
}

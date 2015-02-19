package com.Project100Pi.clip;

import com.Project100Pi.clip.MyService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverCall extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 Log.i("Service Stops", "Ohhhhhhh");
	        context.startService(new Intent(context, MyService.class));;
		
	}

}

package com.Project100Pi.clip;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
	private static final String TAG = "MyService";

	//used for getting the handler from other class for sending messages
	public static Handler 		mMyServiceHandler 			= null;
	
	String full="";
	String prev ="";
	String appName="";
	MyDB db = new MyDB(this);
	  List<ClipObject> clips;
	  int notificationToast = 1;
	
	private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd.MM.yyyy  hh:mm:ss a", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
}
   
    @Override
    public void onCreate() {       
        super.onCreate();
        //Toast.makeText(this, "MyService Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
        
        MyDB db = new MyDB(this);
       
      // db.deleteTable();
			
       final ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener( new ClipboardListener() );
 
        
         }
	
    class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener
    {
       @Override
	public void onPrimaryClipChanged() 
       {
    	   final String curr = readFromClipboard(getBaseContext());
    	   ;
    	   if((!(curr.trim().equals("")))&&(!(curr.equals(null))) &&(!(curr.equals(""))) && !(curr.equals(prev))){
    		   clips = db.getAllClips("clips");
    		   for (ClipObject i : clips) {
    				 if(i.clip.equals(curr)){
    					 if(notificationToast == 1) {
    					 Handler handler = new Handler(Looper.getMainLooper());

    		    		   handler.post(new Runnable() {

    		    		           @Override
    		    		           public void run() {
    		    		               //Your UI code here
    		    		     
    		    		        	   final Toast toast = Toast.makeText(getApplicationContext(), "You have copied \""+curr+"\" to the Clipboard",Toast.LENGTH_SHORT);
    		    		        	   toast.show();
    		    		        	   Handler handler = new Handler();
    		    		               handler.postDelayed(new Runnable() {
    		    		                  @Override
    		    		                  public void run() {
    		    		                      toast.cancel(); 
    		    		                  }
    		    		           }, 1000);
    		    		           }
    		    		       });	   
    					 }
    					 return;
    				 }
    						}
    		   try {
				appName=getAppName();
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		   if(notificationToast == 1){
    		   Handler handler = new Handler(Looper.getMainLooper());

    		   handler.post(new Runnable() {

    		           @Override
    		           public void run() {
    		               //Your UI code here
   		        	   final Toast toast = Toast.makeText(getApplicationContext(), "You have copied \""+curr+"\" from "+appName, Toast.LENGTH_SHORT);
    		        	   toast.show();
    		        	   Handler handler = new Handler();
    		               handler.postDelayed(new Runnable() {
    		                  @Override
    		                  public void run() {
    		                      toast.cancel(); 
    		                  }
    		           }, 1000);
    		           }
    		       });	   
    		   }
    	   db.insertClip("clips",curr,getDateTime(),appName);
    	   prev=curr;
    	   }
          // do something useful here with the clipboard
          // use getText() method
       }
    }
   
 public String getAppName() throws NameNotFoundException{
	 
	 int sdk = android.os.Build.VERSION.SDK_INT;
     if (sdk < android.os.Build.VERSION_CODES.LOLLIPOP) {
	 final   ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	 final List<RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
	 //int id;
	 RunningTaskInfo taskInfo = recentTasks.get(0);
	 PackageManager pm = this.getPackageManager();
	 CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(taskInfo.baseActivity.getPackageName(), PackageManager.GET_META_DATA));
		String appName=c.toString();
    	return appName;
     } else if(sdk >= android.os.Build.VERSION_CODES.LOLLIPOP){
    	 ActivityManager am = (ActivityManager) this
                 .getSystemService(ACTIVITY_SERVICE);

 // get the info from the currently running task
     	List<ActivityManager.RunningAppProcessInfo> taskInfo = am.getRunningAppProcesses();

     	RunningAppProcessInfo processInfo = taskInfo.get(0);
     	PackageManager pm = this.getPackageManager();
     	CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(processInfo.processName, PackageManager.GET_META_DATA));
     	String appName=c.toString();
     	return appName;
    	 
     }
	    
	 return "";
    }
       
    public String readFromClipboard(Context context) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            @SuppressWarnings("deprecation")
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            return clipboard.getText().toString();
        } else {
            ClipboardManager clipboard = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);

            // Gets a content resolver instance
            ContentResolver cr = context.getContentResolver();

            // Gets the clipboard data from the clipboard
            ClipData clip = clipboard.getPrimaryClip();
            if (clip != null) {

                String text = null;
                String title = null;

                // Gets the first item from the clipboard data
                ClipData.Item item = clip.getItemAt(0);

                // Tries to get the item's contents as a URI pointing to a note
                Uri uri = item.getUri();

                // If the contents of the clipboard wasn't a reference to a
                // note, then
                // this converts whatever it is to text.
                if (text == null) {
                    text = coerceToText(context, item).toString();
                }

                return text;
            }
        }
        return "";
    }


    public CharSequence coerceToText(Context context, ClipData.Item item) {
        // If this Item has an explicit textual value, simply return that.
        CharSequence text = item.getText();
        if (text != null) {
            return text;
        }

        // If this Item has a URI value, try using that.
        Uri uri = item.getUri();
        if (uri != null) {

            // First see if the URI can be opened as a plain text stream
            // (of any sub-type). If so, this is the best textual
            // representation for it.
            FileInputStream stream = null;
            try {
                // Ask for a stream of the desired type.
                AssetFileDescriptor descr = context.getContentResolver()
                        .openTypedAssetFileDescriptor(uri, "text/*", null);
                stream = descr.createInputStream();
                InputStreamReader reader = new InputStreamReader(stream,
                        "UTF-8");

                // Got it... copy the stream into a local string and return it.
                StringBuilder builder = new StringBuilder(128);
                char[] buffer = new char[8192];
                int len;
                while ((len = reader.read(buffer)) > 0) {
                    builder.append(buffer, 0, len);
                }
                return builder.toString();

            } catch (FileNotFoundException e) {
                // Unable to open content URI as text... not really an
                // error, just something to ignore.

            } catch (IOException e) {
                // Something bad has happened.
                Log.w("ClippedData", "Failure loading text", e);
                return e.toString();

            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                    }
                }
            }

            // If we couldn't open the URI as a stream, then the URI itself
            // probably serves fairly well as a textual representation.
            return uri.toString();
        }

        // Finally, if all we have is an Intent, then we can just turn that
        // into text. Not the most user-friendly thing, but it's something.
        Intent intent = item.getIntent();
        if (intent != null) {
            return intent.toUri(Intent.URI_INTENT_SCHEME);
        }

        // Shouldn't get here, but just in case...
        return "";
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {       
    	
    	//Toast.makeText(this, "MyService Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		/*
		 Handler handler = new Handler(Looper.getMainLooper());

		   handler.post(new Runnable() {

		           @Override
		           public void run() {
		        	  
		        	   Message msgToActivity = new Message();
						msgToActivity.what = 0;
					msgToActivity.obj = full;
					MainActivity.mUiHandler.sendMessage(msgToActivity);
		    				}
			    
		       });
	  */
		try{
		notificationToast = intent.getExtras().getInt("notificationToast");
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		return START_STICKY;

    }
    @Override
    public IBinder onBind(Intent arg0) {       
        return null;
    }
    @Override
	public void onDestroy() {
        Intent intent = new Intent("com.android.techtrainner");
        sendBroadcast(intent);
    }


}


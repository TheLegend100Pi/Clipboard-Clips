package com.Project100Pi.clip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.Project100Pi.clip.targets.ActionViewTarget;
import com.Project100Pi.clip.targets.ViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private static String TAG = MainActivity.class.getSimpleName();
	 
	ListView mDrawerList;
	RelativeLayout mDrawerPane;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	 
	ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
		private JazzyListView myListView;
		MyDB db = new MyDB(this);
		String full="";
		private SimpleAdapter sa;
		Parcelable state = null;
	  ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	  int selectCount = 0;
	  PopupMenu arrPop= null;
	  List<ClipObject> clips;
	  List<ClipObject> clipsSelected = new ArrayList<ClipObject>();
	  ClipObject temp  = null;
	  int lastVisiblePosition = 0;
	 final int RECENT_FIRST=1;
	 final int RECENT_LAST=2;
	 final int APPNAME_ASCEND=3;
	 final int APPNAME_DESCEND=4;
	 final int FREQUENTLY_USED=5;
	 int arrangement = RECENT_FIRST;
	 MyTextView emptyTextView = null;
	 MyTextView emptyTextView1 = null;
	 RelativeLayout relLayout =  null;
	 int multiSelectionOn = 0;
	 int drawerShow = 0;
	 int drawerSelectPosition = 0;
	 String currentClipView = "clips";
	 
	 // All Static variables
	    // Database Version
	    private static final int DATABASE_VERSION = 1;
	 
	    // Database Name
	    private static final String DATABASE_NAME = "clipManager";
	 
	    // Contacts table name
	    private static final String TABLE = "clips";
	    
	    private static final String MY_TABLE = "myClips";
	 
	    // Contacts Table Columns names
	    private static final String KEY_ID = "id";
	    private static final String KEY_NAME = "clip";


	public void showListData(String tableName){
		currentClipView = tableName;
		clips = db.getAllClips(tableName); 
		if(clips.isEmpty()){
			list.clear();
			try{
			sa.notifyDataSetChanged();
			}catch(NullPointerException e)
			{
				e.printStackTrace();
			}
		 emptyTextView.setVisibility(View.VISIBLE);    
		 emptyTextView1.setVisibility(View.VISIBLE);
			return;
		}
		else {
			emptyTextView.setVisibility(View.GONE);	
			emptyTextView1.setVisibility(View.GONE);
		}
	
	 arrangeByFunc(clips, arrangement);	
	}		
	
					
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
				    // Inflate the menu items for use in the action bar
				    MenuInflater inflater = getMenuInflater();
				    inflater.inflate(R.menu.main_activity_acitons, menu);
				    return super.onCreateOptionsMenu(menu);
	}
	/*		  
	// Called when invalidateOptionsMenu() is invoked
	public boolean onPrepareOptionsMenu(Menu menu) {
	    // If the nav drawer is open, hide action items related to the content view
	    boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	    menu.findItem(R.id.delete_id).setVisible(!drawerOpen);
	    return super.onPrepareOptionsMenu(menu);
	}
	*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		if (mDrawerToggle.onOptionsItemSelected(item)) {
		      return true;
		    }
	    switch (item.getItemId()) {
	        case R.id.action_delete:
	        	new AlertDialog.Builder(this)
	            .setTitle("Warning")
	            .setMessage("Are you sure you want to delete all the Clips?")
	            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	                @Override
					public void onClick(DialogInterface dialog, int which) { 
	                    // continue with delete
	                	db.deleteTable(currentClipView);
	                	showListData(currentClipView);
	    	            refreshScreen();
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
	            
	        case R.id.action_arrange:   
	        	View view = findViewById(R.id.action_arrange);
	        	showMenu(item,view);
	        	return true;

	        default:
	            return super.onOptionsItemSelected(item);
	    }
	
	}
	public void showMenu(MenuItem item,View v){
	    PopupMenu popupMenu = new PopupMenu(this,v); 
	    popupMenu.inflate(R.menu.arrange_actions);
	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				// TODO Auto-generated method stub
				switch(arg0.getItemId()){
				case R.id.arr_appName_ascend:
					arrangement = APPNAME_ASCEND;
					
		            break;
		        case R.id.arr_appName_descend:
		        	arrangement = APPNAME_DESCEND;
		            break;
		        case R.id.arr_recent_last:
		        	arrangement = RECENT_LAST;
		            break;
		        case R.id.arr_recent_first:
		        	arrangement = RECENT_FIRST;
		        
		            break;  
		        case R.id.arr_freqUsed:
		        	arrangement = FREQUENTLY_USED;
		        	break;
				}
				arrangeByFunc(clips, arrangement);
				return true;
			}
		});
	    popupMenu.show();
	}

	public void refreshScreen(){
		list.clear();
		showListData(currentClipView);
		if(!clips.isEmpty()){
			myListView.setVisibility(View.VISIBLE);
		myListView.setAdapter(sa);
		sa.notifyDataSetChanged();
		
		}
		else{
			list.clear();
			myListView.setVisibility(View.INVISIBLE);
			//relLayout.setBackgroundColor(Color.parseColor("#1c1c1c"));
		}
		myListView.post(fitsOnScreen);
	}
	
	
	private void selectItemFromDrawer(int position) {
		/*
	    Fragment fragment = new PreferencesFragment();
	 
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	            .replace(R.id.mainContent, fragment)
	            .commit();
	 
	    mDrawerList.setItemChecked(position, true);
	    setTitle(mNavItems.get(position).mTitle);
	    */
		drawerSelectPosition = position;
	    mDrawerLayout.closeDrawer(mDrawerPane);
	
		
	 
	    // Close the drawer
	    
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    mDrawerToggle.syncState();
	}
   @SuppressWarnings("deprecation")
@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	       // String pastedata;
			db = new MyDB(this);
	   
		overridePendingTransition(R.animator.activity_open_translate,R.animator.activity_close_scale);
	    setContentView(R.layout.activity_main);
	    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	       arrangement = sharedPreferences.getInt("arrangement", 0);
	       currentClipView = sharedPreferences.getString("currentClipView", "clips");
	       if(currentClipView.equals("clips")){
	   		getActionBar().setTitle("Clipboard Clips");
	   		}else if(currentClipView.equals("myClips")){
	   			getActionBar().setTitle("My Notes");	
	   		}
	    emptyTextView = (MyTextView) findViewById(R.id.emptyView);
	    emptyTextView1 = (MyTextView) findViewById(R.id.emptyView1);
	     relLayout = (RelativeLayout) findViewById(R.layout.activity_main);
		myListView = (JazzyListView) findViewById(R.id.jazzyListView1);
		registerForContextMenu(myListView);
		myListView.setDivider(null);
		startService(new Intent(this, MyService.class));
		
		getActionBar().setDisplayHomeAsUpEnabled(true); 
		FloatingActionButton mFab = (FloatingActionButton)findViewById(R.id.fab);
		//mFab.attachToListView(myListView);
		
		mNavItems.add(new NavItem("Clipboard", "Clips", R.drawable.clipboard_icon));
	    mNavItems.add(new NavItem("My Notes", " Clips", R.drawable.notes_icon2));
	    mNavItems.add(new NavItem("About", "Get to know about us", R.drawable.ic_action_about));
	 
	    // DrawerLayout
	    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
	 
	    // Populate the Navigtion Drawer with options
	    mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
	    mDrawerList = (ListView) findViewById(R.id.navList);
	    DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
	    mDrawerList.setAdapter(adapter);
	 
	    // Drawer Item click listeners
	    mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            selectItemFromDrawer(position);
	        }
	    });
	    
	    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
	        @Override
	        public void onDrawerOpened(View drawerView) {
	            super.onDrawerOpened(drawerView);
	            drawerShow =1;
	            invalidateOptionsMenu();
	        }
	     
	        @Override
	        public void onDrawerClosed(View drawerView) {
	            super.onDrawerClosed(drawerView);
	            Log.d(TAG, "onDrawerClosed: " + getTitle());
	            drawerShow = 0;
	            invalidateOptionsMenu();
	            switch (drawerSelectPosition) {
	    		case 0:
	    			getActionBar().setTitle("Clipboard Clips");
	    			showListData("clips");
	    			break;
	    		case 1:
	    			getActionBar().setTitle("My Notes");
	    			showListData("myClips");
	    			break;
	    		case 2:
	    			Intent intent = new Intent(MainActivity.this,AboutActivity.class);
	                startActivity(intent);
	    		break;
	    		default:
	    			
	    			break;
	            }
	        }
	    };
	     
	    mDrawerLayout.setDrawerListener(mDrawerToggle);
		showListData(currentClipView);
		
		myListView.setChoiceMode(JazzyListView.CHOICE_MODE_MULTIPLE_MODAL);
		myListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				multiSelectionOn = 1;
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {  
				// TODO Auto-generated method stub
				multiSelectionOn = 0;
				selectCount = 0;
				clipsSelected.clear();
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				MenuInflater inflater1 = mode.getMenuInflater(); 
				inflater1.inflate(R.menu.my_context_menu, menu);
				
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId()) {
				case R.id.delete_id:
					new AlertDialog.Builder(MainActivity.this)
	        	    .setTitle("Warning")
	        	    .setMessage("Are you sure you want to delete the seleted Clip(s)?")
	        	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        	        @Override
						public void onClick(DialogInterface dialog, int which) { 
	        	            // continue with delete
	        	        	for (ClipObject current : clipsSelected) {
	        	        		db.deleteClip(currentClipView,Integer.parseInt(current.ind));
	        	        	}
	        	        	
	        	        	Toast.makeText(getApplicationContext(),selectCount+" clip(s) deleted", Toast.LENGTH_LONG).show();
	        	        	mode.finish();
	        	        	refreshScreen();
	        	        	selectCount=0;
	        			
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

				default:
					
					return true;
							}
					
			
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				// TODO Auto-generated method stub
				Boolean again = true;
				temp = clips.get(position);
				if(!clipsSelected.isEmpty()){
					for(ClipObject curr: clipsSelected){
						if(curr.ind.equals(temp.ind)){
							clipsSelected.remove(temp);
							selectCount--;
							mode.setTitle(selectCount + " item(s) selected");
							again = false;
							break;
					
							}
					
						}
					}
			if(again){
				selectCount++;
				mode.setTitle(selectCount + " item(s) selected");
				temp = clips.get(position);
				clipsSelected.add(temp); 
			}	
				
			}
		} );
		myListView.post(fitsOnScreen) ; 
		
		    myListView.setOnItemClickListener(new OnItemClickListener() {

		        @Override
		        public void onItemClick(AdapterView<?> parent, View view, int position,
		                long id) 
		        {
		        	if(drawerShow == 1){
		        		return;
		        	}
		        	state = myListView.onSaveInstanceState();
		        	ClipObject send = clips.get(position);
		            Intent intent = new Intent(MainActivity.this,ShowListItem.class);
		            intent.putExtra("id", send);
		            intent.putExtra("tableName", currentClipView);
		            startActivity(intent);

		        }

		    });
		    
		//ImageButton newClip = (ImageButton) findViewById(R.id.ImageButton1); 
		mFab.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	Intent intent = new Intent(MainActivity.this,EditTextAction.class);
	           // intent.putExtra("id", );
	            startActivityForResult(intent,1);
            	
            }
		
		});
		
	
		AdView adView = (AdView) findViewById(R.id.adView);    
		AdRequest adRequest = new AdRequest.Builder()
			
			.build();
		adView.loadAd(adRequest);
		
		
	ImageView homebutton = (ImageView) findViewById(android.R.id.home);
	homebutton.setPadding(30, 0, 0, 0);
	ViewTarget target = new ViewTarget(R.id.fab, this);
	new ShowcaseView.Builder(this)
	.setTarget(target)
    .setContentTitle("Add Clip")
    .setContentText("You can add your new note anytime by touching this button.")
    .hideOnTouchOutside()
    .setStyle(R.style.ShowcaseView)
    .build();
		 	  }
   
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//overridePendingTransition(R.animator.activity_open_scale,R.animator.activity_close_translate);

	    if (requestCode == 1) {
	        if(resultCode == RESULT_OK){
	        	showListData("myClips");
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        	
	        }
	    }
	}//onActivityResult
   
   
   public void arrangeByFunc(final List<ClipObject> clips, final int arrType){
		list.clear();
		switch (arrType){
			
		case RECENT_FIRST:
		case RECENT_LAST:	
		Collections.sort(clips,new Comparator<ClipObject>() {
			SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy  hh:mm:ss a");
			@Override
			public int compare(ClipObject lhs, ClipObject rhs) {
				// TODO Auto-generated method stub
					try {
						if(arrType == RECENT_FIRST){
						return f.parse(rhs.dateTime).compareTo(f.parse(lhs.dateTime));
						}
						else {
							return f.parse(lhs.dateTime).compareTo(f.parse(rhs.dateTime));	
						}
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						throw new IllegalArgumentException(e);
					}
			}
		});
		break;
		case APPNAME_ASCEND:
		case APPNAME_DESCEND:
			Collections.sort(clips,new Comparator<ClipObject>() {
				@Override
				public int compare(ClipObject lhs, ClipObject rhs) {
					// TODO Auto-generated method stub
							if(arrType == APPNAME_ASCEND){
							return lhs.appName.compareTo(rhs.appName);
							}
							else {
								return rhs.appName.compareTo(lhs.appName);	
							}
				}
			});
			break;
		case FREQUENTLY_USED:
			Collections.sort(clips,new Comparator<ClipObject>() {
				@Override
				public int compare(ClipObject lhs, ClipObject rhs) {
					// TODO Auto-generated method stub
							return rhs.copyCount - (lhs.copyCount);
						
				}
			});
			break;
			default: break;
			
		}
		HashMap<String,String> item;
		for (ClipObject curr : clips) {
		  item = new HashMap<String,String>();
		 item.put( "line1", curr.clip);
		  item.put( "line2", curr.dateTime);
		item.put("ind", curr.ind);
		 item.put("appName", curr.appName);
		list.add( item );
				}
		
		sa = new SimpleAdapter(this, list,
	      R.layout.my_two_lines,
	 new String[] { "line1","line2","appName" },
		new int[] {R.id.line_a,R.id.line_b,R.id.line_app}){
			@Override
			public View getView( final int position, View convertView, ViewGroup parent) {
				
			    View v =super.getView(position, convertView, parent);

			    if( convertView != null )
			        v = convertView;
			    else{
			    	//LayoutInflater inflater=getLayoutInflater();
			    	//v=inflater.inflate(R.layout.my_two_lines, parent, false);
			    	//TextView t1 = (TextView) v.findViewById(R.id.line_app);
			    ImageButton button = (ImageButton)v.findViewById(R.id.ImageButton1);
			   //t1.setTag(position);
			    button.setTag(position);
			    button.setOnClickListener(new OnClickListener() {
	                public void onClick(View v) {
	                	if(multiSelectionOn == 1){
		                	return;	
		                	}
	                	PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v); 
	            	    popupMenu.inflate(R.menu.long_click_actions);
	            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
	            			
	            			@Override
	            			public boolean onMenuItemClick(MenuItem item) {
								
	            				ClipObject send = clips.get(position);
	            			       final int index = Integer.parseInt(send.ind);       	
	            			       switch(item.getItemId()){
	            			       case R.id.cnt_mnu_copy:
	            			    	  ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
	            			    	  ClipData clipToCopy = ClipData.newPlainText("clipToCopy", send.clip);
	            			    	  clipBoard.setPrimaryClip(clipToCopy);
	            			    	  db.updateClipcount(currentClipView,send.ind,send.clip,send.dateTime,send.appName,(send.copyCount+1));
	            			    	   break;
	            			           case R.id.cnt_mnu_edit:
	            			               //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();
	            			        	    state = myListView.onSaveInstanceState();
	            				            Intent intent = new Intent(MainActivity.this,EditTextAction.class);
	            				            intent.putExtra("id", send);
	            				            intent.putExtra("tableName", currentClipView);
	            				            startActivity(intent);
	            			               break;
	            			           case R.id.cnt_mnu_delete:
	            			        	   new AlertDialog.Builder(MainActivity.this)
	            			        	    .setTitle("Warning")
	            			        	    .setMessage("Are you sure you want to delete this Clip?")
	            			        	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	            			        	        @Override
	            								public void onClick(DialogInterface dialog, int which) { 
	            			        	            // continue with delete
	            			        	        	db.deleteClip(currentClipView,index);
	            			        	        	refreshScreen();
	            			        	        	//showListData();
	            			        	        	//Toast.makeText(this, "Clip Deleted" , Toast.LENGTH_SHORT).show();
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
	            			        	   
	            			        	   break;
	            			           case R.id.cnt_mnu_share:
	            			        	   Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
	            			        	    sharingIntent.setType("text/plain");
	            			        	    String shareBody = send.clip;
	            			        	    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
	            			        	    startActivity(Intent.createChooser(sharingIntent, "Share via"));
	            			               break;

	            			       }
	            			       refreshScreen();
	            			       showListData(currentClipView);
	            			       return true;
	            				
	            			}
	            	    });
	            	    popupMenu.show();
	                	//Toast.makeText(MainActivity.this, "It works, pos=" + v.getTag(), Toast.LENGTH_LONG).show();
	                }
	                });
			   return v;
			}
				return v;
			}
		};
		
		
		
		myListView.setAdapter( sa );
	     myListView.setTransitionEffect(JazzyHelper.SLIDE_IN);
	     myListView.setVisibility(View.VISIBLE);
	
	
	}
    
   Runnable fitsOnScreen = new Runnable() {
	    @Override
	    public void run() {
	    	try{
	    	lastVisiblePosition= myListView.getLastVisiblePosition();
	    	if(lastVisiblePosition == myListView.getCount() - 1 && myListView.getChildAt(lastVisiblePosition).getBottom() <= myListView.getHeight()) {
	            // It fits!
	        	relLayout.setBackgroundColor(Color.parseColor("#4c4c4c"));
	        }
	        else {
	            // It doesn't fit...
	        	relLayout.setBackgroundColor(Color.parseColor("#4c4c4c"));

	        }
	    	}
	    	catch(NullPointerException e){
	    		e.printStackTrace();
	    		return;
	    		
	    	}
	    }
	};
   
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
	}
	
	@Override
	protected void onStop() {
	// TODO Auto-generated method stub
	super.onStop();
	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt("arrangement", arrangement);
    editor.putString("currentClipView", currentClipView);
    editor.commit();
	}
	@Override
	protected void onRestart(){
		super.onRestart();
		// setContentView(R.layout.activity_main);
		if(currentClipView.equals("clips")){
			drawerSelectPosition = 0;
		}else if(currentClipView.equals("myClips")) {
			drawerSelectPosition=1;
		}
		refreshScreen();
		// Set new items
		myListView.setAdapter(sa);

		// Restore previous state (including selected item index and scroll position)
		myListView.onRestoreInstanceState(state);
	}
	 @Override
	 protected void onPause()
	 {
	 super.onPause();
	 //closing transition animations
	    state = myListView.onSaveInstanceState();

overridePendingTransition(R.animator.activity_open_scale,R.animator.activity_close_translate);
	 }
	 
} 






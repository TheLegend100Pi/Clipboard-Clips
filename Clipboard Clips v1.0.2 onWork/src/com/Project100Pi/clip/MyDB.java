package com.Project100Pi.clip;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;


public class MyDB extends SQLiteOpenHelper {
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
 
    public MyDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		  String CREATE_MY_CLIPS_TABLE = "CREATE TABLE IF NOT EXISTS " + MY_TABLE + "("
	                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
	                + "createdAt TEXT, fromApp TEXT, copyCount INTERGER DEFAULT 0)";
	        db.execSQL(CREATE_MY_CLIPS_TABLE);
	        
		String CREATE_CLIPS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + "createdAt TEXT, fromApp TEXT, copyCount INTERGER DEFAULT 0)";
        db.execSQL(CREATE_CLIPS_TABLE);
        
      
       
       // Toast.makeText(context, "MyService Created", Toast.LENGTH_LONG).show();
		//db.execSQL("CREATE TABLE IF NOT EXISTS clipboard(SNo INTEGER PRIMARY KEY,clip TEXT);");
		
	}
	
	public void insertClip(String tableName, String currClip, String dateTime,String appName) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_NAME, currClip); 
	    values.put("createdAt", dateTime);
	    values.put("fromApp", appName);
	    // Inserting Row
	    db.insert(tableName, null, values);
	    db.close(); // Closing database connection
	}

	public List<ClipObject> getAllClips(String tableName) {
        List<ClipObject> clipList = new ArrayList<ClipObject>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + tableName;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	int ind=cursor.getInt(0);
            	String index=Integer.toString(ind);
            	ClipObject clipObj = new ClipObject(index,cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getInt(4));

                // Adding clip to list
                clipList.add(clipObj);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return clipList;
	}
	
	public void updateClip(String tableName, String index, String Clip,String Date, String appName, int copyCount){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, index);
		values.put(KEY_NAME, Clip);
		values.put("createdAt", Date+" (Edited)");
		values.put("fromApp", appName);
		values.put("copyCount", copyCount);
		long retvalue = db.insertWithOnConflict(tableName, null, values,5);
		//db.execSQL("update "+TABLE+" set "+KEY_NAME+" = '"+Clip+"',createdAt = '"+Date+" (Edited)' where id='"+index+"'");
		
		db.close();
	}
	public void updateClipcount(String tableName, String index,String Clip,String Date,String appName,int copyCount){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, index);
		values.put(KEY_NAME, Clip);
		values.put("createdAt", Date);
		values.put("fromApp", appName);
		values.put("copyCount", copyCount);
		long retvalue = db.insertWithOnConflict(tableName, null, values,5);
		//db.execSQL("update "+TABLE+" set "+KEY_NAME+" = '"+Clip+"',createdAt = '"+Date+" (Edited)' where id='"+index+"'");
		db.close();
		
	}
	public void deleteClip(String tableName, int index) {
		SQLiteDatabase db = this.getWritableDatabase();
		String ind = String.valueOf(index);
        db.execSQL("delete from "+tableName+" where id='"+ind+"'");
    }
	
	public void deleteTable(String tableName){
		
		SQLiteDatabase db = this.getWritableDatabase();
		 db.execSQL("DELETE FROM " + tableName);
		 onCreate(db);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	

}

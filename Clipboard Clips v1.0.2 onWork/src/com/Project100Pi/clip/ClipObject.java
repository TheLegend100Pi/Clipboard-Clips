package com.Project100Pi.clip;

import android.os.Parcel;
import android.os.Parcelable;

public class ClipObject implements Parcelable {
	
	String ind;
	String clip;
	String dateTime;
	String appName;
	int copyCount;



public ClipObject(String ind,String clip, String dateTime,String appName,int copyCount){
	
	
	this.clip =clip;
	this.dateTime = dateTime;
	this.ind =ind;
	this.appName=appName;
	this.copyCount=copyCount;
}

public ClipObject(Parcel in) {
	
    this.clip = in.readString();
    this.dateTime = in.readString();
    this.ind=in.readString();
    this.appName=in.readString();
    this.copyCount=in.readInt();
}

public  ClipObject() {

}

@SuppressWarnings("rawtypes")
public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    @Override
	public ClipObject createFromParcel(Parcel in) {
        return new ClipObject(in);
    }

    @Override
	public ClipObject[] newArray(int size) {
        return new ClipObject[size];
    }
};

@Override
public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
}



@Override
public void writeToParcel(Parcel dest, int flags) {
	
	// TODO Auto-generated method stub
	
	dest.writeString(clip);
    dest.writeString(dateTime);
    dest.writeString(ind);
    dest.writeString(appName);
    dest.writeInt(copyCount);
}

}
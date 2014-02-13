package org.lockerz.compare;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class FirstRun extends Activity {
	
	/**
     * get if this is the first run
     *
     * @return returns true, if this is the first run
     */
        public boolean getFirstRun(int x) {
        	if(x==1)
        return mPrefs.getBoolean("firstRun", true);
        	else if(x==0)
        	return mPrefs2.getBoolean("firstRun", true);
        	else 
           return mPrefs3.getBoolean("firstRun", true);
     }
     
     /**
     * store the first run
     */
     public void setRunned(int x) {
    	 if (x==1){
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean("firstRun", false);
        edit.commit();
    	 }
    	 else if(x==0)
    	 {
    		 SharedPreferences.Editor edit = mPrefs2.edit();
    	        edit.putBoolean("firstRun", false);
    	        edit.commit();
    	 }
    	 else
    	 {
    		 SharedPreferences.Editor edit = mPrefs3.edit();
 	        edit.putBoolean("firstRun", false);
 	        edit.commit();
    	 }
     }
     
     SharedPreferences mPrefs;
     SharedPreferences mPrefs2;
     SharedPreferences mPrefs3;
     
     /**
     * setting up preferences storage
     */
     public void firstRunPreferences() {
        Context mContext = this.getApplicationContext();
        mPrefs = mContext.getSharedPreferences("myAppPrefs", 0); //0 = mode private. only this app can read these preferences
        mPrefs2= mContext.getSharedPreferences("myAppPrefs2", 0);
        mPrefs3= mContext.getSharedPreferences("myAppPrefs3", 0);
     }

	

}

package utilities;

import android.content.Context;
import android.content.SharedPreferences;


public class Config {
	
	private SharedPreferences.Editor editor;
	private SharedPreferences mPrefs;
	private Context context;
	public Config(Context context){
	    mPrefs = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	    editor = mPrefs.edit();
	}
	
	
	
	public void store(String name,String value){
	//	Log.d("config store", name + "=" + value);
		editor.putString(name, value);
		editor.commit();
	}
	public void store(String name,int value){
	//	Log.d("config store", name + "=" + value);
		editor.putInt(name, value);
		editor.commit();
	}
	
	
	public String get(String name){
		String result;
		result = mPrefs.getString(name,"");
	//	Log.d("config get",result);
		return result;
	}
	
	public String get(String name, String defValue){
		String result;
		result = mPrefs.getString(name,defValue);
	//	Log.d("config get",result);
		return result;
	}
	
	public int get(String name, int defValue){
		int result;
		result = mPrefs.getInt(name,defValue);
	//	Log.d("config get",String.valueOf(result));
		return result;
	}
	public void storeLocation(String name){
		store("location",name);
	}
	
	public String getLocation(){
		return get("location");
	}

}

package com.example.inducred;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.media.MediaPlayer;

public class MainActivity extends Activity {
	PlaySound ps;
	private MediaPlayer mediaPlayer;
	private TextView tvText;
	private TextView mv;
	private TextView tvPassword;
private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this.getApplicationContext();
		ps = new PlaySound(context);
		getSSID();
		getPassword();
	}

	
	private void getPassword() {
		tvPassword = (TextView) findViewById(R.id.tvPassword);
		
		tvPassword.setText(ps.getPassword());
	}


	public void startButton(View v) {
		Log.d("start", "clicked");
		tvPassword = (TextView) findViewById(R.id.tvPassword);
		String ssid = tvText.getText().toString();
		String password = tvPassword.getText().toString();	
		
		ps.start(password + "," + ssid);
	}
	
	public void savePassword(View v){	
		tvPassword = (TextView) findViewById(R.id.tvPassword);
		String password = tvPassword.getText().toString();
		ps.savePassword(password);
	}
	public void erasePassword(View v){
		tvPassword = (TextView) findViewById(R.id.tvPassword);
		tvPassword.setText("");
	}
	
	public void abort(View v){
		ps.abort();
	}
	@Override
	protected void onStop() {
		super.onStop();
		ps.abort();
	}

	private void getSSID(){
		 WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		   Log.d("wifiInfo", wifiInfo.toString());
		   Log.d("SSID",wifiInfo.getSSID());
		   tvText = (TextView) findViewById(R.id.text);
		   tvText.setText(wifiInfo.getSSID().replace("\"", ""));
	}

}

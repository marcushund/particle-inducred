package com.example.inducred;

import android.os.Handler;
import android.util.Log;

public class Timer  {
	private Handler handler = new Handler();
	private long interval = 100;
	private int cnt = 0;
	TimerCallback callback;
	
	public Timer(TimerCallback callback){
		this.callback = callback;
	}
	
	private Runnable runnable = new Runnable() {
		public void run() {
			handler.postDelayed(runnable, interval);
			if (cnt > 0) {
				cnt--;
			}
			if (cnt == 1) {
				callback.timerReady();
			}
		}
	};

	
	
	public void set(int time) {
		cnt = time;
		handler.postDelayed(runnable, interval);
		
	}
	public void reset(){
		cnt=0;
	}
	public boolean isRunning(){
		if (cnt>0)
			return true;
		else
			return false;
	}

}

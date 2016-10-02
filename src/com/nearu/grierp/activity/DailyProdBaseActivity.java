package com.nearu.grierp.activity;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.nearu.grierp.web.service.DailyProdService;

public abstract class DailyProdBaseActivity extends BaseActivity{
	public static final String TAG = "DailyProdBaseActivity";
	DailyProdService service = null;
	private float px, py;
	boolean isNextScene = false;
	boolean isFinishing = false;
	private static final int MOVE_STEP = 15;
	
	public DailyProdBaseActivity(){
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x, y;
		x = event.getX();
		y = event.getY();
		if(!isGestureable){
			return true;
		}
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "WOW, DOWN!");
			isNextScene = false;
			isFinishing = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (x - px > MOVE_STEP && !isNextScene){
				Log.d(TAG, "Right move");
				nextStep();
				isNextScene = true;
			}
			if (x - px < -MOVE_STEP && !isFinishing){
				Log.d(TAG, "Left move");
				isFinishing = true;
				preStep();
				this.finish();
			}
			break;
		}
		px = x;
		py = y;
		return true;
	}
	abstract protected void preStep();
	abstract protected void nextStep();
}

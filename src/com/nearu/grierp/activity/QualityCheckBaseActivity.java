package com.nearu.grierp.activity;

import android.util.Log;
import android.view.MotionEvent;

abstract public class QualityCheckBaseActivity extends BaseActivity{
	
	private float px, py;
	boolean isNextScene = false;
	boolean isFinishing = false;
	private static final int MOVE_STEP = 15;
	
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
				nextScene();
				isNextScene = true;
			}
			if (x - px < -MOVE_STEP && !isFinishing){
				Log.d(TAG, "Left move");
				isFinishing = true;
				preScene();
				this.finish();
			}
			break;
		}
		px = x;
		py = y;
		return true;
	}
	abstract protected void nextScene();
	abstract protected void preScene();
}

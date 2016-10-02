package com.nearu.grierp.comm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class CommonReceiver  extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent mainIntent = new Intent();
		mainIntent.setComponent(new ComponentName("com.nearu.grierp",   
                "com.nearu.grierp.activity.LoginActivity"));  
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mainIntent);
	}
}

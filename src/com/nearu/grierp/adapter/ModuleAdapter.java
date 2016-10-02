package com.nearu.grierp.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.nearu.grierp.R;
import com.nearu.grierp.entity.ModuleEntity;
import com.nearu.grierp.view.SlidingLayout;

public class ModuleAdapter extends BaseAdapter {
	public static final String TAG = "ModuleAdapter";
	private ArrayList<ModuleEntity> mList = null;
	private LayoutInflater mInflater = null;
	private int screenHeight;
	private int itemHeight;
	public ModuleAdapter(Context context, ArrayList<ModuleEntity> list) {
		mList = list;
		mInflater = LayoutInflater.from(context);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		screenHeight = wm.getDefaultDisplay().getHeight();
		itemHeight = screenHeight/mList.size();
	}
	@Override
	public int getCount() {
		
		return mList.size();
	}

	@Override
	public Object getItem(int pos) {
		
		return mList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		
		return pos;
	}
	private MyOnTouchListener listener = new MyOnTouchListener();
	@Override
	public View getView(int pos, View view, ViewGroup parent) {
		ViewHolder holder;
		if(view == null) {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.module_item, null);
			holder.mImageView = (ImageView) view.findViewById(R.id.md_icon);
			holder.mTextView = (TextView) view.findViewById(R.id.md_text);
			view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, itemHeight));
			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
		holder.mTextView.setText(mList.get(pos).getModuleName());
		holder.mImageView.setImageResource(mList.get(pos).getModuleImg());
		return view;
	}
	
	private class ViewHolder {
		public ImageView mImageView;
		public TextView mTextView;
	}
	
	class MyOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if(!SlidingLayout.isSliding()){
					Log.d(TAG, "NOT SLIDING");
					v.setBackgroundResource(R.drawable.light_blue);
				}else {
					v.setBackgroundResource(R.drawable.listitem_simple);
				}
				break;
			case MotionEvent.ACTION_UP:
				v.setBackgroundResource(R.drawable.listitem_simple);
				break;
			}
			return false;
		}
	}
}

package com.nearu.grierp.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nearu.grierp.R;

public class tzAdapter extends BaseAdapter {
	
	private Context cxt = null;
	private LayoutInflater mInflater = null;
	private ArrayList<String[]> data = null;
	
	public tzAdapter(Context cxt, ArrayList<String[]> data) {
		this.cxt = cxt;
		this.data = data;
		mInflater = LayoutInflater.from(cxt);
		
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.tz_item, null);
			holder.tzno = (TextView) convertView.findViewById(R.id.tv_tzno);
			holder.zc = (TextView) convertView.findViewById(R.id.tv_zc);
			holder.sm = (TextView) convertView.findViewById(R.id.tv_sm);
			holder.wwgs = (TextView) convertView.findViewById(R.id.tv_wwgs);
			holder.kgs = (TextView) convertView.findViewById(R.id.tv_kgs);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String[] item = data.get(position);
		holder.tzno.setText(item[0]);
		holder.zc.setText(item[1]);
		holder.sm.setText(item[2]);
		holder.wwgs.setText(item[3]);
		holder.kgs.setText(item[4]);
		return convertView;
	}
	class ViewHolder {
		TextView tzno;
		TextView zc;
		TextView sm;
		TextView wwgs;
		TextView kgs;
	}
}

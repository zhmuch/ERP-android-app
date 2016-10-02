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
import com.nearu.grierp.adapter.tzAdapter.ViewHolder;

public class DailyAdapter extends BaseAdapter {

	private Context cxt = null;
	private LayoutInflater mInflater = null;
	private ArrayList<String[]> data = null;
	
	public DailyAdapter(Context cxt, ArrayList<String[]> data) {
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
			convertView = mInflater.inflate(R.layout.daily_prod_record_item, null);
			holder.ygdh = (TextView) convertView.findViewById(R.id.tv_ygdh);
			holder.ygxm = (TextView) convertView.findViewById(R.id.tv_ygxm);
			holder.scsl = (TextView) convertView.findViewById(R.id.tv_scsl);
			holder.scslf = (TextView) convertView.findViewById(R.id.tv_scslf);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String[] item = data.get(position);
		holder.ygdh.setText(item[0]);
		holder.ygxm.setText(item[1]);
		holder.scsl.setText(item[2]);
		holder.scslf.setText(item[3]);
		return convertView;
	}
	class ViewHolder {
		TextView ygdh;
		TextView ygxm;
		TextView scsl;
		TextView scslf;
	}
}

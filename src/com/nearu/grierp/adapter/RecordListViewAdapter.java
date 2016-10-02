package com.nearu.grierp.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nearu.grierp.R;
import com.nearu.grierp.entity.RecordEntity;

public class RecordListViewAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private ArrayList<RecordEntity> mRecordList;
	public RecordListViewAdapter(Context context, ArrayList<RecordEntity> recordList){
		mRecordList = recordList;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		
		return mRecordList.size();
	}

	@Override
	public Object getItem(int pos) {

		return mRecordList.get(pos);
	}

	@Override
	public long getItemId(int pos) {

		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = (LinearLayout)mInflater.inflate(R.layout.recorder_item, null);
			holder = new ViewHolder();
			holder.id = (TextView) convertView.findViewById(R.id.tv_id);
			holder.tm = (TextView) convertView.findViewById(R.id.tv_tm);
			holder.num = (TextView) convertView.findViewById(R.id.tv_num);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		RecordEntity entity = mRecordList.get(pos);
		holder.id.setText(""+pos);
		holder.tm.setText(entity.getTM());
		holder.num.setText(entity.getQTY());
		return convertView;
	}
	class ViewHolder {
		TextView id;
		TextView tm;
		TextView num;
	}

}

package com.nearu.grierp.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nearu.grierp.R;
import com.nearu.grierp.entity.JYDEntity;

public class JYDAdapter extends BaseAdapter{
	private Context cxt;
	private LayoutInflater mInflater;
	ArrayList<JYDEntity> dataList = null;
	public JYDAdapter(ArrayList<JYDEntity> dataList, Context cxt){
		this.cxt = cxt;
		this.dataList = dataList;
		mInflater = LayoutInflater.from(cxt);
	}
	@Override
	public int getCount() {
		
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {

		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = (View) mInflater.inflate(R.layout.jyd_item, null);
			holder = new ViewHolder();
			holder.xmmc = (TextView) convertView.findViewById(R.id.tv_jyxm);
			holder.ygxm = (TextView) convertView.findViewById(R.id.tv_xm);
			holder.cz = (TextView) convertView.findViewById(R.id.tv_cz); 
			holder.bhgyy = (TextView) convertView.findViewById(R.id.tv_bhgyy);
			holder.bhgsl = (TextView) convertView.findViewById(R.id.tv_bhgsl);
			holder.bz = (TextView) convertView.findViewById(R.id.tv_bz);
			
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		JYDEntity entity = dataList.get(pos);
		holder.xmmc.setText(entity.xmmc);
		holder.cz.setText(entity.cz);
		holder.bhgyy.setText(entity.bhgyy);
		holder.bhgsl.setText(entity.bhgl);
		holder.bz.setText(entity.bz);
		holder.ygxm.setText(entity.ygxm);
		return convertView;
	}
	class ViewHolder {
		TextView ygxm;
		TextView xmmc;
		TextView cz;
		TextView bhgsl;
		TextView bhgyy;
		TextView bz;
	}
	
}


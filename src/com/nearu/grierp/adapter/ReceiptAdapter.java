package com.nearu.grierp.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nearu.grierp.R;

public class ReceiptAdapter extends BaseAdapter{

	ArrayList<String> mReceiptList;
	Context mContext;
	LayoutInflater mInflater;
	public ReceiptAdapter(Context context, ArrayList<String> receiptList){
		mContext = context;
		mReceiptList = receiptList;
		mInflater = LayoutInflater.from(mContext);
	}
	
	
	@Override
	public int getCount() {
		
		return mReceiptList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mReceiptList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		TextView receiptButton = null;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.receipt_item, null);
			receiptButton = (TextView) convertView.findViewById(R.id.btn_receipt);
			convertView.setTag(receiptButton);
		}else {
			receiptButton = (TextView) convertView.getTag();
		}
		receiptButton.setText(mReceiptList.get(pos));
		return convertView;
	}


}

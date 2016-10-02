package com.nearu.grierp.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter{
	private ArrayList<View>    viewList = null;
	private ArrayList<String> titleList = null;
	public ViewPagerAdapter(ArrayList<View> viewList, ArrayList<String> titleList){
		this.viewList = viewList;
		this.titleList = titleList;
	}
	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
    @Override  
    public CharSequence getPageTitle(int position) {  
        return titleList.get(position);  
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡         
        container.addView(viewList.get(position), 0);//添加页卡  
        return viewList.get(position);  
   }  


}

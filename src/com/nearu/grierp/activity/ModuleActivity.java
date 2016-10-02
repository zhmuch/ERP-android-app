package com.nearu.grierp.activity;


import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nearu.grierp.R;
import com.nearu.grierp.adapter.ModuleAdapter;
import com.nearu.grierp.comm.Config;
import com.nearu.grierp.comm.Constants;
import com.nearu.grierp.entity.ModuleEntity;
import com.nearu.grierp.view.SlidingLayout;
import com.nearu.grierp.web.service.GetPowerService;
public class ModuleActivity extends BaseActivity{

	public static final String TAG = "ModuleActivity";
	private ListView      mModuleListView      = null;
	private SlidingLayout mSlidingLayout = null;
	private ModuleAdapter mAdapter       = null;
	private Button        mMenuButton    = null;
	private ListView      mMenuListView  = null;
	private TextView      mTvInfo        = null;
	private ArrayList<ModuleEntity> mModuleList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		Log.d(TAG, "locale is " + cookie.getString("lang", "") + " " + locale);
		
	}
	
	void initView() {

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.module_layout);
		mSlidingLayout  = (SlidingLayout) findViewById(R.id.sliding_layout);
		mModuleListView = (ListView) findViewById(R.id.lt_module);
		mTvInfo = (TextView) findViewById(R.id.tv_info);
		
		mTvInfo.append(": "  + cookie.getString("compName",""));
		mModuleListView.setEnabled(true);
		mModuleListView.setDivider(null);
		mModuleList = new ArrayList<ModuleEntity>();
		
		for(int i = 0; i < Config.module.length; i++) {
			if(cookie.getString("lang", "").equals("CH"))
				mModuleList.add(new ModuleEntity(Config.module[i], Config.module_icon[i]));
			else 
				mModuleList.add(new ModuleEntity(Config.moduleEN[i], Config.module_icon[i]));
		}
		mAdapter = new ModuleAdapter(this, mModuleList);
		mModuleListView.setAdapter(mAdapter);
		
		mMenuListView   = (ListView) findViewById(R.id.lt_menu);
		ArrayAdapter<String> menuAdapter = 
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Config.menu[locale]);
		mMenuListView.setAdapter(menuAdapter);
		
		mMenuButton = (Button) findViewById(R.id.btn_menu);
		mSlidingLayout.setScrollEvent(mModuleListView);
		mMenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mSlidingLayout.setMenuHeight();
				mSlidingLayout.setMenuVisiable();
				if(mSlidingLayout.isLeftLayoutVisible()) {
					Log.v(TAG, "menu button is clicked");
					mSlidingLayout.scrollToRightLayout();
				}else {
					Log.v(TAG, "menu button is clicked!!!else ");
					mSlidingLayout.scrollToLeftLayout();
				}
			}
			
		});
		mModuleListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) 
			{
				if(pos != 1 && pos != 2) return;
				Intent intent = new Intent(ModuleActivity.this, ManageReceiptActivity.class);
				intent.putExtra("module", pos);
				ModuleActivity.this.startActivity(intent);
			}
		});
		
		mMenuListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) 
			{
				String[] open = {"打开","open"};
				String[] close = {"关闭", "close"};
				String[] confirm = {"确认", "ok"};
				String[] title = {"扫描声音", "scan sound"};
				switch(pos) {
				case 0: // É¨ÃèÉùÒô
					new AlertDialog.Builder(ModuleActivity.this).setTitle(title[locale]).
					setIcon(android.R.drawable.ic_dialog_info).
					setSingleChoiceItems(
							new String[] { open[locale], close[locale] }, 
							0,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									switch(which){
									case 0:
										editor.putBoolean("sound", true);
										break;
									case 1:
										editor.putBoolean("sound", false);
										break;
									}
									editor.commit();
								}
							}).setPositiveButton(confirm[locale], null).show();
					break;
				case 1:		
					ModuleActivity.this.finish();
					break;
				}
			}
			
		});
		

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Log.d(TAG, "BACK!!!!!!!!!!!");
			Intent MyIntent = new Intent(Intent.ACTION_MAIN);
			MyIntent.addCategory(Intent.CATEGORY_HOME);
			startActivity(MyIntent);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.clear();
		menu.add("扫描声音");
		menu.add("退出");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle().toString().equals("ÍË³ö")) {
			this.finish();
		}
		return true;
	}
}

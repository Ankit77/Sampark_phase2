package com.symphony.report;


import com.symphony.R;
import com.symphony.pager.TabsPagerAdapter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class SymphonyReport extends AppCompatActivity {
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_home);

		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		
		viewPager = (ViewPager)findViewById(R.id.pager);

		
		mAdapter.addPage(new CheckStatusReport());
		mAdapter.addPage(new DistributorReport());
		mAdapter.addPage(new NotificationReport());

		viewPager.setAdapter(mAdapter);

		Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
		setSupportActionBar(toolbar);
 		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
 		getSupportActionBar().setDisplayShowCustomEnabled(false);
 		getSupportActionBar().setIcon(android.R.color.transparent);
 		getSupportActionBar().setDisplayShowTitleEnabled(true);

 		getSupportActionBar().setTitle("eSampark Report");
       // SymphonyUtils.startWipeDataAlram(this);

	}
	

	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		
		 
		 finish();
         return super.onOptionsItemSelected(item);
 
		
	 
	 }
	 
	 
	

	
	
}

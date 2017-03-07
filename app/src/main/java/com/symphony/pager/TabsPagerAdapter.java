package com.symphony.pager;

import java.util.ArrayList;
import java.util.List;






import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> mFragments = new ArrayList<Fragment>();


	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		
		
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		
		
		return ((FragmentTitle)mFragments.get(position)).getTitle();
	}
	
	
	public boolean addPage(Fragment v){
		
		
		if(mFragments!=null){
			mFragments.add(v);
			this.notifyDataSetChanged();
		}

		
		return true;
	}
	
	
	
	
	@Override
	public Fragment getItem(int index) {

		
		return mFragments.get(index);
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return mFragments.size();
	}

}

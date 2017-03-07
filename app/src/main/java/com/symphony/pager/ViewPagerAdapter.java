package com.symphony.pager;

import com.symphony.report.CheckStatusReport;
import com.symphony.report.DistributorReport;
import com.symphony.report.NotificationReport;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	final int PAGE_COUNT = 3;
	// Tab Titles
	private String tabtitles[] = new String[] { "CHECK STATUS", "DISTRIBUTOR","NOTIFICATION"};
	Context context;

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {

			// Open FragmentTab1.java
		case 0:
			CheckStatusReport fragmenttab1 = new CheckStatusReport();
			return fragmenttab1;

			// Open FragmentTab2.java
		case 1:
			DistributorReport fragmenttab2 = new DistributorReport();
			return fragmenttab2;

			// Open FragmentTab3.java
		case 2:
			NotificationReport fragmenttab3 = new NotificationReport();
			return fragmenttab3;
		}
		return null;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabtitles[position];
	}
}

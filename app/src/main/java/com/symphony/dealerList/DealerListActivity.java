package com.symphony.dealerList;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.symphony.E_Sampark;
import com.symphony.R;
import com.symphony.model.MasterDataModel;

import java.util.ArrayList;

/**
 * Created by indianic on 12/06/17.
 */

public class DealerListActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {
    private ListView lvDealerList;
    private DealerAdapter dealerAdapter;
    private E_Sampark e_sampark;
    private SearchView searchView;
    private static String searchTerm;
    private MenuItem seachMenuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealerlist);
        e_sampark = (E_Sampark) getApplicationContext();
        loadActionBar();
        lvDealerList = (ListView) findViewById(R.id.lvdealerlist);
        loadData();

    }

    private void loadData() {
        ArrayList<MasterDataModel> dealerlist = e_sampark.getSymphonyDB().getMasterDataList();
        if (dealerlist != null && dealerlist.size() > 0) {
            dealerAdapter = new DealerAdapter(DealerListActivity.this, dealerlist);
            lvDealerList.setAdapter(dealerAdapter);
        }
    }

    private void loadActionBar() {


        Toolbar toolbar = (Toolbar) findViewById(R.id.dealer_toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setTitle("CHECK IN/OUT");
        getSupportActionBar().setTitle("Dealer List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setIcon(android.R.color.transparent);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public void onClick(View view) {
//        if (view == imgBack) {
//            finish();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dealer_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        seachMenuItem = menu.findItem(R.id.dealer_search);

        searchView =
                (SearchView) MenuItemCompat.getActionView(seachMenuItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        MenuItemCompat.setOnActionExpandListener(seachMenuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        searchTerm = "";
                        searchView.onActionViewCollapsed();
                        searchView.setQuery("", false);
                        searchView.clearFocus();
                        // Do something when collapsed
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Log.e("Search Text", s);
        if (dealerAdapter != null) {
            dealerAdapter.getFilter().filter(s.toString());
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Log.e("Search Text", s);
        if (dealerAdapter != null) {
            dealerAdapter.getFilter().filter(s.toString());
        }
        return true;
    }
}

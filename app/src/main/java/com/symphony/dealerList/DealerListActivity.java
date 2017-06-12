package com.symphony.dealerList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class DealerListActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView lvDealerList;
    private DealerAdapter dealerAdapter;
    private E_Sampark e_sampark;

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
        getSupportActionBar().setTitle("CHECK IN/OUT");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

package com.symphony.dealerList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.symphony.R;
import com.symphony.model.MasterDataModel;

import java.util.ArrayList;

/**
 * Created by indianic on 12/06/17.
 */

public class DealerAdapter extends BaseAdapter {

    private ArrayList<MasterDataModel> masterList;
    private Context context;
    private LayoutInflater inflater;
    public DealerAdapter(Context context, ArrayList<MasterDataModel> masterList)
    {
        this.context=context;
        this.masterList=masterList;
        inflater = LayoutInflater.from(this.context);

    }
    @Override
    public int getCount() {
        return masterList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dealer_list_row, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        MasterDataModel currentListData = masterList.get(i);

        mViewHolder.tvName.setText(currentListData.getName());
        mViewHolder.tvAddress.setText(currentListData.getAddr());

        return convertView;
    }
    private class MyViewHolder {
        TextView tvName, tvAddress;

        public MyViewHolder(View item) {
            tvName = (TextView) item.findViewById(R.id.dealer_name);
            tvAddress = (TextView) item.findViewById(R.id.dealer_name);
        }
    }
}

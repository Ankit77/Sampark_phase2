package com.symphony.dealerList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.symphony.R;
import com.symphony.model.MasterDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by indianic on 12/06/17.
 */

public class DealerAdapter extends BaseAdapter {
    private ArrayList<MasterDataModel> filteredList;
    private ArrayList<MasterDataModel> masterList;
    private List<MasterDataModel> dictionaryWords;
    private Context context;
    private LayoutInflater inflater;
    private CustomFilter mFilter;

    public DealerAdapter(Context context, ArrayList<MasterDataModel> masterList) {
        this.context = context;
        this.masterList = masterList;
        inflater = LayoutInflater.from(this.context);
        dictionaryWords = masterList;
        filteredList = new ArrayList<>();
        filteredList.addAll(dictionaryWords);
        mFilter = new CustomFilter(DealerAdapter.this);


    }

    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public int getCount() {
        return filteredList.size();
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

        MasterDataModel currentListData = filteredList.get(i);
        mViewHolder.tvName.setText(currentListData.getName());
        mViewHolder.tvAddress.setText(currentListData.getAddr());
        return convertView;
    }

    private class MyViewHolder {
        TextView tvName, tvAddress;

        public MyViewHolder(View item) {
            tvName = (TextView) item.findViewById(R.id.dealer_name);
            tvAddress = (TextView) item.findViewById(R.id.dealer_address);
        }
    }

    public class CustomFilter extends Filter {
        private DealerAdapter mAdapter;

        private CustomFilter(DealerAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(dictionaryWords);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final MasterDataModel mWords : dictionaryWords) {
                    if (mWords.getName().toLowerCase().startsWith(filterPattern) || mWords.getAddr().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(mWords);
                    }
                }
            }
            System.out.println("Count Number " + filteredList.size());
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((List<MasterDataModel>) results.values).size());
            this.mAdapter.notifyDataSetChanged();
        }
    }

}

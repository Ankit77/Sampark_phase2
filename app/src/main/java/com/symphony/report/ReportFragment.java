package com.symphony.report;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.symphony.E_Sampark;
import com.symphony.R;
import com.symphony.http.WSReport;
import com.symphony.model.ReportModel;
import com.symphony.pager.FragmentTitle;
import com.symphony.utils.Const;
import com.symphony.utils.SymphonyUtils;

import org.apache.commons.net.io.Util;

import java.util.ArrayList;

/**
 * Created by chandnichudasama on 10/06/17.
 */

public class ReportFragment extends Fragment implements FragmentTitle {
    private View view;
    private TextView tvYesterdayVisitMessage;
    private TextView tvYesterdayVisitCount;
    private TextView tvtotalVisitCount;
    private TextView tvtotalvisitMessage;
    private LinearLayout llmain;
    private ProgressBar pbLoad;
    private AsyncGetReport asyncGetReport;
    private E_Sampark e_sampark;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_report,null);
        e_sampark=(E_Sampark)getActivity().getApplicationContext();
        tvYesterdayVisitMessage=(TextView)view.findViewById(R.id.fragment_report_tv_yesterdayvisit);
        tvYesterdayVisitCount=(TextView)view.findViewById(R.id.fragment_report_tv_yesterdayvisitcount);
        tvtotalvisitMessage=(TextView)view.findViewById(R.id.fragment_report_tv_totalvisit);
        tvtotalVisitCount=(TextView)view.findViewById(R.id.fragment_report_tv_totalvisitcount);
        pbLoad=(ProgressBar)view.findViewById(R.id.fragment_report_pb_load);
        llmain=(LinearLayout)view.findViewById(R.id.fragment_report_llmain);

        if(SymphonyUtils.isNetworkAvailable(getActivity()))
        {
            asyncGetReport=new AsyncGetReport();
            asyncGetReport.execute();
        }
        return view;
    }

    @Override
    public String getTitle() {
        return "REPORT";
    }

    private class AsyncGetReport extends AsyncTask<Void,Void,ArrayList<ReportModel>>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoad.setVisibility(View.VISIBLE);
            llmain.setVisibility(View.GONE);

        }

        @Override
        protected ArrayList<ReportModel> doInBackground(Void... params) {
            String url="http://61.12.85.74:800/eSampark_Report.asp?NM=track_new&PASS=track123&mno="+e_sampark.getSharedPreferences().getString("usermobilenumber", null)+"&empid="+e_sampark.getSharedPreferences().getString(Const.EMPID, "");
            WSReport wsReport=new WSReport();
            return wsReport.executeVisitReport(url);
        }

        @Override
        protected void onPostExecute(ArrayList<ReportModel> reportModels) {
            super.onPostExecute(reportModels);
            if (!isCancelled()) {
                pbLoad.setVisibility(View.GONE);
                llmain.setVisibility(View.VISIBLE);
                if (reportModels != null && reportModels.size() > 0) {
                    for (int i = 0; i < reportModels.size(); i++) {
                        if (i == 0 && reportModels.get(0) != null) {
                            tvYesterdayVisitMessage.setText(reportModels.get(0).getVisitmessage() + " :- ");
                            tvYesterdayVisitCount.setText(reportModels.get(0).getVisitCount());
                        } else if (i == 1 && reportModels.get(1) != null) {
                            tvtotalvisitMessage.setText(reportModels.get(1).getVisitmessage() + " :- ");
                            tvtotalVisitCount.setText(reportModels.get(1).getVisitCount());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(asyncGetReport!=null && asyncGetReport.getStatus()== AsyncTask.Status.RUNNING)
        {
            asyncGetReport.cancel(true);
        }
    }
}

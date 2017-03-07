package com.symphony.settings;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.symphony.E_Sampark;
import com.symphony.R;
import com.symphony.sms.SMSService;


public class SymphonySettings extends Fragment {

//extends ActionBarActivity{


    private SharedPreferences prefs;
    private Editor editor;

    private Button saveMobileNumberBtn;
    private EditText centralMobileNumberField;
    private E_Sampark e_sampark;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.symphony_settings, null);
        e_sampark = (E_Sampark) getActivity().getApplicationContext();
        saveMobileNumberBtn = (Button) v.findViewById(R.id.saveSettingsBtn);
        prefs = e_sampark.getSharedPreferences();

        centralMobileNumberField = (EditText) v.findViewById(R.id.centralMobileNumberField);

        v.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }

        });
        return v;

    }

    @Override
    public void onPause() {
        super.onPause();


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHECK IN/OUT");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false); // disable the button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // remove the left caret
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        String centralMobileNumber = prefs.getString("centralmobilenumer", null);

        Log.e("CENTRAL NUMBER", centralMobileNumber + "");
        if (centralMobileNumber != null) {
            centralMobileNumberField.setText(centralMobileNumber);

        } else {

            centralMobileNumberField.setText(SMSService.CENTRAL_MOBILE_NUMBER);


        }

        saveMobileNumberBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (inputManager != null) {
                    if (getActivity().getCurrentFocus() != null)
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }


                if (!TextUtils.isEmpty(centralMobileNumberField.getText().toString())) {


                    if (centralMobileNumberField.getText().toString().length() < 10) {

                        Toast.makeText(getActivity(), "Mobile number must be 10 digit", Toast.LENGTH_SHORT).show();

                    } else {


                        editor = prefs.edit();
                        editor.putString("centralmobilenumer", centralMobileNumberField.getText().toString());
                        editor.commit();

                        Toast.makeText(getActivity(), "Centeral mobile number is saved", Toast.LENGTH_SHORT).show();


                    }


                } else {

                    Toast.makeText(getActivity(), "Please enter centeral mobile mumber", Toast.LENGTH_SHORT).show();
                }


            }


        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home:

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHECK IN / CHECK OUT");
                getFragmentManager().popBackStack();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.distributer_refresh).setVisible(false);
        menu.findItem(R.id.distributer_search).setVisible(false);
        menu.findItem(R.id.distributer_listview).setVisible(false);
        menu.findItem(R.id.symphony_settings).setVisible(false);


    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true); // disable the button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // remove the left caret
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


}

package com.symphony.distributer;

import com.symphony.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogAlert extends DialogFragment {

	
	private DistributerActivityListener mDistributerListener;

    public static DialogAlert newInstance(int title) {
    	DialogAlert frag = new DialogAlert();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");


        mDistributerListener = (DistributerActivityListener) getActivity();
        
        
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.success)
                .setTitle(title)
                .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                           
                            
                        	mDistributerListener.onOKPressed();
                            
                        
                              
                        }
                    }
                )
                .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        	mDistributerListener.onCanclePressed();
                        	
                        }
                    }
                )
                .create();
    }
}
package com.symphony;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.CameraFragmentApi;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.listeners.CameraFragmentControlsAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentVideoRecordTextAdapter;
import com.github.florent37.camerafragment.widgets.RecordButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ANKIT on 3/25/2017.
 */

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String FRAGMENT_TAG = "camera";
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;
    private static final int TAKE_PHOTO = 1001;

    RecordButton recordButton;
    RelativeLayout cameraLayout;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        recordButton = (RecordButton) findViewById(R.id.record_button);
        cameraLayout = (RelativeLayout) findViewById(R.id.cameraLayout);
        recordButton.setOnClickListener(this);
        initCamera();

    }

    private void initCamera() {
        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
            } else addCamera();
        } else {
            addCamera();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == recordButton) {
            final CameraFragmentApi cameraFragment = getCameraFragment();
            if (cameraFragment != null) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "/images/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                                                           @Override
                                                           public void onVideoRecorded(String filePath) {
                                                               Toast.makeText(getBaseContext(), "onVideoRecorded " + filePath, Toast.LENGTH_SHORT).show();
                                                           }

                                                           @Override
                                                           public void onPhotoTaken(byte[] bytes, String filePath) {
//                                                               Toast.makeText(getBaseContext(), "onPhotoTaken " + filePath, Toast.LENGTH_SHORT).show();
                                                               Intent intent = new Intent();
                                                               intent.putExtra("PATH", filePath);
                                                               setResult(RESULT_OK, intent);
                                                               finish();//finishing activity
                                                           }
                                                       },
                        file.getPath(),
                        "" + System.currentTimeMillis());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            addCamera();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() {
        cameraLayout.setVisibility(View.VISIBLE);

        final CameraFragment cameraFragment = CameraFragment.newInstance(new Configuration.Builder()
                .setCamera(Configuration.CAMERA_FACE_REAR).build());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();

        if (cameraFragment != null) {
            //cameraFragment.setResultListener(new CameraFragmentResultListener() {
            //    @Override
            //    public void onVideoRecorded(String filePath) {
            //        Intent intent = PreviewActivity.newIntentVideo(MainActivity.this, filePath);
            //        startActivityForResult(intent, REQUEST_PREVIEW_CODE);
            //    }
//
            //    @Override
            //    public void onPhotoTaken(byte[] bytes, String filePath) {
            //        Intent intent = PreviewActivity.newIntentPhoto(MainActivity.this, filePath);
            //        startActivityForResult(intent, REQUEST_PREVIEW_CODE);
            //    }
            //});

            cameraFragment.setStateListener(new CameraFragmentStateAdapter() {

                @Override
                public void onCurrentCameraBack() {
                }

                @Override
                public void onCurrentCameraFront() {
                }

                @Override
                public void onFlashAuto() {
                }

                @Override
                public void onFlashOn() {
                }

                @Override
                public void onFlashOff() {
                }

                @Override
                public void onCameraSetupForPhoto() {

                    recordButton.displayPhotoState();
                }

                @Override
                public void onCameraSetupForVideo() {
                    recordButton.displayVideoRecordStateReady();
                }

                @Override
                public void shouldRotateControls(int degrees) {
                }

                @Override
                public void onRecordStateVideoReadyForRecord() {
                    recordButton.displayVideoRecordStateReady();
                }

                @Override
                public void onRecordStateVideoInProgress() {
                    recordButton.displayVideoRecordStateInProgress();
                }

                @Override
                public void onRecordStatePhoto() {
                    recordButton.displayPhotoState();
                }

                @Override
                public void onStopVideoRecord() {
                    //cameraSwitchView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onStartVideoRecord(File outputFile) {
                }
            });

            cameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                @Override
                public void lockControls() {
                    recordButton.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    recordButton.setEnabled(true);
                }

                @Override
                public void allowCameraSwitching(boolean allow) {
                }

                @Override
                public void allowRecord(boolean allow) {
                    recordButton.setEnabled(allow);
                }

                @Override
                public void setMediaActionSwitchVisible(boolean visible) {
                }
            });

            cameraFragment.setTextListener(new CameraFragmentVideoRecordTextAdapter() {
                @Override
                public void setRecordSizeText(long size, String text) {
                }

                @Override
                public void setRecordSizeTextVisible(boolean visible) {
                }

                @Override
                public void setRecordDurationText(String text) {
                }

                @Override
                public void setRecordDurationTextVisible(boolean visible) {
                }
            });
        }
    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }
}

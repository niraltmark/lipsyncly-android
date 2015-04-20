package com.example.niraltmark.lipsyncly;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraFragment extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    private VideoRecorder mVideoRecorder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mCamera = getCameraInstance();
        mPreview = new CameraPreview(rootView.getContext(), mCamera);
        mVideoRecorder = new VideoRecorder(mCamera, mPreview);

        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Button captureButton = (Button) rootView.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(mVideoRecorder);

        return rootView;

//      This code will work but for some reason it will cause a change in the camerapreviewobject and not in the frame
//        RelativeLayout.LayoutParams layoutParams =  (RelativeLayout.LayoutParams)preview.getLayoutParams();
//        int marginTop = CalculateFrameMarginTop();
//        layoutParams.setMargins(0, -1 * marginTop, 0, marginTop);
//        preview.setLayoutParams(layoutParams);

    }

    public int CalculateFrameMarginTop()
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int height = (metrics.widthPixels / 4) * 3;

        return (metrics.heightPixels - height) / 2;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
            Camera.getCameraInfo( camIdx, cameraInfo );
            if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
                try {
                    return Camera.open( camIdx );
                } catch (RuntimeException e) {
                    // Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return null;
    }

    @Override
    public void onPause() {
        super.onPause();

        mVideoRecorder.release();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


}
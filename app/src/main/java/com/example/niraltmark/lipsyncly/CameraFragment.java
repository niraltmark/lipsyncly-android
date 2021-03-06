package com.example.niraltmark.lipsyncly;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraFragment extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    private VideoRecorder mVideoRecorder;
    private File mFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mFile = getOutputMediaFile();
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(rootView.getContext(), mCamera);
        mVideoRecorder = new VideoRecorder(mCamera, mPreview, mFile);

        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        final VideoView videoView = (VideoView) rootView.findViewById(R.id.videoView);
        videoView.setVideoPath(Environment.getExternalStorageDirectory() + "/uptown-funk-short.mp4");

        Button captureButton = (Button) rootView.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        videoView.start();
                    }
                }, 5000);

                mVideoRecorder.onClick(v);
            }
        });

        Button exposureButton = (Button) rootView.findViewById(R.id.button_exposure);
        exposureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCamera.stopPreview();

                mCamera.setDisplayOrientation(90);

                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(1920,1080);
                parameters.setPreviewFpsRange(30000, 30000); // for 30 fps
                parameters.setAutoWhiteBalanceLock(true);
                parameters.setAutoExposureLock(true);
                mCamera.setParameters(parameters);

                mCamera.startPreview();
            }
        });
        
        Button uploadButton = (Button) rootView.findViewById(R.id.button_upload);
        uploadButton.setOnClickListener(new VideoUploader(mFile));

        return rootView;

//      This code will work but for some reason it will cause a change in the camerapreviewobject and not in the frame
//        RelativeLayout.LayoutParams layoutParams =  (RelativeLayout.LayoutParams)preview.getLayoutParams();
//        int marginTop = CalculateFrameMarginTop();
//        layoutParams.setMargins(0, -1 * marginTop, 0, marginTop);
//        preview.setLayoutParams(layoutParams);

    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                // Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
//        if (type == MEDIA_TYPE_IMAGE){
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
//        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
//        } else {
//            return null;
//        }

        return mediaFile;
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
package com.example.niraltmark.lipsyncly;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);


//
//        this.setLayoutParams(new FrameLayout.LayoutParams(width, height));
//        this.setBackgroundColor(Color.parseColor("#fc9a24"));

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);

//            Camera.Parameters parameters=mCamera.getParameters();
//            parameters.setPreviewSize(640,480);
//            mCamera.setParameters(parameters);
//
//            mHolder.setFixedSize(640,480);
            mCamera.startPreview();
        } catch (IOException e) {
            Toast.makeText(getContext(), "where is my preview?", Toast.LENGTH_LONG);
            // Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        Toast
                .makeText(getContext(), "surface changed", Toast.LENGTH_LONG)
                .show();

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview


        }

//        initPreview(w, h);
        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);

            mCamera.startPreview();

        } catch (Exception e){
            // Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private boolean cameraConfigured=false;
    private void initPreview(int width, int height) {
        if (mCamera!=null && mHolder.getSurface()!=null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
            }
            catch (Throwable t) {
//                Log.e("PreviewDemo-surfaceCallback",
//                        "Exception in setPreviewDisplay()", t);
                Toast
                        .makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters=mCamera.getParameters();
                Camera.Size size=getBestPreviewSize(width, height,
                        parameters);

                if (size!=null) {
                    parameters.setPreviewSize(size.width, size.height);
                    mCamera.setParameters(parameters);
                    cameraConfigured=true;
                }
            }
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {

            if (size.width / 4 == size.height / 3)
            {
                return size;
            }
        }

        return(result);
    }
}
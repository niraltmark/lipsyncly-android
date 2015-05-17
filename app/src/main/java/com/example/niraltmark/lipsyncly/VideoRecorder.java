package com.example.niraltmark.lipsyncly;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoRecorder implements View.OnClickListener
{
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private CameraPreview mPreview;
    private File file;

    public VideoRecorder(Camera mCamera, CameraPreview mPreview, File file) {

        this.mCamera = mCamera;
        this.mPreview = mPreview;
        this.file = file;
    }

    private boolean isRecording = false;

    @Override
    public void onClick(View v) {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            release(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            // setCaptureButtonText("Capture");
            isRecording = false;
        } else {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                // inform the user that recording has started
                // setCaptureButtonText("Stop");
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                release();
                // inform user
            }
        }
    }

    private boolean prepareVideoRecorder(){

        mMediaRecorder = new MediaRecorder();

        // cam_mode is not supported in S4 (at least this is what it seems)
        Camera.Parameters parameters = mCamera.getParameters();

        // If we won't lock the auto exposure the FPS won't be high, it can even reduced to less
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        parameters.setAutoExposureLock(true);
        mCamera.setParameters(parameters);
//        mCamera.stopPreview();  // call this if you had started preview before or else recording wont work on Android versions <= 2.3

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(file.toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        mMediaRecorder.setOrientationHint(270);

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            release();
            return false;
        } catch (IOException e) {
            // Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            release();
            return false;
        }
        return true;
    }

    public void release(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }
}

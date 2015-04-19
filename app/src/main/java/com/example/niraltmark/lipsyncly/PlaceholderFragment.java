package com.example.niraltmark.lipsyncly;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

// import com.example.niraltmark.lipsyncly;

/**
 * Created by niraltmark on 4/17/2015.
 */
public  class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        VideoView videoView = (VideoView) rootView.findViewById(R.id.videoView);
//
//
        videoView.setVideoPath(Environment.getExternalStorageDirectory() + "/uptown-funk.mp4");
        videoView.start();
//
        return rootView;
    }
}

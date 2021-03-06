package com.example.videoa;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoFragment extends Fragment {
	
	private static final String VIDEO_PATH="video_path";
	private String videoPath;
	private VideoView mVideoView;
	private Button btnClose;
	
	public static VideoFragment newInstance(String videoPath){
		
		VideoFragment fragment = new VideoFragment();
		Bundle args = new Bundle();
		args.putString(VIDEO_PATH,videoPath);
		fragment.setArguments(args);
		return fragment;
	}
	
	public VideoFragment(){
		
	}

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(getArguments()!=null){
			videoPath = getArguments().getString(VIDEO_PATH);
		}
	}
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,
			Bundle savedInstanceState){
		
		View view = inflater.inflate(R.layout.fragment_video, container,false);
		mVideoView = (VideoView)view.findViewById(R.id.video_view);
		btnClose = (Button)view.findViewById(R.id.btn_close);
		//������Ӧ��Ƶ
		mVideoView.setMediaController(new MediaController(getActivity()));
		mVideoView.setVideoURI(Uri.parse(videoPath));
		mVideoView.start();
		
		btnClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().beginTransaction().remove(VideoFragment.this).commit();
				
			}
		});
		return view;
	}
}

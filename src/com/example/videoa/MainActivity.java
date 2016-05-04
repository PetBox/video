package com.example.videoa;



import java.io.IOException;
import java.util.logging.LogRecord;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;
import android.media.*;
public class MainActivity extends Activity {

	private MovieRecorderView mRecorderView;
	private Button mShootBtn;
	private boolean isFinish=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (Button)findViewById(R.id.shoot_button);
        try{
        mShootBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					mRecorderView.record(new MovieRecorderView.OnRecordFinishListener(){
						public void onRecordFinish(){
							handler.sendEmptyMessage(1);
						}
		});   
    }else if (event.getAction()==MotionEvent.ACTION_UP){
        if(mRecorderView.getTimeCount()>1)
        	handler.sendEmptyMessage(1);
        else{
           if(mRecorderView.getmRecordFile()!=null)
        	   mRecorderView.getmRecordFile().delete();
           mRecorderView.stop();
           Toast.makeText(MainActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();  
        }
        }
				return true;
    }
        });}catch(RuntimeException e){
        	e.printStackTrace();
        }
    }

    
    public void onResume(){
    	super.onResume();
    	isFinish=true;
    }

    
    public void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
    	isFinish=false;
    	mRecorderView.stop();
    }
    
    public void onPause(){
    	super.onPause();
    }
    
    public void onDestory(){
    	super.onDestroy();
    }
    
    private Handler handler=new Handler(){
    	public void handleMessage(Message msg){
    		finishActivity();
    	}

    };
    
    private void finishActivity(){

    	Intent intent = new Intent("com.example.videoa.FirstActivity");
    	if(isFinish){
    		mRecorderView.stop();
    		//返回到播放页面
    		
    		Log.d("TAG",mRecorderView.getmRecordFile().getAbsolutePath());
    		intent.putExtra("path", mRecorderView.getmRecordFile().getAbsolutePath()); 
    		setResult(RESULT_OK,intent);
    		
    	}
    	finish();    	
		startActivity(intent);
    }
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/
    


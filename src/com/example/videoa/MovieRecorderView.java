package com.example.videoa;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.media.MediaMuxer.OutputFormat;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.os.Environment;
import android.os.Message;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MovieRecorderView extends LinearLayout implements OnErrorListener {

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private ProgressBar mProgressBar;
	
	private MediaRecorder mMediaRecorder;
	private Camera mCamera;
	private Timer mTimer;//计时器
	private OnRecordFinishListener mOnRecordFinishListener;//录制完成回调接口
	
	private int mWidth;//视频分辨率宽度
	private int mHeight;//视频分辨率高度
	private boolean isOpenCamera;//是否一开始打开摄像头
	private int mRecordMaxTime;//一次拍摄最长时间
	private int mTimeCount;//时间计数
	private File mRecordFile=null;//文件
	
	public MovieRecorderView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	public MovieRecorderView(Context context,AttributeSet attrs) {
		this(context,attrs,0);
		// TODO Auto-generated constructor stub
	}
	 public MovieRecorderView(Context context, AttributeSet attrs, int defStyle){
		 super(context, attrs, defStyle);  
		 // 初始化各项组件  
		 TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieRecorderView, defStyle, 0);  
		 mWidth = a.getInteger(R.styleable.MovieRecorderView_video_width, 320);// 默认320  
		 mHeight = a.getInteger(R.styleable.MovieRecorderView_video_height, 240);// 默认240  
		 
		 isOpenCamera = a.getBoolean(R.styleable.MovieRecorderView_is_open_camera, true);// 默认打开  
		 mRecordMaxTime = a.getInteger(R.styleable.MovieRecorderView_record_max_time, 10);// 默认为10  
		 
		 LayoutInflater.from(context).inflate(R.layout.movie_recorder_view, this); 
		 mSurfaceView= (SurfaceView) findViewById(R.id.surfaceview);
		 mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
		 mProgressBar.setMax(mRecordMaxTime);//设置进度条最大量
		 
	     mSurfaceHolder=mSurfaceView.getHolder();
	     mSurfaceHolder.addCallback(new CustomCallBack());
	     mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	     
	     a.recycle();
	 }
	
	 
	 
	 private class CustomCallBack implements Callback{

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if(!isOpenCamera)
				return;
			try{
				initCamera();
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if(!isOpenCamera)
				return;
			freeCameraResource();
		}

	 }
	 
	 
	 
	 
	 
	 //初始化摄像头
	 private void initCamera() throws IOException{
		 if(mCamera!=null){
			 freeCameraResource();
		 }
		 try{
			 mCamera=Camera.open();			 
		 }catch(Exception e){
			 e.printStackTrace();
			 freeCameraResource();
		 }
		 if(mCamera==null)
			 return;
		 
		 mCamera.setDisplayOrientation(90);
		 mCamera.setPreviewDisplay(mSurfaceHolder);
		 mCamera.startPreview();
		 mCamera.unlock();
	 }
	 
	 
	 
	 
	 
	 
	 //释放摄像头资源
	 
	 private void freeCameraResource(){
		 if(mCamera!=null){
			 mCamera.setPreviewCallback(null);
			 mCamera.stopPreview();
			 mCamera.lock();
			 mCamera.release();
			 mCamera=null;
			 
		 }
	 }
	 
	 
	 
	 private void createRecordDir(){
		 File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + "im/video/"); 
		 if(!sampleDir.exists()){
			 sampleDir.mkdirs();
		 }
		 File vecordDir = sampleDir;
		 //创建文件
		 try{
			 SimpleDateFormat format=new SimpleDateFormat("yyyymmddHHmm");
			 Date nowdate=new Date(System.currentTimeMillis());
			 String record=format.format(nowdate);
			 
			 Log.i("TAG",record);
			 //存贮
			 mRecordFile = File.createTempFile(record, ".mp4",vecordDir);//MP4格式
			 Log.i("TAG",mRecordFile.getAbsolutePath());
		 }catch(IOException e){
			 
		 }
	 }
	 
	 
	 
	 
	 
	 //初始化
	 
	 private void initRecord() throws IOException{
		 mMediaRecorder = new MediaRecorder();
		 mMediaRecorder.reset();
		 if(mCamera!=null)
			 mMediaRecorder.setCamera(mCamera);
		 mMediaRecorder.setOnErrorListener(this);
		 mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		 mMediaRecorder.setVideoSource(VideoSource.CAMERA);//视频源
		 mMediaRecorder.setAudioSource(AudioSource.MIC);//音频源
		 mMediaRecorder.setOutputFormat(OutputFormat.MUXER_OUTPUT_MPEG_4);//视频输出格式
		 mMediaRecorder.setAudioEncoder(AudioEncoder.AMR_NB);//音频格式
		 mMediaRecorder.setVideoSize(mWidth, mHeight);//设置分辨率
		 mMediaRecorder.setVideoEncodingBitRate(1*1280*720);//设置帧频率，清晰
		 mMediaRecorder.setOrientationHint(90);//输出旋转90度，保持竖屏录制
		 mMediaRecorder.setVideoEncoder(VideoEncoder.MPEG_4_SP);//视频录制格式
		 mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
		 mMediaRecorder.prepare();
		 try{
			 mMediaRecorder.start();
		 }catch(IllegalStateException e){
			 e.printStackTrace();
		 }catch(RuntimeException e){
			 e.printStackTrace();
		 }catch (Exception e){
			 e.printStackTrace();
		 }		 		 
	 }
	 
	 
	 
	 
	 
	 
	 //开始录制视频
	 
	 public void record(final OnRecordFinishListener onRecordFinishListener) {
		 this.mOnRecordFinishListener = onRecordFinishListener;
		 createRecordDir();
		 try{
			 if(!isOpenCamera)//如果未打开摄像头，则打开
				 initCamera();
			 initRecord();
			 mTimeCount = 0;//时间计数器重新赋值
			 mTimer = new Timer();
			 mTimer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mTimeCount++;
					mProgressBar.setProgress(mTimeCount);//设置进度条
					if(mTimeCount==mRecordMaxTime-1){//到达指定时间，停止拍摄
						stop();
						if(mOnRecordFinishListener!=null)
							mOnRecordFinishListener.onRecordFinish();
					}
				}
			}, 0, 1000);
		 }catch(IOException e){
			 e.printStackTrace();
		 }
	 }
	 
	 
	 
	 
	 
	 
	 //停止拍摄
	 
	 public void stop(){
		 stopRecord();
		 releaseRecord();
		 freeCameraResource();
	 }

	 
	 //停止录制
	 
	 public void stopRecord(){
		 mProgressBar.setProgress(0);
		 if(mTimer!=null)
			 mTimer.cancel();
		 if(mMediaRecorder!=null){
			 //设置后不会崩溃
			 mMediaRecorder.setOnErrorListener(null);
			 try{
				 mMediaRecorder.stop();
			 }catch(IllegalStateException e){
				 e.printStackTrace();
			 }catch(RuntimeException e){
				 e.printStackTrace();
			 }catch(Exception e){
				 e.printStackTrace();
			 }
			 mMediaRecorder.setPreviewDisplay(null);
		 }
	 }
	 
	 
	 
	 
	 //释放资源
	 
	 private void releaseRecord(){
		 if(mMediaRecorder!=null){
			 mMediaRecorder.setOnErrorListener(null);
		 try{
			 mMediaRecorder.release();
		 }catch(IllegalStateException e){
			 e.printStackTrace();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }
	      mMediaRecorder =null;
	  
}
	 public int getTimeCount(){
		 return mTimeCount;
	 }
	 
	 public File getmRecordFile(){
		 return mRecordFile;
	 }
	 
	 //录制完成回调接口
	 
	 public interface OnRecordFinishListener{
		 public void onRecordFinish();
	 }
	 
	 
	 public void onError(MediaRecorder mr,int what,int extra){
		 try{
			 if(mr!=null)
				 mr.reset();
		 }catch(IllegalStateException e){
			 e.printStackTrace();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }
	 }

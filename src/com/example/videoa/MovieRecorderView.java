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
	private Timer mTimer;//��ʱ��
	private OnRecordFinishListener mOnRecordFinishListener;//¼����ɻص��ӿ�
	
	private int mWidth;//��Ƶ�ֱ��ʿ��
	private int mHeight;//��Ƶ�ֱ��ʸ߶�
	private boolean isOpenCamera;//�Ƿ�һ��ʼ������ͷ
	private int mRecordMaxTime;//һ�������ʱ��
	private int mTimeCount;//ʱ�����
	private File mRecordFile=null;//�ļ�
	
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
		 // ��ʼ���������  
		 TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieRecorderView, defStyle, 0);  
		 mWidth = a.getInteger(R.styleable.MovieRecorderView_video_width, 320);// Ĭ��320  
		 mHeight = a.getInteger(R.styleable.MovieRecorderView_video_height, 240);// Ĭ��240  
		 
		 isOpenCamera = a.getBoolean(R.styleable.MovieRecorderView_is_open_camera, true);// Ĭ�ϴ�  
		 mRecordMaxTime = a.getInteger(R.styleable.MovieRecorderView_record_max_time, 10);// Ĭ��Ϊ10  
		 
		 LayoutInflater.from(context).inflate(R.layout.movie_recorder_view, this); 
		 mSurfaceView= (SurfaceView) findViewById(R.id.surfaceview);
		 mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
		 mProgressBar.setMax(mRecordMaxTime);//���ý����������
		 
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
	 
	 
	 
	 
	 
	 //��ʼ������ͷ
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
	 
	 
	 
	 
	 
	 
	 //�ͷ�����ͷ��Դ
	 
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
		 //�����ļ�
		 try{
			 SimpleDateFormat format=new SimpleDateFormat("yyyymmddHHmm");
			 Date nowdate=new Date(System.currentTimeMillis());
			 String record=format.format(nowdate);
			 
			 Log.i("TAG",record);
			 //����
			 mRecordFile = File.createTempFile(record, ".mp4",vecordDir);//MP4��ʽ
			 Log.i("TAG",mRecordFile.getAbsolutePath());
		 }catch(IOException e){
			 
		 }
	 }
	 
	 
	 
	 
	 
	 //��ʼ��
	 
	 private void initRecord() throws IOException{
		 mMediaRecorder = new MediaRecorder();
		 mMediaRecorder.reset();
		 if(mCamera!=null)
			 mMediaRecorder.setCamera(mCamera);
		 mMediaRecorder.setOnErrorListener(this);
		 mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		 mMediaRecorder.setVideoSource(VideoSource.CAMERA);//��ƵԴ
		 mMediaRecorder.setAudioSource(AudioSource.MIC);//��ƵԴ
		 mMediaRecorder.setOutputFormat(OutputFormat.MUXER_OUTPUT_MPEG_4);//��Ƶ�����ʽ
		 mMediaRecorder.setAudioEncoder(AudioEncoder.AMR_NB);//��Ƶ��ʽ
		 mMediaRecorder.setVideoSize(mWidth, mHeight);//���÷ֱ���
		 mMediaRecorder.setVideoEncodingBitRate(1*1280*720);//����֡Ƶ�ʣ�����
		 mMediaRecorder.setOrientationHint(90);//�����ת90�ȣ���������¼��
		 mMediaRecorder.setVideoEncoder(VideoEncoder.MPEG_4_SP);//��Ƶ¼�Ƹ�ʽ
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
	 
	 
	 
	 
	 
	 
	 //��ʼ¼����Ƶ
	 
	 public void record(final OnRecordFinishListener onRecordFinishListener) {
		 this.mOnRecordFinishListener = onRecordFinishListener;
		 createRecordDir();
		 try{
			 if(!isOpenCamera)//���δ������ͷ�����
				 initCamera();
			 initRecord();
			 mTimeCount = 0;//ʱ����������¸�ֵ
			 mTimer = new Timer();
			 mTimer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mTimeCount++;
					mProgressBar.setProgress(mTimeCount);//���ý�����
					if(mTimeCount==mRecordMaxTime-1){//����ָ��ʱ�䣬ֹͣ����
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
	 
	 
	 
	 
	 
	 
	 //ֹͣ����
	 
	 public void stop(){
		 stopRecord();
		 releaseRecord();
		 freeCameraResource();
	 }

	 
	 //ֹͣ¼��
	 
	 public void stopRecord(){
		 mProgressBar.setProgress(0);
		 if(mTimer!=null)
			 mTimer.cancel();
		 if(mMediaRecorder!=null){
			 //���ú󲻻����
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
	 
	 
	 
	 
	 //�ͷ���Դ
	 
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
	 
	 //¼����ɻص��ӿ�
	 
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

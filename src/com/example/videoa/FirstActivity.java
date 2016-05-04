package com.example.videoa;

//import android.app.ActionBar;
import java.io.File;

import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class FirstActivity extends Activity {
      
	  //录制按钮
      private Button btnRecordAudio;
      //播放按钮
      private ImageButton btnPlay;
      //取消发布视屏按钮
      private ImageButton btndel;
      //发送视频按钮
      private ImageButton btnsend;
      //文件路径
      private String path="";
      @Override
      protected void onCreate(Bundle savedInstanceState)
      {
    	  super.onCreate(savedInstanceState);
    	  setContentView(R.layout.activity_first);
    	  
    	  btnRecordAudio=(Button)findViewById(R.id.btn_record_audio);
    	  btnPlay=(ImageButton)findViewById(R.id.play);
    	  btndel=(ImageButton)findViewById(R.id.video_delete);
    	  btnsend=(ImageButton)findViewById(R.id.video_send);
    	  //重新拍摄video
    	  btnRecordAudio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//启动拍摄的Activity
				Intent intent=new Intent(FirstActivity.this,MainActivity.class);
				FirstActivity.this.startActivityForResult(intent, 200);
			}
		});
    	  //播放video
    	  btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					//显示播放页面
				//添加path内容
				    Intent intent=getIntent();
				    path=intent.getStringExtra("path");
				    VideoFragment bigPic = VideoFragment.newInstance(path);  
					android.app.FragmentManager mFragmentManager = getFragmentManager();
					FragmentTransaction transaction = mFragmentManager.beginTransaction();
					transaction.replace(R.id.main_menu, bigPic);
					transaction.commit();
			}
		});
    	  btndel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//获取路径
				Intent intent=getIntent();
				path=intent.getStringExtra("path");
				File file=new File(path);
				//删除视频
				file.delete();
				//返回 录制界面
				Intent intentmain=new Intent(FirstActivity.this,MainActivity.class);
				FirstActivity.this.startActivityForResult(intentmain, 200);
				
			}
		});
    	  btnsend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//获取路径
				Intent intent=getIntent();
				path=intent.getStringExtra("path");
				//返回 录制界面
				Intent intentmain=new Intent(FirstActivity.this,MainActivity.class);
				FirstActivity.this.startActivityForResult(intentmain, 200);
				
			}
		});
      }
      
   /*   public boolean onCreateOptuionsMenu(Menu menu){
    	  
    	  getMenuInflater().inflate(R.menu.menu_first, menu);
    	  return true;
      }*/
      
      public boolean onOptionsItemSelected(MenuItem item){
    	  
    	  int id = item.getItemId();
    	  
    	  if(id==R.id.action_settings){
    		  return true;
    	  }
    	  
    	  return super.onOptionsItemSelected(item);
      }
      
      protected void onActivityResult(int requestCode,int resultCode,Intent data){
    	  super.onActivityResult(requestCode, resultCode, data);
    	  
    	  switch(requestCode){
    	  case 200:
    		  if(resultCode==RESULT_OK){
    			  //成功
    			  path = data.getStringExtra("path");
    			  Toast.makeText(FirstActivity.this, "存储路径为："+path, Toast.LENGTH_SHORT).show();
    			  //通过路径获取第一帧的缩略图并显示
    			  Bitmap bitmap = Utils.createVideoThubnail(path);
    			  BitmapDrawable drawable = new BitmapDrawable(bitmap);
    			  drawable.setTileModeXY(Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
    			  drawable.setDither(true);
    			  btnPlay.setBackground(drawable);
    		  }else{
    			  //失败
    		  }
    		  break;
    	  }
      }
}

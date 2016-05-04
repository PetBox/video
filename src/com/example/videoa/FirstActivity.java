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
      
	  //¼�ư�ť
      private Button btnRecordAudio;
      //���Ű�ť
      private ImageButton btnPlay;
      //ȡ������������ť
      private ImageButton btndel;
      //������Ƶ��ť
      private ImageButton btnsend;
      //�ļ�·��
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
    	  //��������video
    	  btnRecordAudio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//���������Activity
				Intent intent=new Intent(FirstActivity.this,MainActivity.class);
				FirstActivity.this.startActivityForResult(intent, 200);
			}
		});
    	  //����video
    	  btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					//��ʾ����ҳ��
				//���path����
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
				//��ȡ·��
				Intent intent=getIntent();
				path=intent.getStringExtra("path");
				File file=new File(path);
				//ɾ����Ƶ
				file.delete();
				//���� ¼�ƽ���
				Intent intentmain=new Intent(FirstActivity.this,MainActivity.class);
				FirstActivity.this.startActivityForResult(intentmain, 200);
				
			}
		});
    	  btnsend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//��ȡ·��
				Intent intent=getIntent();
				path=intent.getStringExtra("path");
				//���� ¼�ƽ���
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
    			  //�ɹ�
    			  path = data.getStringExtra("path");
    			  Toast.makeText(FirstActivity.this, "�洢·��Ϊ��"+path, Toast.LENGTH_SHORT).show();
    			  //ͨ��·����ȡ��һ֡������ͼ����ʾ
    			  Bitmap bitmap = Utils.createVideoThubnail(path);
    			  BitmapDrawable drawable = new BitmapDrawable(bitmap);
    			  drawable.setTileModeXY(Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
    			  drawable.setDither(true);
    			  btnPlay.setBackground(drawable);
    		  }else{
    			  //ʧ��
    		  }
    		  break;
    	  }
      }
}

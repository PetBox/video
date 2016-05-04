package com.example.videoa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

public class Utils {

	public static Bitmap createVideoThubnail(String filePath){
		Class<?>clazz=null;
		Object instance=null;
		try{
			clazz=Class.forName("android.media.MediaetadataRetriever");
			instance = clazz.newInstance();
			
			Method method = clazz.getMethod("setDataSource", String.class);
			
			if(Build.VERSION.SDK_INT<=9){
				return (Bitmap)clazz.getMethod("captureFrame").invoke(instance);				
			}else{
				byte[] data=(byte[])clazz.getMethod("getEmbeddedPicture").invoke(instance);
				if(data !=null){
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					if(bitmap!=null)return bitmap;
				}
				return (Bitmap)clazz.getMethod("getFrameAtTime").invoke(instance);
		}
	}catch(IllegalArgumentException ex){
	
	}catch(RuntimeException ex){
		
	}catch(InstantiationException e){
		Log.e("TAG","createVideoThumbnail",e);
	}catch(InvocationTargetException e){
		Log.e("TAG","createVideoThumbnail",e);
	}catch(ClassNotFoundException e){
		Log.e("TAG","createVideoThumbnail",e);
	}catch(NoSuchMethodException e){
		Log.e("TAG","createVideoThumbnail",e);
	}catch(IllegalAccessException e){
		Log.e("TAG","createVideoThumbnail",e);
	}finally{
		try{
			if(instance !=null){
				clazz.getMethod("release").invoke(instance);
			}
		}catch(Exception ignored){			
		}
	}
		return null;
	}
}

package com.szxyyd.mpxyhl.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	
	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/Photo_LJ/";

	public static String getSDPATH(){
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return SDPATH;
		}
		return null;
	}

	public static void saveBitmap(Bitmap bm, String picName) {
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			File f = new File(SDPATH, picName + ".png");
			if (f.exists()) {
				f.delete();
			}

			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			Log.e("FileUtils","out.flush()");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}
	
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); 
			else if (file.isDirectory())
				deleteDir(); 
		}
		dir.delete();
	}

	public static boolean fileIsExists(String path) {
		try {
				File f = new File(path);
				if (!f.exists()) {
					return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

	/**
	 * 根据path读取SD卡上的图片，转为Bitmap
	 * @param imagePath
	 * @return
     */
	public static Bitmap getPhotoFromSDCard(String imagePath) {
		//	String imagePath = SDPATH +  photoName + ".png";
				Bitmap photoBitmap = BitmapFactory.decodeFile(imagePath);
				if (photoBitmap == null) {
					return null;
				} else {
					return photoBitmap;
		}
	}
	/**
	 * 获取SD卡上图片的路径
	 */
	public static String getImagePath(String photoName){
		String result = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			result = SDPATH +  photoName + ".png";
			if (result == null) {
				return null;
			} else {
				return result;
			}
		}
		return null;
	}
}

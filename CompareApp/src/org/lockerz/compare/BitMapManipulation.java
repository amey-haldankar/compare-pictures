package org.lockerz.compare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;

public class BitMapManipulation {

    /**
     * 
     * @brief createResizedBitmap method
     * 
     * @param src: gives the source of the file(0=camera,1=gallery)
     * @param path
     * @param dstW
     * @param dstH
     * @return
     * 
     * @detail
     */
    public Bitmap createResizedBitmap(String path,int dstW,int dstH) {
        int inWidth = 0;
        int inHeight = 0;
        InputStream in = null;
        Bitmap roughBitmap = null;
        
        try
        {
            String[] s = null;
            s= path.split("file://");
            if(s.length == 1) {
                path = s[0];
            }else{
                path = s[1];
            }
            File file = new File(path);
            in = new FileInputStream(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            inWidth = options.outWidth;
            inHeight = options.outHeight;

            file = new File(path);
            in = new FileInputStream(file);
            options = new BitmapFactory.Options();
            options.inSampleSize = Math.max(inWidth/dstW, inHeight/dstH);

            roughBitmap = BitmapFactory.decodeStream(in,null,options);

            /*Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0,0, dstW, dstH);
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.FILL);
            //m.postScale(1.5f, 1.5f);
            float[] values = new float[9];
            m.getValues(values);

            // resize bitmap
            resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, dstW, dstH, false);*/
/*
            if(src == 0) {
                file = new File(path);
            }else if(src == 1) {
                File resolveMeSDCard = new File("/mnt/sdcard/Pictures/MyCameraApp/newImage.jpg");
                boolean fileCreated = resolveMeSDCard.createNewFile();
                file = new File("/mnt/sdcard/Pictures/MyCameraApp/newImage.jpg");
            }
            
            if(file.exists()) {
                FileOutputStream out = new FileOutputStream(file);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                
                //ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                //resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bOut);
                //byte[] buffer = bOut.toByteArray();
                //Bitmap bmp = BitmapFactory.decodeByteArray(buffer, 0, 0);
            } */

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (Exception e)
        {
            Log.e("Image", e.getMessage(), e);
        }

        /*if(src == 0) {
            return path;
        }else {
            return "/mnt/sdcard/Pictures/MyCameraApp/newImage.jpg";
        }*/
        
        return roughBitmap;
    }
    
    public Bitmap createBMP(int src,String path,int dstW,int dstH){
    	String[] s = null;
    	InputStream in = null;
        s= path.split("file://");
        int inWidth = 0;
        int inHeight = 0;
        
        if(s.length == 1) {
            path = s[0];
        }else{
            path = s[1];
        }
        File file = new File(path);
        
        try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        try
        {
            in.close();
        } catch (IOException e1)
        {
            e1.printStackTrace();
        }
        in = null;

        inWidth = options.outWidth;
        inHeight = options.outHeight;

        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        options.inSampleSize = Math.max(inWidth/dstW, inHeight/dstH);
        
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inPreferredConfig = Config.ARGB_8888;
		options.inJustDecodeBounds = false;
		
		Bitmap roughBitmap = BitmapFactory.decodeStream(in,null,options);
		
		//roughBitmap = Bitmap.createScaledBitmap(roughBitmap, dstW, dstH, true);

        if(src == 0)
        {
            file = new File(path);
        }
        else if(src == 1)
        {
        	String pathExt = Environment.getExternalStorageDirectory().toString();
            File resolveMeSDCard = new File(pathExt+"Pictures/MyCameraApp/newImage.jpg");
            try {
				boolean fileCreated = resolveMeSDCard.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
            file = new File(pathExt+"Pictures/MyCameraApp/newImage.jpg");
        }
        
        if(file.exists()) {
            FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			roughBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } 
		return roughBitmap;
    	
    }

}

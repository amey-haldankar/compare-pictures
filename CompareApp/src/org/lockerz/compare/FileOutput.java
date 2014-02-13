package org.lockerz.compare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;


public class FileOutput {
    
    //String fileName;
    //Bitmap bmp;
    
    public FileOutput()
    {
        
    }
    
    public void writeToFile(String fileName, Bitmap bmp)
    {
        String pathExt = Environment.getExternalStorageDirectory().toString();
        File resolveMeSDCard = new File(pathExt + "/Pictures/MyCameraApp/" + fileName + ".jpg");
        
        try {
            boolean fileCreated = resolveMeSDCard.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        File file = new File(pathExt+"/Pictures/MyCameraApp/" + fileName + ".jpg");

        if(file.exists()) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } 
    }
}

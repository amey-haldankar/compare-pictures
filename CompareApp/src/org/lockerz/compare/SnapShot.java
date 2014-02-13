package org.lockerz.compare;

import android.graphics.Bitmap;
import android.view.View;


public class SnapShot {
    Bitmap bmp;
    
    public Bitmap takeSnapShot(View v) {
        v.setDrawingCacheEnabled(true);
        bmp = v.getDrawingCache();
        return bmp;
    }
}

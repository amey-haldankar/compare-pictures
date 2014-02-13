package org.lockerz.compare;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class SnapActivity extends Activity {
    
    ImageView snapImage;
    
    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snap);
        
        //snapImage = (ImageView)findViewById(R.id.snap);
        //snapImage.setImageBitmap(captureActivity.snapBmp);
    }

    public void shareSnap(View v)
    {
        Intent shareIntent =  new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://" + "/mnt/sdcard/Pictures/MyCameraApp/snapImage.jpg"));
        startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }
}

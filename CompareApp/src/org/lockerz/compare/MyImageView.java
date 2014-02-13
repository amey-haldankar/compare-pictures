package org.lockerz.compare;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class MyImageView extends ImageView{

    int currentW = 0;
    int currentH = 0;

    public MyImageView (Context context)
    {
        super(context);
    }
    public MyImageView (Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyImageView (Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        currentH = h;
        currentW = w;
    }


}



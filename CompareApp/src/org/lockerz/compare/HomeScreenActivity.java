package org.lockerz.compare;

import java.io.File;

import org.lockerz.compare.guideScreens.DemoActivity2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;

public class HomeScreenActivity extends FirstRun 
{

    Intent lnchCmparScrn;
    Button compareButton;
    Gallery gallery;
    int MAX_COL = 1;
    public static int selEnabled = 0;
    ImageView selected;
    public static int itemsSelected[] = new int[2];
    static int i = 0;
    MyGalleryAdapter mAdapter;
    //SharedPreferences sPref;
    static Context mContext;
    
    
    public static AppPreference appPref;
    
 // Physical display width and height.
    public static int displayWidth = 0;
    public static int displayHeight = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Display display = ((WindowManager)
                getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        displayWidth = display.getWidth();      
        displayHeight = display.getHeight(); 
        
        mContext = this;
        appPref = new AppPreference(getApplicationContext());
        
        gallery = (Gallery)findViewById(R.id.gallery);
        
        mAdapter = new MyGalleryAdapter(this);
        gallery.setAdapter(mAdapter);
        
        String pathExt = Environment.getExternalStorageDirectory().toString();
        File resolveMeSDCard = new File(pathExt + "/Pictures/MyCameraApp");
        if(!resolveMeSDCard.exists())
        {
        	resolveMeSDCard.mkdirs();
        }
        
        //sPref = PreferenceManager.getDefaultSharedPreferences(HomeScreenActivity.mContext);
        
        for(int i=0;i<2;i++)
        {
            itemsSelected[i] = -999;
        }

        compareButton = (Button)findViewById(R.id.compareButton);

        compareButton.setOnClickListener(new OnClickListener() 
        {

            @Override
            public void onClick(View v) 
            {
                //Toast.makeText(HomeScreenActivity.this, "Hell Yeah", Toast.LENGTH_LONG).show();

                lnchCmparScrn = new Intent(HomeScreenActivity.this,captureActivity.class);
                lnchCmparScrn.putExtra("MAX_COL", MAX_COL);
                lnchCmparScrn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(selEnabled > 0 && mAdapter.imgPath.size()!=0)
                {
                    for(int i=0;i<2;i++)
                    {
                        int pathPos = itemsSelected[i];
                        if(itemsSelected[i] != -999) {
                            lnchCmparScrn.putExtra(Integer.toString(i), mAdapter.imgPath.get(pathPos));
                        }
                    }
                }
                startActivity(lnchCmparScrn);

                //finish();
            }
        });


        gallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick (AdapterView<?> parent, View v, int position,
                    long Id)
            {

                selected = (ImageView)v.findViewById(R.id.selected);
                if(selected.getVisibility() == View.GONE)
                {
                    itemsSelected[i] = position;
                    i = (i+1)%2;
                    if(selEnabled<2)
                    {
                        selEnabled++;
                    }
                }
                else
                {
                    for(int j=0;j<2;j++)
                    {
                        if(itemsSelected[j] == position)
                        {
                            itemsSelected[j] = -999;
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

        });

        firstRunPreferences();
        if(getFirstRun(1))
        {
            Intent intent = new Intent(HomeScreenActivity.this,DemoActivity2.class);
            startActivity(intent);
            setRunned(1);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mAdapter = new MyGalleryAdapter(this);
        gallery.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        selEnabled = 0;
        for(int i=0;i<2;i++) {
            itemsSelected[i] = -999;
        }
    }

    public void setColumns(View v) {

        MAX_COL = 2;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        selEnabled = 0;
        for(int i=0;i<2;i++) {
            itemsSelected[i] = -999;
        }
    }


}
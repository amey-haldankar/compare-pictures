package org.lockerz.compare;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MyGalleryAdapter extends BaseAdapter {
	/*
	 * Sample array of images to populate in gallery view just for reference...
	 */
	public static int SAMPLE_IMAGES[] = { R.drawable.s1, R.drawable.s2,
			R.drawable.s3, R.drawable.s4 };
	public static ArrayList<String> imgPath = new ArrayList<String>();
	Context mContext;
	LayoutInflater inflater;

	Bitmap bmp;
	HashMap<Integer, Bitmap> imagesMap;

	@SuppressLint("UseSparseArrays")
	public MyGalleryAdapter(Context c) {
		this.mContext = c;
		inflater = LayoutInflater.from(c);
		imagesMap = new HashMap<Integer, Bitmap>();

		try {

			imgPath = HomeScreenActivity.appPref.getPathList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		if (imgPath.size() == 0) {
			return 4;
		} else {
			int imagesCount = imgPath.size();
			return imagesCount;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (imgPath.size() == 0) {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.gallery_items, null);
				holder.gImage = (ImageView) convertView
						.findViewById(R.id.gImage);
				holder.selected = (ImageView) convertView
						.findViewById(R.id.selected);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// setting of images in gallery
			holder.gImage.setAdjustViewBounds(true);
			holder.gImage.setMaxHeight(HomeScreenActivity.displayHeight / 4);
			holder.gImage.setMaxWidth(HomeScreenActivity.displayWidth / 4);
			holder.gImage.setImageResource(SAMPLE_IMAGES[position]);

			if (HomeScreenActivity.selEnabled > 0) {
				for (int i = 0; i < 2; i++) {
					if (position == HomeScreenActivity.itemsSelected[i]) {
						holder.selected.setVisibility(View.VISIBLE);
					}
				}
			}
			return convertView;
		}

		else {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.gallery_items, null);
				holder.gImage = (ImageView) convertView
						.findViewById(R.id.gImage);
				holder.selected = (ImageView) convertView
						.findViewById(R.id.selected);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// set from imgpath

			String _imagesPath = imgPath.get(position);
			BitMapManipulation bm = new BitMapManipulation();
			if (imagesMap.get(position) == null) {
				bmp = bm.createResizedBitmap(_imagesPath,
						HomeScreenActivity.displayWidth / 3,
						HomeScreenActivity.displayWidth / 3);
				imagesMap.put(position, bmp);
			} else {
				bmp = imagesMap.get(position);
			}

			holder.gImage.setImageBitmap(bmp);
			if (HomeScreenActivity.selEnabled > 0) {
				for (int i = 0; i < 2; i++) {
					if (position == HomeScreenActivity.itemsSelected[i]) {
						holder.selected.setVisibility(View.VISIBLE);
					}
				}
			}

			return convertView;
		}
	}

	public class ViewHolder {
		ImageView gImage;
		ImageView selected;
	}

}

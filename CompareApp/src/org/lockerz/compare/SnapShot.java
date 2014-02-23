package org.lockerz.compare;

import android.graphics.Bitmap;
import android.view.View;

public class SnapShot {

	public Bitmap takeSnapShot(View v) {
		v.setDrawingCacheEnabled(true);
		return v.getDrawingCache();

	}
}

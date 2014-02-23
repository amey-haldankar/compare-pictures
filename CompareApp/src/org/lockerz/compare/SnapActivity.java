package org.lockerz.compare;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

public class SnapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snap);

	}

	public void shareSnap(View v) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		shareIntent.putExtra(
				Intent.EXTRA_STREAM,
				Uri.parse("file://"
						+ Environment.getExternalStorageDirectory().toString()
						+ "/ComparePictures/snapImage.jpg"));
		startActivity(Intent.createChooser(shareIntent, "Share image using"));
	}
}

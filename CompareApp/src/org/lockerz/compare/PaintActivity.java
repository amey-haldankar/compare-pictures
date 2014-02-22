package org.lockerz.compare;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.lockerz.compare.guideScreens.DemoActivity3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class PaintActivity extends FirstRun implements
ColorPickerDialog.OnColorChangedListener {

	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;
	// display width
	public int displayWidth;
	// display height
	public int displayHeight;
	public MyView mView;

	// Bitmaps
	public Bitmap mBitmap, mBackBitmap/* ,bmOverlay,mutableBitmap */;

	// is saved
	public boolean isSaved = false;

	public final String SAVE = "save";
	public final String SHARE = "share";
	public String filePublic = "";
	int check = -1;
	public String shareSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		displayWidth = getWindowManager().getDefaultDisplay().getWidth();
		displayHeight = getWindowManager().getDefaultDisplay().getHeight();

		setContentView(R.layout.snap);

		mView = new MyView(this);
		RelativeLayout root = (RelativeLayout) findViewById(R.id.root);
		root.addView(mView, 0);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(0xFF00FF00);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

		firstRunPreferences();
		if (getFirstRun(2)) {
			Intent intentDemo = new Intent(PaintActivity.this,
					DemoActivity3.class);
			startActivity(intentDemo);
			setRunned(2);
		}
	}

	/**
	 * 
	 * @brief saveBitmap method
	 * 
	 * @param v
	 * 
	 * @detail
	 */

	public void saveBitmap(View v) {
		mView.saveBitmaps(SAVE);
	}

	public void shareSnap(View v) {
		mView.saveBitmaps(SHARE);
	}

	@Override
	public void colorChanged(int color) {
		mPaint.setColor(color);
	}

	public class MyView extends View {

		private static final float MINP = 0.25f;
		private static final float MAXP = 0.75f;

		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;
		private float sX, sY;
		private static final float TOUCH_TOLERANCE = 1;

		public MyView(Context c) {
			super(c);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inPreferredConfig = Config.ARGB_8888;
			options.inJustDecodeBounds = false;

			String pathExt = Environment.getExternalStorageDirectory()
					.toString();
			String path = pathExt + "/Pictures/MyCameraApp/snapImage.jpg";

			/*
			 * mBackBitmap = BitmapFactory
			 * .decodeFile("/mnt/sdcard/Pictures/MyCameraApp/snapImage.jpg"
			 * ,options);
			 */

			mBackBitmap = BitmapFactory.decodeFile(path, options);

			// mBackBitmap = Bitmap.createScaledBitmap(mBackBitmap,
			// mBackBitmap.getWidth(), mBackBitmap.getHeight(), true);
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);

			/*
			 * mBitmap = Bitmap.createScaledBitmap(mBackBitmap, mBackBitmap
			 * .getWidth(), mBackBitmap.getHeight(), true); mutableBitmap =
			 * mBitmap.copy(Bitmap.Config.ARGB_8888, true);
			 * 
			 * mCanvas = new Canvas(mutableBitmap);
			 */
			mBitmap = mBackBitmap.copy(mBackBitmap.getConfig(), true);
			mCanvas = new Canvas(mBitmap);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(0x00FFFFFF);
			// canvas.drawBitmap(mBackBitmap, 0, 0, null);
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

			canvas.drawPath(mPath, mPaint);
		}

		public void Reset() {
			mPath.reset();
			mBitmap = mBackBitmap.copy(mBackBitmap.getConfig(), true);
			mCanvas = new Canvas(mBitmap);
			invalidate();
		}

		/*
		 * public Bitmap overlay (Bitmap bmp1, Bitmap bmp2) { bmOverlay =
		 * Bitmap.createBitmap(bmp1.getWidth(), bmp1 .getHeight(),
		 * bmp1.getConfig());
		 * 
		 * Canvas canvas = new Canvas(bmOverlay); canvas.drawBitmap(bmp1, new
		 * Matrix(), null); canvas.drawBitmap(bmp2, 0, 0, null); return
		 * bmOverlay; }
		 */

		private void touchStart(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			sX = x;
			sY = y;
		}

		private void touchMove(float x, float y) {
			float dx = Math.abs(x - sX);
			float dy = Math.abs(y - sY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(sX, sY, (x + sX) / 2, (y + sY) / 2);
				sX = x;
				sY = y;
			}
		}

		private void touchUp() {
			mPath.lineTo(sX, sY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			mPath.reset();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchStart(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touchMove(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touchUp();
				invalidate();
				break;
			}
			return true;
		}

		public void saveBitmaps(String option) {
			if (option == "0") {
				new SaveBitmapped().execute(null);
				dismissDialog(0);
			} else if (option != "1" && option != "0") {
				shareSave = option;

				showDialog(0);
			} else {
				dismissDialog(0);
			}

		}

		/**
		 * 
		 * @brief
		 * 
		 * @detail Async task for creating file and saving the bitmap
		 */
		class SaveBitmapped extends AsyncTask<Void, Void, Void> {
			ProgressDialog pd;

			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(PaintActivity.this);
				pd.setTitle(R.string.save_img_text);
				pd.setMessage("Saving...");
				pd.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					// mBackBitmap = overlay(mBackBitmap, mBitmap);
					FileOutput fo = new FileOutput();
					fo.writeToFile(filePublic, mBitmap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				isSaved = true;
				pd.dismiss();
				if (shareSave == SAVE) {

				} else if (shareSave == SHARE) {
					String pathExt = Environment.getExternalStorageDirectory()
							.toString();
					String path = pathExt + "/Pictures/MyCameraApp/"
							+ filePublic + ".jpg";

					Intent shareIntent = new Intent(Intent.ACTION_SEND);
					shareIntent.setType("image/*");

					/*
					 * shareIntent.putExtra(Intent.EXTRA_STREAM,
					 * Uri.parse("file://" +
					 * "/mnt/sdcard/Pictures/MyCameraApp/editedImage.jpg"));
					 */

					shareIntent.putExtra(Intent.EXTRA_STREAM,
							Uri.parse("file://" + path));

					startActivity(Intent.createChooser(shareIntent,
							"Share image using"));
				}
			}

		}
	}

	private static final int COLOR_MENU_ID = Menu.FIRST;
	private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
	private static final int BLUR_MENU_ID = Menu.FIRST + 2;
	private static final int RESET_MENU_ID = Menu.FIRST + 3;
	// private static final int ERASE_MENU_ID = Menu.FIRST + 3;
	private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, COLOR_MENU_ID, 0, R.string.menu_color)
		.setShortcut('3', 'c');
		menu.add(0, EMBOSS_MENU_ID, 0, R.string.menu_emboss).setShortcut('4',
				's');
		menu.add(0, BLUR_MENU_ID, 0, R.string.menu_blur).setShortcut('5', 'z');
		menu.add(0, RESET_MENU_ID, 0, R.string.menu_reset)
		.setShortcut('5', 'z');
		// menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
		// menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
		case COLOR_MENU_ID:
			new ColorPickerDialog(this, this, mPaint.getColor()).show();
			return true;
		case EMBOSS_MENU_ID:
			if (mPaint.getMaskFilter() != mEmboss) {
				mPaint.setMaskFilter(mEmboss);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case BLUR_MENU_ID:
			if (mPaint.getMaskFilter() != mBlur) {
				mPaint.setMaskFilter(mBlur);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
			/*
			 * case ERASE_MENU_ID : mPaint.setXfermode(new
			 * PorterDuffXfermode(PorterDuff.Mode.CLEAR)); return true;
			 */
		case RESET_MENU_ID:
			mView.Reset();
			return true;

		case SRCATOP_MENU_ID:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
			mPaint.setAlpha(0x80);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBackBitmap != null) {
			mBackBitmap.recycle();
			mBackBitmap = null;
		}
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
		/*
		 * if(bmOverlay != null) { bmOverlay.recycle(); bmOverlay = null; }
		 * if(mutableBitmap !=null) { mutableBitmap.recycle(); mutableBitmap=
		 * null ; }
		 */

	}

	@Override
	public void onBackPressed() {

		AlertDialog.Builder alertbox = new AlertDialog.Builder(
				PaintActivity.this);
		//
		View view = View.inflate(PaintActivity.this,R.layout.alert_box_layout, null);

		alertbox.setView(view);

		alertbox.setMessage(R.string.unsaved_changes_text); // Message to be displayed.
		
		Button continueBtn = (Button)view.findViewById(R.id.contBtn);
		Button cancelBtn = (Button)view.findViewById(R.id.cancelBtn);
		
		continueBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				PaintActivity.super.onBackPressed();
			}
		});
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});

		/*alertbox.setPositiveButton(R.string.continue_btn,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						PaintActivity.super.onBackPressed();
					}

				});

		alertbox.setNegativeButton(R.string.cancel_btn,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}

				});*/

		// show the alert box will be swapped by other code later
		alertbox.show();

	}

	EditText filEdit;
	Button yesButton;
	Button noButton;

	protected Dialog onCreateDialog(int d) {

		Dialog dialog = new Dialog(PaintActivity.this);

		dialog.setContentView(R.layout.save_image);
		dialog.setTitle(R.string.save_img_as_text);
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		.format(new Date());

		// dialog.setTitle(R.string.save_img_as_text);
		filEdit = (EditText) dialog.findViewById(R.id.editText1);
		yesButton = (Button) dialog.findViewById(R.id.buttonYes);
		noButton = (Button) dialog.findViewById(R.id.buttonNo);

		filEdit.setText(timeStamp);

		yesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				filePublic = filEdit.getText().toString().trim();
				new MyView(getApplicationContext()).saveBitmaps("0");
			}

		});
		noButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new MyView(getApplicationContext()).saveBitmaps("1");
			}
		});

		return dialog;

	}

}

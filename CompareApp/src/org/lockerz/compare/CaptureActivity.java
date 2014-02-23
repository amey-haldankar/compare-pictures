package org.lockerz.compare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lockerz.compare.guideScreens.DemoActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class CaptureActivity extends FirstRun implements OnTouchListener {

	public boolean rot = false;
	public boolean check = true;
	public final static long vibr = 100;
	public int k = 0;
	Bitmap bmp[] = new Bitmap[4];

	// //////////////////////////////////
	public static final String TAG = "Touch";
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	public static Context mContext;
	// ///////////////////////////////////

	// Physical display width and height.
	public static int displayWidth = 0;
	public static int displayHeight = 0;

	private LinearLayout root;
	private LayoutInflater inflater;

	// maximum number of rows
	private static final int MAX_ROW = 2;
	// maximum number columns
	private int MAX_COL = 1;

	private View[] captView;
	private MyImageView[] captImg;
	public int imgIdG;
	public String picturePath;
	public ScrollView[] sideBar;
	public ImageView rotLeft[], rotRight[], capture[], share[];
	public ImageView home;
	public TextView text;

	// //////////////////////////////////
	Button dialogButton;
	ImageView capImage;
	RelativeLayout cameraRelLay, galleryRelLay;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final String pathFolder = Environment
			.getExternalStorageDirectory().toString() + "/ComparePictures/";

	/**
	 * this variable stores the integer value of camera that is sent via intent
	 * for startActivityForResult()
	 */
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	/**
	 * this variable stores the integer value of gallery that is sent via intent
	 * for startActivityForResult()
	 */
	public static final int SELECT_PICTURE = 200;

	/**
	 * this variable stores the file uri of the image picked-up from gallery
	 */
	private Uri fileUri;

	public int savedIndex = 0;
	public String savedPaths[] = new String[2];
	// //////////////////////////////////

	public long startTime = 0;
	public long endTime = 0;
	public long time = 0;
	public long totalTime = 0;
	public int clickCount = 0;

	public static final long DURATION = 500;

	String imageSelPath[] = new String[2];

	// snapshot
	public static Bitmap snapBmp;

	RelativeLayout showDrawer;

	RelativeLayout myLayout;
	View hiddenInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// displayWidth and displayHeight will change depending on screen
		// orientation. To get these dynamically, we should hook
		// onSizeChanged().
		// This simple example uses only landscape mode, so it's ok to get them
		// once on startup and use those values throughout.

		mContext = this;
		Intent intent = getIntent();
		if (intent != null) {
			MAX_COL = intent.getIntExtra("MAX_COL", 1);
			for (int i = 0; i < 2; i++) {
				imageSelPath[i] = intent.getStringExtra(Integer.toString(i));
			}

		}
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		setContentView(R.layout.main_capture_screen);

		// ///
		home = (ImageView) findViewById(R.id.homeLogoD);
		text = (TextView) findViewById(R.id.title);

		showDrawer = (RelativeLayout) findViewById(R.layout.drawer);

		myLayout = (RelativeLayout) findViewById(R.id.titleBarIn);
		hiddenInfo = getLayoutInflater().inflate(R.layout.drawer, myLayout,
				false);
		home.setVisibility(4);
		text.setVisibility(4);
		myLayout.addView(hiddenInfo);

		// //////

		initialize();

		BitMapManipulation bm = new BitMapManipulation();
		int j = 0;
		for (int i = 0; i < 2; i++) {
			if (imageSelPath[i] != null) {
				captView[j].findViewById(R.id.captBtn1)
						.setVisibility(View.GONE);
				captImg[j].setVisibility(View.VISIBLE);
				captImg[j].setLongClickable(true);
				captImg[j].setOnTouchListener(this);
				if (captImg[j] != null) {
					captImg[j].destroyDrawingCache();
				}

				System.gc();
				Runtime.getRuntime().gc();

				bmp[0] = bm.createBMP(1, imageSelPath[i], displayWidth,
						displayHeight / 2 - 20);
				resetView(captImg[0]);
				captImg[j].setImageBitmap(bmp[0]);
				j++;
			} else if (MyGalleryAdapter.imgPath.size() == 0) {
				if (HomeScreenActivity.selEnabled > 0) {
					if (HomeScreenActivity.itemsSelected[i] != -999) {
						captView[j].findViewById(R.id.captBtn1).setVisibility(
								View.GONE);
						captImg[j].setVisibility(View.VISIBLE);
						captImg[j].setLongClickable(true);
						captImg[j].setOnTouchListener(this);
						captImg[j]
								.setImageResource(MyGalleryAdapter.SAMPLE_IMAGES[HomeScreenActivity.itemsSelected[i]]);
						j++;
					}

				}
			}
		}

		firstRunPreferences();
		if (getFirstRun(0)) {
			Intent demoIntent = new Intent(CaptureActivity.this,
					DemoActivity.class);
			startActivity(demoIntent);
			setRunned(0);
		}
	}

	public void onCaptureBtn(View v) {
		String s = v.getTag().toString();
		if (s != null) {
			imgIdG = Integer.parseInt(s);
		}

		showDialog(0);

	}

	/*
	 * showDialog() method to display dialog box
	 */

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog = new Dialog(CaptureActivity.this);
		switch (id) {
		case 0:

			dialog.setContentView(R.layout.custom_dailog);
			dialog.setTitle("Choose to load image");
			cameraRelLay = (RelativeLayout) dialog.findViewById(R.id.cameraRel);
			galleryRelLay = (RelativeLayout) dialog
					.findViewById(R.id.galleryRel);

			/*
			 * onCliclk listener for relative layout to display camera and click
			 * image
			 */
			cameraRelLay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create
																		// a
																		// file
																		// to
																		// save
																		// the
																		// image
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set
																		// the
																		// image
																		// file
																		// name

					// start the image capture Intent
					startActivityForResult(intent,
							CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					dialog.dismiss();
				}
			});

			/*
			 * onCliclk listener for relative layout to browse image from
			 * gallery
			 */
			galleryRelLay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_PICK);
					startActivityForResult(
							Intent.createChooser(intent, "Select Picture"),
							SELECT_PICTURE);
					dialog.dismiss();
				}
			});

			break;
		}
		return dialog;

	}

	/**
	 * 
	 * @brief getOutputMediaFileUri method
	 * 
	 * @param type
	 * @return
	 * 
	 * @detail Create a file Uri for saving an image or video
	 */

	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * 
	 * @brief getOutputMediaFile method
	 * 
	 * @param type
	 * @return
	 * 
	 * @detail Create a File for saving an image or video
	 */

	@SuppressLint({ "NewApi", "NewApi" })
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), "/ComparePictures/");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("ComparePictures", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	/*
	 * onStartActivity for camera intent and browsing images from gallery
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * setting images from camera click intent
		 */
		try {
			if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
				if (resultCode == RESULT_OK) {

					/*
					 * setting of images captured from camera in imageView
					 */
					setCaptImage(fileUri, 0);
					savedPaths[savedIndex] = fileUri.toString();
					savedIndex = (savedIndex + 1) % 2;
					// setPref(fileUri.getPath());
					HomeScreenActivity.appPref.savePath(fileUri.getPath());
				} else if (resultCode == RESULT_CANCELED) {
					// User cancelled the image capture
				} else {
					// Image capture failed, advise user
				}
			}
		} catch (Exception e) {
			Toast.makeText(CaptureActivity.this, "Unable to load image !!",
					Toast.LENGTH_SHORT).show();
		}

		/*
		 * setting images browsing from gallery
		 */
		try {
			if (requestCode == SELECT_PICTURE
					&& resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				picturePath = cursor.getString(columnIndex);
				// setPref(picturePath);
				HomeScreenActivity.appPref.savePath(picturePath);
				Uri ur = Uri.parse(picturePath);
				savedPaths[savedIndex] = ur.toString();
				savedIndex = (savedIndex + 1) % 2;
				setCaptImage(ur, 1);
				cursor.close();
			}
		} catch (Exception e) {
			Toast.makeText(CaptureActivity.this, "Unable to load image !!",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void setCaptImage(Uri ur, int src) {
		BitMapManipulation bm = new BitMapManipulation();
		// String path;
		switch (imgIdG) {
		case 0:
			captView[0].findViewById(R.id.captBtn1).setVisibility(View.GONE);
			captImg[0].setVisibility(View.VISIBLE);
			captImg[0].setLongClickable(true);
			captImg[0].setOnTouchListener(this);

			if (captImg[0] != null) {
				captImg[0].destroyDrawingCache();
			}
			System.gc();
			Runtime.getRuntime().gc();
			bmp[0] = bm.createBMP(src, ur.toString(), captImg[0].getWidth(),
					captImg[0].getHeight());
			resetView(captImg[0]);
			captImg[0].setImageBitmap(bmp[0]);
			captImg[0].invalidate();

			Toast.makeText(this, "id: " + imgIdG, Toast.LENGTH_SHORT).show();
			break;
		case 1:
			if (MAX_COL == 1) {
				captView[1].findViewById(R.id.captBtn1)
						.setVisibility(View.GONE);
				captImg[1].setVisibility(View.VISIBLE);
				captImg[1].setOnTouchListener(this);
				bmp[1] = bm.createBMP(src, ur.toString(),
						captImg[0].getWidth(), captImg[0].getHeight());
				resetView(captImg[1]);
				captImg[1].setImageBitmap(bmp[1]);
			} else {
				captView[0].findViewById(R.id.captBtn2)
						.setVisibility(View.GONE);
				captImg[1].setVisibility(View.VISIBLE);
				captImg[1].setOnTouchListener(this);
				bmp[1] = bm.createBMP(src, ur.toString(),
						captImg[0].getWidth(), captImg[0].getHeight());
				resetView(captImg[1]);
				captImg[1].setImageBitmap(bmp[1]);
			}
			Toast.makeText(this, "id: " + imgIdG, Toast.LENGTH_SHORT).show();
			break;
		case 2:
			captView[1].findViewById(R.id.captBtn1).setVisibility(View.GONE);
			captImg[2].setVisibility(View.VISIBLE);
			captImg[2].setOnTouchListener(this);
			bmp[2] = bm.createBMP(src, ur.toString(), captImg[2].getWidth(),
					captImg[2].getHeight());
			resetView(captImg[2]);
			captImg[2].setImageBitmap(bmp[2]);
			break;
		case 3:
			captView[1].findViewById(R.id.captBtn2).setVisibility(View.GONE);
			captImg[3].setVisibility(View.VISIBLE);
			captImg[3].setOnTouchListener(this);
			bmp[3] = bm.createBMP(src, ur.toString(), captImg[0].getWidth(),
					captImg[0].getHeight());
			resetView(captImg[3]);
			captImg[3].setImageBitmap(bmp[3]);
			break;
		}
		System.gc();
		Runtime.getRuntime().gc();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		MyImageView image = (MyImageView) v;

		matrix.set(image.getImageMatrix());

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			startTime = System.currentTimeMillis();
			clickCount++;
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			// Log.d(TAG, "mode = drag");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_UP:

		case MotionEvent.ACTION_POINTER_UP:
			time = System.currentTimeMillis() - startTime;
			totalTime = totalTime + time;
			if (clickCount == 2) {
				if (totalTime <= DURATION) {
					onDoubleTap(v);
				}
				clickCount = 0;
				totalTime = 0;
			}
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				float scrollByX = event.getX() - start.x;
				float scrollByY = event.getY() - start.y;
				matrix.postTranslate(scrollByX, scrollByY);
			}
			if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					float scale = newDist / oldDist;
					if (SmallerThanIdentity(matrix) && scale < 1) {
						matrix.postScale(1, 1, mid.x, mid.y);
					} else {
						matrix.set(savedMatrix);
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
			}

			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				mid(mid, event);
				mode = ZOOM;
			}
			break;

		}

		image.setImageMatrix(matrix);
		return true;
	}

	/**
	 * 
	 * @brief shouldSrcoll method
	 * 
	 * @param newMatrix
	 * @param oldMatrix
	 * @param image
	 * @return
	 * 
	 * @detail NOT USED: checks scroll bound
	 */
	public boolean shouldSrcoll(Matrix newMatrix, Matrix oldMatrix,
			MyImageView image) {
		float newValues[] = new float[9];
		float oldValues[] = new float[9];

		newMatrix.getValues(newValues);
		oldMatrix.getValues(oldValues);

		float zoomPercentX = newValues[0] / oldValues[0];
		float zoomPercdntY = newValues[4] / oldValues[4];

		if (newValues[2] < 0 || newValues[5] < 0) {
			float tmpW = -(image.getWidth() / 4) * zoomPercentX;
			float tmpH = -(image.getHeight() / 4) * zoomPercdntY;
			return ((newValues[2] < 0.0f) && (newValues[2] > tmpW)
					&& (newValues[5] < 0.0f) && (newValues[5] > tmpH));
		} else {
			float tmpW = (image.getWidth() / 4) * zoomPercentX;
			float tmpH = (image.getHeight() / 4) * zoomPercdntY;
			return ((newValues[2] > 0.0f) && (newValues[2] < tmpW)
					&& (newValues[5] > 0.0f) && (newValues[5] < tmpH));
		}

	}

	/**
	 * 
	 * @brief resetView method
	 * 
	 * @param v
	 * 
	 * @detail This method is called when the reset button is clicked and will
	 *         reset the pictures is zoomed in/out or rotated left/right
	 */

	public void resetView(View v) {
		ImageView view = (ImageView) v;
		matrix = new Matrix();

		view.setScaleType(ImageView.ScaleType.MATRIX);
		view.setImageMatrix(matrix);
	}

	public void resetLayout(View v) {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(vibr);
		matrix = new Matrix();
		captImg[0].setScaleType(ImageView.ScaleType.MATRIX);
		captImg[0].setImageMatrix(matrix);
		captImg[1].setScaleType(ImageView.ScaleType.MATRIX);
		captImg[1].setImageMatrix(matrix);

	}

	public boolean SmallerThanIdentity(Matrix m) {
		float[] values = new float[9];

		m.getValues(values);

		Log.d("values", "values " + values[0] + " " + values[4] + " "
				+ values[8]);
		if (rot) {
			Log.d("array", values[0] + " " + values[1] + " " + values[2] + " "
					+ values[3] + " " + values[4] + " " + values[5] + " "
					+ values[6] + " " + values[7] + " " + values[8]);
			return ((Math.abs(values[1]) < 1.0) || (Math.abs(values[3]) < 1.0));
		} else {
			return ((Math.abs(values[0]) < 1.0) || (Math.abs(values[4]) < 1.0) || (Math
					.abs(values[8]) < 1.0));
		}

	}

	/**
	 * 
	 * @brief spacing method
	 * 
	 * @param event
	 * @return
	 * 
	 * @detail
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);

		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 
	 * @brief mid method
	 * 
	 * @param point
	 * @param event
	 * 
	 * @detail
	 */
	private void mid(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);

		point.set(x / 2, y / 2);
	}

	/**
	 * 
	 * @brief onDoubleTap method
	 * 
	 * @param v
	 * 
	 * @detail Handles view visibility on double tap on the picture
	 */

	public void onDoubleTap(View v) {
		int id = Integer.parseInt(v.getTag().toString());
		if (rotLeft[id].getVisibility() == View.GONE) {
			rotLeft[id].setVisibility(View.VISIBLE);
			rotRight[id].setVisibility(View.VISIBLE);
			capture[id].setVisibility(View.VISIBLE);
			share[id].setVisibility(View.VISIBLE);
		} else {
			rotLeft[id].setVisibility(View.GONE);
			rotRight[id].setVisibility(View.GONE);
			capture[id].setVisibility(View.GONE);
			share[id].setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * @brief rotateRight method
	 * 
	 * @param v
	 * 
	 * @detail This method is called when the rotate button on the capture
	 *         screen is clicked and will rotate the current picture to right
	 */

	public void rotateLeft(View v) {
		rot = !rot;
		int id = Integer.parseInt(v.getTag().toString());
		Matrix m = new Matrix();
		m.set(captImg[id].getImageMatrix());
		m.postRotate(-90, captImg[id].getWidth() / 2,
				captImg[id].getHeight() / 2);
		captImg[id].setImageMatrix(m);
	}

	/**
	 * 
	 * @brief rotateRight method
	 * 
	 * @param v
	 * 
	 * @detail This method is called when the rotate button on the capture
	 *         screen is clicked and will rotate the current picture to right
	 */

	public void rotateRight(View v) {
		rot = !rot;
		int id = Integer.parseInt(v.getTag().toString());
		Matrix m = new Matrix();
		m.set(captImg[id].getImageMatrix());
		m.postRotate(90, captImg[id].getWidth() / 2,
				captImg[id].getHeight() / 2);
		captImg[id].setImageMatrix(m);
	}

	/**
	 * 
	 * @brief share method
	 * 
	 * @param v
	 * 
	 * @detail This method is called when the share button on the capture screen
	 *         is clicked and will launch a list of applications which can do
	 *         this operation
	 */

	public void share(View v) {
		final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(vibr);
		int id = Integer.parseInt(v.getTag().toString());
		String path = savedPaths[id];
		final Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
		startActivity(Intent.createChooser(shareIntent, "Share image using"));

	}

	/**
	 * 
	 * @brief onSnapShot method
	 * 
	 * @param v
	 * 
	 * @detail This method is called when the snapShot button is clicked
	 */

	public void onSnapShot(View v) {
		final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(vibr);

		root.invalidate();
		root.setDrawingCacheEnabled(true);
		snapBmp = root.getDrawingCache();

		FileOutput fo = new FileOutput();
		fo.writeToFile("snapImage", snapBmp);

		final Intent intent = new Intent(this, PaintActivity.class);
		startActivity(intent);
	}

	public void launchHome(View v) {

		super.onBackPressed();
	}

	/**
	 * 
	 * @brief getDrawer method
	 * 
	 * @param v
	 * 
	 * @detail This method is called when the options button is clicked to
	 *         inflate the drawer layout which contains buttons for
	 *         resetView,snapShot and rateUs
	 */

	public void getDrawer(View v) {
		final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(vibr);

		if (check) {

			myLayout.removeAllViewsInLayout();
			myLayout.removeView(hiddenInfo);
			home.setVisibility(0);
			text.setVisibility(0);
			check = false;

		} else {
			home.setVisibility(4);
			text.setVisibility(4);
			myLayout.addView(hiddenInfo);

			check = true;
		}

	}

	/**
	 * 
	 * @brief rateUS method
	 * 
	 * @param v
	 * 
	 * @detail This method is called when the rateUs button is clicked this
	 *         calls another method launchmarket which handles the call and
	 *         contains the app's market link
	 */

	public void rateUS(View v) {
		final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(vibr);

		final AlertDialog.Builder alertbox = new AlertDialog.Builder(
				CaptureActivity.this);

		alertbox.setMessage(R.string.rate_us_text); // Message to be displayed

		alertbox.setPositiveButton(R.string.yes_btn,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						launchMarket();
					}

				});

		alertbox.setNegativeButton(R.string.no_btn,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}

				});

		// show the alert box will be swapped by other code later
		alertbox.show();

	}

	private void launchMarket() {
		final Uri uri = Uri.parse("market://details?id=" + getPackageName());
		final Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "Couldn't Launch Market", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (bmp[0] != null) {
			bmp[0].recycle();
		}
		if (bmp[1] != null) {
			bmp[1].recycle();
		}
		if (snapBmp != null) {
			snapBmp.recycle();
		}
	}

	/**
	 * 
	 * @brief initialize method
	 * 
	 * 
	 * @detail This method initializes views, inflate layouts and add them to
	 *         the root layout
	 */
	private void initialize() {

		root = (LinearLayout) findViewById(R.id.root);
		inflater = LayoutInflater.from(this);
		captView = new View[MAX_ROW];
		captImg = new MyImageView[MAX_COL * MAX_ROW];
		rotLeft = new ImageView[MAX_COL * MAX_ROW];
		rotRight = new ImageView[MAX_COL * MAX_ROW];
		capture = new ImageView[MAX_COL * MAX_ROW];
		share = new ImageView[MAX_COL * MAX_ROW];

		for (int i = 0; i < MAX_ROW; i++) {
			if (MAX_COL == 1) {
				captView[i] = inflater.inflate(R.layout.one_img_template, null);
				captView[i].findViewById(R.id.captImg1).setTag(
						Integer.toString(i));
				captView[i].findViewById(R.id.captBtn1).setTag(
						Integer.toString(i));
			} else {
				captView[i] = inflater.inflate(R.layout.two_img_template, null);
				captView[i].findViewById(R.id.captImg1).setTag(
						Integer.toString(MAX_ROW * i));
				captView[i].findViewById(R.id.captBtn1).setTag(
						Integer.toString(MAX_ROW * i));
				captView[i].findViewById(R.id.captImg2).setTag(
						Integer.toString(MAX_ROW * i + 1));
				captView[i].findViewById(R.id.captBtn2).setTag(
						Integer.toString(MAX_ROW * i + 1));
			}
			root.addView(captView[i], LayoutParams.FILL_PARENT,
					(displayHeight / 2 - 25));
			View v = new View(this);
			v.setBackgroundColor(0xcc0000);
			root.addView(v, LayoutParams.FILL_PARENT, 5);

			captImg[MAX_COL * i] = (MyImageView) captView[i]
					.findViewById(R.id.captImg1);
			captImg[MAX_COL * i].setTag(Integer.toString(MAX_COL * i));

			rotLeft[MAX_COL * i] = (ImageView) captView[i]
					.findViewById(R.id.rotLeft);
			rotLeft[MAX_COL * i].setTag(Integer.toString(MAX_COL * i));

			rotRight[MAX_COL * i] = (ImageView) captView[i]
					.findViewById(R.id.rotRight);
			rotRight[MAX_COL * i].setTag(Integer.toString(MAX_COL * i));

			capture[MAX_COL * i] = (ImageView) captView[i]
					.findViewById(R.id.capture);
			capture[MAX_COL * i].setTag(Integer.toString(MAX_COL * i));

			share[MAX_COL * i] = (ImageView) captView[i]
					.findViewById(R.id.ishare);
			share[MAX_COL * i].setTag(Integer.toString(MAX_COL * i));

			if (MAX_COL == 2) {
				captImg[MAX_COL * i + 1] = (MyImageView) captView[i]
						.findViewById(R.id.captImg2);
				captImg[MAX_COL * i + 1].setTag(Integer.toString(MAX_COL * i
						+ 1));

				rotLeft[MAX_COL * i + 1] = (ImageView) captView[i]
						.findViewById(R.id.rotLeft2);
				rotLeft[MAX_COL * i + 1].setTag(Integer.toString(MAX_COL * i
						+ 1));

				rotRight[MAX_COL * i + 1] = (ImageView) captView[i]
						.findViewById(R.id.rotRight2);
				rotRight[MAX_COL * i + 1].setTag(Integer.toString(MAX_COL * i
						+ 1));

				capture[MAX_COL * i + 1] = (ImageView) captView[i]
						.findViewById(R.id.capture2);
				capture[MAX_COL * i + 1].setTag(Integer.toString(MAX_COL * i
						+ 1));

			}

		}
	}
}

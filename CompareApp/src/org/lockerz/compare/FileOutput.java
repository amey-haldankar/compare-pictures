package org.lockerz.compare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;

public class FileOutput {

	public static final String pathFolder = Environment
			.getExternalStorageDirectory().toString() + "/ComparePictures/";

	public FileOutput() {

	}

	public void writeToFile(String fileName, Bitmap bmp) {

		File resolveMeSDCard = new File(pathFolder + fileName + ".jpg");

		try {
			boolean fileCreated = resolveMeSDCard.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File file = new File(pathFolder + fileName + ".jpg");

		if (file.exists()) {
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

package wavetech.facelocker;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FaceRegister implements Runnable{
  private int picSuffix=0;
  private  static String TAG = CameraActivity.TAG;
  private final String imgPath = Environment.DIRECTORY_PICTURES;
  private Debouncer debouncer;
  FaceRegister(){

  }
  private void saveMatToImg(Mat mat) {


      // Resize image to 100x100
      Mat resizedImg = new Mat();
      Size size = new Size(100, 100);
      Imgproc.resize(mat, resizedImg, size);
      File path = Environment.getExternalStoragePublicDirectory(imgPath);
      String filename = "pic" + picSuffix + ".jpg";
      picSuffix++;
      File file = new File(path, filename);

      Boolean bool = null;
      filename = file.toString();

      //Bitmap for processing and saving image
      Bitmap bitmap= Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
      Utils.matToBitmap(resizedImg,bitmap);
      try {
        FileOutputStream savedImageStream = new FileOutputStream(filename,false);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, savedImageStream);
        savedImageStream.close();
      }
      catch (Exception e) {
        Log.e(TAG,e.getCause()+" "+e.getMessage());
        e.printStackTrace();
      }

      /*bool = Highgui.imwrite(filename, resizedImg);

      if (bool == true)
        Log.i(TAG, "SUCCESS writing image to external storage");
      else
        Log.i(TAG, "Failure writing image to external storage");*/

      /*Intent mediaScanIntent = new Intent(
        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
      Uri contentUri = Uri.fromFile(file);
      mediaScanIntent.setData(contentUri);
      this.sendBroadcast(mediaScanIntent);*/
    }



  private long lastDebounceTime;
  private void debounce(Object message,long delay){
    long lastClickTime = lastDebounceTime;
    long now = System.currentTimeMillis();
    lastDebounceTime = now;
    if (now - lastClickTime < delay) {
      Log.d(TAG, "Too much call ignored");
    }
  }
}

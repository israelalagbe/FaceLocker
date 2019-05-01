package wavetech.facelocker.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

//import com.googlecode.javacv.cpp.opencv_core;
//import com.googlecode.javacv.cpp.opencv_core.IplImage;
//
//import static com.googlecode.javacv.cpp.opencv_contrib.createEigenFaceRecognizer;
//import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
//import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
//import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
//import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.opencv.core.CvType.CV_32SC1;
import static org.opencv.imgcodecs.Imgcodecs.CV_IMWRITE_JPEG_QUALITY;
//import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;





//New imports
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_face.FisherFaceRecognizer;
//import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;





import wavetech.facelocker.CameraActivity;

public class FaceRegister{
  private int savedImagesCount=0;
  private  static String TAG = CameraActivity.TAG;
  private final String imgPath = Environment.DIRECTORY_PICTURES;
  private final int defaultFaceLabel=1;
  FaceRecognizer faceRecognizer;
  static{
    //System.loadLibrary("tbb");
    //System.loadLibrary("opencv_core");

  }
  public FaceRegister(){

    faceRecognizer =   FisherFaceRecognizer.create();//com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer(2,8,8,8,200);
  }
  public void predict(Context context,opencv_core.Mat image) throws IOException{
    IntPointer label = new IntPointer(defaultFaceLabel);
    DoublePointer confidence = new DoublePointer(1);
    FaceRecognizer faceRecognizer = FisherFaceRecognizer.create();
    faceRecognizer.predict(image, label, confidence);
    int predictedLabel = label.get(0);
    Log.v(TAG,"Predicted Label: "+predictedLabel);
  }
  public void trainModels(Context context) throws IOException{
    FilenameFilter fileNameFilter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".jpg")||name.toLowerCase().endsWith(".jpeg");

      };
    };
    File path = context.getFilesDir();
    File[] capturedImages=path.listFiles(fileNameFilter);
    opencv_core.Mat labels = new opencv_core.Mat(capturedImages.length, 1, CV_32SC1);
    //int[] labels = new int[capturedImages.length];
    MatVector images = new MatVector(capturedImages.length);
    IntBuffer labelsBuf = labels.createBuffer();
    for (int i=0;i<capturedImages.length;i++)
    {

      File imageFile=capturedImages[i];
      opencv_core.Mat img = imread(imageFile.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

      if(img==null){
        throw  new IOException("Unable to load CV IMage from path: "+imageFile.getAbsolutePath());
      }



//      opencv_core.IplImage grayImg = opencv_core.IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
//      cvCvtColor(img, grayImg, CV_BGR2GRAY);
//      images.put
      labelsBuf.put(i, defaultFaceLabel);
      images.put(i, img);
      //Label corresponding to the id of t
      // he image to be used for recognizing which image it is,
      // I'd be using ID 1 for now
      //labels.put(img);
      //labels[i]=defaultFaceLabel;


    }
    FaceRecognizer faceRecognizer = FisherFaceRecognizer.create();
    faceRecognizer.train(images,labels);
    File targetFile=new File(path, "train.xml");
    faceRecognizer.save(targetFile.getAbsolutePath());
    Log.v(TAG,"Saved: "+targetFile.exists());
    opencv_core.Mat testImage=imread(capturedImages[0].getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
    this.predict(context,testImage);
  }
  private void saveMatToImg(Context context,Mat mat) throws IOException {


      // Resize image to 100x100
      Mat resizedImg = new Mat();
      Size size = new Size(200, 200);
      Imgproc.resize(mat, resizedImg, size);
      File path = context.getFilesDir();//Environment.getExternalStoragePublicDirectory(imgPath);
      Log.v(TAG,"Path Exists: "+path.exists()+" Path: "+path.getAbsolutePath());

      if (savedImagesCount<10) savedImagesCount++;

      String filename = "pic" + savedImagesCount + ".jpeg";
      File file = new File(path, filename);
      Log.v(TAG,"Created: "+file.createNewFile());

      Boolean bool = null;
      filename = file.toString();

      //Bitmap for processing and saving image
      //Bitmap bitmap= Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
      MatOfInt param=new MatOfInt(CV_IMWRITE_JPEG_QUALITY,100);
      boolean saved=Imgcodecs.imwrite(filename,resizedImg,param);
      if(!saved)
        throw  new IOException("Failed to save image: "+filename+" to external storage!");

//      Utils.matToBitmap(resizedImg,bitmap);
//      try {
//        FileOutputStream savedImageStream = new FileOutputStream(filename,false);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, savedImageStream);
//        savedImageStream.close();
//      }
//      catch (Exception e) {
//        Log.e(TAG,e.getCause()+" "+e.getMessage());
//        e.printStackTrace();
//      }

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

  public int getSavedImagesCount() {
    return savedImagesCount;
  }

  private long lastDebounceTime;
  public void debounceImageSaveCall(Context context, Mat mat, long delay) throws  IOException{
    long lastClickTime = lastDebounceTime;
    long now = System.currentTimeMillis();
    lastDebounceTime = now;
    if (now - lastClickTime < delay) {
      //Log.d(TAG, "Too much call ignored");
    }
    else{
      Log.v(TAG,"Calling save");
      saveMatToImg(context,mat);
    }
  }
}
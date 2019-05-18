package wavetech.facelocker.utils;


import java.io.File;

import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.resize;





//New imports
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_face.FisherFaceRecognizer;
import org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer;
//import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import wavetech.facelocker.CameraActivity;

public class FaceRegister{
  private int savedImagesCount=0;
  private  static String TAG = CameraActivity.TAG;
  private final String imgPath = Environment.DIRECTORY_PICTURES;
  FaceRecognizer faceRecognizer;
  static{
    //System.loadLibrary("tbb");
    //System.loadLibrary("opencv_core");

  }
  public FaceRegister(){
    //faceRecognizer =   FisherFaceRecognizer.create();//com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer(2,8,8,8,200);
  }
  private long lastPredictTime;
  public boolean predict(Context context,opencv_core.Mat mat) throws IOException{

    //Debounce
    long lastClickTime = lastPredictTime;
    long now = System.currentTimeMillis();
    if (now - lastClickTime < 500) {
      //Log.d(TAG, "Too much predict call ignored");
      return false;
    }
    lastPredictTime=now;
    //end Debounce

    PasswordStore passwordStore=new PasswordStore(context);
    File path = context.getFilesDir();
    File file = new File(path, "current.jpg");




    opencv_core.Mat resizeimage=new opencv_core.Mat();
    opencv_core.Size size = new opencv_core.Size(200, 200);
    resize(mat,resizeimage,size);
    imwrite(file.getAbsolutePath(),resizeimage);

    opencv_core.Mat image=imread(file.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

    IntPointer label = new IntPointer(1);
    DoublePointer confidence = new DoublePointer(1);


    if(faceRecognizer==null){
      File trainingFile=new File(path, "train.xml");
      faceRecognizer = LBPHFaceRecognizer.create(2,8,8,8,200);
      faceRecognizer.read(trainingFile.getAbsolutePath());
    }
    faceRecognizer.predict(image, label, confidence);
    int predictedLabel = label.get(0);


    Log.v(TAG,"Predicted Label: "+predictedLabel);
    Log.v(TAG,"Predicted Confidence: "+confidence.get(0));

    image.release();
    resizeimage.release();
    if(passwordStore.hasFaceLabel(predictedLabel) && confidence.get(0)<100)
      return true;
    return false;
  }
  public boolean clearFaceDatabase(Context context){
    File path = context.getFilesDir();
    File targetFile=new File(path, "train.xml");
    return targetFile.delete();
  }
  public void trainModels(Context context) throws IOException{
    FilenameFilter fileNameFilter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return /*name.toLowerCase().endsWith(".jpg")||*/name.toLowerCase().endsWith(".jpeg");

      };
    };
    File path = context.getFilesDir();
    File[] capturedImages=path.listFiles(fileNameFilter);
    opencv_core.Mat labels = new opencv_core.Mat(capturedImages.length, 1, opencv_core.CV_32SC1);
    PasswordStore passwordStore=new PasswordStore(context);
    int label=passwordStore.getIncrementFaceLabel();
    //int[] labels = new int[capturedImages.length];
    MatVector images = new MatVector(capturedImages.length);
    opencv_core.Mat mats[] =new opencv_core.Mat[capturedImages.length];
    IntBuffer labelsBuf = labels.createBuffer();
    for (int i=0;i<capturedImages.length;i++)
    {

      File imageFile=capturedImages[i];
      opencv_core.Mat img = imread(imageFile.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

      if(img==null){
        throw  new IOException("Unable to load CV IMage from path: "+imageFile.getAbsolutePath());
      }



      labelsBuf.put(i, label);
      images.put(i, img);
      mats[i]=img;
    }
    File databaseModelFile=new File(path, "train.xml");
    FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create(2,8,8,8,200);
    //Read existing faces database
    if(databaseModelFile.exists())
      faceRecognizer.read(databaseModelFile.getAbsolutePath());

    faceRecognizer.train(images,labels);

    faceRecognizer.save(databaseModelFile.getAbsolutePath());
    passwordStore.addFace(passwordStore.getCurrentFaceName(),label);
    Log.v(TAG,"Saving Face: Name"+passwordStore.getCurrentFaceName()+" Label:"+label);
    passwordStore.save();
    Log.v(TAG,"Saved: "+databaseModelFile.exists()+" image path: " + capturedImages[0].getAbsolutePath());


    for(opencv_core.Mat mat:mats) mat.release();
  }
  private void saveMatToImg(Context context,opencv_core.Mat mat) throws IOException {


      // Resize image to 100x100

      opencv_core.Mat resizeimage=new opencv_core.Mat();
      opencv_core.Size size = new opencv_core.Size(200, 200);
      resize(mat,resizeimage,size);
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
      //MatOfInt param=new MatOfInt(CV_IMWRITE_JPEG_QUALITY,100);
      boolean saved=imwrite(filename,resizeimage);
      if(!saved)
        throw  new IOException("Failed to save image: "+filename+" to external storage!");
  }

  public int getSavedImagesCount() {
    return savedImagesCount;
  }

  private long lastDebounceTime;
  public void debounceImageSaveCall(Context context, opencv_core.Mat mat, long delay) throws  IOException{
    long lastClickTime = lastDebounceTime;
    long now = System.currentTimeMillis();

    if (now - lastClickTime < delay) {
      //Log.d(TAG, "Too much call ignored");
    }
    else{
      lastDebounceTime = now;
      Log.v(TAG,"Calling save");
      saveMatToImg(context,mat);
    }
  }
}

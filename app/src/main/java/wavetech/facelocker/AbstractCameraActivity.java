package wavetech.facelocker;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import wavetech.facelocker.utils.FaceRegister;

abstract  public class AbstractCameraActivity
    extends AppCompatActivity
    implements CameraBridgeViewBase.CvCameraViewListener2
{
  // Used for logging success or failure messages
  public static final String TAG = "OCVSample::Activity";

  // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
  protected CameraBridgeViewBase mOpenCvCameraView;


  protected FaceRegister faceRegister=new FaceRegister();

  // Used in Camera selection from menu (when implemented)
  protected boolean              mIsJavaCamera = true;
  protected MenuItem mItemSwitchCamera = null;

  // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
  Mat mRgba;
  Mat mRgbaF;
  Mat mRgbaT;


  protected CascadeClassifier cascadeClassifier;
  protected Mat grayscaleImage;
  protected int absoluteFaceSize;

  //Initialize the loader
  protected KProgressHUD progressLoader;



//  static  {
//    System.loadLibrary("opencv_java3");
//  }

  //Now, lets call OpenCV manager to help our app communicate with android phone to make OpenCV work
  protected BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
    @Override
    public void onManagerConnected(int status) {

      switch (status) {
        case LoaderCallbackInterface.SUCCESS:
        {
          Log.i(TAG, "OpenCV loaded successfully");
          initializeOpenCVDependencies();
//          mOpenCvCameraView.enableView();
          //System.loadLibrary("detection_based_tracker");

        } break;
        default:
        {
          super.onManagerConnected(status);
        } break;
      }
    }
  };
  protected void initializeOpenCVDependencies() {

    try {
      // Copy the resource into a temp file so OpenCV can load it
      InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
      File cascadeDir = getDir("cascade", getApplicationContext().MODE_PRIVATE);
      File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
      FileOutputStream os = new FileOutputStream(mCascadeFile);


      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = is.read(buffer)) != -1) {
        os.write(buffer, 0, bytesRead);
      }
      is.close();
      os.close();

      // Load the cascade classifier
      cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
    } catch (Exception e) {
      Log.e("OpenCVActivity", "Error loading cascade", e);
    }

    // And we are ready to go
    mOpenCvCameraView.enableView();
  }
  /*
   *  The following three functions handle the
   *  events when the app is Paused,
   *  Resumed and Closed/Destroyed
   *
   */
  @Override
  public void onPause()
  {
    super.onPause();
    if (mOpenCvCameraView != null)
      mOpenCvCameraView.disableView();
  }

  @Override
  public void onResume()
  {
    super.onResume();
    /*static {
    OpenCVLoader.initDebug();

  }*/

    if (!OpenCVLoader.initDebug()) {
      Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
      OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    } else {
      //System.loadLibrary("opencv_java");
      Log.d(TAG, "OpenCV library found inside package. Using it!");
      mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }
  }

  public void onDestroy() {
    super.onDestroy();
    if (mOpenCvCameraView != null)
      mOpenCvCameraView.disableView();
  }
  //Receive Image Data when the camera preview starts on your screen 😀
  @Override
  public void onCameraViewStarted(int width, int height) {
    mRgba = new Mat(height, width, CvType.CV_8UC4);
    mRgbaF = new Mat(height, width, CvType.CV_8UC4);
    mRgbaT = new Mat(width, width, CvType.CV_8UC4);

    grayscaleImage = new Mat(height, width, CvType.CV_8UC4);

    // The faces will be a 20% of the height of the screen
    absoluteFaceSize = (int) (height * 0.2);
  }
  //Destroy image data when you stop camera preview on your phone screen
  @Override
  public void onCameraViewStopped() {
    mRgba.release();
  }



}

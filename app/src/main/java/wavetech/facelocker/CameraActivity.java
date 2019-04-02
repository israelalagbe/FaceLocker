package wavetech.facelocker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
//OpenCV Java Classes
import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class CameraActivity extends AppCompatActivity implements CvCameraViewListener2 {
  // Used for logging success or failure messages
  private static final String TAG = "OCVSample::Activity";

  // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
  private CameraBridgeViewBase mOpenCvCameraView;

  // Used in Camera selection from menu (when implemented)
  private boolean              mIsJavaCamera = true;
  private MenuItem             mItemSwitchCamera = null;

  // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
  Mat mRgba;
  Mat mRgbaF;
  Mat mRgbaT;

  //Now, lets call OpenCV manager to help our app communicate with android phone to make OpenCV work
  private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
    @Override
    public void onManagerConnected(int status) {
      switch (status) {
        case LoaderCallbackInterface.SUCCESS:
        {
          Log.i(TAG, "OpenCV loaded successfully");
          mOpenCvCameraView.enableView();
          //System.loadLibrary("detection_based_tracker");

        } break;
        default:
        {
          super.onManagerConnected(status);
        } break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_camera);
    askForPermissions();
    mOpenCvCameraView = (JavaCameraView) findViewById(R.id.camera_view);
    mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
    mOpenCvCameraView.setCvCameraViewListener(this);

  }
  private void askForPermissions(){
    // Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(this,
      Manifest.permission.CAMERA)
      != PackageManager.PERMISSION_GRANTED) {

      // Permission is not granted
      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
        Manifest.permission.CAMERA)) {
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
      } else {
        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},80);


        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    }
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
    if (!OpenCVLoader.initDebug()) {
      Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
      OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    } else {
      Log.d(TAG, "OpenCV library found inside package. Using it!");
      mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }
  }

  public void onDestroy() {
    super.onDestroy();
    if (mOpenCvCameraView != null)
      mOpenCvCameraView.disableView();
  }


  /**
   * Now, this one is interesting! OpenCV orients the camera
   * to left by 90 degrees. So if the app is in portrait more,
   * camera will be in -90 or 270 degrees orientation. We fix that in the n
   * ext and the most important function. There you go!
   * @param inputFrame
   * @return
   */
  @Override
  public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    mRgba = inputFrame.rgba();
    // Rotate mRgba 90 degrees
    Core.transpose(mRgba, mRgbaT);
    Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
    Core.flip(mRgbaF, mRgba, 1 );

    return mRgba; // This function must return
  }


  //Receive Image Data when the camera preview starts on your screen 😀
  @Override
  public void onCameraViewStarted(int width, int height) {
    mRgba = new Mat(height, width, CvType.CV_8UC4);
    mRgbaF = new Mat(height, width, CvType.CV_8UC4);
    mRgbaT = new Mat(width, width, CvType.CV_8UC4);
  }
  //Destroy image data when you stop camera preview on your phone screen
  @Override
  public void onCameraViewStopped() {
    mRgba.release();
  }
}

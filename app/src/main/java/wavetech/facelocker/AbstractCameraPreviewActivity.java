package wavetech.facelocker;

import android.support.v7.app.AppCompatActivity;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;

import wavetech.facelocker.utils.FaceRegister;

import static org.bytedeco.javacpp.opencv_core.LINE_8;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;

abstract  public class AbstractCameraPreviewActivity
  extends AppCompatActivity
  implements CvCameraPreview.CvCameraViewListener
{
  public CvCameraPreview cameraView;
  public int absoluteFaceSize = 0;
  public opencv_objdetect.CascadeClassifier faceDetector;
  FaceRegister faceRegister=new FaceRegister();

  @Override
  public void onCameraViewStarted(int width, int height) {
    absoluteFaceSize = (int) (width * 0.32f);
  }

  @Override
  public void onCameraViewStopped() {

  }




}

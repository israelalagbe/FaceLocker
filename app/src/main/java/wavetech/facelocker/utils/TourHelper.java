package wavetech.facelocker.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

public class TourHelper {
  public static void showTourForView(Activity context, View targetView, String title, String description, TapTargetView.Listener listener){
    TapTargetView.showFor(context,
      TapTarget.forView(targetView,title,description)
        .transparentTarget(true)
      ,listener);
  }
  public static void showTourForView(Activity context, View targetView, String title, String description){
    TapTargetView.showFor(context,
      TapTarget.forView(targetView,title,description)
      .transparentTarget(true)
    );
  }
}

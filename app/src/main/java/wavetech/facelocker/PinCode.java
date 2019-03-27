package wavetech.facelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PinCode extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pin_code);

    Button continueButton=findViewById(R.id.continueBtn);
    continueButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        launchCameraActivity();
      }
    });
  }
  private void launchCameraActivity(){
    Intent intent=new Intent(PinCode.this,CameraActivity.class);
    startActivity(intent);
  }
}

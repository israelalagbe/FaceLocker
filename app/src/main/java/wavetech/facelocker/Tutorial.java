package wavetech.facelocker;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class Tutorial extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tutorial);
  }
  public void launchPinCodeActivity(View view){
    Intent intent=new Intent(Tutorial.this,PatternActivity.class);
    startActivity(intent);
  }
}

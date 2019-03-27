package wavetech.facelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
  Switch enableLockSwitch;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    enableLockSwitch=findViewById(R.id.enableLockSwitch);
    enableLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if(checked){
          launchPinCodeActivity();
        }
      }
    });
  }
  private void launchPinCodeActivity(){
    Intent intent=new Intent(MainActivity.this,PinCode.class);
    startActivity(intent);
  }
}

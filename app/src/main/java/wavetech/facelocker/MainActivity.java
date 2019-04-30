package wavetech.facelocker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import wavetech.facelocker.utils.LockscreenService;
import wavetech.facelocker.utils.PasswordStore;

public class MainActivity extends AppCompatActivity {
  Switch enableLockSwitch;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final PasswordStore passwordStore=new PasswordStore(getApplicationContext());
    enableLockSwitch=findViewById(R.id.enableLockSwitch);
    enableLockSwitch.setChecked(passwordStore.getIsScreenLockEnabled());
    if(passwordStore.getIsScreenLockEnabled())
      startScreenLock();
//    Toast.makeText(getApplicationContext(),"Pincode:"+passwordStore.getPinCode()+" Pattern code: "+passwordStore.getPatternCode(),Toast.LENGTH_LONG).show();
    enableLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if(checked){
          askForPermissions();
          launchPinCodeActivity();
        }
        else{
          passwordStore.reset();
          stopScreenLock();
        }
      }
    });

  }
  private void stopScreenLock(){
    stopService(new Intent(this, LockscreenService.class));
  }
  private void startScreenLock(){
    startService(new Intent(this, LockscreenService.class));
  }
  private void launchPinCodeActivity(){
    Intent intent=new Intent(MainActivity.this,PinCode.class);
    startActivity(intent);
  }
  private void askForPermissions(){
    askPermissionWithCode(Manifest.permission.CAMERA);
    askPermissionWithCode(Manifest.permission.READ_EXTERNAL_STORAGE);
    askPermissionWithCode(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    askPermissionWithCode(Manifest.permission.DISABLE_KEYGUARD);
    askPermissionWithCode(Manifest.permission.WAKE_LOCK);
    askPermissionWithCode(Manifest.permission.RECEIVE_BOOT_COMPLETED);
    askPermissionWithCode(Manifest.permission.READ_PHONE_STATE);
    askPermissionWithCode(Manifest.permission.SYSTEM_ALERT_WINDOW);
    askPermissionWithCode(Manifest.permission.REORDER_TASKS);
  }
  private void askPermissionWithCode(String code){
    // Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(this,
      code)
      != PackageManager.PERMISSION_GRANTED) {

      // Permission is not granted
      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
        code)) {
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
      } else {
        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(this,new String[]{code},80);


        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    }
  }
}

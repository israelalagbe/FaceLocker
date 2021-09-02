package wavetech.facelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.getkeepsafe.taptargetview.TapTargetView;

import wavetech.facelocker.utils.PasswordStore;
import wavetech.facelocker.utils.TourHelper;

public class PincodeActivity extends AppCompatActivity {
  private EditText pinCodeInput;
  private Button btnContinue;
  String pinCodeText="";
  private PasswordStore passwordStore;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pin_code_alternative);
    passwordStore= new PasswordStore(getApplicationContext());
    pinCodeInput = findViewById(R.id.pinCodeInput);
    btnContinue=findViewById(R.id.btnContinue);
    TourHelper.showTourForView(this,pinCodeInput,"Password","Please enter your password not less than 4 characters");
    initializeListeners();
  }
  private void initializeListeners(){
    pinCodeInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        pinCodeText=charSequence.toString();
        if(pinCodeText.length()>=4){
          btnContinue.setVisibility(View.VISIBLE);
          TourHelper.showTourForView(PincodeActivity.this,btnContinue,"Save button","Click this button now to go to the next stage" ,new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
            @Override
            public void onTargetClick(TapTargetView view) {
              super.onTargetClick(view);      // This call is optional
              continueButtonClick();
            }
          });
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {

      }
    });

    btnContinue.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        continueButtonClick();
      }
    });
  }
  private void launchCameraActivity(){
    Intent intent=new Intent(PincodeActivity.this,CameraActivity.class);
    startActivity(intent);
  }
  private void continueButtonClick(){
    passwordStore.setPinCode(pinCodeText);
    passwordStore.save();
    launchCameraActivity();
  }
}

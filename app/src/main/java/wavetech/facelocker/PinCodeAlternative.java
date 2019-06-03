package wavetech.facelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import wavetech.facelocker.utils.PasswordStore;

public class PinCodeAlternative extends AppCompatActivity {
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
        if(pinCodeText.length()>=8)
          btnContinue.setVisibility(View.VISIBLE);
      }

      @Override
      public void afterTextChanged(Editable editable) {

      }
    });

    btnContinue.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        passwordStore.setPinCode(pinCodeText);
        passwordStore.save();
        launchCameraActivity();
      }
    });
  }
  private void launchCameraActivity(){
    Intent intent=new Intent(PinCodeAlternative.this,CameraActivity.class);
    startActivity(intent);
  }
}

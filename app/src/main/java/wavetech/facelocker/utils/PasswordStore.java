package wavetech.facelocker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class PasswordStore {
  private static final String StorageKey = "Passwords" ;
  private SharedPreferences sharedpreferences;
  private String patternCode;
  private String pinCode;
  private Map<String,Integer> facesLabels=new HashMap();
  private boolean isScreenLockEnabled;

  public  PasswordStore(Context context){
    sharedpreferences = context.getSharedPreferences(StorageKey, Context.MODE_PRIVATE);
    isScreenLockEnabled = sharedpreferences.getBoolean("isScreenLockEnabled",false);
    patternCode = sharedpreferences.getString("patternCode",null);
    pinCode = sharedpreferences.getString("pinCode",null);

    //Map<String,Integer> storedFacesLabels=sharedpreferences.get
  }


  public String getPatternCode() {
    return patternCode;
  }

  public void setPatternCode(String patternCode) {
    this.patternCode = patternCode;
  }

  public String getPinCode() {
    return pinCode;
  }

  public void setPinCode(String pinCode) {
    this.pinCode = pinCode;
  }

  public void setIsScreenLockEnabled(boolean isScreenLockEnabled) {
    this.isScreenLockEnabled = isScreenLockEnabled;
  }

  public boolean getIsScreenLockEnabled() {
    return isScreenLockEnabled;
  }

  public void save(){
    SharedPreferences.Editor editor = sharedpreferences.edit();
    editor.putBoolean("isScreenLockEnabled",isScreenLockEnabled);
    editor.putString("patternCode", patternCode);
    editor.putString("pinCode", pinCode);
    editor.commit();
  }
  public void reset(){
    setPatternCode(null);
    setPinCode(null);
    setIsScreenLockEnabled(false);
    save();
  }

}

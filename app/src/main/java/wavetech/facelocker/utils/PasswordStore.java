package wavetech.facelocker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

import wavetech.facelocker.CameraActivity;

public class PasswordStore {
  private static final String StorageKey = "Passwords" ;
  private SharedPreferences sharedpreferences;
  private String patternCode;
  private String pinCode;
  private JSONObject faces;
  private boolean isScreenLockEnabled;
  private static String currentFaceName;

  public  PasswordStore(Context context){
    sharedpreferences = context.getSharedPreferences(StorageKey, Context.MODE_PRIVATE);
    isScreenLockEnabled = sharedpreferences.getBoolean("isScreenLockEnabled",false);
    patternCode = sharedpreferences.getString("patternCode",null);
    pinCode = sharedpreferences.getString("pinCode",null);
    JSONParser parser=new JSONParser();
    try {
      faces=  (JSONObject)parser.parse(sharedpreferences.getString("faces","{}")); //sharedpreferences.get
    }
    catch (Exception e){
      faces=new JSONObject();
      Log.e(CameraActivity.TAG,"JSON object faces error: "+e.getMessage());
    }
    //faces.put("Israel",1);
  }

  public String getCurrentFaceName() {
    return currentFaceName;
  }

  public void setCurrentFaceName(String currentFaceName) {
    this.currentFaceName = currentFaceName;
    Log.v(CameraActivity.TAG,"Setting Current face: "+ getCurrentFaceName());
  }

  public Map<String, Long> getFaces() {
    return faces;
  }
  public int getIncrementFaceLabel(){
    int label=0;
    for ( Long faceLabel: getFaces().values()){
      if(faceLabel.intValue() > label)
        label=faceLabel.intValue();
    }
    label++;
    return label;
  }
  public void clearFaces(){
    faces.clear();
  }

  public void addFace(String name,int label){
    faces.put(name,label);
  }

  public boolean hasFace(String name){
    return faces.containsKey(name);
  }
  public boolean hasFaceLabel(int label){
    return getFaces().values().contains(new Long(label));
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
    editor.putString("faces", (faces).toJSONString());
    editor.apply();
  }
  public void reset(){
    setPatternCode(null);
    setPinCode(null);
    setIsScreenLockEnabled(false);
    faces.clear();
    save();
  }

}

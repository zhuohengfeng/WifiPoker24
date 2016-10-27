package com.ryan_xm.wifipoke24.activity;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ryan_xm.wifipoke24.R;
import com.ryan_xm.wifipoke24.util.AccSensorEventListener;
import com.ryan_xm.wifipoke24.util.Constant;
import com.ryan_xm.wifipoke24.util.TwentyFour;
import com.ryan_xm.wifipoke24.util.Utils;

public class SingleGameActivity extends Activity implements View.OnClickListener{
	
	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	private SensorManager sensorManager;  
    private Vibrator vibrator;  
    
    private AccSensorEventListener mAccSensorEventListener;
    
    
	private Button mHelpBtn;
	private Button mVerifyBtn;
	private Button mCleanBtn;
	private Button mNoSolutonBtn;
	private EditText mVerifyText;
	private ImageView mPork01;
	private ImageView mPork02;
	private ImageView mPork03;
	private ImageView mPork04;
	
	private ImageView mOptAdd;
	private ImageView mOptMinus;
	private ImageView mOptMul;
	private ImageView mOptDiv;
	private ImageView mOptLeft;
	private ImageView mOptRight;
	
	private int[] bcard = new int[]{0,0,0,0};
    private int[] card = new int[4];
    
    private Bitmap[] mBitmapArray;
    
    private String mExpression="";
    
    /** 
     * 动作执行 
     */  
    Handler handler = new Handler() {  
  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch (msg.what) {  
            case Constant.SENSOR_SHAKE:  
        		//第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复  
        		vibrator.vibrate( new long[]{400,150,400,150}, -1);
        		changeThePork();
                break;
            case Constant.SENSOR_SHAKE_TIME:
            	Toast.makeText(SingleGameActivity.this, R.string.take_easy, Toast.LENGTH_SHORT).show();
            	break;
        	default:
        		break;
            }  
        }  
    };
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.single_gameview);
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
		
        mAccSensorEventListener = new AccSensorEventListener(handler);
        
        mBitmapArray = new Bitmap[4];
        
		initView();
	}
	
	@Override  
    protected void onResume() {  
        super.onResume();  
        if (sensorManager != null) {// 注册监听器  
            sensorManager.registerListener(mAccSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);  
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率  
        }  
    }  
	  
	@Override  
	protected void onPause() {  
	    super.onPause();  
	    if (sensorManager != null) {// 取消监听器  
	        sensorManager.unregisterListener(mAccSensorEventListener);  
	    }  
	}  
	
    
	private void initView(){
		mPork01 = (ImageView)findViewById(R.id.pork01);
		mPork01.setOnClickListener(this);
		mPork02 = (ImageView)findViewById(R.id.pork02);
		mPork02.setOnClickListener(this);
		mPork03 = (ImageView)findViewById(R.id.pork03);
		mPork03.setOnClickListener(this);
		mPork04 = (ImageView)findViewById(R.id.pork04);
		mPork04.setOnClickListener(this);
		
		mOptAdd = (ImageView)findViewById(R.id.opt_add);
		mOptAdd.setOnClickListener(this);
		mOptMinus = (ImageView)findViewById(R.id.opt_minus);
		mOptMinus.setOnClickListener(this);
		mOptMul = (ImageView)findViewById(R.id.opt_mul);
		mOptMul.setOnClickListener(this);
		mOptDiv = (ImageView)findViewById(R.id.opt_div);
		mOptDiv.setOnClickListener(this);
		mOptLeft = (ImageView)findViewById(R.id.opt_left);
		mOptLeft.setOnClickListener(this);
		mOptRight = (ImageView)findViewById(R.id.opt_right);
		mOptRight.setOnClickListener(this);
		
		mVerifyText = (EditText)findViewById(R.id.VerifyText);
		mVerifyBtn = (Button)findViewById(R.id.VerifyBtn);
		mVerifyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				evaluateExpression();
			}
		});
		
		mHelpBtn = (Button)findViewById(R.id.helpbtn);
		mHelpBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				helpExpression();
			}
		});
		
		mCleanBtn = (Button)findViewById(R.id.CleanBtn);
		mCleanBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mExpression = "";
				mVerifyText.setText("");
			}
		});
		
		mNoSolutonBtn = (Button)findViewById(R.id.NoSolutionBtn);
		mNoSolutonBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				noSolutonCheck();
			}
		});
	}
	
	/** 随机产生4张扑克 */
	private void changeThePork(){
        for(int i = 0;i < 4;i++){
        	// 随便产生4张扑克牌
        	card[i] = (int)(1 + Math.random() * 52);
        	bcard[i] = card[i] % 13;
            if(card[i] % 13 == 0)
                bcard[i] = 13;
            
            if(mBitmapArray[i] != null){
            	mBitmapArray[i].recycle();
            }
            
            mBitmapArray[i] = Utils.readBitMap(SingleGameActivity.this, Constant.PORK_ID_ARRAY[card[i]]);
        }
        
		mPork01.setImageBitmap(mBitmapArray[0]);
		mPork02.setImageBitmap(mBitmapArray[1]);
		mPork03.setImageBitmap(mBitmapArray[2]);
		mPork04.setImageBitmap(mBitmapArray[3]);
		
		mExpression = "";
	}
	
	
	/** 直接判断是无解 */
	private void noSolutonCheck(){
		if(bcard[0]==0 && bcard[1]==0 && bcard[2]==0 && bcard[3]==0){
			Toast.makeText(SingleGameActivity.this, R.string.please_turn_on, Toast.LENGTH_SHORT).show();
			return;
		}
		
		int count = TwentyFour.dispose(bcard[0],bcard[1],bcard[2],bcard[3]);
		if(count <=0 ){
			Toast.makeText(SingleGameActivity.this, R.string.evaluate_pass, Toast.LENGTH_SHORT).show();
			mVerifyText.setText("");
        	mExpression = "";
        	changeThePork();
		}
		else{
			Toast.makeText(SingleGameActivity.this, getResources().getString(R.string.solution_count)+":"+count+getResources().getString(R.string.solution_count_after), Toast.LENGTH_SHORT).show();
		}
	}
	
	/** 验证表达式是否正确 */
	private void evaluateExpression(){
		if(bcard[0]==0 && bcard[1]==0 && bcard[2]==0 && bcard[3]==0){
			Toast.makeText(SingleGameActivity.this, R.string.please_turn_on, Toast.LENGTH_SHORT).show();
			return;
		}
		
		String expStr = mVerifyText.getText().toString().trim();
		if(!TextUtils.isEmpty(expStr)){
			// 先检查表达式中的数字是我们列出的数字
			Pattern pattern = Pattern.compile("\\d+");
	    	Matcher matcher = pattern.matcher(expStr);
	    	int sum = 0;
	    	if(matcher != null){
	    		while(matcher.find()){
		    		sum += Integer.valueOf(matcher.group());
		    	}
	    	}
			if(sum != (bcard[0]+bcard[1]+bcard[2]+bcard[3])){
				Toast.makeText(SingleGameActivity.this, R.string.expression_error, Toast.LENGTH_SHORT).show();
				return;
			}
			
			
			// 再检查表达式
			try{
				int result = TwentyFour.evaluateExpression(expStr);
	            if(result == 24){
	            	Toast.makeText(SingleGameActivity.this, R.string.evaluate_pass, Toast.LENGTH_SHORT).show();
	            	mVerifyText.setText("");
	            	mExpression = "";
	            	changeThePork();
	            }
	            else{
	            	Toast.makeText(SingleGameActivity.this, R.string.evaluate_failed, Toast.LENGTH_SHORT).show();
	            }
			}
			catch(Exception e){
				Toast.makeText(SingleGameActivity.this, R.string.evaluate_failed, Toast.LENGTH_SHORT).show();
			}
		}
		else{
			Toast.makeText(SingleGameActivity.this, R.string.expression_not_null, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/** 提示当前有几种解法 */
	private void helpExpression(){
		if(bcard[0]==0 && bcard[1]==0 && bcard[2]==0 && bcard[3]==0){
			Toast.makeText(SingleGameActivity.this, R.string.please_turn_on, Toast.LENGTH_SHORT).show();
			return;
		}
		
		String solution = TwentyFour.disposeStr(bcard[0],bcard[1],bcard[2],bcard[3]);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		builder.setMessage(solution);  
		AlertDialog alert = builder.create();  
		alert.show();
	}
	
	
	@Override
	public void onClick(View v) {
		if(bcard[0]==0 && bcard[1]==0 && bcard[2]==0 && bcard[3]==0){
			Toast.makeText(SingleGameActivity.this, R.string.please_turn_on, Toast.LENGTH_SHORT).show();
			return;
		}
		
		switch(v.getId()){
			case R.id.pork01:
				mExpression += bcard[0]+"";
				break;
			case R.id.pork02:
				mExpression += bcard[1]+"";
				break;
			case R.id.pork03:
				mExpression += bcard[2]+"";
				break;
			case R.id.pork04:
				mExpression += bcard[3]+"";
				break;
			case R.id.opt_add:
				mExpression += "+";
				break;
			case R.id.opt_minus:
				mExpression += "-";
				break;
			case R.id.opt_mul:
				mExpression += "*";
				break;
			case R.id.opt_div:
				mExpression += "/";
				break;
			case R.id.opt_left:
				mExpression += "(";
				break;
			case R.id.opt_right:
				mExpression += ")";
				break;
			default:
				break;
		}
		
		mVerifyText.setText(mExpression);
	}
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}


	
	
	
	
	
	
	
	
	
}

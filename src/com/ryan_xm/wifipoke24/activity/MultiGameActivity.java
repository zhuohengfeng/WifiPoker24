package com.ryan_xm.wifipoke24.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ryan_xm.wifipoke24.R;
import com.ryan_xm.wifipoke24.server.DomainServer;
import com.ryan_xm.wifipoke24.server.GameServer;
import com.ryan_xm.wifipoke24.server.Porks;
import com.ryan_xm.wifipoke24.util.AccSensorEventListener;
import com.ryan_xm.wifipoke24.util.Constant;
import com.ryan_xm.wifipoke24.util.TwentyFour;
import com.ryan_xm.wifipoke24.util.Utils;

public class MultiGameActivity extends Activity implements View.OnClickListener{
	
	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	private DomainServer dc;
    
	private Porks mPorks;
	
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
	
	private TextView mScoreText;
	
	private int[] bcard = new int[]{0,0,0,0};
    
    private Bitmap[] mBitmapArray;
    
    private String mExpression="";
    
    private int Score = 0;
    
    
    /** 
     * 动作执行 
     */  
    Handler handler = new Handler() {  
  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            
			Bundle b = msg.getData();
			String type = b.getString("type");

			if (type.equals("SHUTDOWN")) {
				Toast.makeText(getBaseContext(), R.string.game_err_dis_connect, Toast.LENGTH_SHORT).show();
				finish();
			}
			else if(type.equals("PORKS")){
				getThePorksForGameServer();
				//Toast.makeText(getBaseContext(),text, Toast.LENGTH_SHORT).show();
			}
			else if(type.equals("PLAYER")){
				// 如果有用户回答正确了，先禁用按钮， 在等待重新发牌“PORKS”
				mVerifyBtn.setEnabled(false);
				mNoSolutonBtn.setEnabled(false);
				
				String name = b.getString("player");
				if(!name.equals(dc.getPlayerName())){
					Toast.makeText(getBaseContext(),name+", 回答正确！！！！", Toast.LENGTH_SHORT).show();
				}
				getThePorksForGameServer();
				
				mVerifyBtn.setEnabled(true);
				mNoSolutonBtn.setEnabled(true);
			}
        }  
    };
    
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.multi_gameview);
		
		dc = DomainServer.getInstance();
		dc.setHandlerUI(handler);
		
        mBitmapArray = new Bitmap[4];
        
		initView();
	}
	
	
	@Override  
    protected void onResume() {  
        super.onResume();  
        // 一开始就翻盘
        getThePorksForGameServer();
    }  
	  
	@Override  
	protected void onPause() {  
	    super.onPause();  
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
		
		
		mScoreText = (TextView)findViewById(R.id.changeTxt);
		Score= 0;
		mScoreText.setText(Score+"");
	}
	
	
	/** 每个客户端需要检查自己的操作是否正确，如果正确就把结果发送给服务端，并在广播给各个客户端 */
	private void changeThePork() {
		// TODO Auto-generated method stub
		Score ++;
		mScoreText.setText(Score+"");
		dc.sendMessage(DomainServer.getInstance().getPlayerName());
	}
	
	
	/** 随机产生4张扑克 */
	private void getThePorksForGameServer(){
		
		// 客户端获取从之前服务端发送过来的Porks
		mPorks = GameServer.getInstance().getPorks();
		
        for(int i = 0;i < 4;i++){
        	// 随便产生4张扑克牌
        	bcard[i] = mPorks.getPorkCardNum()[i];
            
            if(mBitmapArray[i] != null){
            	mBitmapArray[i].recycle();
            }
            
            mBitmapArray[i] = Utils.readBitMap(MultiGameActivity.this, mPorks.getPorkCardResId()[i]);
        }
        
		mPork01.setImageBitmap(mBitmapArray[0]);
		mPork02.setImageBitmap(mBitmapArray[1]);
		mPork03.setImageBitmap(mBitmapArray[2]);
		mPork04.setImageBitmap(mBitmapArray[3]);
		
		mExpression = "";
		
		mVerifyBtn.setEnabled(true);
		mNoSolutonBtn.setEnabled(true);
	}
	
	
	/** 直接判断是无解 */
	private void noSolutonCheck(){
		if(bcard[0]==0 && bcard[1]==0 && bcard[2]==0 && bcard[3]==0){
			Toast.makeText(MultiGameActivity.this, R.string.please_turn_on, Toast.LENGTH_SHORT).show();
			return;
		}
		
		int count = TwentyFour.dispose(bcard[0],bcard[1],bcard[2],bcard[3]);
		if(count <=0 ){
			Toast.makeText(MultiGameActivity.this, R.string.evaluate_pass, Toast.LENGTH_SHORT).show();
			mVerifyText.setText("");
        	mExpression = "";
        	changeThePork();
		}
		else{
			Toast.makeText(MultiGameActivity.this, R.string.have_solution, Toast.LENGTH_SHORT).show();
		}
	}
	



	/** 验证表达式是否正确 */
	private void evaluateExpression(){
		if(bcard[0]==0 && bcard[1]==0 && bcard[2]==0 && bcard[3]==0){
			Toast.makeText(MultiGameActivity.this, R.string.please_turn_on, Toast.LENGTH_SHORT).show();
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
				Toast.makeText(MultiGameActivity.this, R.string.expression_error, Toast.LENGTH_SHORT).show();
				return;
			}
			
			
			// 再检查表达式
			try{
				int result = TwentyFour.evaluateExpression(expStr);
	            if(result == 24){
	            	Toast.makeText(MultiGameActivity.this, R.string.evaluate_pass, Toast.LENGTH_SHORT).show();
	            	mVerifyText.setText("");
	            	mExpression = "";
	            	changeThePork();
	            }
	            else{
	            	Toast.makeText(MultiGameActivity.this, R.string.evaluate_failed, Toast.LENGTH_SHORT).show();
	            }
			}
			catch(Exception e){
				Toast.makeText(MultiGameActivity.this, R.string.evaluate_failed, Toast.LENGTH_SHORT).show();
			}
		}
		else{
			Toast.makeText(MultiGameActivity.this, R.string.expression_not_null, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	@Override
	public void onClick(View v) {
		if(bcard[0]==0 && bcard[1]==0 && bcard[2]==0 && bcard[3]==0){
			Toast.makeText(MultiGameActivity.this, R.string.please_turn_on, Toast.LENGTH_SHORT).show();
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
	
	
	
	/**
	 * Hook to control onKeyDown
	 * 
	 * @param keyCode	The keyCode pressed
	 * @param event		The event referring the keycode
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dc.stopGame();
		}
		return super.onKeyDown(keyCode, event);
	}

	
}

package com.ryan_xm.wifipoke24.util;

import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utils {
	
	private static final String SP_NAME = "sp_game";

	private static final String SP_PLAYER_NAME = "sp_game_player_name";
	
	public static String playerName;
	
	/** 
	 * 以最省内存的方式读取本地资源的图片 
	 * @param context 
	 * @param resId 
	 * @return 
	 */  
	public static Bitmap readBitMap(Context context, int resId){  
	    BitmapFactory.Options opt = new BitmapFactory.Options();  
	    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
	    opt.inPurgeable = true;  
	    opt.inInputShareable = true;  
	       //获取资源图片  
	    InputStream is = context.getResources().openRawResource(resId);  
	    return BitmapFactory.decodeStream(is,null,opt);  
	}  

	
    /** 保存玩家名称 */
    public static void savePlayerName(Context context, String name){
    	SharedPreferences mSp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = mSp.edit();
        spe.putString(SP_PLAYER_NAME, name);
        spe.commit();
        playerName = name;
    }
    
    public static String readPlayerName(Context context){
    	SharedPreferences mSp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    	playerName = mSp.getString(SP_PLAYER_NAME, "Player01");
        return playerName;
    }
	
	
	
	
	
	
	
	
}

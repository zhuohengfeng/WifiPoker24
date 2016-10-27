package com.ryan_xm.wifipoke24.server;

import java.io.Serializable;
import com.ryan_xm.wifipoke24.util.Constant;


/**
 * 用户传输当前的牌
 */
public class Porks implements Serializable  {
	//private static final long serialVersionUID = 1346787542L;
	private static final long serialVersionUID = 1L;

	private int[] porkCardResId = new int[4];
	
	private int[] porkCardNum = new int[4];
	
	
	public Porks(){
		CreatRandomPorks();
	}
	
	public void CreatRandomPorks(){
        for(int i = 0;i < 4;i++){
        	// 随便产生4张扑克牌
        	int card = (int)(1 + Math.random() * 52);
        	porkCardNum[i] = card % 13;
            if(card % 13 == 0)
            	porkCardNum[i] = 13;
            
            porkCardResId[i] = Constant.PORK_ID_ARRAY[card];
        }
	}

	public int[] getPorkCardResId() {
		return porkCardResId;
	}


	public int[] getPorkCardNum() {
		return porkCardNum;
	}

}

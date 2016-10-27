package com.ryan_xm.wifipoke24.util;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.Message;

public class AccSensorEventListener implements SensorEventListener {

	private static final int UPTATE_INTERVAL_TIME = 1200;
	private long lastUpdateTime=System.currentTimeMillis();
	
	private Handler mHandler;
	
	public AccSensorEventListener(Handler mHandler) {
		super();
		this.mHandler = mHandler;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
		if(sensorType != Sensor.TYPE_ACCELEROMETER){
			return;
		}
		
    	float x = event.values[0];
    	float y = event.values[1];
    	float z = event.values[2];
    	int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了  
        if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
        	long currentUpdateTime = System.currentTimeMillis();
        	long timeInterval = currentUpdateTime - lastUpdateTime;
        	if (timeInterval < UPTATE_INTERVAL_TIME){
        		Message msg = new Message();  
                msg.what = Constant.SENSOR_SHAKE_TIME;  
                mHandler.sendMessage(msg);  
        		return;
        	}
        	lastUpdateTime = currentUpdateTime;
    		
            Message msg = new Message();  
            msg.what = Constant.SENSOR_SHAKE;  
            mHandler.sendMessage(msg);  
    	}
	}

}

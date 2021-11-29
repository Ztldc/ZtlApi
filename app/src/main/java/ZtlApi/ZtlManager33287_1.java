package ZtlApi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.storage.StorageManager;
import android.util.Log;
import android.app.AlarmManager;


import android.app.PendingIntent;

import androidx.annotation.RequiresPermission;

public class ZtlManager33287_1 extends ZtlManager {

    /*
     *	注意：调用休眠和休眠唤醒接口，需要声明使用权限，并且需要系统签名
     *	android:sharedUserId="android.uid.system
     *	<permission android:name="android.permission.DEVICE_POWER"></permission>
     */
    //休眠
    @Override
    public void goToSleep() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent mIntent = new Intent("com.ztl.action.boardstate");
        mIntent.putExtra("state", 0);
        mContext.sendBroadcast(mIntent);
/*
        PowerManager powerManager= (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
        try {
            powerManager.getClass().getMethod("goToSleep", new Class[]{long.class}).invoke(powerManager, SystemClock.uptimeMillis());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
*/
    }

    //休眠唤醒
    @Override
    public void wakeUp() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent mIntent = new Intent("com.ztl.action.boardstate");
        mIntent.putExtra("state", 1);
        mContext.sendBroadcast(mIntent);
/* 
       PowerManager powerManager= (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
        try {
            powerManager.getClass().getMethod("wakeUp", new Class[]{long.class}).invoke(powerManager, SystemClock.uptimeMillis());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
*/
    }

    @Override
    @RequiresPermission(Manifest.permission.SET_TIME_ZONE)
    public void setAutoTimezone(boolean bAuto) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        int value = bAuto ? 1 : 0;
        Intent mIntent = new Intent("com.ztl.action.autotimezone");
        mIntent.putExtra("checked", value);
        mContext.sendBroadcast(mIntent);
    }

    //设置系统的时区是否自动获取
    @Override
    @RequiresPermission(Manifest.permission.SET_TIME_ZONE)
    public int setAutoTimeZone(int checked) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return -1;
        }

        Intent mIntent = new Intent("com.ztl.action.autotimezone");
        mIntent.putExtra("checked", checked);
        mContext.sendBroadcast(mIntent);
        return 0;
/*		try{
		  android.provider.Settings.Global.putInt(mContext.getContentResolver(),
		          android.provider.Settings.Global.AUTO_TIME_ZONE, checked);
		   }catch(Exception e){
		   		e.printStackTrace();
		      return -1;
		   }
		   return 0;
*/
    }

    //时间-设置系统的时间是否需要自动获取
    @Override
    @RequiresPermission(Manifest.permission.SET_TIME_ZONE)
    public void setAutoDateTime(boolean bAuto) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        int value = bAuto ? 1 : 0;
        Intent mIntent = new Intent("com.ztl.action.autodatetime");
        mIntent.putExtra("checked", value);
        mContext.sendBroadcast(mIntent);
    }

    //设置系统的时间是否需要自动获取
    @Override
    @RequiresPermission(Manifest.permission.SET_TIME_ZONE)
    public int setAutoDateTime(int checked) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return -1;
        }

        Intent mIntent = new Intent("com.ztl.action.autodatetime");
        mIntent.putExtra("checked", checked);
        mContext.sendBroadcast(mIntent);
/*
		 try {
		  android.provider.Settings.Global.putInt(mContext.getContentResolver(),
		          android.provider.Settings.Global.AUTO_TIME, checked);
		  } catch (Exception e) {
		   	e.printStackTrace();
		      return -1;
		  }
*/
        return 0;
    }

    //增大音量，音量+1	1
    @Override
    public int setRaiseSystemVolume() {
/*		try {
		 AudioManager am=(AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
		// am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
		am.adjustVolume(AudioManager.ADJUST_RAISE, 0);
		 } catch (Exception e) {
		   	e.printStackTrace();
		      return -1;
		 }
*/
        int curVolume = getSystemCurrenVolume();
        return setSystemVolumeIndex(curVolume + 1);
    }

    //减小音量，音量-1	1
    @Override
    public int setLowerSystemVolume() {
/*		try {
			AudioManager am=(AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
		//	am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
			am.adjustVolume(AudioManager.ADJUST_LOWER, 0); 
		} catch (Exception e) {
		   	e.printStackTrace();
		      return -1;
		 }
*/
        int curVolume = getSystemCurrenVolume();
        return setSystemVolumeIndex(curVolume - 1);
    }


    //设置系统亮度值(需支持pwm设置)	1
    @Override
    public int setSystemBrightness(int brightness) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return -1;
        }

        Intent mIntent = new Intent("com.ztl.action.setbrightness");
        mIntent.putExtra("brightness", brightness);
        mContext.sendBroadcast(mIntent);
/*
    	try {
			if(brightness >=0 && brightness <= 255){
				try{  
				   Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
				   Uri uri = Settings.System.getUriFor("screen_brightness");
				   mContext.getContentResolver().notifyChange(uri, null);
				}catch (Exception localException){  
				   localException.printStackTrace();  
				}  
			}else{
				LOGD("brightness index 0~255 , please check it");
				return -1;
			}
		} catch (Exception e) {
		   	e.printStackTrace();
		      return -1;
		}
*/
        return 0;
    }

    /*
     *	注意：恢复出厂设置需要系统签名，另外需要声明权限
     *	android:sharedUserId="android.uid.system">
     *	<uses-permission android:name="android.permission.MASTER_CLEAR"/>
     */
    //恢复出厂设置	1
    @Override
    public void recoverySystem() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent clearIntent = new Intent("com.ztl.action.recovery");
        mContext.sendBroadcast(clearIntent);
/*
    	Intent clearIntent = new Intent("android.intent.action.MASTER_CLEAR");
		clearIntent.putExtra("isReformate", true);
		mContext.sendBroadcast(clearIntent);
*/
    }


    //获取屏幕方向	1
    @Override
    public int getDisplayOrientation() {

        String state = getSystemProperty("persist.sys.ztlOrientation", "0");

        return Integer.valueOf(state).intValue();
    }

    //系统-USB调试状态
    @Override
    public boolean isUsbDebugOpen() {
        String state = getSystemProperty("persist.usb.mode", "1");
        int instate = Integer.valueOf(state).intValue();
        if (instate == 1) {
            return true;
        }
        return false;
    }

    //获取USB调试状态	1
    @Override
    public int getUsbDebugState() {
        String state = getSystemProperty("persist.usb.mode", "1");    //1 : disconnect to pc  0: connect to pc
        if (state.equals("0") || state.equals("2")) {
            state = "1";
        } else {
            state = "0";
        }
        return Integer.valueOf(state).intValue();
    }

    //使能左右分屏功能
    @Override
    public void setSplitScreenLeftRightEnable(boolean isEnable) {
/*		if(isEnable){
			setSystemProperty("persist.sys.leftRightEnable","true");
		}else{
			setSystemProperty("persist.sys.leftRightEnable","false");
		}
*/
    }

    //使能上下分屏功能
    @Override
    public void setSplitScreenUpDownEnable(boolean isEnable) {
/*		if(isEnable){
			setSystemProperty("persist.sys.upDownEnable","true");
		}else{
			setSystemProperty("persist.sys.upDownEnable","false");
		}
*/
    }

    //显示-获取支持的分辨率列表
    @Override
    public String[] getScreenModes() {
        String displayModes;
        displayModes = getSystemProperty("persist.sys.displaymdoes", "");
        String modes[] = displayModes.split(",");
        return modes;
    }

    //设置分辨率		1
    @Override
    public void setScreenMode(String mode) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent setModeIntent = new Intent("android.ztl.action.SET_SCREEN_MODE");
        setModeIntent.putExtra("mode", mode);
        mContext.sendBroadcast(setModeIntent);

/*		int index = 0;
		int i=0;
		String framebufferMode = getSystemProperty("persist.sys.framebuffer.main","1920x1080");
		String modes[] = {"1920x1080","1600x900","1440x900","1366x768","1280x720","1280x1024","1024x768","800x600"};

		for(i = 0; i < modes.length; i++){
			if(mode.equals(modes[i])){
				framebufferMode = mode;
				break;
			}
		}

		LOGD("steve set framebuffer "+framebufferMode);
		setSystemProperty("persist.sys.framebuffer.main",framebufferMode);
		setSystemProperty("persist.sys.framebuffer.aux",framebufferMode);
	
		execRootCmdSilent("reboot");   
*/
    }

}

package ZtlApi;


import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;

import android.net.Uri;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Log;

import android.os.SystemProperties;

import android.media.AudioManager;

import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.util.Map;


public class ZtlManager3128 extends ZtlManager {

    private String TAG = "Arctan";

    static final String SYSTEM_BAR_STATE = "persist.sys.systemBar";
    static final String SYSTEM_BAR_SHOW = "show";
    static final String SYSTEM_BAR_HIDE = "hide";

    private final static String SYS_NODE_VGA_MODES =
            "/sys/devices/platform/display-subsystem/drm/card0/card0-VGA-1/modes";
    private final static String SYS_NODE_VGA_MODE =
            "/sys/devices/platform/display-subsystem/drm/card0/card0-VGA-1/mode";

    private List<String> readStrListFromFile(String pathname) throws IOException {

        List<String> fileStrings = new ArrayList<>();
        File filename = new File(pathname);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            fileStrings.add(line);
        }
        Log.d(TAG, "readStrListFromFile - " + fileStrings.toString());
        return fileStrings;
    }

    private String readStrFromFile(String filename) throws IOException {
        Log.d(TAG, "readStrFromFile - " + filename);
        File f = new File(filename);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        return line;
    }

    public void LwlTest(int a) {

        Log.d(TAG, "22LLLLL ----> " + a);
        try {
            readStrListFromFile(SYS_NODE_VGA_MODES);
            readStrFromFile(SYS_NODE_VGA_MODE);
            Log.d(TAG, getDisplayMode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ZtlManager3128() {
        DEBUG_ZTL = getSystemProperty("persist.sys.ztl.debug", "false").equals("true");
    }

    /*
     *	设置系统日期和时间，需要系统签名
     */
    //设置系统日期
/*	public void setSystemDate(int year,int month,int day)
	{
		  LOGD("set system Date "+year+"/"+month+"/"+day);
		  Calendar c = Calendar.getInstance();
		  c.set(Calendar.YEAR, year);
		  c.set(Calendar.MONTH, month+1);
		  c.set(Calendar.DAY_OF_MONTH, day);
		  long when = c.getTimeInMillis();
		  if(when / 1000 < Integer.MAX_VALUE){
		      ((AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
		  }
	}*/

   /* //设置系统时间	1
    @Override
    public void setSystemTime(int hour, int minute, int second, int millisecond) {
        LOGD("set system time " + hour + ":" + minute);
        Intent mIntent = new Intent("com.ztl.action.settime");
        mIntent.putExtra("hour", hour);
        mIntent.putExtra("minute", minute);
        mIntent.putExtra("second", second);
        mIntent.putExtra("millisecond", millisecond);
        mContext.sendBroadcast(mIntent);

//		  Calendar c = Calendar.getInstance();
//		  c.set(Calendar.HOUR_OF_DAY, hour);
//		  c.set(Calendar.MINUTE, minute);
//		  c.set(Calendar.SECOND, second);
//		  c.set(Calendar.MILLISECOND, millisecond);
//		  long when = c.getTimeInMillis();
//		  if(when / 1000 < Integer.MAX_VALUE){
//		      ((AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
//		  }

    }*/

    //设置系统日期	1
    @Override
    public void setSystemDate(int year, int month, int day) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        LOGD("set system Date " + year + "/" + month + "/" + day);
        Intent mIntent = new Intent("com.ztl.action.setdate");
        mIntent.putExtra("year", year);
        mIntent.putExtra("month", month);
        mIntent.putExtra("day", day);
        mContext.sendBroadcast(mIntent);

		/*
		  Calendar c = Calendar.getInstance();
		  c.set(Calendar.YEAR, year);
		  c.set(Calendar.MONTH, month-1);
		  c.set(Calendar.DAY_OF_MONTH, day);
		  long when = c.getTimeInMillis();
		  if(when / 1000 < Integer.MAX_VALUE){
		      ((AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
		  }
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
        try {
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    //减小音量，音量-1	1
    @Override
    public int setLowerSystemVolume() {
        try {
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    //设置系统亮度值(需支持pwm设置)	1
    @Override
    @RequiresPermission(Manifest.permission.WRITE_SETTINGS)
    public int setSystemBrightness(int brightness) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return -1;
        }

        Log.d("Arctan", "ztl enter set ");
        try {
            if (brightness >= 0 && brightness <= 255) {
                try {
                    //   Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
                    //   Uri uri = Settings.System.getUriFor("screen_brightness");
                    //   mContext.getContentResolver().notifyChange(uri, null);
                    Log.d("Arctan", "before send brodcast");
                    Intent mIntent = new Intent("ZTL.ACTION.SET.SYSTEMBRIGHTNESS");
                    mIntent.putExtra("ztl_brightness", brightness);
                    mContext.sendBroadcast(mIntent);

                } catch (Exception localException) {
                    localException.printStackTrace();
                }
            } else {
                LOGD("brightness index 0~255 , please check it");
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    //获取屏幕方向	1
    @Override
    public int getDisplayOrientation() {
        //	String state = getSystemProperty("persist.sys.ztlOrientation","0");
        String state = getSystemProperty("persist.sys.ztlOrientation", "0");
        return Integer.valueOf(state).intValue();
    }

    @Override
    //系统-打开/关闭导航栏状态栏
    public void openSystemBar(boolean bOpen) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        if (bOpen) {
            Intent systemBarOpen = new Intent("ZTL.ACTION.OPEN.SYSTEMBAR");
            mContext.sendBroadcast(systemBarOpen);
        } else {
            Intent systemBarClose = new Intent("ZTL.ACTION.CLOSE.SYSTEMBAR");
            mContext.sendBroadcast(systemBarClose);
        }
    }

    //打开导航兰	1
    @Override
    public void setOpenSystemBar() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent systemBarIntent = new Intent("ZTL.ACTION.OPEN.SYSTEMBAR");
        mContext.sendBroadcast(systemBarIntent);
    }

    //隐藏导航兰	1
    @Override
    public void setCloseSystemBar() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent systemBarIntent = new Intent("ZTL.ACTION.CLOSE.SYSTEMBAR");
        mContext.sendBroadcast(systemBarIntent);
    }

    //系统-获取当前导航栏状态 显示还是隐藏       true：显示 false：隐藏
    @Override
    public boolean isSystemBarOpen() {
        String state = SystemProperties.get(SYSTEM_BAR_STATE);
        if (state.equals(SYSTEM_BAR_SHOW)) {
            return true;
        } else if (state.equals(SYSTEM_BAR_HIDE)) {
            return false;
        }
        return false;
    }

    //获取状态栏状态	1        1：隐藏 0：显示
    @Override
    public int getSystemBarState() {
        int ret = -1;
        //String state = getSystemProperty(SYSTEM_BAR_STATE,"1");
        String state = SystemProperties.get(SYSTEM_BAR_STATE);
        if (state.equals(SYSTEM_BAR_SHOW)) {
            ret = 1;
        } else if (state.equals(SYSTEM_BAR_HIDE)) {
            return 0;
        }
        return ret;
    }

    //获取屏幕分辨率列表
    @Override
    public String[] getScreenModes() {
        String displayModes;
        displayModes = getSystemProperty("persist.sys.displaymdoes", "");
        String modes[] = displayModes.split(",");

        return modes;
    }

    //显示-获取HDMI状态
    @Override
    public String getHDMIState() {
        return execRootCmd("cat /sys/devices/virtual/switch/hdmi/state");
    }

    //显示-设置HDMI开关(true:使能/false:不使能)
    @Override
    public void setHDMIEnable(boolean enable) {
        if (enable) {
            execRootCmdSilent("echo 1 > /sys/devices/virtual/display/HDMI/enable");
        } else {
            execRootCmdSilent("echo 0 > /sys/devices/virtual/display/HDMI/enable");
        }
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
    }

}

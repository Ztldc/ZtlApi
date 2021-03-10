package ZtlApi;


import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;

import android.util.Log;

import android.os.SystemProperties;

import android.media.AudioManager;

import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.util.Map;
import android.view.Display;
import android.view.WindowManager;
import android.util.DisplayMetrics;


public class ZtlManager31284_4 extends ZtlManager {

    private String TAG = "ZTS_3128_44";

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

    ZtlManager31284_4() {
        DEBUG_ZTL = getSystemProperty("persist.sys.ztl.debug", "false").equals("true");
    }

    //获取U盘路径	1
    @Override
    public String getUsbStoragePath(){
        String usbPath = null;

        try{
            File file = new File("/mnt/usb_storage/USB_DISK1/udisk0/");
            // 多个节点的时候
            // /mnt/usb_storage/USB_DISK1/udisk0/
            // /mnt/usb_storage/USB_DISK1/udiskxxxx/
            //usbPath = getSystemProperty("persist.sys.usbDisk","unKnown");
            if(file.exists()){
                usbPath = "/mnt/usb_storage/USB_DISK1/udisk0/";
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return usbPath;
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
 /*   @Override
    public void setSystemDate(int year, int month, int day) {
        LOGD("set system Date " + year + "/" + month + "/" + day);
        Intent mIntent = new Intent("com.ztl.action.setdate");
        mIntent.putExtra("year", year);
        mIntent.putExtra("month", month);
        mIntent.putExtra("day", day);
        mContext.sendBroadcast(mIntent);

    }*/

    //设置系统的时间是否需要自动获取
 /*   @Override
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public int setAutoDateTime(int checked) {
        Intent mIntent = new Intent("com.ztl.action.autodatetime");
        mIntent.putExtra("checked", checked);
        mContext.sendBroadcast(mIntent);

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

*/
    //获取当前GPIO的值
    public int Getcurrentgpio(int port) {
        execRootCmdSilent("cat /sys/class/gpio/gpio" + port + "/value");
        String value = null;
        try {
            value = readStrFromFile("/sys/class/gpio/gpio" + port + "/value");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Integer.parseInt(value);
    }

    @Override
    public void setDisplayOrientation(int rotation) {
        if (mContext == null) {
            Log.e("上下文为空", "不执行");
            return;
        }
        if (rotation == getDisplayOrientation()) {
            Log.e("当前方向", "与旋转方向一致，不执行");
            return;
        }
        try {
            setSystemProperty("persist.ztl.hwrotation",rotation+"");
            execRootCmdSilent("reboot");
        } catch (Exception exc) {
            Log.e(TAG, "set rotation err!");
        }
    }

    //显示-设置触摸方向
    @Override
    public void setTouchOrientation(int orientation, boolean rebootnow) {
        try {
            setSystemProperty(TP_ORIENTATION_PROP, orientation+"");
        } catch (Exception exc) {
            return;
        }
        if (rebootnow) {
            execRootCmdSilent("reboot");
        }
    }

    //显示-获取触摸方向
    @Override
    public int getTouchOrientation() {
        String value = getSystemProperty(TP_ORIENTATION_PROP, "0");
        int ret = Integer.valueOf(value).intValue();
        return ret;
    }

    /*
        //打开导航兰	1
        @Override
        public void setOpenSystemBar() {
            Intent systemBarIntent = new Intent("ZTL.ACTION.OPEN.SYSTEMBAR");
            mContext.sendBroadcast(systemBarIntent);
        }

        //隐藏导航兰	1
        @Override
        public void setCloseSystemBar() {
            Intent systemBarIntent = new Intent("ZTL.ACTION.CLOSE.SYSTEMBAR");
            mContext.sendBroadcast(systemBarIntent);
        }

        //获取状态栏状态	1
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
    */

    @Override
    public String getDisplayMode() {
        if (mContext == null) {
            Log.e("上下文为空", "不执行");
            return null;
        }
        try{
            DisplayMetrics dm = new DisplayMetrics();
            dm = mContext.getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;

            //String Mode = getSystemProperty("persist.sys.screenmode", "0");
            return screenWidth+"x"+screenHeight;
        }catch (Exception exc) {
            Log.e(TAG, "getDisplayMode err!");
        }
        return "1920x1080";
    }

    //获取屏幕分辨率列表
    @Override
    public String[] getScreenModes() {
        // 1920x1080 1280x720

        return new String[]{"1920x1080", "1280x720"};
    }

    //设置分辨率  1 没做
    @Override
    public void setScreenMode(String mode) {
        if (mode.contains("1920x1080")){
            execRootCmdSilent("/system/xbin/resolution 1080 1920 60");// 1920x1080
            reboot(0);
        } else if (mode.contains("1280x720")){
            execRootCmdSilent("/system/xbin/resolution 720  1280 60");// 1280x720
            reboot(0);
        }
    }
}

package ZtlApi;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

////////////
//import android.net.ethernet.IEthernetManager;
//import android.net.ethernet.EthernetManager;
//import android.net.ethernet.EthernetDevInfo;
/////////////

public class ZtlManagerkt11_32 extends ZtlManager {


    //系统-打开/关闭导航栏状态栏
    @Override
    public void openSystemBar(boolean bOpen) {
        if (mContext == null) {
            Log.e(TAG, "上下文为空,不执行");
            return;
        }
        Intent systemBarIntent = new Intent("com.ztl.action.systembar");
        //systemBarIntent.setPackage("com.android.systemui");
        systemBarIntent.putExtra("enable", bOpen);
        mContext.sendBroadcast(systemBarIntent);
    }

    //系统-获取当前导航栏状态 显示还是隐藏
    @Override
    public boolean isSystemBarOpen() {
        String state = getSystemProperty("persist.ztl.systembar", "true");
        return Boolean.valueOf(state);
    }

    //系统-获取设备唯一ID	1
    @Override
    public String getDeviceID() {
        BufferedReader bre = null;
        String lineInfo;
        String cpuSerial;

        //kunt11 平台cat /proc/cpuinfo 并没有serial，技术支持告我们用下面这个节点
        return execRootCmd("cat /sys/block/mmcblk0/device/cid");
//		File cpuInfo = new File("/sys/block/mmcblk0/device/cid");
//		if (!cpuInfo.exists()) {
//			LOGD("/proc/cpuinfo not found!");
//			return null;
//		}
//
//		try {
//			bre = new BufferedReader(new FileReader(cpuInfo));
//			while ((lineInfo = bre.readLine()) != null) {
//				if (!lineInfo.contains("Serial")) {
//					continue;
//				}
//				LOGD(lineInfo.length() + lineInfo);
//
//				cpuSerial = lineInfo.substring(lineInfo.indexOf(":") + 2);
//				LOGD(cpuSerial);
//				return cpuSerial;
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
    }

    //系统-设置OTG口连接状态
    @Override
    public void setUSBtoPC(boolean toPC) {

        //改Uboot 参数。避免开机启动时OTG口状态不对
        ComponentName componetName = new ComponentName(
                "com.yian.yiansettings",  //这个参数是另外一个app的包名
                "com.yian.yiansettings.ZTLService");   //这个是要启动的Service的全路径名

        Intent intent = new Intent();
        intent.setComponent(componetName);
        intent.putExtra("cmd", "set_otg"); //value填的需要和ztlhelper统一
        intent.putExtra("isotg", toPC);  //这里填要传入的参数，第一个name需要和ztlhelper统一
        intent.setPackage("com.yian.yiansettings");
        mContext.startService(intent);

    }

    //系统-获取OTG口连接状态 //1是勾选 0是不勾选
    @Override
    public boolean getUSBtoPC() {
        try {
            String state = getSystemProperty("persist.usb.mode", "");
            if (state.contains("1")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //显示-设置屏幕方向 传入0 90 180 270
    @Override
    public void setDisplayOrientation(int rotation) {
        //转发到系统实现，以便正确修改ubootlogo
//        setSystemProperty("persist.ztl.hwrotation", Integer.toString(rotation));
//        setSystemProperty("persist.ztl.extend.rotation", Integer.toString(rotation));

        if (rotation == getDisplayOrientation()) {
            Log.e("当前方向", "与旋转方向一致，不执行");
            return;
        }
        setPrimaryDisplayOrientation(rotation);
        setExtendDisplayOrientation(rotation);
        reboot(1);
    }

    //显示-设置屏幕方向(主屏)
    public void setPrimaryDisplayOrientation(int rotation) {
        ComponentName componetName = new ComponentName(
                "com.yian.yiansettings",  //这个参数是另外一个app的包名
                "com.yian.yiansettings.ZTLService");   //这个是要启动的Service的全路径名

        if (rotation == getDisplayOrientation()) {
            Log.e("当前方向", "与旋转方向一致，不执行");
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(componetName);
        intent.putExtra("cmd", "set_display_rotation"); //value填的需要和ztlhelper统一
        intent.putExtra("rotation", rotation);
        intent.setPackage("com.yian.yiansettings");
        intent.putExtra("display", "primary");
        mContext.startService(intent);
    }

    //显示-设置屏幕方向(副屏)
    public void setExtendDisplayOrientation(int rotation) {
        ComponentName componetName = new ComponentName(
                "com.yian.yiansettings",  //这个参数是另外一个app的包名
                "com.yian.yiansettings.ZTLService");   //这个是要启动的Service的全路径名

        if (rotation == getDisplayOrientation()) {
            Log.e("当前方向", "与旋转方向一致，不执行");
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(componetName);
        intent.putExtra("cmd", "set_display_rotation"); //value填的需要和ztlhelper统一
        intent.putExtra("rotation", rotation);  //这里填要传入的参数，第一个name需要和ztlhelper统一
        intent.putExtra("display", "extend");  //这里填要传入的参数，第一个name需要和ztlhelper统一
        intent.setPackage("com.yian.yiansettings");
        mContext.startService(intent);
    }

    //获取HDMI分辨率列表
    @Override
    public String[] getHDMIResolutions() {
    	try{
			String[] wmSize = new String[1];
			wmSize[0] = execRootCmd("wm size") + "\r mipi屏不支持获取HDMI分辨率列表";
			return wmSize;
		}catch (Exception e){
    		e.printStackTrace();
		}
    	return null;
    }


    //杰发设置SNapi。放到ztlmanagetkt11_32.java
    @Override
    public int setBuildSerial(String sn) {
        ComponentName componetName = new ComponentName(
                "com.yian.yiansettings",  //这个参数是另外一个app的包名
                "com.yian.yiansettings.ZTLService");   //这个是要启动的Service的全路径名

        Intent intent = new Intent();
        intent.setComponent(componetName);
        intent.setPackage("com.yian.yiansettings");
        intent.putExtra("cmd", "set_sn");
        intent.putExtra("value", sn);
        mContext.startService(intent);

        return 0;
    }



//	private Context mContext;
//
//	private boolean DEBUG_ZTL = true;
//	private String TAG = "ztl";
////	private final String POWER_ON_TIME = "persist.sys.powerontimemillis";
////	private final String IS_OPEN_ALARM = "persist.sys.powerOnEnable";
////	private final String POWER_OFF_ALARM = "persist.sys.poweroffalarm";
//	String POWER_ON_TIME = "persist.sys.powerontime";
//	String IS_OPEN_ALARM = "persist.sys.isopenalarm";
//	String POWER_OFF_ALARM = "persist.sys.poweroffalarm";
//
//	String ALARM_ON = "1";
//	String ALARM_OFF = "0";
//
//	// Arctan add
//    static final String SYSTEM_BAR_STATE = "persist.sys.systemBar";
//    static final String SYSTEM_BAR_SHOW = "show";
//    static final String SYSTEM_BAR_HIDE = "hide";
//    boolean isShowSystemBar = true;
//
//
//
//	private final static String SYS_NODE_VGA_MODES =
//			            "/sys/devices/platform/display-subsystem/drm/card0/card0-VGA-1/modes";
//	private final static String SYS_NODE_VGA_MODE =
//				            "/sys/devices/platform/display-subsystem/drm/card0/card0-VGA-1/mode";
//	private final static String SYS_NODE_VGA_STATUS =
//					            "/sys/devices/platform/display-subsystem/drm/card0/card0-VGA-1/status";
//	private final static String PROP_RESOLUTION_HDMI = "persist.sys.resolution.aux";
//
//	public void ztl_sync_disk(){
//		setSystemProperty("ztl.sync.disk", "sync");
//	}
//
//	public ZtlManagerkt11_32(){
//		DEBUG_ZTL = getSystemProperty("persist.sys.ztl.debug","false").equals("true");
//	}
//
//	void LOGD(String msg){
//		if(DEBUG_ZTL){
//			Log.d(TAG, msg);
//		}
//	}
//
//	//休眠
//	@Override
//	public void goToSleep() {
//		Intent intent = new Intent();
//		intent.setAction("ZTL.ACTION.GOTO.SLEEP");
//		mContext.sendBroadcast(intent);
//	}
//
//    //休眠唤醒
//	@Override
//    public void wakeUp() {
//		Intent intent = new Intent();
//		intent.setAction("ZTL.ACTION.WAKEUP");
//		mContext.sendBroadcast(intent);
//
//    }
//
//    //获取外部SD卡路径	1
//	@Override
//	public String getExternalSDCardPath(){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return null;
//		}
//		String sdPath = null;
//	    StorageManager mstorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
//		try {
//			Class<?> diskIndoClass = Class.forName("android.os.storage.DiskInfo");
//            Method isUsb = diskIndoClass.getMethod("isUsb");
//            Method isSd = diskIndoClass.getMethod("isSd");
//            //VolumeInfo
//            Class<?> volumeClass = Class.forName("android.os.storage.VolumeInfo");
//            Method volumeDisk = volumeClass.getMethod("getDisk");
//            Method fsUuid = volumeClass.getMethod("getFsUuid");
//            Method path = volumeClass.getMethod("getPath");
//
//            Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");
//            List volumeInfoList = (List) getVolumes.invoke(mstorageManager);
//			for(int i= 0 ;i<volumeInfoList.size();i++){
//				if(volumeDisk.invoke(volumeInfoList.get(i)) != null && (boolean)isSd.invoke(volumeDisk.invoke(volumeInfoList.get(i)))){
//                    //sdPath = "/storage/"+fsUuid.invoke(volumeInfoList.get(i));
//					sdPath = ""+path.invoke(volumeInfoList.get(i));
//                	return sdPath;
//				}
//			}
//		}catch(Exception e){
//
//		}
//		return sdPath;
//	}
//
//	//获取U盘路径	1
//	@Override
//	public String getUsbStoragePath(){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return null;
//		}
//		String usbPath = null;
//	    StorageManager mstorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
//		try {
//			Class<?> diskIndoClass = Class.forName("android.os.storage.DiskInfo");
//            Method isUsb = diskIndoClass.getMethod("isUsb");
//            Method isSd = diskIndoClass.getMethod("isSd");
//            //VolumeInfo
//            Class<?> volumeClass = Class.forName("android.os.storage.VolumeInfo");
//            Method volumeDisk = volumeClass.getMethod("getDisk");
//            Method fsUuid = volumeClass.getMethod("getFsUuid");
//            Method path = volumeClass.getMethod("getPath");
//
//            Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");
//            List volumeInfoList = (List) getVolumes.invoke(mstorageManager);
//			for(int i= 0 ;i<volumeInfoList.size();i++){
//				if(volumeDisk.invoke(volumeInfoList.get(i)) != null && (boolean)isUsb.invoke(volumeDisk.invoke(volumeInfoList.get(i)))){
//					usbPath = ""+path.invoke(volumeInfoList.get(i));
//                	return usbPath;
//				}
//			}
//		}catch(Exception e){
//		}
//		return usbPath;
//	}
//
//	private Vector<String> getUsbStoragePath_Vector() {
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return null;
//		}
//        Vector<String> mUsbVector = new  Vector<>();
//        String usbPath = null;
//        StorageManager mstorageManager = (StorageManager)mContext.getSystemService(Context.STORAGE_SERVICE);
//
//        try {
//            Class<?> diskIndoClass = Class.forName("android.os.storage.DiskInfo");
//            Method isUsb = diskIndoClass.getMethod("isUsb");
//            Method isSd = diskIndoClass.getMethod("isSd");
//            Class<?> volumeClass = Class.forName("android.os.storage.VolumeInfo");
//            Method volumeDisk = volumeClass.getMethod("getDisk");
//            Method fsUuid = volumeClass.getMethod("getFsUuid");
//            Method getLabel = volumeClass.getMethod("getDescription");
//            Method path = volumeClass.getMethod("getPath");
//            Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");
//            List volumeInfoList = (List)getVolumes.invoke(mstorageManager);
//
//            for(int i = 0,j = 0; i < volumeInfoList.size(); ++i) {
//                if (volumeDisk.invoke(volumeInfoList.get(i)) != null && (Boolean)isUsb.invoke(volumeDisk.invoke(volumeInfoList.get(i)))) {
//                    usbPath = "/storage/" + fsUuid.invoke(volumeInfoList.get(i));
//                    String userLabel = (String) getLabel.invoke(volumeInfoList.get(i));
//					mUsbVector.add(userLabel+";"+usbPath);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return mUsbVector;
//    }
//
//	//设置系统时间	1
//	@Override
//	public void setSystemTime(int hour,int minute,int second,int millisecond){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return;
//		}
//		  System.out.println("Arctan set system time "+hour+":"+minute);
//	//	  Calendar c = Calendar.getInstance();
//	//	  c.set(Calendar.HOUR_OF_DAY, hour);
//	//	  c.set(Calendar.MINUTE, minute);
//	//	  c.set(Calendar.SECOND, second);
//	//	  c.set(Calendar.MILLISECOND, millisecond);
//	//	  long when = c.getTimeInMillis();
//	//	  if(when / 1000 < Integer.MAX_VALUE){
//	//	      ((AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
//	//	  }
//		  Intent timeIntent = new Intent();
//		  timeIntent.setAction("ZTL.ACTION.SET.TIME");
//		  timeIntent.putExtra("hour",hour);
//		  timeIntent.putExtra("minute",minute);
//		  timeIntent.putExtra("second",second);
//		  timeIntent.putExtra("millisecond",millisecond);
//		  mContext.sendBroadcast(timeIntent);
//	}
//
//	//设置系统日期	1
//	@Override
//	public void setSystemDate(int year,int month,int day){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return;
//		}
//		  System.out.println("Arctan set system Date "+year+"/"+month+"/"+day);
//		  Intent dateIntent = new Intent();
//		  dateIntent.setAction("ZTL.ACTION.SET.DATE");
//		  dateIntent.putExtra("year",year);
//		  dateIntent.putExtra("month",month);
//		  dateIntent.putExtra("day",day);
//		  mContext.sendBroadcast(dateIntent);
//
//		  //Calendar c = Calendar.getInstance();
//		  //c.set(Calendar.YEAR, year);
//		  //c.set(Calendar.MONTH, month-1);
//		  //c.set(Calendar.DAY_OF_MONTH, day);
//		  //long when = c.getTimeInMillis();
//		  //if(when / 1000 < Integer.MAX_VALUE){
//		  //    ((AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
//		  //}
//	}
//
//	//设置系统的时区是否自动获取
//	@Override
//	public int setAutoTimeZone(int checked){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return -1;
//		}
//		Intent intent = new Intent();
//		intent.setAction("ZTL.ACTION.AUTO.TIMEZONE");
//		intent.putExtra("ischeck",checked);
//		mContext.sendBroadcast(intent);
//		return 1;
//	}
//
//	//设置系统的时间是否需要自动获取
//	@Override
//	public int setAutoDateTime(int checked){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return -1;
//		}
//		Intent intent = new Intent();
//		intent.setAction("ZTL.ACTION.AUTO.DATETIME");
//		intent.putExtra("ischeck",checked);
//		mContext.sendBroadcast(intent);
//		return 1;
//	}
//
//	//增大音量，音量+1	1
//	@Override
//	public int setRaiseSystemVolume(){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return -1;
//		}
//		try {
//		 AudioManager am=(AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
//		 am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
//		 } catch (Exception e) {
//		   	e.printStackTrace();
//		      return -1;
//		 }
//		  return 0;
//	}
//
//	//减小音量，音量-1	1
//	@Override
//	public int setLowerSystemVolume(){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return -1;
//		}
//		try {
//			AudioManager am=(AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
//			am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
//		} catch (Exception e) {
//		   	e.printStackTrace();
//		      return -1;
//		 }
//		  return 0;
//	}
//
//    //调大系统亮度 +1	1
//	@Override
//	@RequiresPermission(Manifest.permission.WRITE_SETTINGS)
//    public int setRaiseSystemBrightness(){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return -1;
//		}
//    	int curBrightnss = getSystemBrightness();
//		Intent intent = new Intent();
//		intent.setAction("ZTL.ACTION.SET.BRIGHTNESS");
//		intent.putExtra("brightness",curBrightnss+1);
//		mContext.sendBroadcast(intent);
//    	return 1;
//    }
//
//    //调低系统亮度 -1	1
//	@Override
//	@RequiresPermission(Manifest.permission.WRITE_SETTINGS)
//     public int setLowerSystemBrightness(){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return -1;
//		}
//    	int curBrightnss = getSystemBrightness();
//    	Intent intent = new Intent();
//		intent.setAction("ZTL.ACTION.SET.BRIGHTNESS");
//		intent.putExtra("brightness",curBrightnss-1);
//		mContext.sendBroadcast(intent);
//    	return 1;
//    }
//
//    //设置系统亮度值(需支持pwm设置)	1
//	@Override
//	@RequiresPermission(Manifest.permission.WRITE_SETTINGS)
//    public int setSystemBrightness(int brightness){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return -1;
//		}
//		if(brightness >=0 && brightness <= 255){
//			Intent intent = new Intent();
//			intent.setAction("ZTL.ACTION.SET.BRIGHTNESS");
//			intent.putExtra("brightness",brightness);
//			mContext.sendBroadcast(intent);
//		}else {
//			return -1;
//		}
//		return 0;
//    }
//
///*
//*	注意：恢复出厂设置需要系统签名，另外需要声明权限
//*	android:sharedUserId="android.uid.system">
//*	<uses-permission android:name="android.permission.MASTER_CLEAR"/>
//*/
//    //恢复出厂设置	1
//	@Override
//    public void recoverySystem(){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return;
//		}
//		Intent clearIntent = new Intent("ZTL.ACTION.RECOVERY.SYSTEM");
//		mContext.sendBroadcast(clearIntent);
//    	//Intent clearIntent = new Intent("android.intent.action.MASTER_CLEAR");
//		//clearIntent.putExtra("isReformate", true);
//		//mContext.sendBroadcast(clearIntent);
//    }
//
//    //关机	1
//	@Override
//    public void shutDownSystem(){
//		if (mContext == null) {
//			Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
//			return;
//		}
//		Intent mIntent = new Intent("ZTL.ACTION.SHTUDOWN.SYSTEM");
//		mContext.sendBroadcast(mIntent);
//    }
//
//    //重启	1
//	@Override
//    public void rebootSystem(){
//		Intent mIntent = new Intent("ZTL.ACTION.REBOOT.SYSTEM");
//		mContext.sendBroadcast(mIntent);
//    }
//
//
///*
//	//定时开机，指定某一天
//	public void serPowerOnAlarm(int year, int month , int day ,int hour,int minute,boolean enableSchedulPowerOn){
//    	 long now = System.currentTimeMillis();
//         long now_totalSeconds = now / 1000;
//
//         long set_totalSeconds = 0;
//         long set_time_l = 0;
//    	AlarmManager alarmManager;
//    	alarmManager =((AlarmManager)mContext.getSystemService("alarm"));
//
//        String targetTime =  year+"-"+month+"-"+day+" "+hour+":"+minute+":"+"00";
//
//    	String Powerontime = getSystemProperty(POWER_ON_TIME,"unknown");
//
//        set_time_l = getStringToDate(targetTime, "yyyy-MM-dd HH:mm:ss");
//
//        set_totalSeconds = set_time_l / 1000;
//
//		if(set_time_l < now){
//			Log.d(TAG,"steve power on time "+(set_time_l-now)+" invalid, set closer power on alarm");
//	        setSystemProperty(POWER_ON_TIME,"unknown");
//    		setSystemProperty("persist.sys.powerOnEnable","false");
//			setSystemProperty("persist.sys.powerOnEveryday","false");
//			return;
//		}
//
//        if(enableSchedulPowerOn){
//			alarmManager.set(AlarmManager.RTC_WAKEUP, set_time_l, getPendingIntent("android.timerswitch.run_power_on", 44372));
//			alarmManager.set(4, set_time_l, getPendingIntent("android.timerswitch.run_power_on", 44372));
//	        setSystemProperty(POWER_ON_TIME,set_time_l+"");
//	        setSystemProperty(IS_OPEN_ALARM,ALARM_ON);
//	        setSystemProperty("persist.sys.powerOnEveryday","false");
//			Log.d(TAG,"steve set next time power on "+year+"/"+month+"/"+day+" "+hour+":"+minute);
//        	Log.i(TAG,"steve get now "+now_totalSeconds+" ,set time "+set_totalSeconds+" sult "+(set_totalSeconds - now_totalSeconds));
//        	Log.i(TAG, Powerontime);
//        }else{
//			Log.i(TAG,"steve set close power on alarm");
//			setSystemProperty(IS_OPEN_ALARM,ALARM_OFF);
//	        setSystemProperty(POWER_ON_TIME,"unknown");
//    		setSystemProperty("persist.sys.powerOnEnable","false");
//			setSystemProperty("persist.sys.powerOnEveryday","false");
//		}
//
//
//    }
//
//	public static long getStringToDate(String dateString, String pattern) {
//      SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
//      Date date = new Date();
//      try{
//          date = dateFormat.parse(dateString);
//          Log.d("steve", " "+date.getYear()+" "+date.getMonth()+" "+date.getDay()+" "+date.getHours()+" "+date.getMinutes()+" "+date.getSeconds());
//      } catch(ParseException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//      }
//
//      return date.getTime();
//  	}
//
//
//    //设置定时开机时间
//    public void setSchedulePowerOn(int hour,int minute,boolean enableSchedulPowerOn){
//    	int year;
//    	int month;
//    	int day;
//    	long curTime;
//    	long targetTime;
//    	AlarmManager alarmManager;
//    	alarmManager =((AlarmManager)mContext.getSystemService("alarm"));
//    	Calendar c=Calendar.getInstance();
//
//    	curTime = c.getTimeInMillis();
//
//    	if(enableSchedulPowerOn){
//			year = c.get(Calendar.YEAR);
//			month = c.get(Calendar.MONTH);
//			day = c.get(Calendar.DAY_OF_MONTH);
//			c.set(year,month,day,hour,minute,0);
//			targetTime = c.getTimeInMillis();
//			if(targetTime < curTime){
//				day = day+1;
//				c.set(year,month,day,hour,minute,0);
//			}
//			Log.d(TAG,"set next time power on "+year+"/"+(month+1)+"/"+day+" "+hour+":"+minute);
//			alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), getPendingIntent("android.timerswitch.run_power_on", 44372));
//			alarmManager.set(4, c.getTimeInMillis(), getPendingIntent("android.timerswitch.run_power_on", 44372));
//			setSystemProperty("persist.sys.powerOnTime",hour+":"+minute);
//			setSystemProperty("persist.sys.powerOnEnable","true");
//			setSystemProperty("persist.sys.powerOnEveryday","true");
//			Log.i(TAG,"Next time power on "+hour+":"+minute);
//    	}else{
//			Log.i(TAG,"steve set close power on alarm");
//    		alarmManager.set(4, c.getTimeInMillis(), getPendingIntent("android.timerswitch.run_power_on", 44372));
//    		setSystemProperty("persist.sys.powerOnTime","unknown");
//    		setSystemProperty("persist.sys.powerOnEnable","false");
//			setSystemProperty("persist.sys.powerOnEveryday","false");
//
//    	}
//
//    }
//
//
//
//	//定时关机，指定某一天
//    public void setPowerOffAlarm(int year, int month , int day ,int hour,int minute,boolean enableSchedulPowerOff){
//
//    	long curTime;
//    	long targetTime;
//    	AlarmManager am = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//    	String s_powerOffTime = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+"00";
//    	Calendar c=Calendar.getInstance();
//    	curTime = c.getTimeInMillis();
//
//    	if(enableSchedulPowerOff){
//
//    		Log.d(TAG,"steve set power off");
//    		month = month - 1;
//			c.set(year,month,day,hour,minute,0);
//			targetTime = c.getTimeInMillis();
//			s_powerOffTime = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+"00";
//			Log.d(TAG,"set false tar "+targetTime+" cur"+curTime);
//			if(targetTime < curTime){
//				Log.d(TAG,"steve power off time "+(targetTime-curTime)+" invalid, set closer power off alarm");
//				setSystemProperty("persist.sys.powerOffEveryday","false");
//				setSystemProperty("persist.sys.powerOffTime","unknown");
//	    		setSystemProperty("persist.sys.powerOffEnable","false");
//				return;
//			}
//
//			Log.d(TAG,"set next time power off "+year+"/"+(month+1)+"/"+day+" "+hour+":"+minute);
//			Intent intent = new Intent("com.android.settings.action.REQUEST_POWER_OFF");
//		    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		    am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//		    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//		    setSystemProperty("persist.sys.powerOffTime",hour+":"+minute);
//		    setSystemProperty("persist.sys.powerOffEnable","true");
//		    setSystemProperty("persist.sys.powerOffEveryday","false");
//		    setSystemProperty("persist.sys.powerOffTimeMillis",targetTime+"");
//
////		    Log.i(TAG,"Next time power off "+hour+":"+minute);
//		}else{
//			Log.i(TAG,"steve set close power off alarm");
//    		setSystemProperty("persist.sys.powerOffTime","unknown");
//    		setSystemProperty("persist.sys.powerOffEnable","false");
//			setSystemProperty("persist.sys.powerOffEveryday","false");
//    	}
//    }
//
//    //设置定时关机时间
//    public void setSchedulePowerOff(int hour,int minute,boolean enableSchedulPowerOff){
//    	int year;
//    	int month;
//    	int day;
//    	long curTime;
//    	long targetTime;
//    	AlarmManager am = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//
//    	Calendar c=Calendar.getInstance();
//    	curTime = c.getTimeInMillis();
//
//    	if(enableSchedulPowerOff){
//			year = c.get(Calendar.YEAR);
//			month = c.get(Calendar.MONTH);
//			day = c.get(Calendar.DAY_OF_MONTH);
//			c.set(year,month,day,hour,minute,0);
//			targetTime = c.getTimeInMillis();
//			if(targetTime < curTime){
//				day = day+1;
//				c.set(year,month,day,hour,minute,0);
//			}
//
//			Log.d(TAG,"set next time power off "+year+"/"+(month+1)+"/"+day+" "+hour+":"+minute);
//			Intent intent = new Intent("com.android.settings.action.REQUEST_POWER_OFF");
//		    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		    am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//		    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//		    setSystemProperty("persist.sys.powerOffTime",hour+":"+minute);
//		    setSystemProperty("persist.sys.powerOffEnable","true");
//			setSystemProperty("persist.sys.powerOffEveryday","true");
//		    Log.i(TAG,"Next time power off "+hour+":"+minute);
//		}else{
//			Log.i(TAG,"steve set close power off alarm");
//    		setSystemProperty("persist.sys.powerOffTime","unknown");
//    		setSystemProperty("persist.sys.powerOffEnable","false");
//    	}
//    }
//*/
//
///*千惠固件版本，修改开关机为休眠唤醒*/
///*  //设置定时开机时间
//    public void setSchedulePowerOn(int hour,int minute,boolean enableSchedulPowerOn){
//    	int year;
//    	int month;
//    	int day;
//    	long curTime;
//    	long targetTime;
//    	AlarmManager am = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
////    	AlarmManager alarmManager;
////    	alarmManager =((AlarmManager)mContext.getSystemService("alarm"));
//    	Calendar c=Calendar.getInstance();
//
//    	curTime = c.getTimeInMillis();
//
//    	if(enableSchedulPowerOn){
//			year = c.get(Calendar.YEAR);
//			month = c.get(Calendar.MONTH);
//			day = c.get(Calendar.DAY_OF_MONTH);
//			c.set(year,month,day,hour,minute,0);
//			targetTime = c.getTimeInMillis();
//			if(targetTime < curTime){
//				day = day+1;
//				c.set(year,month,day,hour,minute,0);
//			}
//			Log.d(TAG,"set next time power on "+year+"/"+(month+1)+"/"+day+" "+hour+":"+minute);
////			alarmManager.set(4, c.getTimeInMillis(), getPendingIntent("android.timerswitch.run_power_on", 44372));
//
//			Intent intent = new Intent("android.timerswitch.run_power_on");
//			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		    am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//		    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//
//			setSystemProperty("persist.sys.powerOnTime",hour+":"+minute);
//			setSystemProperty("persist.sys.powerOnEnable","true");
//			Log.i(TAG,"Next time power on "+hour+":"+minute);
//    	}else{
//    	//	alarmManager.set(4, c.getTimeInMillis(), getPendingIntent("android.timerswitch.run_power_on", 44372));
//    		setSystemProperty("persist.sys.powerOnTime","unknown");
//    		setSystemProperty("persist.sys.powerOnEnable","false");
//
//    	}
//
//    }
//
//    //设置定时关机时间
//    public void setSchedulePowerOff(int hour,int minute,boolean enableSchedulPowerOff){
//    	int year;
//    	int month;
//    	int day;
//    	long curTime;
//    	long targetTime;
//    	AlarmManager am = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//
//    	Calendar c=Calendar.getInstance();
//    	curTime = c.getTimeInMillis();
//
//    	if(enableSchedulPowerOff){
//			year = c.get(Calendar.YEAR);
//			month = c.get(Calendar.MONTH);
//			day = c.get(Calendar.DAY_OF_MONTH);
//			c.set(year,month,day,hour,minute,0);
//			targetTime = c.getTimeInMillis();
//			if(targetTime < curTime){
//				day = day+1;
//				c.set(year,month,day,hour,minute,0);
//			}
//
//			Log.d(TAG,"set next time power off "+year+"/"+(month+1)+"/"+day+" "+hour+":"+minute);
//			Intent intent = new Intent("com.android.settings.action.REQUEST_POWER_OFF");
//		    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		    am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//		    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//		    setSystemProperty("persist.sys.powerOffTime",hour+":"+minute);
//		    setSystemProperty("persist.sys.powerOffEnable","true");
//		    Log.i(TAG,"Next time power off "+hour+":"+minute);
//		}else{
//    		setSystemProperty("persist.sys.powerOffTime","unknown");
//    		setSystemProperty("persist.sys.powerOffEnable","false");
//    	}
//    }
//*/
//
//	//设置屏幕方向	1
//	@Override
//	public void setDisplayOrientation(int rotation){
//	//	System.out.println("data");
//	//	int rotation = intent.getIntExtra("rotation",-1);
//		int oritation = 0;
//		switch(rotation){
//			case 0:
//				oritation = 0;
//				break;
//			case 90:
//				oritation = 1;
//				break;
//			case 180:
//				oritation = 2;
//				break;
//			case 270:
//				oritation = 3;
//				break;
//			default:
//				Log.e(TAG,"rotation(0,90,180,270) err,please check it");
//				return;
//		}
//		try {
//			Intent oritationIntent = new Intent("ACTION_ZTL_ROTATION");
//			oritationIntent.putExtra("rotation", oritation);
//			mContext.sendBroadcast(oritationIntent);
//		} catch (Exception exc) {
//        	Log.e(TAG,"set rotation err!");
//        }
//
//	}
//
//	//打开导航兰	1
//	@Override
//	public void setOpenSystemBar(){
//		Intent systemBarIntent = new Intent("ZTL.ACTION.OPEN.SYSTEMBAR");
//		mContext.sendBroadcast(systemBarIntent);
//	}
//
//	//隐藏导航兰	1
//	@Override
//	public void setCloseSystemBar(){
//		Intent systemBarIntent = new Intent("ZTL.ACTION.CLOSE.SYSTEMBAR");
//		mContext.sendBroadcast(systemBarIntent);
//	}
//
//	//获取状态栏状态	1
//	@Override
//	public int getSystemBarState(){
//		int ret=-1;
//		//String state = getSystemProperty(SYSTEM_BAR_STATE,"1");
//		String state = SystemProperties.get(SYSTEM_BAR_STATE);
//		if(state.equals(SYSTEM_BAR_SHOW)){
//			ret = 1;
//		}else if(state.equals(SYSTEM_BAR_HIDE)){
//			return 0;
//		}
//		return ret;
//	}
//
//	//设置分辨率		1
//	@Override
//	public void setScreenMode(String mode){
//		String cmd = "wm size ";
//		if(mode.equals("reset")){
//			execRootCmdSilent(cmd+mode);
//		}else{
//			String split[] = mode.split("@");
//			this.setSystemProperty("persist.sys.screenmode", mode);
//			execRootCmdSilent(cmd+split[0]);
//		}
//
//	}
}

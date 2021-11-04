package ZtlApi;

import android.annotation.SuppressLint;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ZtlManager3568 extends ZtlManager {

    private String TAG = "ZtlManager3568";

    ZtlManager3568() {
        DEBUG_ZTL = getSystemProperty("persist.sys.ztl.debug", "false").equals("true");
    }

    //系统-存储-获取内部存储大小，单位：字节 	(储存器容量)
    @SuppressLint("WrongConstant")
    @Override
    public long getTotalInternalMemorySize() {
        StorageStatsManager storageStatsManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            storageStatsManager = (StorageStatsManager) mContext.getSystemService(Context.STORAGE_STATS_SERVICE);
            try {
                long totalBytes = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT);//总空间大小
                long availBytes = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT);//可用空间大小
                long systemBytes = totalBytes - availBytes;//系统所占不可用空间大小
                return totalBytes;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
/*
    //系统-截取当前屏幕
    @Override
    public void startScreenShot(String path, String fileName) {
        if (isExist(path)) {
            String filePath = path + "/" + fileName;
            String screenShotCmd = "pngtest 1 " + filePath;
            execRootCmdSilent(screenShotCmd);
        } else {
            Log.e(TAG, "file path " + path + " not exist");
        }
    }
*/
    //显示-设置HDMI分辨率		1
    @Override
    public void setScreenMode(String mode) {
    //    mode = mode.replace("@60p", "");
    //    setSystemProperty("persist.ztl.hdmiresolution", mode);
    //    reboot(0);
    }

    @Override
    public String[] getHDMIResolutions() {
        try {
            String sss = loadFileAsString("/sys/class/drm/card0-HDMI-A-1/modes");
            String[] texts = sss.split("\n");
            return texts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //系统-打开USB调试 使用安卓官方接口，需要申请系统权限
/*    @Override
    public void openUsbDebug(boolean bOpen) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }
        String value = bOpen ? "1" : "0";
        Intent systemBarIntent = new Intent("com.ding.adbsetting");
        systemBarIntent.putExtra("enable", value);
        systemBarIntent.setPackage("com.yian.yiansettings");
        mContext.sendBroadcast(systemBarIntent);
    }
*/
    //系统-设置OTG口连接状态 // usb adb debug
    @Override
    public void setUSBtoPC(boolean toPC) {
		//open adb : setprop persist.usb.mode 1 
		//open usb : setprop persist.usb.mode 2
		if (mContext == null) {
            Log.e(TAG, "上下文为空,不执行");
            return;
        }
		if(toPC){
			setSystemProperty("persist.usb.mode", "1");
			execRootCmdSilent("echo otg > /sys/devices/platform/fe8a0000.usb2-phy/otg_mode");
		}else{
			setSystemProperty("persist.usb.mode", "2");
			execRootCmdSilent("echo host > /sys/devices/platform/fe8a0000.usb2-phy/otg_mode");
		}
       // String value = toPC ? "1" : "2";
        //setSystemProperty("persist.usb.mode", value);
        //writeMethod("/sys/devices/platform/ffe09080.usb3phy/ztl_usb_ctrl/ztl_usb_ctrl", value);
		
    }

    //系统-获取OTG口连接状态 //勾中的时候是2 不勾的时候是1
    @Override
    public boolean getUSBtoPC() {
        try {
            String state = loadFileAsString("/sys/devices/platform/fe8a0000.usb2-phy/otg_mode");
            if (state.equals("otg")) {// 
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;// host
    }

    //显示-设置屏幕方向 传入0 90 180 270
    @Override
    public void setDisplayOrientation(int rotation) {
        setSystemProperty("persist.ztl.hwrotation", Integer.toString(rotation));
        reboot(0);
    }

    //系统-打开/关闭导航栏状态栏
    @Override
    public void openSystemBar(boolean bOpen) {
        if (mContext == null) {
            Log.e(TAG, "上下文为空,不执行");
            return;
        }
        Intent systemBarIntent = new Intent("com.ztl.action.systembar");
        systemBarIntent.setPackage("com.android.systemui");
        systemBarIntent.putExtra("enable", bOpen);
        mContext.sendBroadcast(systemBarIntent);
    }

    //系统-获取当前导航栏状态 显示还是隐藏
    @Override
    public boolean isSystemBarOpen() {
        String state = getSystemProperty("persist.ztl.systembar", "true");
        return Boolean.valueOf(state);
    }
}

package ZtlApi;

import android.content.Context;
import android.content.Intent;


import java.io.File;


import android.os.SystemClock;
import android.os.PowerManager;
import android.os.storage.StorageManager;
import android.util.Log;
import android.os.SystemProperties;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class ZtlManager3368 extends ZtlManager {

    ZtlManager3368() {
        this.DEBUG_ZTL = SystemProperties.get("persist.sys.ztl.debug", "false").equals("true");
    }

    //休眠
    @Override
    public void goToSleep() {
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        try {
            powerManager.getClass().getMethod("goToSleep", new Class[]{Long.TYPE}).
                    invoke(powerManager, new Object[]{Long.valueOf(SystemClock.uptimeMillis())});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //唤醒
    @Override
    public void wakeUp() {
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        try {
            powerManager.getClass().getMethod("wakeUp", new Class[]{Long.TYPE})
                    .invoke(powerManager, new Object[]{Long.valueOf(SystemClock.uptimeMillis())});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
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

//    //获取屏幕方向
//    @Override
//    public int getDisplayOrientation() {
//        String state = getSystemProperty("persist.sys.ztlOrientation", "0");
//        return Integer.valueOf(state).intValue();
//    }

    //获取USB调试状态
    @Override
    public int getUsbDebugState() {
        String state = getSystemProperty("persist.usb.mode", "1");
        if ((state.equals("0")) || (state.equals("2"))) {
            state = "1";
        } else {
            state = "0";
        }
        return Integer.valueOf(state).intValue();
    }

    //获取状态栏信息
    @Override
    public int getSystemBarState() {
        String state = getSystemProperty("persist.sys.barState", "1");
        return Integer.valueOf(state).intValue();
    }

    //左右屏分屏功能
    @Override
    public void setSplitScreenLeftRightEnable(boolean isEnable) {
    }

    //上下分屏功能
    @Override
    public void setSplitScreenUpDownEnable(boolean isEnable) {
    }

    //获取支持的分辨率列表
    @Override
    public String[] getScreenModes() {
        String displayModes = getSystemProperty("persist.sys.displaymdoes", "");
        String[] modes = displayModes.split(",");

        return modes;
    }

    //设置分辨率
    @Override
    public void setScreenMode(String mode) {
        Intent setModeIntent = new Intent("android.ztl.action.SET_SCREEN_MODE");
        setModeIntent.putExtra("mode", mode);
        this.mContext.sendBroadcast(setModeIntent);
    }

}


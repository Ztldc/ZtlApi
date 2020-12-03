package ZtlApi;

import android.content.Context;
import android.content.Intent;


import java.io.File;


import android.os.SystemClock;
import android.os.PowerManager;
import android.util.Log;
import android.os.SystemProperties;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ZtlManager3368 extends ZtlManager {

    ZtlManager3368() {
        this.DEBUG_ZTL = SystemProperties.get("persist.sys.ztl.debug", "false").equals("true");
    }

    //休眠
    @Override
    public void goToSleep() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

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
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

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

    //获取屏幕方向
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
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent setModeIntent = new Intent("android.ztl.action.SET_SCREEN_MODE");
        setModeIntent.putExtra("mode", mode);
        this.mContext.sendBroadcast(setModeIntent);
    }

}


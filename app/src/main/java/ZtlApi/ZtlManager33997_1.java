package ZtlApi;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.storage.StorageManager;
import android.util.Log;
import android.os.SystemProperties;

public class ZtlManager33997_1 extends ZtlManager {

    ZtlManager33997_1() {
        DEBUG_ZTL = SystemProperties.get("persist.sys.ztl.debug", "false").equals("true");
    }

    //获取屏幕方向	1	20210701 查看build.prop没用到这个节点，用的节点是：persist.ztl.hwrotation 和3288-5.1一致
//	@Override
//	public int getDisplayOrientation(){
//		String state = getSystemProperty("persist.sys.ztlOrientation","0");
//		return Integer.valueOf(state).intValue();
//	}

    //系统-USB调试状态
    @Override
    public boolean isUsbDebugOpen() {
        String state = getSystemProperty("persist.usb.mode", "1");
        if (state.contains("1")) {
            return false;
        }
        return true;

		/*String state = getSystemProperty("persist.usb.mode", "1");
		int instate = Integer.valueOf(state).intValue();
		if (instate == 1) {
			return true;
		}
		return false;*/
    }

    //显示-获取HDMI状态	1 = 显示 	0 = 不显示
    @Override
    public String getHDMIState() {
        return execRootCmd("cat /sys/devices/virtual/switch/hdmi/state");
    }

    //显示-设置HDMI开关(true:使能/false:不使能)
    @Override
    public void setHDMIEnable(boolean enable) {
        if (enable) {
            execRootCmdSilent("echo on > /sys/class/drm/card0-HDMI-A-1/status");
        } else {
            execRootCmdSilent("echo off > /sys/class/drm/card0-HDMI-A-1/status");
        }
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

    //获取OTG口连接状态 勾的时候是2 不勾的时候是1
    @Override
    public boolean getUSBtoPC() {
        //String state = loadFileAsString("/sys/kernel/debug/usb@fe800000/rk_usb_force_mode");
        String state = getSystemProperty("persist.usb.mode", "");
        if (state.equals("2")) {
            return true;
        }
        return false;
    }

    //设置OTG口连接状态
    @Override
    public void setUSBtoPC(boolean toPC) {
        if (toPC) {
            setSystemProperty("persist.usb.mode", "2");
            writeMethod("/sys/kernel/debug/usb@fe800000/rk_usb_force_mode", "2");
        } else{
            setSystemProperty("persist.usb.mode", "1");
            writeMethod("/sys/kernel/debug/usb@fe800000/rk_usb_force_mode", "1");
        }
    }

    //使能左右分屏功能
    @Override
    public void setSplitScreenLeftRightEnable(boolean isEnable) {
        Log.e("ztllib", "unsupport fucntion now for this board.todo later.");
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
        Log.e("ztllib", "unsupport fucntion now for this board.todo later.");
/*		if(isEnable){
			setSystemProperty("persist.sys.upDownEnable","true");
		}else{
			setSystemProperty("persist.sys.upDownEnable","false");
		}
*/
    }

    //获取HDMI分辨率列表
    @Override
    public String[] getHDMIResolutions() {
        try {
            String rawTxt = loadFileAsString("/sys/class/drm/card0-HDMI-A-1/modes");
            String[] array = rawTxt.split("\n");
            List<ZtlManagerU202.rotaionString> list_src = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                ZtlManagerU202.rotaionString src = new ZtlManagerU202.rotaionString(array[i]);
                list_src.add(src);
            }
            Collections.sort(list_src);
            String[] mdoes = new String[list_src.size()];
            for (int j = 0; j < mdoes.length; j++) {
                mdoes[j] = list_src.get(j).res;
            }
            return mdoes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取支持的分辨率列表
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
    }

    //设置GPU性能模式
    @Override
    public void setGPUMode(String mode) {
        String fmt = String.format("echo " + mode + " >/sys/bus/platform/devices/ff9a0000.gpu/devfreq/ff9a0000.gpu/governor");
        execRootCmdSilent(fmt);
    }
}

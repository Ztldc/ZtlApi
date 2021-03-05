package ZtlApi;

import android.content.Intent;
import android.util.Log;

public class ZtlManagerU202 extends ZtlManager {

    private String TAG = "ZtlManagerU202";

    ZtlManagerU202() {
        DEBUG_ZTL = getSystemProperty("persist.sys.ztl.debug", "false").equals("true");
    }

    //系统-打开/关闭导航栏状态栏
    @Override
    public void openSystemBar(boolean bOpen) {
        if (mContext == null) {
            Log.e(TAG, "上下文为空,不执行");
            return;
        }
        Log.e(TAG, "openSystemBar");
        Intent systemBarIntent = new Intent("com.ztl.action.systembar");
        systemBarIntent.setPackage("com.android.systemui");
        systemBarIntent.putExtra("enable", bOpen);
        mContext.sendBroadcast(systemBarIntent);
    }

    //显示-设置HDMI分辨率		1
    @Override
    public void setScreenMode(String mode) {
        Log.e(TAG, "setScreenMode" + mode);
        mode = mode.replace("@60p","");
        setSystemProperty("persist.ztl.hdmiresolution", mode);
        reboot(0);
    }

    //显示-获取屏幕分辨率	1
    @Override
    public String getDisplayMode() {
        String Mode = getSystemProperty("persist.ztl.hdmiresolution", "0");
        return Mode += "@60p";
    }

    //系统-打开USB调试 使用安卓官方接口，需要申请系统权限
    @Override
    public void openUsbDebug(boolean bOpen) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }
        Log.e(TAG, "openUsbDebug");
        String value = bOpen ? "1" : "0";
        Intent systemBarIntent = new Intent("com.ding.adbsetting");
        systemBarIntent.putExtra("enable", value);
        systemBarIntent.setPackage("com.yian.yiansettings");
        mContext.sendBroadcast(systemBarIntent);
    }

    //显示-设置屏幕方向 传入0 90 180 270
    @Override
    public void setDisplayOrientation(int rotation) {
        Log.e(TAG, "setDisplayOrientation");
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        if (rotation == getDisplayOrientation()) {
            Log.e("当前方向", "与旋转方向一致，不执行");
            return;
        }
        int oritation = rotation / 90;
        try {
            Intent oritationIntent = new Intent("ACTION_ZTL_ROTATION");
            oritationIntent.putExtra("rotation", oritation);
            oritationIntent.setPackage("com.yian.yiansettings");
            mContext.sendBroadcast(oritationIntent);
        } catch (Exception exc) {
            Log.e(TAG, "set rotation err!");
        }
    }

    //系统-获取当前导航栏状态 显示还是隐藏
    @Override
    public boolean isSystemBarOpen() {
        String state = getSystemProperty("persist.ztl.systembar", "true");
        return Boolean.valueOf(state);
    }
}

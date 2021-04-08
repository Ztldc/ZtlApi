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

    @Override
    public String getExternalSDCardPath() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return null;
        }
        String path1 = "";// 内部存储路径
        String path2 = "";// sd卡存储路径
        String path3 = "";// usb存储路径
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method getDescription = storageVolumeClazz.getMethod("getDescription", Context.class);
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                String description = (String) getDescription.invoke(storageVolumeElement, mContext);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (true == removable) {// 可拆卸设备
                    if (description.endsWith("USB") || description.endsWith("U 盘") || description.endsWith("USB 存储器")) {// usb外置卡
//                        path3 = path;
                        //Log.e("U盘", "外置U盘路径" + path3);
                    } else if (description.endsWith("SD") || description.endsWith("SD 卡")) {//sd卡可判断
                        path2 = path;
                        //Log.e("外置SD卡11111111", "***********************" + path2);
                    } else {//其它sd卡不可通过SD、SD卡来判断识别
                        path2 = path;
                        //Log.e("外置SD卡22222222", "***********************" + path2);
                    }
                } else {// 内置卡存储路径
//                    path1 = path;
                    //Log.e("内置存储卡", "----------------------------" + path1);
                }
            }
            return path2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //系统-存储-获取U盘路径	1
    @Override
    public String getUsbStoragePath() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return null;
        }
        String path1 = "";// 内部存储路径
        String path2 = "";// sd卡存储路径
        String path3 = "";// usb存储路径
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method getDescription = storageVolumeClazz.getMethod("getDescription", Context.class);
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                String description = (String) getDescription.invoke(storageVolumeElement, mContext);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (true == removable) {// 可拆卸设备
                    if (description.endsWith("USB") || description.endsWith("U 盘") || description.endsWith("USB 存储器")) {// usb外置卡
                        path3 = path;
                        //Log.e("U盘", "外置U盘路径" + path3);
                    } else if (description.endsWith("SD") || description.endsWith("SD 卡")) {//sd卡可判断
//                        path2 = path;
//                        Log.e("外置SD卡11111111", "***********************" + path2);
                    } else {//其它sd卡不可通过SD、SD卡来判断识别
//                        path2 = path;
//                        Log.e("外置SD卡22222222", "***********************" + path2);
                    }
                } else {// 内置卡存储路径
//                    path1 = path;
                    // Log.e("内置存储卡", "----------------------------" + path1);
                }
            }
            return path3;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    //获取外部U盘路径
//    @Override
//    public String getUsbStoragePath() {
//        String usbPath = null;
//        String usbBasePath = "/storage/";
//
//        File file = new File(usbBasePath);
//        try {
//            if ((file.exists()) && (file.isDirectory())) {
//                File[] files = file.listFiles();
//                for (int i = 0; i < files.length; i++) {
//                    usbPath = files[i].getAbsolutePath();
//                    LOGD("shx : get file path " + usbPath);
//                    if (usbPath.contains("udisk")) {
//                        LOGD("shx : open " + usbPath);
//                        File usbFile = new File(usbPath);
//                        if ((usbFile.exists()) && (usbFile.isDirectory())) {
//                            usbPath = usbFile.getAbsolutePath();
//                            LOGD("shx : usbPath " + usbPath);
//                            break;
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return usbPath;
//    }

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

    //获取当前GPIO的值
    public int Getcurrentgpio(int port) {
        execRootCmdSilent("cat /sys/class/gpio/gpio" + port + "value");
        return 1;
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


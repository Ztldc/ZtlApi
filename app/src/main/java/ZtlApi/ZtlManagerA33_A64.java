package ZtlApi;

import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.io.File;

import android.os.SystemClock;
import android.util.Log;

import android.os.SystemProperties;

import android.media.AudioManager;

import java.io.BufferedReader;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class ZtlManagerA33_A64 extends ZtlManager {

    private boolean DEBUG_ZTL = false;
    private String TAG = "ZtlManagerA33_A64";

    public ZtlManagerA33_A64() {
        init_gpiomap();
        DEBUG_ZTL = SystemProperties.get("persist.sys.ztl.debug", "false").equals("true");
    }

    void LOGD(String msg) {
        if (DEBUG_ZTL) {
            Log.d(TAG, msg);
        }
    }

    //获取U盘路径	1
    @Override
    public String getUsbStoragePath() {
        String usbPath = null;

        try {
            usbPath = getSystemProperty("persist.sys.usbDisk", "unKnown");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return usbPath;
    }

    //A64获取芯片唯一ID
    @Override
    public String getDeviceID() {
        BufferedReader bre = null;
        String lineInfo;
        String cpuSerial;

        File cpuInfo = new File("/sys/class/android_usb/android0/iSerial");
        if (!cpuInfo.exists()) {
            return null;
        }

        try {
            bre = new BufferedReader(new FileReader(cpuInfo));
            cpuSerial = bre.readLine(); // 一次读入一行数据
            return cpuSerial;
			/*while((lineInfo = bre.readLine())!= null){
				if(!lineInfo.contains("androidboot.serialno")){
					continue;
				}
				LOGD(lineInfo.length() + lineInfo);
				
				cpuSerial = lineInfo.substring(lineInfo.indexOf(":")+2);
				LOGD(cpuSerial);
				return cpuSerial;
			}*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    //增大音量，音量+1	1
    @Override
    public int setRaiseSystemVolume() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return -1;
        }

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
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return -1;
        }

        try {
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    //设置开机自启动APP包名和Activity		1
    @Override
    public void setBootPackageActivity(String pkgName, String pkgActivity) {
        if (pkgName != null && pkgActivity != null) {
            //setSystemProperty("persist.sys.bootPkgName",pkgName);
            //setSystemProperty("persist.sys.bootPkgActivity",pkgActivity);
            execRootCmdSilent("setprop persist.sys.bootPkgName " + pkgName);
            execRootCmdSilent("setprop persist.sys.bootPkgActivity " + pkgActivity);
        } else {
            Log.e(TAG, "pkgName (" + pkgName + ") or pkgActivity (" + pkgActivity + ") err");
            return;
        }
        return;
    }

    //设置APP加密密钥
    public int setAppKey(String key) {
        if (key != null) {
            Intent systemBarIntent = new Intent("com.ztl.key");
            String str = key;
            systemBarIntent.putExtra("enable", str);
            mContext.sendBroadcast(systemBarIntent);
        } else {
            Log.e(TAG, "设置APP加密密钥值错误");
            return -1;
        }
        return 0;
    }

    //获取屏幕方向	1
    @Override
    public int getDisplayOrientation() {
        Log.e(TAG, "等待后续系统开发此功能");
        return 0;
    }

    //设置屏幕方向,设置完后重启系统
    @Override
    public void setDisplayOrientation(int rotation) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent systemBarIntent = new Intent("com.ztl.rotation");
        int nowrotation = rotation;
        systemBarIntent.putExtra("enable", nowrotation);
        mContext.sendBroadcast(systemBarIntent);
    }

    //系统-打开/关闭导航栏状态栏
    @Override
    public void openSystemBar(boolean bOpen) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        String value = bOpen ? "0" : "1";
        Intent systemBarIntent = new Intent("com.ztl.systembar");
        systemBarIntent.putExtra("enable", value);
        mContext.sendBroadcast(systemBarIntent);
    }

    //打开导航兰	1
    @Override
    public void setOpenSystemBar() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent systemBarIntent = new Intent("com.ztl.systembar");
        String str = "1";
        systemBarIntent.putExtra("enable", str);
        mContext.sendBroadcast(systemBarIntent);
    }

    //隐藏导航兰	1
    @Override
    public void setCloseSystemBar() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent systemBarIntent = new Intent("com.ztl.systembar");
        String str = "0";
        systemBarIntent.putExtra("enable", str);
        mContext.sendBroadcast(systemBarIntent);
    }

    //设置分辨率		1
    @Override
    public void setScreenMode(String mode) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent systemBarIntent = new Intent("com.ztl.vga");
        String str = mode;
        systemBarIntent.putExtra("enable", str);
        mContext.sendBroadcast(systemBarIntent);
    }

    //获取USB调试状态	1
    @Override
    public int getUsbDebugState() {
        Log.e(TAG, "等待后续系统开发此功能");
        return 0;
    }

    //获取状态栏状态	1
    @Override
    public int getSystemBarState() {
        Log.e(TAG, "等待后续系统开发此功能");
        return 0;
    }
    //设置GPIO值-GPIO只有设置输出的值才有意义。所以这里默认就是设置输出
    @Override
    public void setGpioValue(String port, int value) {
        if (port.contains("PE") == false) {
            Log.e(TAG, "传入参数错误,请传入PE1之类的，实际以规格书为准");
            return;
        }
        Gpio gpio = new Gpio();
        if (gpio.open(port) == false) {
            return;
        }
        gpio.setValue("out", value);
    }

    //获取GPIO值
    @Override
    public int getGpioValue(String port, String direction) {
        if (port.contains("PE") == false) {
            Log.e(TAG, "传入参数错误,请传入PE1之类的，实际以规格书为准");
            return -1;
        }
        Gpio gpio = new Gpio();
        if (gpio.open(port) == false) {
            return -1;
        }

        gpio.setDirection(direction);
        return gpio.getValue();
    }

    //设置GPIO方向
    @Override
    public void setGpioDirection(String port, String direction) {
        if (port.contains("PE") == false) {
            Log.e(TAG, "传入参数错误,请传入PE1之类的，实际以规格书为准");
            return;
        }
        Gpio gpio = new Gpio();
        if (gpio.open(port) == false) {
            return;
        }
        gpio.setDirection(direction);
    }

    //获取GPIO方向
    @Override
    public String getGpioDirection(String port) {
        if (port.contains("PE") == false) {
            Log.e(TAG, "传入参数错误,请传入PE1之类的，实际以规格书为准");
            return null;
        }
        Gpio gpio = new Gpio();
        if (gpio.open(port) == false) {
            return null;
        }
        return gpio.getDirection();
    }

    Map<String, Integer> gpios = new HashMap<>();

    void init_gpiomap() {
        gpios.put("PE1", 129);
        gpios.put("PE2", 130);
        gpios.put("PE3", 131);
        gpios.put("PE4", 132);
        gpios.put("PE7", 135);
    }

    @Override
    public int gpioStringToInt(String strGpioName) {
        if (gpios.containsKey(strGpioName) == false){
            return -1;
        }
        Object v = gpios.get(strGpioName);
        if (v == null) {
            Log.e("gpio", "name" + strGpioName + "缺乏映射，请联系管理员添加");
        }

        return gpios.get(strGpioName);
    }

    @Override
    public String[] getCPUFreq(){
        return null;
    }

}

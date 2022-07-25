package ZtlApi;

import android.annotation.SuppressLint;
import android.app.usage.StorageStatsManager;
import android.content.ComponentName;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ZtlManagerU202 extends ZtlManager {

    private String TAG = "ZtlManagerU202";

    ZtlManagerU202() {
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

    //系统-截取当前屏幕
    @Override
    public void startScreenShot(String path, String fileName) {
        ComponentName componetName = new ComponentName(
                "com.yian.yiansettings",  //这个参数是另外一个app的包名
                "com.yian.yiansettings.ZTLService");   //这个是要启动的Service的全路径名

        Intent intent = new Intent();
        intent.setComponent(componetName);
        intent.putExtra("cmd", "snapshot"); //value填的需要和ztlhelper统一
        intent.putExtra("filepath", path + "/" + fileName);  //这里填要传入的参数，第一个name需要和ztlhelper统一
        mContext.startService(intent);

//        if (isExist(path)) {
//            String filePath = path + "/" + fileName;
//            String screenShotCmd = "pngtest 1 " + filePath;
//            execRootCmdSilent(screenShotCmd);
//        } else {
//            Log.e(TAG, "file path " + path + " not exist");
//        }
    }

    @Override
    public void setHDMIResolution(String value) {
        execRootCmdSilent("echo " + value + " > /sys/class/display/mode");
    }

    //显示-设置HDMI分辨率
    //只支持1080p 720p 2160p 其他暂不支持
    @Override
    public void setScreenMode(String mode) {
        ComponentName componetName = new ComponentName(
                "com.droidlogic.tv.settings",  //这个参数是另外一个app的包名
                "com.droidlogic.tv.settings.ZTLService");   //这个是要启动的Service的全路径名

        Intent intent = new Intent();
        intent.setComponent(componetName);
        intent.putExtra("cmd", "set_hdmiresolution"); //value填的需要和ztlhelper统一
        intent.putExtra("mode", mode);  //这里填要传入的参数，第一个name需要和ztlhelper统一
        mContext.startService(intent);

//        if( mode.contains("1080") ){
//            setHDMIResolution( mode );
//            setSystemProperty("persist.ztl.hdmiresolution", "1920x1080");
//            reboot(0);
//        }else if( mode.contains("720") ){
//            setHDMIResolution( mode );
//            setSystemProperty("persist.ztl.hdmiresolution", "1280x720");
//            reboot(0);
//        }else if( mode.contains("2160") ){
//            setHDMIResolution( mode );
//            setSystemProperty("persist.ztl.hdmiresolution", "3840x2160");
//            reboot(0);
//        }else{
//            Log.e("905d3","暂不支持其他分辨率");
//        }
//        mode = mode.replace("@60p", "");
//        setSystemProperty("persist.ztl.hdmiresolution", mode);
//        reboot(0);
    }

    @Override
    public int getDisplayCount() {
        int count = 0;
        String mode1 = execRootCmd("cat /sys/class/display/mode");
        String mode2 = execRootCmd("cat /sys/class/display2/mode");
        if (mode1.equals("null") == false) {
            count++;
        }

        if (mode2.equals("null") == false) {
            count++;
        }
        return count;
    }

    //GPIO计算方式
    //没人整合到jar去 而且尚未经过测试。所以放这
    //S905D3的IO口分为 AO EE。这两个的基础值不一样。 外加各个口的偏移。所以搞得一团糟，就封装一个函数里搞定
    class s905d3_gpio {
        String name;
        boolean is_ao;
        int offset;

        public s905d3_gpio(String name, boolean is_ao, int offset) {
            this.name = name;
            this.is_ao = is_ao;
            this.offset = offset;
        }

        int getValue() {
            if (is_ao)
                return 496 + offset;
            else
                return 410 + offset;
        }
    }

    HashMap<String, s905d3_gpio> s905d3_gpios = new HashMap<>();

    int get905d3GpioValue(String name) {
        if (s905d3_gpios.size() <= 0) {
            s905d3_gpios.put("GPIOAO_0", new s905d3_gpio("GPIOAO_0", true, 0));
            s905d3_gpios.put("GPIOAO_1", new s905d3_gpio("GPIOAO_1", true, 1));
            s905d3_gpios.put("GPIOAO_2", new s905d3_gpio("GPIOAO_2", true, 2));
            s905d3_gpios.put("GPIOAO_3", new s905d3_gpio("GPIOAO_3", true, 3));
            s905d3_gpios.put("GPIOAO_4", new s905d3_gpio("GPIOAO_4", true, 4));
            s905d3_gpios.put("GPIOAO_5", new s905d3_gpio("GPIOAO_5", true, 5));
            s905d3_gpios.put("GPIOAO_6", new s905d3_gpio("GPIOAO_6", true, 6));
            s905d3_gpios.put("GPIOAO_7", new s905d3_gpio("GPIOAO_7", true, 7));
            s905d3_gpios.put("GPIOAO_8", new s905d3_gpio("GPIOAO_8", true, 8));
            s905d3_gpios.put("GPIOAO_9", new s905d3_gpio("GPIOAO_9", true, 9));
            s905d3_gpios.put("GPIOAO_10", new s905d3_gpio("GPIOAO_10", true, 10));
            s905d3_gpios.put("GPIOAO_11", new s905d3_gpio("GPIOAO_11", true, 11));
            s905d3_gpios.put("GPIOE_0", new s905d3_gpio("GPIOE_0", true, 12));
            s905d3_gpios.put("GPIOE_1", new s905d3_gpio("GPIOE_1", true, 13));
            s905d3_gpios.put("GPIOE_2", new s905d3_gpio("GPIOE_2", true, 14));
            s905d3_gpios.put("GPIO_TEST_N", new s905d3_gpio("GPIO_TEST_N", true, 15));
            //EE
            s905d3_gpios.put("GPIOV_0", new s905d3_gpio("GPIOV_0", false, 0));
            s905d3_gpios.put("GPIOZ_0", new s905d3_gpio("GPIOZ_0", false, 1));
            s905d3_gpios.put("GPIOZ_1", new s905d3_gpio("GPIOZ_1", false, 2));
            s905d3_gpios.put("GPIOZ_2", new s905d3_gpio("GPIOZ_2", false, 3));
            s905d3_gpios.put("GPIOZ_3", new s905d3_gpio("GPIOZ_3", false, 4));
            s905d3_gpios.put("GPIOZ_4", new s905d3_gpio("GPIOZ_4", false, 5));
            s905d3_gpios.put("GPIOZ_5", new s905d3_gpio("GPIOZ_5", false, 6));
            s905d3_gpios.put("GPIOZ_6", new s905d3_gpio("GPIOZ_6", false, 7));
            s905d3_gpios.put("GPIOZ_7", new s905d3_gpio("GPIOZ_7", false, 8));
            s905d3_gpios.put("GPIOZ_8", new s905d3_gpio("GPIOZ_8", false, 9));
            s905d3_gpios.put("GPIOZ_9", new s905d3_gpio("GPIOZ_9", false, 10));
            s905d3_gpios.put("GPIOZ_10", new s905d3_gpio("GPIOZ_10", false, 11));
            s905d3_gpios.put("GPIOZ_11", new s905d3_gpio("GPIOZ_11", false, 12));
            s905d3_gpios.put("GPIOZ_12", new s905d3_gpio("GPIOZ_12", false, 13));
            s905d3_gpios.put("GPIOZ_13", new s905d3_gpio("GPIOZ_13", false, 14));
            s905d3_gpios.put("GPIOZ_14", new s905d3_gpio("GPIOZ_14", false, 15));
            s905d3_gpios.put("GPIOZ_15", new s905d3_gpio("GPIOZ_15", false, 16));
            s905d3_gpios.put("GPIOH_0", new s905d3_gpio("GPIOH_0", false, 17));
            s905d3_gpios.put("GPIOH_1", new s905d3_gpio("GPIOH_1", false, 18));
            s905d3_gpios.put("GPIOH_2", new s905d3_gpio("GPIOH_2", false, 19));
            s905d3_gpios.put("GPIOH_3", new s905d3_gpio("GPIOH_3", false, 20));
            s905d3_gpios.put("GPIOH_4", new s905d3_gpio("GPIOH_4", false, 21));
            s905d3_gpios.put("GPIOH_5", new s905d3_gpio("GPIOH_5", false, 22));
            s905d3_gpios.put("GPIOH_6", new s905d3_gpio("GPIOH_6", false, 23));
            s905d3_gpios.put("GPIOH_7", new s905d3_gpio("GPIOH_7", false, 24));
            s905d3_gpios.put("GPIOH_8", new s905d3_gpio("GPIOH_8", false, 25));
            s905d3_gpios.put("BOOT_0", new s905d3_gpio("BOOT_0", false, 26));
            s905d3_gpios.put("BOOT_1", new s905d3_gpio("BOOT_1", false, 27));
            s905d3_gpios.put("BOOT_2", new s905d3_gpio("BOOT_2", false, 28));
            s905d3_gpios.put("BOOT_3", new s905d3_gpio("BOOT_3", false, 29));
            s905d3_gpios.put("BOOT_4", new s905d3_gpio("BOOT_4", false, 30));
            s905d3_gpios.put("BOOT_5", new s905d3_gpio("BOOT_5", false, 31));
            s905d3_gpios.put("BOOT_6", new s905d3_gpio("BOOT_6", false, 32));
            s905d3_gpios.put("BOOT_7", new s905d3_gpio("BOOT_7", false, 33));
            s905d3_gpios.put("BOOT_8", new s905d3_gpio("BOOT_8", false, 34));
            s905d3_gpios.put("BOOT_9", new s905d3_gpio("BOOT_9", false, 35));
            s905d3_gpios.put("BOOT_10", new s905d3_gpio("BOOT_10", false, 36));
            s905d3_gpios.put("BOOT_11", new s905d3_gpio("BOOT_11", false, 37));
            s905d3_gpios.put("BOOT_12", new s905d3_gpio("BOOT_12", false, 38));
            s905d3_gpios.put("BOOT_13", new s905d3_gpio("BOOT_13", false, 39));
            s905d3_gpios.put("BOOT_14", new s905d3_gpio("BOOT_14", false, 40));
            s905d3_gpios.put("BOOT_15", new s905d3_gpio("BOOT_15", false, 41));
            s905d3_gpios.put("GPIOC_0", new s905d3_gpio("GPIOC_0", false, 42));
            s905d3_gpios.put("GPIOC_1", new s905d3_gpio("GPIOC_1", false, 43));
            s905d3_gpios.put("GPIOC_2", new s905d3_gpio("GPIOC_2", false, 44));
            s905d3_gpios.put("GPIOC_3", new s905d3_gpio("GPIOC_3", false, 45));
            s905d3_gpios.put("GPIOC_4", new s905d3_gpio("GPIOC_4", false, 46));
            s905d3_gpios.put("GPIOC_5", new s905d3_gpio("GPIOC_5", false, 47));
            s905d3_gpios.put("GPIOC_6", new s905d3_gpio("GPIOC_6", false, 48));
            s905d3_gpios.put("GPIOC_7", new s905d3_gpio("GPIOC_7", false, 49));
            s905d3_gpios.put("GPIOA_0", new s905d3_gpio("GPIOA_0", false, 50));
            s905d3_gpios.put("GPIOA_1", new s905d3_gpio("GPIOA_1", false, 51));
            s905d3_gpios.put("GPIOA_2", new s905d3_gpio("GPIOA_2", false, 52));
            s905d3_gpios.put("GPIOA_3", new s905d3_gpio("GPIOA_3", false, 53));
            s905d3_gpios.put("GPIOA_4", new s905d3_gpio("GPIOA_4", false, 54));
            s905d3_gpios.put("GPIOA_5", new s905d3_gpio("GPIOA_5", false, 55));
            s905d3_gpios.put("GPIOA_6", new s905d3_gpio("GPIOA_6", false, 56));
            s905d3_gpios.put("GPIOA_7", new s905d3_gpio("GPIOA_7", false, 57));
            s905d3_gpios.put("GPIOA_8", new s905d3_gpio("GPIOA_8", false, 58));
            s905d3_gpios.put("GPIOA_9", new s905d3_gpio("GPIOA_9", false, 59));
            s905d3_gpios.put("GPIOA_10", new s905d3_gpio("GPIOA_10", false, 60));
            s905d3_gpios.put("GPIOA_11", new s905d3_gpio("GPIOA_11", false, 61));
            s905d3_gpios.put("GPIOA_12", new s905d3_gpio("GPIOA_12", false, 62));
            s905d3_gpios.put("GPIOA_13", new s905d3_gpio("GPIOA_13", false, 63));
            s905d3_gpios.put("GPIOA_14", new s905d3_gpio("GPIOA_14", false, 64));
            s905d3_gpios.put("GPIOA_15", new s905d3_gpio("GPIOA_15", false, 65));
            s905d3_gpios.put("GPIOX_0", new s905d3_gpio("GPIOX_0", false, 66));
            s905d3_gpios.put("GPIOX_1", new s905d3_gpio("GPIOX_1", false, 67));
            s905d3_gpios.put("GPIOX_2", new s905d3_gpio("GPIOX_2", false, 68));
            s905d3_gpios.put("GPIOX_3", new s905d3_gpio("GPIOX_3", false, 69));
            s905d3_gpios.put("GPIOX_4", new s905d3_gpio("GPIOX_4", false, 70));
            s905d3_gpios.put("GPIOX_5", new s905d3_gpio("GPIOX_5", false, 71));
            s905d3_gpios.put("GPIOX_6", new s905d3_gpio("GPIOX_6", false, 72));
            s905d3_gpios.put("GPIOX_7", new s905d3_gpio("GPIOX_7", false, 73));
            s905d3_gpios.put("GPIOX_8", new s905d3_gpio("GPIOX_8", false, 74));
            s905d3_gpios.put("GPIOX_9", new s905d3_gpio("GPIOX_9", false, 75));
            s905d3_gpios.put("GPIOX_10", new s905d3_gpio("GPIOX_10", false, 76));
            s905d3_gpios.put("GPIOX_11", new s905d3_gpio("GPIOX_11", false, 77));
            s905d3_gpios.put("GPIOX_12", new s905d3_gpio("GPIOX_12", false, 78));
            s905d3_gpios.put("GPIOX_13", new s905d3_gpio("GPIOX_13", false, 79));
            s905d3_gpios.put("GPIOX_14", new s905d3_gpio("GPIOX_14", false, 80));
            s905d3_gpios.put("GPIOX_15", new s905d3_gpio("GPIOX_15", false, 81));
            s905d3_gpios.put("GPIOX_16", new s905d3_gpio("GPIOX_16", false, 82));
            s905d3_gpios.put("GPIOX_17", new s905d3_gpio("GPIOX_17", false, 83));
            s905d3_gpios.put("GPIOX_18", new s905d3_gpio("GPIOX_18", false, 84));
            s905d3_gpios.put("GPIOX_19", new s905d3_gpio("GPIOX_19", false, 85));
        }

//        System.out.println("gppo77777777aa:"+s905d3_gpios.size());
//        System.out.println("gppo77777777bb:"+name);
        if (s905d3_gpios.containsKey(name)) {
//        if (s905d3_gpios.containsKey(name)) {
//            System.out.println("gppo77777777dd:"+name);
            return s905d3_gpios.get(name).getValue();
        }
//        System.out.println("gppo77777777cc:"+name);
        return 0;
    }

    @Override
    public int gpioStringToInt(String port) {
        return get905d3GpioValue(port);
    }

    //排序分辨率
    protected static class rotaionString implements Comparable {

        public String res = "";     //保存分辨率列表 比如 1080i60hz 1080p60hz 1080i50hz 1080p50hz
        public String number = "";  //比较数字 比如1080i60hz 取出108060
        public String tary = "";    //比较字母 比如1080i60hz 和 1080p60hz 取出 ihz 和 phz

        public rotaionString(String rotaion) {
            res = rotaion;
            if (res != null) {
                handled();
            }
        }

        //处理排序
        protected void handled() {
            for (int i = 0; i < res.length(); i++) {
                if (res.charAt(i) <= '9' && res.charAt(i) >= '0') {
                    number += res.charAt(i);
                } else {
                    tary += res.charAt(i);
                }
            }
        }

        @Override
        public int compareTo(Object o) {
            return -1;
//            rotaionString src = (rotaionString) o;
//            int compare = 0;
//            if (Integer.valueOf(number) < Integer.valueOf(src.number)) {
//                return -1;
//            } else if (Integer.valueOf(number) > Integer.valueOf(src.number)) {
//                return 1;
//            } else if (Integer.valueOf(number) == Integer.valueOf(src.number)) {
//                return tary.compareTo(src.tary);
//            }
//            return compare;
//            return compare;
        }
    }

    //获取HDMI分辨率列表
    @Override
    public String[] getHDMIResolutions() {
        try {
            String rawTxt = loadFileAsString("/sys/class/amhdmitx/amhdmitx0/disp_cap");
            if (rawTxt.contains("*")) {
                rawTxt = rawTxt.replace("*", "");
            }
            String[] array = rawTxt.split("\n");
            List<rotaionString> list_src = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                rotaionString src = new rotaionString(array[i]);
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

    //显示-获取屏幕分辨率	1
    @Override
    public String getDisplayMode() {

        String rawTxt = null;
        try {
            rawTxt = loadFileAsString("sys/class/display/mode");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawTxt;

        //String Mode = getSystemProperty("persist.ztl.hdmiresolution", "0");
        //return Mode += "@60p";
    }

    //系统-设置生成序列号
    //http://www.vaststargames.com/read.php?tid=24
    @Override
    public int setBuildSerial(String sn) {
        execRootCmdSilent("echo usid > /sys/class/unifykeys/name");
        execRootCmdSilent("echo \"" + sn + "\" > /sys/class/unifykeys/write");
        return 0;
    }

    //系统-打开USB调试 使用安卓官方接口，需要申请系统权限
    @Override
    public void openUsbDebug(boolean bOpen) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }
        String value = bOpen ? "1" : "0";
        Intent systemBarIntent = new Intent("com.ding.adbsetting");
        systemBarIntent.putExtra("enable", value);
        systemBarIntent.setPackage("com.yian.yiansettings");
        systemBarIntent.putExtra("skip_permission", true);
        mContext.sendBroadcast(systemBarIntent);
    }

    //系统-设置OTG口连接状态
    @Override
    public void setUSBtoPC(boolean toPC) {
        String value = toPC ? "1" : "0";
        setSystemProperty("persist.usb.mode", value);
        writeMethod("/sys/devices/platform/ffe09080.usb3phy/ztl_usb_ctrl/ztl_usb_ctrl", value);

        //改Uboot 参数。避免开机启动时OTG口状态不对
        ComponentName componetName = new ComponentName(
                "com.droidlogic.tv.settings",  //这个参数是另外一个app的包名
                "com.droidlogic.tv.settings.ZTLService");   //这个是要启动的Service的全路径名

        Intent intent = new Intent();
        intent.setComponent(componetName);
        intent.putExtra("cmd", "set_otg"); //value填的需要和ztlhelper统一
        intent.putExtra("isotg", toPC);  //这里填要传入的参数，第一个name需要和ztlhelper统一
        mContext.startService(intent);
    }

    //系统-获取OTG口连接状态 //勾中的时候是2 不勾的时候是1
    @Override
    public boolean getUSBtoPC() {
        try {
            String state = loadFileAsString("/sys/devices/platform/ffe09080.usb3phy/ztl_usb_ctrl/ztl_usb_ctrl");
            if (state.contains("1")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //获取串口是否被占用; true：被系统占用  false：未被系统占用 //重启生效
    public boolean isDebugSerialEnable() {
        String value = getSystemProperty("ro.ztl.debugSerialState", "-1");   //1：系统使用 0：用户使用
        if (value.equals("1")) {
            return true;
        } else if (value.equals("0")) {
            return false;
        } else {
            Log.e(TAG, "系统不支持，请更新");
            return true;
        }
    }

    //设置调试串口使能 true:系统用 false：不启用(用户可以用) //重启生效
    public void enableDebugSerial(boolean enable, boolean restartNow) {
        ComponentName componetName = new ComponentName(
                "com.droidlogic.tv.settings",  //这个参数是另外一个app的包名
                "com.droidlogic.tv.settings.ZTLService");   //这个是要启动的Service的全路径名

        Intent intent = new Intent();
        intent.setComponent(componetName);
        intent.setPackage("com.droidlogic.tv.settings");
        intent.putExtra("cmd", "setDebugSerial");
        intent.putExtra("enable", enable);
        mContext.startService(intent);
        if (restartNow) {
            reboot(0);
        }
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
        //setExtendDisplayOrientation(rotation);
        reboot(1);
    }

    /**
     * 显示-设置多屏幕方向
     *
     * @param screen   要设置的屏幕,0为主屏；1为副屏1，2为副屏2，副屏以此类推……；-1为同时设定
     * @param rotation 旋转角度为：0,90,180,270
     * @return -1 不支持,0设置成功
     */
    @Override
    public int setDisplayOrientation(int screen, int rotation) {
        if (screen == 0) {
            setPrimaryDisplayOrientation(rotation);
            reboot(1);
        } else if (screen == 1) {
            setExtendDisplayOrientation(rotation);
            reboot(1);
        } else if (screen == -1) {
            setPrimaryDisplayOrientation(rotation);
            setExtendDisplayOrientation(rotation);
            reboot(1);
        }

        return 0;
    }

    //显示-设置屏幕方向(主屏)
    public void setPrimaryDisplayOrientation(int rotation) {
        ComponentName componetName = new ComponentName(
                "com.droidlogic.tv.settings",  //这个参数是另外一个app的包名
                "com.droidlogic.tv.settings.ZTLService");   //这个是要启动的Service的全路径名

        if (rotation == getDisplayOrientation(0)) {
            Log.e("当前方向", "与旋转方向一致，不执行");
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(componetName);
        intent.putExtra("cmd", "set_display_rotation"); //value填的需要和ztlhelper统一
        intent.putExtra("rotation", rotation);
        intent.putExtra("display", "primary");
        mContext.startService(intent);
    }

    //获取U盘列表
    public List<String> getUSBDisks() {
        String usbPath = null;
        String usbBasePath = "";

        if (getAndroidVersion().contains("5.1.1") || getAndroidVersion().contains("4.4")) {
            usbBasePath = "/mnt/usb_storage/";
        } else {
            usbBasePath = "/storage/";
        }

        List<String> Files1 = new ArrayList<>();
        File file = new File(usbBasePath);
        try {
            if (file.exists() && file.isDirectory()) { //open usb_storage
                File[] files = file.listFiles();
                if (files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        String absPath = files[i].getAbsolutePath();
                        if (absPath.equals("/storage/emulated") || absPath.equals("/storage/self")
                                || absPath.equals(getAppRootOfSdCardRemovable())) {
                            continue;
                        } else {
                            File usbFile = new File(absPath);
                            if (usbFile.exists() && usbFile.isDirectory()) {
                                if (getAndroidVersion().contains("5.1") || getAndroidVersion().contains("4.4")) {
                                    File[] usbFiles = usbFile.listFiles();
                                    usbPath = usbFiles[i].getPath();
                                    Files1.add(usbPath);
                                } else {
                                    Files1.add(absPath);
                                }
                            }
                        }
                    }
//                    //todo 有bug 插入两个U盘的时候返回一个
//                    for (int i = 0; i < Files1.size(); i++) {
//                        Files1 = Collections.singletonList(Files1.get(i));    //udisk0
//                        break;
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Files1;
    }

    //显示-设置屏幕方向(副屏)
    public void setExtendDisplayOrientation(int rotation) {
        ComponentName componetName = new ComponentName(
                "com.droidlogic.tv.settings",  //这个参数是另外一个app的包名
                "com.droidlogic.tv.settings.ZTLService");   //这个是要启动的Service的全路径名

        if (rotation == getDisplayOrientation(1)) {
            Log.e("当前方向", "与旋转方向一致，不执行");
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(componetName);
        intent.putExtra("cmd", "set_display_rotation"); //value填的需要和ztlhelper统一
        intent.putExtra("rotation", rotation);  //这里填要传入的参数，第一个name需要和ztlhelper统一
        intent.putExtra("display", "extend");  //这里填要传入的参数，第一个name需要和ztlhelper统一
        mContext.startService(intent);
    }

    //系统-隐藏导航栏与状态栏	1
    @Override
    public void setCloseSystemBar() {
        openSystemBar(false);
    }

    //系统-打开/关闭导航栏状态栏
    @Override
    public void openSystemBar(boolean bOpen) {
        if (mContext == null) {
            Log.e(TAG, "上下文为空,不执行");
            return;
        }
        Intent systemBarIntent = new Intent("com.ztl.action.systembar");
//        systemBarIntent.setPackage("com.android.systemui");       //因为framework 也要处理 所以不能指定包名接收
        systemBarIntent.putExtra("enable", bOpen);
        systemBarIntent.putExtra("skip_permission", true);
        mContext.sendBroadcast(systemBarIntent);
    }

    //系统-获取当前导航栏状态 显示还是隐藏
    @Override
    public boolean isSystemBarOpen() {
        String state = getSystemProperty("persist.ztl.systembar", "true");
        return Boolean.valueOf(state);
    }
}

package ZtlApi;

import android.content.Intent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import android.util.Log;
import android.os.SystemProperties;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class ZtlManager32887_1 extends ZtlManager {
    // Arctan add

    ZtlManager32887_1() {
        init_gpiomap();
    }

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
        Log.d(ZtlManager.TAG, "readStrListFromFile - " + fileStrings.toString());
        return fileStrings;
    }

    private String readStrFromFile(String filename) throws IOException {
        Log.d(ZtlManager.TAG, "readStrFromFile - " + filename);
        File f = new File(filename);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        //	Log.d(TAG,"readStrFromFile - " + line);
        return line;
    }

    public void LwlTest(int a) {
        Log.d(ZtlManager.TAG, "LLLLL ----> " + a);
        try {
            readStrListFromFile(SYS_NODE_VGA_MODES);
            readStrFromFile(SYS_NODE_VGA_MODE);
            Log.d(ZtlManager.TAG, getDisplayMode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取屏幕方向	1
    @Override
    public int getDisplayOrientation() {
        //	String state = getSystemProperty("persist.sys.ztlOrientation","0");
        String state = getSystemProperty("persist.sys.orientation", "0");
        return Integer.valueOf(state).intValue();
    }

    @Override
    //系统-打开/关闭导航栏状态栏
    public void openSystemBar(boolean bOpen) {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        if (bOpen) {
            Intent systemBarIntent = new Intent("ZTL.ACTION.OPEN.SYSTEMBAR");
            mContext.sendBroadcast(systemBarIntent);
        } else {
            Intent systemBarIntent = new Intent("ZTL.ACTION.CLOSE.SYSTEMBAR");
            mContext.sendBroadcast(systemBarIntent);
        }
    }

    //打开导航兰	1
    @Override
    public void setOpenSystemBar() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent systemBarIntent = new Intent("ZTL.ACTION.OPEN.SYSTEMBAR");
        mContext.sendBroadcast(systemBarIntent);
    }

    //隐藏导航兰	1
    @Override
    public void setCloseSystemBar() {
        if (mContext == null) {
            Log.e("上下文为空，不执行", "请检查是否已调用setContext()");
            return;
        }

        Intent systemBarIntent = new Intent("ZTL.ACTION.CLOSE.SYSTEMBAR");
        mContext.sendBroadcast(systemBarIntent);
    }

    //系统-获取当前导航栏状态 显示还是隐藏
    @Override
    public boolean isSystemBarOpen() {
        String state = SystemProperties.get(SYSTEM_BAR_STATE);
        if (state.equals(SYSTEM_BAR_SHOW)) {
            return true;
        } else if (state.equals(SYSTEM_BAR_HIDE)) {
            return false;
        }
        return true;
    }

    //获取状态栏状态	1
    @Override
    public int getSystemBarState() {
        String state = SystemProperties.get(SYSTEM_BAR_STATE);
        if (state.equals(SYSTEM_BAR_SHOW)) {
            return 1;
        } else if (state.equals(SYSTEM_BAR_HIDE)) {
            return 0;
        }

        return -1;
    }

    //设置分辨率		1
    @Override
    public void setScreenMode(String mode) {
        setSystemProperty("persist.sys.screenmode", mode);
        setSystemProperty("ztl.Screen", "Set");
    }


    Map<String, Integer> gpios = new HashMap<>();

    void init_gpiomap() {
        gpios.put("GPIO0_A0", 0);
        gpios.put("GPIO0_A1", 1);
        gpios.put("GPIO0_A2", 2);
        gpios.put("GPIO0_A3", 3);
        gpios.put("GPIO0_A4", 4);
        gpios.put("GPIO0_A5", 5);
        gpios.put("GPIO0_A6", 6);
        gpios.put("GPIO0_A7", 7);

        gpios.put("GPIO0_B0", 8);
        gpios.put("GPIO0_B1", 9);
        gpios.put("GPIO0_B2", 10);
        gpios.put("GPIO0_B3", 11);
        gpios.put("GPIO0_B4", 12);
        gpios.put("GPIO0_B5", 13);
        gpios.put("GPIO0_B6", 14);
        gpios.put("GPIO0_B7", 15);

        gpios.put("GPIO0_C0", 16);
        gpios.put("GPIO0_C1", 17);
        gpios.put("GPIO0_C2", 18);
        gpios.put("GPIO0_C3", 19);
        gpios.put("GPIO0_C4", 20);
        gpios.put("GPIO0_C5", 21);
        gpios.put("GPIO0_C6", 22);
        gpios.put("GPIO0_C7", 23);

        gpios.put("GPIO0_D0", 24);
    }

    @Override
    public int gpioStringToInt(String strGpioName) {

        //卢工说3288 7.1 计算方式为正常计算方式 5.1计算方式 -8
        Object v = gpios.get(strGpioName);
        if (v != null){
            return super.gpioStringToInt(strGpioName);
        }else
            return super.gpioStringToInt(strGpioName) - 8;



//		if (v == null){
//			Log.e("gpio","name"+strGpioName+"缺乏映射，请联系管理员添加");
//			return -1;
//		}
//		return gpios.get( strGpioName );
    }

    //设置GPU性能模式
    @Override
    public void setGPUMode(String mode) {
        String fmt = String.format("echo " + mode + " >/sys/bus/platform/devices/ffa30000.gpu/devfreq/ffa30000.gpu/governor");
        execRootCmdSilent(fmt);
    }
}

	

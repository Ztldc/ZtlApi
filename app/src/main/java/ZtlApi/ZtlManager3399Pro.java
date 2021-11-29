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
import java.util.Collections;
import java.util.List;

public class ZtlManager3399Pro extends ZtlManagerU202 {

    private String TAG = "ZtlManager3399Pro";

    ZtlManager3399Pro() {
        DEBUG_ZTL = getSystemProperty("persist.sys.ztl.debug", "false").equals("true");
    }

    class ResolutionInfo implements Comparable<ResolutionInfo> {
        int width;
        int height;
        String dev;

        public ResolutionInfo(String info) {
            dev = info;
            String sss = info.substring(0, info.indexOf('p'));
            String[] texts = sss.split("x");
            width = Integer.valueOf(texts[0]);
            height = Integer.valueOf(texts[1]);
        }

        public ResolutionInfo(int w, int h) {
            width = w;
            height = h;
            dev = String.format("%dx%dp60", w, h);
        }

        public String toString() {
            return dev;
        }

        public boolean isSame(ResolutionInfo ri) {
            if (width == ri.width && height == ri.height)
                return true;
            return false;
        }

        @Override
        public int compareTo(ResolutionInfo rhs) {
            if (this.width > rhs.width) {
                return -1;
            } else if (this.width == rhs.width) {
                if (this.height > rhs.height)
                    return -1;
                else if (this.height < rhs.height)
                    return 1;
                else {
                    if (dev != null) return -dev.compareTo(rhs.dev);
                    else return 0;
                }
            } else {
                return 1;
            }
        }
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

    @Override
    //显示-设置屏幕方向 传入0 90 180 270
    public void setDisplayOrientation(int rotation) {
        setSystemProperty("persist.ztl.hwrotation", String.valueOf(rotation));
        reboot(0);
    }

    //显示-设置HDMI分辨率  1
    @Override
    public void setScreenMode(String mode) {
        int sub_mode_pos = mode.indexOf('p');
        if (sub_mode_pos == -1)
            return;
        String raw_mode = mode.substring(0, sub_mode_pos);
        setSystemProperty("persist.sys.framebuffer.main", raw_mode);
        setSystemProperty("persist.sys.resolution.aux", mode);
        reboot(0);
    }

    //显示-获取屏幕分辨率 1
    @Override
    public String getDisplayMode() {
        try {
            String sss = loadFileAsString("/sys/class/drm/card0-HDMI-A-1/modes");
            return sss;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isValidResolution(ResolutionInfo ru) {
        List<ResolutionInfo> resolutionInfos = getHdmiResolu();
        for (ResolutionInfo ri : resolutionInfos) {
            if (ri.isSame(ru))
                return true;
        }

        return false;
    }

    //GPIO计算方式
    @Override
    public int gpioStringToInt(String port) {
        if (port.contains("GPIO") == false) {
            Log.e(TAG, "传入参数错误,请传入GPIO7_A5之类的，实际以规格书为准");
            return -1;
        }
        int A = port.charAt(4);
        int B = port.charAt(6);
        int C = port.charAt(7);
        int value = ((A - '0') & 0xff) * 32 + (B - 'A') * 8 + C - '0';
        return value;
    }

    List<ResolutionInfo> getHdmiResolu() {
        String sss = null;
        try {
            sss = loadFileAsString("/sys/class/drm/card0-HDMI-A-1/modes");
            String[] texts = sss.split("\n");

            List<ResolutionInfo> resolutionInfos = new ArrayList<>();
            for (int i = 0; i < texts.length; i++) {
                ResolutionInfo ri = new ResolutionInfo(texts[i]);
                resolutionInfos.add(ri);
            }
            Collections.sort(resolutionInfos);
            return resolutionInfos;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<ResolutionInfo>();
        }
    }

    List<String> getHdmiResolutions() {

        List<ResolutionInfo> resolutionInfos = getHdmiResolu();

        List<String> rets = new ArrayList<>();
        for (ResolutionInfo ri : resolutionInfos) {
            rets.add(ri.toString());
        }

        return rets;
    }

    //系统-设置OTG口连接状态
    @Override
    public void setUSBtoPC(boolean toPC) {
        if (toPC) {
            setSystemProperty("persist.usb.mode", "2");
            execRootCmdSilent("echo 2 > /sys/devices/platform/usb0/dwc3_mode");
        } else {
            setSystemProperty("persist.usb.mode", "1");
            execRootCmdSilent("echo 1 > /sys/devices/platform/usb0/dwc3_mode");
        }
//        if (toPC) {
//            execRootCmdSilent("echo otg > /sys/devices/platform/ff770000.syscon/ff770000.syscon:usb2-phy@e450/otg_mode");
//        } else {
//            execRootCmdSilent("echo host > /sys/devices/platform/ff770000.syscon/ff770000.syscon:usb2-phy@e450/otg_mode");
//        }
    }

    //系统-获取OTG口连接状态 //勾中的时候是2 不勾的时候是1
    @Override
    public boolean getUSBtoPC() {

            String state = getSystemProperty("persist.usb.mode", "");
            if (state.equals("2")) {
                return true;
            }

//        try {
//            String state = loadFileAsString("/sys/devices/platform/ff770000.syscon/ff770000.syscon:usb2-phy@e450/otg_mode");
//            if (state.contains("otg")) {
//                return true;
//            }else {
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return false;
    }
}

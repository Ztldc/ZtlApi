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

    class ResolutionInfo implements Comparable<ResolutionInfo>{
        int width;
        int height;
        String dev;

        public ResolutionInfo(String info){
            dev = info;
            String sss = info.substring( 0, info.indexOf('p'));
            String[] texts = sss.split("x");
            width = Integer.valueOf(texts[0]);
            height = Integer.valueOf(texts[1]);
        }

        public ResolutionInfo(int w, int h){
            width = w;
            height = h;
            dev = String.format("%dx%dp60", w, h);
        }

        public String toString(){
            return dev;
        }

        public boolean isSame(ResolutionInfo ri){
            if( width == ri.width && height == ri.height)
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
                    if(dev != null) return -dev.compareTo(rhs.dev);
                    else return 0;
                }
            } else {
                return 1;
            }
        }
    }

    //显示-设置HDMI分辨率		1
    @Override
    public void setScreenMode(String mode) {
        mode = mode.replace("@60p", "");
        String ssa[] = mode.split("x" );
        ResolutionInfo ri = new ResolutionInfo( Integer.valueOf(ssa[0]), Integer.valueOf(ssa[1]));

        if( isValidResolution(ri) == false){
            Log.e("设置分辨率错误", mode + "不是可用分辨率");
            return;
        }
        setSystemProperty("persist.sys.framebuffer.main", mode);
        setSystemProperty("persist.sys.resolution.aux", ri.toString());
        reboot(0);
    }

    //显示-获取屏幕分辨率	1
    @Override
    public String getDisplayMode() {
        String Mode = getSystemProperty("persist.sys.framebuffer.main", "0");
        return Mode += "@60p";
    }

    public boolean isValidResolution(ResolutionInfo ru)
    {
        List<ResolutionInfo> resolutionInfos = getHdmiResolu();
        for(ResolutionInfo ri :resolutionInfos){
            if( ri.isSame( ru ))
                return true;
        }

        return false;
    }

    List<ResolutionInfo> getHdmiResolu(){
        String sss = null;
        try {
            sss = loadFileAsString("/sys/class/drm/card0-HDMI-A-1/modes");
            String[] texts = sss.split("\n");

            List<ResolutionInfo> resolutionInfos = new ArrayList<>();
            for(int i = 0; i< texts.length; i++){
                ResolutionInfo ri = new ResolutionInfo( texts[i] );
                resolutionInfos.add( ri );
            }
            Collections.sort(resolutionInfos);
            return  resolutionInfos;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<ResolutionInfo>();
        }
    }

    List<String> getHdmiResolutions(){

        List<ResolutionInfo> resolutionInfos = getHdmiResolu();

        List<String> rets = new ArrayList<>();
        for( ResolutionInfo ri : resolutionInfos ){
            rets.add( ri.toString() );
        }

        return rets;
    }

    //系统-设置OTG口连接状态
    @Override
    public void setUSBtoPC(boolean toPC) {
        String value = toPC ? "2" : "1";
        setSystemProperty("persist.usb.mode", value);
        writeMethod("/sys/bus/platform/drivers/usb20_otg/force_usb_mode", value);
    }

    //系统-获取OTG口连接状态 //勾中的时候是2 不勾的时候是1
    @Override
    public boolean getUSBtoPC() {
        try {
            String state = loadFileAsString("/sys/bus/platform/drivers/usb20_otg/force_usb_mode");
            if (state.contains("2")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

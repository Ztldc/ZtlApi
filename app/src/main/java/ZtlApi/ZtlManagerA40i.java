package ZtlApi;

import android.os.SystemProperties;
import android.util.Log;

import java.io.File;

public class ZtlManagerA40i extends ZtlManager {

    private boolean DEBUG_ZTL = false;
    private String TAG = "ZtlManagerA40i";

    public ZtlManagerA40i() {
        //	init_gpiomap();
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
            File file = new File("/storage/udisk3");
            // 多个节点的时候
            // /storage/udisk3
            // /storage/udisk31
            //usbPath = getSystemProperty("persist.sys.usbDisk","unKnown");
            if (file.exists()) {
                usbPath = "/storage/udisk3";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return usbPath;
    }


    /*
        //增大音量，音量+1	1
        @Override
        public int setRaiseSystemVolume(){
            try {
             AudioManager am=(AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
             am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
             } catch (Exception e) {
                   e.printStackTrace();
                  return -1;
             }

              return 0;
        }

        //减小音量，音量-1	1
        @Override
        public int setLowerSystemVolume(){
            try {
                AudioManager am=(AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            } catch (Exception e) {
                   e.printStackTrace();
                  return -1;
             }

              return 0;
        }

        //设置开机自启动APP包名和Activity		1
        @Override
        public void setBootPackageActivity(String pkgName,String pkgActivity){
            if(pkgName != null && pkgActivity != null){
                //setSystemProperty("persist.sys.bootPkgName",pkgName);
                //setSystemProperty("persist.sys.bootPkgActivity",pkgActivity);
                execRootCmdSilent("setprop persist.sys.bootPkgName "+pkgName);
                execRootCmdSilent("setprop persist.sys.bootPkgActivity "+pkgActivity);
            }else{
                Log.e(TAG,"pkgName ("+pkgName+") or pkgActivity ("+pkgActivity+") err");
                return;
            }
            return;
        }

         //设置APP加密密钥
        public int setAppKey(String key){
            if(key != null){
                Intent systemBarIntent = new Intent("com.ztl.key");
                String str = key;
                systemBarIntent.putExtra("enable", str);
                mContext.sendBroadcast(systemBarIntent);
            }else{
                Log.e(TAG,"设置APP加密密钥值错误");
                return -1;
            }
            return 0;
        }

        //获取屏幕方向	1
        @Override
        public int getDisplayOrientation(){
            return 0;
        }

        //设置屏幕方向,设置完后重启系统
        @Override
        public void setDisplayOrientation(int rotation){
            Intent systemBarIntent = new Intent("com.ztl.rotation");
            int nowrotation = rotation;
            systemBarIntent.putExtra("enable", nowrotation);
            mContext.sendBroadcast(systemBarIntent);
        }

        //打开导航兰	1
        @Override
        public void setOpenSystemBar(){
            Intent systemBarIntent = new Intent("com.ztl.systembar");
            String str = "1";
            systemBarIntent.putExtra("enable", str);
            mContext.sendBroadcast(systemBarIntent);
        }

        //隐藏导航兰	1
        @Override
        public void setCloseSystemBar(){
            Intent systemBarIntent = new Intent("com.ztl.systembar");
            String str = "0";
            systemBarIntent.putExtra("enable", str);
            mContext.sendBroadcast(systemBarIntent);
        }
        */
    //设置分辨率		1
    @Override
    public void setScreenMode(String mode) {
        //	Intent systemBarIntent = new Intent("com.ztl.vga");
        //	String str = mode;
        //	systemBarIntent.putExtra("enable", str);
        //	mContext.sendBroadcast(systemBarIntent);
    }
/*
	//获取USB调试状态	1
	@Override
	public int getUsbDebugState(){
		return 0;
	}

	//获取状态栏状态	1
	@Override
	public int getSystemBarState(){
		return 0;
	}
*/

    @Override
    //GPIO计算方式 PE01 PE2 PE03 PG2 PG02
    public int gpioStringToInt(String port) {
        try {
            if (port.startsWith("P") == false) {
                Log.e(TAG, "传入参数错误,请传入PE7之类的，实际以规格书为准");
                return -1;
            }

            int iTemp = port.charAt(1) - 'A';
            if ((iTemp > 9) || (iTemp < 0)) {
                Log.e(TAG, "传入参数错误,请传入PE7之类的，实际以规格书为准");
                return -1;
            }

            int iTemp2 = Integer.parseInt(port.substring(2));
            if ((iTemp2 > 31) || (iTemp2 < 0)) {
                Log.e(TAG, "传入参数错误,请传入PE7之类的，实际以规格书为准");
                return -1;
            }
            return iTemp * 32 + iTemp2;
        } catch (Exception e) {
            // TODO: handle exception
            return -1;
        }

    }

}

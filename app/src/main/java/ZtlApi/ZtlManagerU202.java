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

        Intent systemBarIntent = new Intent("com.ztl.action.systembar");
        systemBarIntent.setPackage("com.android.systemui");
        systemBarIntent.putExtra("enable", bOpen);
        mContext.sendBroadcast(systemBarIntent);
    }

    //系统-获取当前导航栏状态 显示还是隐藏
    @Override
    public boolean isSystemBarOpen() {
        String state = getSystemProperty("persist.ztl.systembar", "true");
        return Boolean.valueOf(state);
    }
}

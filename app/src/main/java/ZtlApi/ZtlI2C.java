package ZtlApi;

//注意：本对象并非线程安全，也不支持实例化多个对象。因为c底层配置是全局变量。请把它当成一个文件对象，要么打开操作并及时关闭。 要么自己保存全局句柄。
//如需扩展，请自行修改代码。本公司不提供技术支持。如需额外技术支持，请联系业务洽谈。
//具体用法请参看MainActivity的用法

import android.util.Log;

public class ZtlI2C {
    static{
        try{
            System.loadLibrary("customi2c");
        }catch (Exception e){
            Log.e("ZtlI2C", "缺少so库，请联系智通利技术支持");
            e.printStackTrace();
        }
    }

    //addrLen：铁电存储器 看规格书。加密芯片：=1
    public boolean open(String filePath, int chipID,  int addrLen){
        this.filePath = filePath;
        this.chipID = chipID;
        return i2c_open( filePath, chipID, addrLen) > 0;
    }

    //铁电存储器专用写接口 参数：1.写入的地址 2.内容 3.内容长度
    public void flash_write(int addr, byte[]data, int ncount){
        i2c_write(addr, data, ncount);
    }

    //铁电存储器专用读接口 参数：1.读地址 2.读内容长度。 地址长度是1时，请不要传入大于255的值。否则一切后果自负
    public byte[] flash_read(int addr, int ncount){
        return  i2c_read(addr, ncount);
    }

    //加密芯片专用写接口。 cmd是你芯片里面程序代码的命令码
    public void chip_write(int cmd, byte[] data, int ncount){
        i2c_write(cmd, data, ncount);
    }

    //加密芯片专用读接口 参数：1.要读的内容，就是你芯片里面代码的命令码，根据命令码返回的那东西。 如果要直接读（比如你上次调用写后要直接获取返回），请传-1. 2.要读的个数
    //如果写了以后马上要读 最好间隔10ms或以上
    public  byte[] chip_read(int cmd, int ncount){
        return  i2c_read(cmd, ncount);
    }

    public void close(){
        i2c_close();
    }

    String filePath;//系统总线
    int chipID;//器件地址

    //参数1 节点名称
    //参数2 写入的i2c器件地址 注意不要跟系统的起冲突 否则会导致系统加密校验失败起不来
    //参数3 写入的地址位长度 比如RC16就是1位地址长度（支持读取8位地址。也就是你只能在read write参数1里传入0-255） RC128就是2位地址长度.具体看器件文档
    public native static int i2c_open(String path, int chipID, int addrlen);//返回FD。 大于0表示正常.地址位数。看器件说明书。
    public native static void i2c_close();
    public native static int i2c_write(int addr, byte[]data, int ncount);//返回IOCTRL结果。 大于0表示正常
    public native static byte[] i2c_read(int addr, int ncount);
}

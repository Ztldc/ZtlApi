package ZtlApi;


import android.os.Build;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.opengles.GL10;

import ZtlApi.ZtlManager;

public class CpuInfo {
    // /sys/devices/system/cpu/

    //CPU 当前频率（KHZ）
    //cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq
    //最大频率 （KHZ）
    //cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq
    //最小频率
    //cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq
    //CPU当前策略
    //cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor
    //echo "performance" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor
    //CPU 策略policy
    //cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors
//    ondemand 表示使用内核提供的功能，可以动态调节频率
//    userspace 表示用户模式，在此模式下允许其他用户程序调节CPU频率
//    powersvae 表示省电模式，通常是在最低频率下运行
//    interactive ondemand相似,规则是“快升慢降”
//    performance 表示不降频，最高性能
//    sched 基于调度器的 CPU 调频机制

    //cpu温度
    //3288_5.1  cat /sys/bus/platform/drivers/tsadc/ff280000.tsadc/temp1_input
    //3288_7.1 /sys/class/thermal/thermal_zone0 # cat temp 除以1000
    //cat /sys/class/hwmon/hwmon0/device/temp1_input

    //cat sys/bus/platform/drivers/tsadc/ff280000.tsadc/temp2_input   //GPU温度

    //使用率
//    总的Cpu使用率计算
//    计算方法：
//      1、  采样两个足够短的时间间隔的Cpu快照，分别记作t1,t2，其中t1、t2的结构均为：
//            (user、nice、system、idle、iowait、irq、softirq、stealstolen、guest)的9元组;
//
//      2、  计算总的Cpu时间片totalCpuTime
//      a)         把第一次的所有cpu使用情况求和，得到s1;
//      b)         把第二次的所有cpu使用情况求和，得到s2;
//      c)         s2 - s1得到这个时间间隔内的所有时间片，即totalCpuTime = j2 - j1 ;
//
//      3、计算空闲时间idle
//          idle对应第四列的数据，用第二次的idle - 第一次的idle即可
//            idle=第二次的idle - 第一次的idle
//
//      4、计算cpu使用率
//            pcpu =100* (total-idle)/total
//
//5、同理可以用同样的方法求出其他进程和线程所占cpu资源
//    参数 解释
//    user (432661) 从系统启动开始累计到当前时刻，用户态的CPU时间（单位：jiffies） ，不包含 nice值为负进程。1jiffies=0.01秒
//    nice (13295) 从系统启动开始累计到当前时刻，nice值为负的进程所占用的CPU时间（单位：jiffies）
//    system (86656) 从系统启动开始累计到当前时刻，核心时间（单位：jiffies）
//    idle (422145968) 从系统启动开始累计到当前时刻，除硬盘IO等待时间以外其它等待时间（单位：jiffies）
//    iowait (171474) 从系统启动开始累计到当前时刻，硬盘IO等待时间（单位：jiffies） ，
//    irq (233) 从系统启动开始累计到当前时刻，硬中断时间（单位：jiffies）
//    softirq (5346) 从系统启动开始累计到当前时刻，软中断时间（单位：jiffies）
//
//    CPU时间=user+system+nice+idle+iowait+irq+softirq

    //====================GPU==============================
    //cat /sys/devices/ffa30000.gpu/dvfs clock查看显卡频率 和占用率
    //dvfs  line2:gpu_utilisation : 99
    //      line3:current_gpu_clk_freq : 297 MHz
    // /sys/bus/platform/devices/ffa30000.gpu # cat gpuinfo
    //Mali-T76x MP4 r1p0 0x0750
    //================内存============================
    //cat /proc/meminfo

    public static class SubCore {
        public String name;
        public int max_freq;
        public int min_freq;
        public int cur_freq;
        public String governor;//调度策略
        public Object tag;//help me to save context
        DevType mdevType;

        //原始数据是KHZ 格式化成MHZ输出
        public String formatFreq() {
            int cfMhz = cur_freq / 1000;
            int mfMhz = max_freq / 1000;
            int minfMhz = min_freq / 1000;
            //800/(163-1800)Mhz
            if (cfMhz == 0 && (mdevType == DevType.dt_A33 || mdevType == DevType.dt_A64))
                return "休眠";
            else
                return String.format("%d/(%d-%d)Mhz", cfMhz, minfMhz, mfMhz);
        }

        public void Init(DevType devType) {
            mdevType = devType;
            String strVaule = getOnelinevalue(String.format("/sys/devices/system/cpu/%s/cpufreq/cpuinfo_max_freq", name));
            if (strVaule != null)
                max_freq = Integer.valueOf(strVaule);
            else
                max_freq = 0;

            strVaule = getOnelinevalue(String.format("/sys/devices/system/cpu/%s/cpufreq/cpuinfo_min_freq", name));
            if (strVaule != null)
                min_freq = Integer.valueOf(strVaule);
            else
                min_freq = 0;

            if (devType == DevType.dt_A33 || devType == DevType.dt_A64)
                ZtlManager.GetInstance().execRootCmdSilent("chmod 777 " + String.format("/sys/devices/system/cpu/%s/cpufreq/scaling_governor", name));

            governor = getOnelinevalue(String.format("/sys/devices/system/cpu/%s/cpufreq/scaling_governor", name));
        }

        public void Speedup() {
            //conservative //ondemand //per //performance
            String fmt = String.format("echo \"performance\" > /sys/devices/system/cpu/%s/cpufreq/scaling_governor", name);
            ZtlManager.GetInstance().execRootCmdSilent(fmt);
        }

        public static String[] getFreq() {
            String oneLine = getOnelinevalue("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
            return oneLine.split(" ");
        }

        public void SetFreq(String freq) {
            String fmt = String.format("echo \"userspace\" > /sys/devices/system/cpu/%s/cpufreq/scaling_governor", name);
            ZtlManager.GetInstance().execRootCmdSilent(fmt);

            String fmt1 = String.format("echo %s > /sys/devices/system/cpu/%s/cpufreq/scaling_setspeed", freq, name);
            ZtlManager.GetInstance().execRootCmdSilent(fmt1);
        }

        public void Restore() {
            String fmt = String.format("echo \"interactive\" > /sys/devices/system/cpu/%s/cpufreq/scaling_governor", name);
            ZtlManager.GetInstance().execRootCmdSilent(fmt);
        }

        public static boolean isExist(String string) {
            File mFile = new File(string);
            return mFile.exists();
        }

        public void Update(boolean updateGover) {
            if (isExist(String.format("/sys/devices/system/cpu/%s/cpufreq/scaling_cur_freq", name))) {
                String strVaule = getOnelinevalue(String.format("/sys/devices/system/cpu/%s/cpufreq/scaling_cur_freq", name));
                if (strVaule != null)
                    cur_freq = Integer.valueOf(strVaule);
                else
                    cur_freq = 0;
            } else
                cur_freq = 0;

            if (updateGover) {
                governor = getOnelinevalue(String.format("/sys/devices/system/cpu/%s/cpufreq/scaling_governor", name));
            }
        }
    }

    public List<SubCore> mCores = new ArrayList<>();

    public int cpuTemp;//cpu温度
    public int gpuTemp;//gpu温度
    public float usage;//cpu利用率

    public String gpuName;//gpu 型号
    public int gpuFreq;//in MHz
    public int gpuMaxFreq;//in KHz
    public int gpu_utilisation;//负载

    public int memTotal;//in KB
    public int memFree;//in KB

    enum DevType {
        dt_3288_5_1,
        dt_3288_7_1,
        dt_3399,
        dt_3328,
        dt_3128,
        dt_3368,
        dt_A33,
        dt_A64,
        dt_3568

    }

    DevType devType = DevType.dt_3288_5_1;//3288

    abstract class BaseDev {
        public String gpu_base_path = "";
        public String temp_base_path = "";

        abstract String getGPUName();

        abstract int getCPUTemp();

        abstract int getGPUTemp();

        abstract int getGPUMaxFreq();//in KHz

        abstract int getGPUUtilisation();

        abstract int getGPUCurfreq();

        abstract void speedUp();
    }

    class BD3288_51 extends BaseDev {
        BD3288_51() {
            gpu_base_path = "/sys/bus/platform/devices/ffa30000.gpu/";
            temp_base_path = "/sys/bus/platform/drivers/tsadc/ff280000.tsadc/";
            // 小邵说这可以从这里获取温度 /sys/class/hwmon/hwmon0/device/temp1_input
        }

        String getGPUName() {
            return getOnelinevalue(gpu_base_path + "gpuinfo");
        }

        void speedUp() {
        }

        int getGPUMaxFreq() {
            List<String> lines = getLines(gpu_base_path + "clock", 2);
            if (lines != null && lines.size() >= 2) {
                String gpu_max_freq = lines.get(1);
                String[] gpufreqs = gpu_max_freq.split(",");
                String lastFreq = gpufreqs[gpufreqs.length - 1];
                if (lastFreq.contains("KHz")) {
                    lastFreq = lastFreq.replace("possible_freqs :", "");
                    lastFreq = lastFreq.trim();
                    String[] freqs = lastFreq.split(" ");
                    return Integer.valueOf(freqs[0]);
                    //Log.e("adsf", "adsf");
                }
            }

            return 0;
        }

        int getCPUTemp() {
            String value = getOnelinevalue(temp_base_path + "temp1_input");
            if (value != null)
                return Integer.valueOf(value);

            return -1;
        }

        int getGPUTemp() {
            String value = getOnelinevalue(temp_base_path + "temp2_input");
            if (value != null)
                return Integer.valueOf(value);

            return -1;
        }

        int gpuFreq0 = 0;

        int getGPUUtilisation() {
            int gpu_utilisation0 = 0;
            List<String> lines = getLines(gpu_base_path + "dvfs", 3);
            if (lines.size() >= 3) {
                String utilisation = lines.get(1);
                if (utilisation.contains("gpu_utilisation")) {
                    utilisation = utilisation.replace("gpu_utilisation :", "");
                    utilisation = utilisation.trim();
                    //String []utilisations = utilisation.split(" ");
                    gpu_utilisation0 = Integer.valueOf(utilisation);
                }

                //当前gpu 频率
                String cur_freq = lines.get(2);
                if (cur_freq.contains("current_gpu_clk_freq")) {
                    cur_freq = cur_freq.replace("current_gpu_clk_freq : ", "");
                    cur_freq = cur_freq.replace("MHz", "");
                    cur_freq = cur_freq.trim();
                    gpuFreq0 = Integer.valueOf(cur_freq);
                    //Log.e("adsf", "adsf");
                }
            } else if (lines.size() >= 2) {//line 1 current_gpu_clk_freq : 480 MHz
                String cur_freq = lines.get(1);
                if (cur_freq.contains("current_gpu_clk_freq")) {
                    cur_freq = cur_freq.replace("current_gpu_clk_freq : ", "");
                    cur_freq = cur_freq.replace("MHz", "");
                    cur_freq = cur_freq.trim();
                    //Log.e("adsf", "adsf");
                    gpuFreq0 = Integer.valueOf(cur_freq);
                }
            }

            return gpu_utilisation0;
        }

        int getGPUCurfreq() {
            return gpuFreq0;
        }
    }

    class BD3288_71 extends BaseDev {
        BD3288_71() {
            gpu_base_path = "/sys/bus/platform/devices/ffa30000.gpu/";
            temp_base_path = "/sys/bus/platform/drivers/tsadc/ff280000.tsadc/";
        }

        String getGPUName() {
            return getOnelinevalue(gpu_base_path + "gpuinfo");
        }

        void speedUp() {
        }


        int getGPUMaxFreq() {
            String oneLine = getOnelinevalue(gpu_base_path + "devfreq/ffa30000.gpu/available_frequencies");
            String[] gpufreqs = oneLine.split(" ");
            long aaa = Long.valueOf(gpufreqs[gpufreqs.length - 1]);
            aaa /= 1000;
            return (int) aaa;
        }

        public int getCPUTemp() {
            String value = getOnelinevalue("/sys/class/thermal/thermal_zone0/temp");
            if (value != null)
                return Integer.valueOf(value) / 1000;

            return -1;
        }

        int getGPUTemp() {
            String value = getOnelinevalue("/sys/class/thermal/thermal_zone1/temp");
            if (value != null)
                return Integer.valueOf(value) / 1000;

            return -1;
        }

        int gpuFreq0 = 0;

        int getGPUUtilisation() {
            String value = getOnelinevalue(gpu_base_path + "utilisation");
            if (value != null)
                return Integer.valueOf(value);

            return -1;
        }

        int getGPUCurfreq() {
            String value = getOnelinevalue(gpu_base_path + "devfreq/ffa30000.gpu/cur_freq");
            if (value != null) {
                long l = Long.valueOf(value);
                l /= 1000000;
                return (int) l;
            }

            return -1;
        }
    }

    class BD3399 extends BaseDev {
        BD3399() {
            gpu_base_path = "/sys/bus/platform/devices/ff9a0000.gpu/";
            ///sys/bus/platform/devices/ff9a0000.gpu/devfreq/ff9a0000.gpu $
            temp_base_path = "/sys/class/thermal/thermal_zone0/temp/";
        }

        void speedUp() {
            //rk3399_all:/sys/bus/platform/devices/ff9a0000.gpu/devfreq/ff9a0000.gpu $ cat governor
            //performance
            String fmt = String.format("echo \"performance\" > /sys/bus/platform/devices/ff9a0000.gpu/devfreq/ff9a0000.gpu/governor");
            ZtlManager.GetInstance().execRootCmdSilent(fmt);
        }

        String getGPUName() {
            return getOnelinevalue(gpu_base_path + "gpuinfo");
        }

        //in KHz
        int getGPUMaxFreq() {
            //rk3399_all:/sys/bus/platform/devices/ff9a0000.gpu/devfreq/ff9a0000.gpu $ cat available_freq
            //200000000 300000000 400000000 600000000 800000000
            String oneLine = getOnelinevalue(gpu_base_path + "devfreq/ff9a0000.gpu/available_frequencies");
            String[] gpufreqs = oneLine.split(" ");
            long aaa = Long.valueOf(gpufreqs[gpufreqs.length - 1]);
            aaa /= 1000;
            return (int) aaa;
        }

        //3399的温度 拿不到 别折腾了
        int getCPUTemp() {
            //String value = getOnelinevalue("/sys/class/thermal/thermal_zone0/temp");
            //if( value != null)
            //   return Integer.valueOf( value );

            return -1;
        }

        int getGPUTemp() {
            //String value = getOnelinevalue("/sys/class/thermal/thermal_zone1/temp");
            //if( value != null)
            //   return Integer.valueOf( value );

            return -1;
        }

        int getGPUUtilisation() {
            //3399的gpu负载cat /sys/bus/platform/devices/ff9a0000.gpu/utilisation
            String value = getOnelinevalue(gpu_base_path + "utilisation");
            if (value != null)
                return Integer.valueOf(value);

            return -1;
        }

        // cat /sys/bus/platform/devices/ff9a0000.gpu/devfreq/ff9a0000.gpu/cur_freq inHZ
        int getGPUCurfreq() {
            String value = getOnelinevalue(gpu_base_path + "devfreq/ff9a0000.gpu/cur_freq");
            if (value != null) {
                long l = Long.valueOf(value);
                l /= 1000000;
                return (int) l;
            }

            return -1;
        }

    }

    class BD3328 extends BD3399 {
        BD3328() {
            gpu_base_path = "/sys/bus/platform/devices/ff300000.gpu/";
            ///sys/bus/platform/devices/ff9a0000.gpu/devfreq/ff9a0000.gpu $
            temp_base_path = "/sys/class/thermal/thermal_zone0/temp/";
        }

        void speedUp() {
        }

        @Override
        String getGPUName() {
            return "";
        }

        @Override
        int getGPUTemp() {
            return -1;
        }

        @Override
        public int getCPUTemp() {
            int temp = super.getCPUTemp();
            return temp / 1000;
        }

        @Override
        int getGPUUtilisation() {
            return super.getGPUUtilisation() / 256 * 100;
        }

        int getGPUMaxFreq() {
            //rk3399_all:/sys/bus/platform/devices/ff9a0000.gpu/devfreq/ff9a0000.gpu $ cat available_freq
            //200000000 300000000 400000000 600000000 800000000
            List<String> lines = getLines(gpu_base_path + "available_frequencies", 10);
            String strMaxFreq = lines.get(lines.size() - 1);
            long aaa = Long.valueOf(strMaxFreq);
            aaa /= 1000;
            return (int) aaa;
        }

        int getGPUCurfreq() {
            String value = getOnelinevalue(gpu_base_path + "clock");
            if (value != null) {
                long l = Long.valueOf(value);
                l /= 1000000;
                return (int) l;
            }

            return -1;
        }
    }

    class BD3128 extends BD3328 {
        BD3128() {
            gpu_base_path = "/sys/bus/platform/devices/10091000.gpu/";
            ///sys/bus/platform/devices/ff9a0000.gpu/devfreq/ff9a0000.gpu $
            //temp_base_path = "/sys/class/thermal/thermal_zone0/temp/";
        }

        void speedUp() {
        }

        @Override
        String getGPUName() {
            return "Mali-400MP2";
        }

        @Override
        int getGPUTemp() {
            return -1;
        }

        @Override
        public int getCPUTemp() {
            return -1;
        }

        @Override
        int getGPUMaxFreq() {

            ZtlManager.GetInstance().execRootCmdSilent("chmod 777 " + gpu_base_path + "available_frequencies");
            ZtlManager.GetInstance().execRootCmdSilent("chmod 777 " + gpu_base_path + "clock");

            //rk3399_all:/sys/bus/platform/devices/ff9a0000.gpu/devfreq/ff9a0000.gpu $ cat available_freq
            //200000000 300000000 400000000 600000000 800000000
            List<String> lines = getLines(gpu_base_path + "available_frequencies", 10);
            String strMaxFreq = lines.get(lines.size() - 1);
            long aaa = Long.valueOf(strMaxFreq);
            aaa /= 1000;
            return (int) aaa;
        }
    }

    class BD3368 extends BaseDev {

        BD3368() {
            gpu_base_path = "/sys/devices/platform/ffa30000.rogue-g6110/devfreq/ffa30000.rogue-g6110/";
        }

        @Override
        String getGPUName() {
            return "PowerVR G6110";
        }

        void speedUp() {
        }

        @Override
        public int getCPUTemp() {
            String value = getOnelinevalue("/sys/class/thermal/thermal_zone0/temp");
            if (value != null)
                return Integer.valueOf(value) / 1000;

            return -1;
        }

        @Override
        int getGPUTemp() {
            String value = getOnelinevalue("/sys/class/thermal/thermal_zone1/temp");
            if (value != null)
                return Integer.valueOf(value) / 1000;

            return -1;
        }

        @Override
        int getGPUMaxFreq() {//也可以cat  max_freq
            //same like 3399
            String oneLine = getOnelinevalue(gpu_base_path + "available_frequencies");
            String[] gpufreqs = oneLine.split(" ");
            long aaa = Long.valueOf(gpufreqs[gpufreqs.length - 1]);
            aaa /= 1000;
            return (int) aaa;
        }

        @Override
        int getGPUUtilisation() {
            //rk3368H_64:/sys/devices/platform/ffa30000.rogue-g6110/devfreq/ffa30000.rogue-g6110 # cat load
            //0@200000000Hz
            String value = getOnelinevalue(gpu_base_path + "load");
            if (value != null) {
                int nLaod = value.indexOf('@');
                value = value.substring(0, nLaod);

                return Integer.valueOf(value);
            }

            return -1;
        }

        @Override
        int getGPUCurfreq() {
            String value = getOnelinevalue(gpu_base_path + "cur_freq");
            if (value != null) {
                long l = Long.valueOf(value);
                l /= 1000000;
                return (int) l;
            }

            return -1;
        }
    }

    class BDA33 extends BaseDev {

        void speedUp() {
        }

        @Override
        String getGPUName() {
            return "Mali-400 MP2";
        }

        @Override
        public int getCPUTemp() {
            return -1;
        }

        @Override
        int getGPUTemp() {
            return -1;
        }

        @Override
        int getGPUMaxFreq() {
            // /sys/kernel/debug/clk/hosc/pll_gpu/gpu # cat clk_rate
            String value = getOnelinevalue("/sys/kernel/debug/clk/hosc/pll_gpu/gpu/clk_rate");
            if (value != null) {
                long l = Long.valueOf(value);
                l /= 1000;
                return (int) l;
            }
            return -1;
        }

        @Override
        int getGPUUtilisation() {
            return -1;
        }

        @Override
        int getGPUCurfreq() {
            return -1;
        }
    }

    class BDA64 extends BaseDev {

        void speedUp() {
        }

        @Override
        String getGPUName() {
            return "Mali-400 MP2";
        }

        @Override
        public int getCPUTemp() {
            //root@tulip-p1_v1:/sys/class/thermal/thermal_zone0 # cat temp
            //52
            String value = getOnelinevalue("/sys/class/thermal/thermal_zone0/temp");
            if (value != null)
                return Integer.valueOf(value);

            return -1;
        }

        @Override
        int getGPUTemp() {
            // /sys/bus/platform/drivers/mali-utgard/1c40000.gpu/dvfs/tempctrl
            //sensor: 2, status: 1, temperature: 43
            String value = getOnelinevalue("/sys/bus/platform/drivers/mali-utgard/1c40000.gpu/dvfs/tempctrl");
            value = value.substring(value.indexOf("temperature:"));
            value = value.replace("temperature: ", "");
            value = value.trim();

            if (value != null) {
                int l = Integer.valueOf(value);
                return l;
            }
            return -1;
        }

        @Override
        int getGPUMaxFreq() {
            //480 MHz
            String value = getOnelinevalue("/sys/bus/platform/drivers/mali-utgard/1c40000.gpu/dvfs/android");
            value = value.replace("MHz", "");
            value = value.trim();

            if (value != null) {
                long l = Long.valueOf(value);
                l *= 1000;
                return (int) l;
            }
            return -1;
        }

        @Override
        int getGPUUtilisation() {
            return -1;
        }

        @Override
        int getGPUCurfreq() {
            return -1;
        }
    }

    class BD3568 extends BaseDev {

        void speedUp() {
        }

        @Override
        String getGPUName() {
            return "Mali-G52";
        }

        @Override
        public int getCPUTemp() {
            String value = getOnelinevalue("/sys/class/thermal/thermal_zone0/temp");
            if (value != null)
                return Integer.valueOf(value);

            return -1;
        }

        @Override
        int getGPUTemp() {
            // /sys/bus/platform/drivers/mali-utgard/1c40000.gpu/dvfs/tempctrl
            //sensor: 2, status: 1, temperature: 43
//            String value = getOnelinevalue("/sys/bus/platform/drivers/mali-utgard/1c40000.gpu/dvfs/tempctrl");
//            value = value.substring(value.indexOf("temperature:"));
//            value = value.replace("temperature: ", "");
//            value = value.trim();
//
//            if (value != null) {
//                int l = Integer.valueOf(value);
//                return l;
//            }
            return -1;
        }

        @Override
        int getGPUMaxFreq() {
            //480 MHz
            String value = getOnelinevalue("/sys/bus/platform/drivers/mali/fde60000.gpu/devfreq/fde60000.gpu/max_freq");
            value = value.replace("MHz", "");
            value = value.trim();

            if (value != null) {
                long l = Long.valueOf(value);
                l *= 1000;
                return (int) l;
            }
            return -1;
        }

        @Override
        int getGPUUtilisation() {
            return -1;
        }

        @Override
        int getGPUCurfreq() {
            return -1;
        }
    }

    BaseDev curDev = null;

    public interface Eventhandler {
        void OnInitOK(CpuInfo ci);

        void OnUpdate();
    }

    Eventhandler eventhandler = null;

    public void setCPUFreq(String freq) {
        for (int i = 0; i < mCores.size(); i++)
            mCores.get(i).SetFreq(freq);
    }

    public void Init(Eventhandler evt) {
        eventhandler = evt;
        String[] cpu_names = getCpuCount();

        String devTypeStr = ZtlManager.GetInstance().getDeviceVersion();
        if (devTypeStr.contains("3399")) {
            devType = DevType.dt_3399;
            curDev = new BD3399();
        } else if (devTypeStr.contains("3288")) {
            String sys_v = ZtlManager.GetInstance().getAndroidVersion();
            if (sys_v.contains("5.1")) {
                devType = DevType.dt_3288_5_1;
                curDev = new BD3288_51();
            } else if (sys_v.contains("7.1")) {
                devType = DevType.dt_3288_7_1;
                curDev = new BD3288_71();
            }
        } else if (devTypeStr.contains("3328")) {
            devType = DevType.dt_3328;
            curDev = new BD3328();
        } else if (devTypeStr.contains("3126c") || devTypeStr.contains("3128")) {
            devType = DevType.dt_3128;
            curDev = new BD3128();
        } else if (devTypeStr.contains("3368")) {
            devType = DevType.dt_3368;
            curDev = new BD3368();
        } else if (devTypeStr.contains("A33")) {
            devType = DevType.dt_A33;
            curDev = new BDA33();
        } else if (devTypeStr.contains("A64")) {
            devType = DevType.dt_A64;
            curDev = new BDA64();
        } else if (devTypeStr.contains("3568")){
            devType = DevType.dt_3568;
            curDev = new BD3568();
        }
        if (curDev == null) {
            curDev = new BD3288_51();
        }

        for (int i = 0; i < cpu_names.length; i++) {
            SubCore soc = new SubCore();
            soc.name = cpu_names[i];
            soc.Init(devType);
            if ((devType == DevType.dt_A33 || devType == DevType.dt_A64) && i >= 1) {//A33休眠的时候无法获取最大最小值 。所以取第一个cpud的
                soc.min_freq = mCores.get(0).min_freq;
                soc.max_freq = mCores.get(0).max_freq;
                soc.governor = mCores.get(0).governor;
            }
            mCores.add(soc);
        }
        ///
        gpuName = curDev.getGPUName();
        gpuMaxFreq = curDev.getGPUMaxFreq();

        String strMemTotal = getOnelinevalue("/proc/meminfo");
        if (strMemTotal.contains("MemTotal")) {
            strMemTotal = strMemTotal.replace("MemTotal:", "");
            strMemTotal = strMemTotal.trim();
            String[] memtos = strMemTotal.split(" ");
            memTotal = Integer.valueOf(memtos[0]);
        }

        if (eventhandler != null)
            eventhandler.OnInitOK(this);

       /* new Thread(new Runnable() {
            @Override
            public void run() {
                while (bexit== false){
                    for(int i = 0; i< mCores.size(); i++){
                        SubCore soc = mCores.get(i);
                        soc.Update(false );
                    }

                    cpuTemp = curDev.getCPUTemp();
                    gpuTemp = curDev.getGPUTemp();
                    usage = getRate();

                    //gpu 负载
                    gpu_utilisation = curDev.getGPUUtilisation();
                    gpuFreq = curDev.getGPUCurfreq();

                    //List<String> linesMem = getSpecail("du", 3 );
                    try {
                       String asu = ZtlManager.GetInstance().execCMD("dumpsys meminfo");
                        //String asu = _execCmdAsSUString("su","dumpsys meminfo");
                        int indexof = asu.indexOf("Free RAM:");
                        int endline = asu.indexOf("\n", indexof + 1);
                        asu = asu.substring(indexof, endline);
                        asu = asu.replace("Free RAM:","");
                        asu = asu.trim();
                        String ass[] = asu.split(" ");
                        String av = ass[0];
                        av = av.replace(",","");
                        av = av.replace("K","");
                        av = av.replace("k","");
                        memFree = Integer.valueOf( av );

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*Process p = null;
                    try {
                        p = Runtime.getRuntime().exec("su dumpsys meminfo");
                        InputStream is = p.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        String line;
                        while((line = reader.readLine())!= null){
                            System.out.println(line);
                        }
                        p.waitFor();
                        is.close();
                        reader.close();
                        p.destroy();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }


                    if( linesMem.size() >2){
                        String memfreeLine = linesMem.get(1);
                        if(memfreeLine.contains("MemFree:")){
                            memfreeLine = memfreeLine.replace("MemFree:","");
                            memfreeLine = memfreeLine.trim();
                            String []memtos = memfreeLine.split(" ");
                            memFree = Integer.valueOf( memtos[0] );
                        }
                    }

                    if( eventhandler != null )
                        eventhandler.OnUpdate( );

                    try {
                        Thread.sleep(700 );//因为getRate也停了300。所以凑够1s
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }

    public void SpeedUp() {
        for (int i = 0; i < mCores.size(); i++)
            mCores.get(i).Speedup();

        if (curDev != null)
            curDev.speedUp();
    }

    public void Restore() {
        for (int i = 0; i < mCores.size(); i++)
            mCores.get(i).Restore();
    }

    boolean bexit = false;

    public void close() {
        bexit = true;
    }

    //获取CPU使用率
    float getRate() {
        Map<String, String> map1 = getCPUUsageSnap();
        long totalTime1 = Long.parseLong(map1.get("user")) + Long.parseLong(map1.get("nice"))
                + Long.parseLong(map1.get("system")) + Long.parseLong(map1.get("idle"))
                + Long.parseLong(map1.get("iowait")) + Long.parseLong(map1.get("irq"))
                + Long.parseLong(map1.get("softirq"));//获取totalTime1
        long idleTime1 = Long.parseLong(map1.get("idle"));//获取idleTime1
        try {
            Thread.sleep(360);//等待360ms
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> map2 = getCPUUsageSnap();
        long totalTime2 = Long.parseLong(map2.get("user")) + Long.parseLong(map2.get("nice"))
                + Long.parseLong(map2.get("system")) + Long.parseLong(map2.get("idle"))
                + Long.parseLong(map2.get("iowait")) + Long.parseLong(map2.get("irq"))
                + Long.parseLong(map2.get("softirq"));//获取totalTime2
        long idleTime2 = Long.parseLong(map2.get("idle"));//获取idleTime2
        float cpuRate = 100 * ((totalTime2 - totalTime1) - (idleTime2 - idleTime1)) / (totalTime2 - totalTime1);
        return cpuRate;
    }

    //获取CPU使用率-快照
    static Map<String, String> getCPUUsageSnap() {
        String[] cpuInfos = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")));//读取CPU信息文件
            String load = br.readLine();
            br.close();
            cpuInfos = load.split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user", cpuInfos[2]);
        map.put("nice", cpuInfos[3]);
        map.put("system", cpuInfos[4]);
        map.put("idle", cpuInfos[5]);
        map.put("iowait", cpuInfos[6]);
        map.put("irq", cpuInfos[7]);
        map.put("softirq", cpuInfos[8]);
        return map;
    }

    public static String[] getCpuCount() {
        List<String> cpu_count = new ArrayList<String>();

        File file = new File("/sys/devices/system/cpu");
        File[] fs = file.listFiles();
        for (File f : fs) {
            //System.out.println(f.getName());
            if (f.getName().contains("cpu")) {
                char c = f.getName().charAt(3);
                if (c < '0' || c > '9')
                    continue;
                else {
                    cpu_count.add(f.getName());
                }
            }
        }
        String[] strs1 = cpu_count.toArray(new String[cpu_count.size()]);
        return strs1;
    }

    static String getOnelinevalue(String path) {
        List<String> astring = getLines(path, 1);
        if (astring != null)
            return astring.get(0);
        else
            return null;
    }

    static List<String> getLines(String path, int lineCount) {
        FileReader fr = null;
        BufferedReader br = null;
        List<String> lines = new ArrayList<>();
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);

            String text;
            int curLine = 0;
            while (curLine < lineCount) {
                text = br.readLine();
                if (text == null)
                    break;

                curLine++;
                lines.add(text);
            }

            fr.close();
            fr = null;
            return lines;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    static String getSpecail(String path, String key) {
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);

            String text = br.readLine();

            while (text != null) {
                if (text.contains(key))
                    return text;
                text = br.readLine();
                if (text == null)
                    break;

            }

            fr.close();
            fr = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

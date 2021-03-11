package ZtlApi;

import java.util.HashMap;

public class GpioName {

    public static final String rk3288[] = {"", "GPIO0_C2", "GPIO7_B5", "GPIO8_B0", "GPIO7_B4", "GPIO7_C5", "GPIO7_B3", "GPIO8_A2", "GPIO7_A6", "GPIO8_A1", "GPIO7_A5"};

    public static final String rk3126c[] = {"", "GPIO1_C7", "GPIO2_A2", "GPIO1_A7", "GPIO2_A0", "GPIO2_A1", "GPIO0_C7", "GPIO2_A6", "GPIO2_A3", "GPIO1_A3", "GPIO3_C5"};
    public static final String CQA64[] = {"", "PE7", "PE4", "PE3", "PE2", "PE1"};
    //public static final String rk3328[] = {"GPIO3_A0"};
    public static final String rk3368[] = {"", "GPIO1_A2", "GPIO1_B6", "GPIO1_A3", "GPIO1_B7", "GPIO1_A4", "GPIO1_C0", "GPIO1_A5", "GPIO1_C1", "GPIO1_A6", "GPIO1_A7"};
    public static final String rk3399[] = {"", "GPIO2_B2", "GPIO2_A5", "GPIO2_A3", "GPIO2_A1", "GPIO2_A6", "GPIO2_B0", "GPIO2_A4", "GPIO2_B1", "GPIO2_B4","GPIO2_A0","GPIO2_A7","GPIO2_A2"};
//    public static final String rk3399[] = {"GPIO1_D0", "GPIO1_A0", "GPIO1_A2", "GPIO1_C6", "GPIO2_A5", "GPIO2_A3", "GPIO2_A6", "GPIO2_A4", "GPIO2_A2"};

    public static final HashMap<String, String[]> GpioNameMap = new HashMap<String, String[]>(){{
        put("rk3288",rk3288);
        put("rk3126",rk3126c);
        put("A64",CQA64);
        //put("rk3328",rk3328);
        put("rk3368",rk3368);
        put("rk3399",rk3399);
    }};

}

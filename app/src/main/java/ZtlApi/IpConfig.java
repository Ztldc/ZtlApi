package ZtlApi;

import java.io.Serializable;

class IpConfig implements Serializable {
    public enum IP_MODE {
        DHCP,
        STATIC
    }

    public String ipAddress;
    public String netMask;
    public String gateway;
    public String dns1;
    public String dns2;
    public IP_MODE mode;
}
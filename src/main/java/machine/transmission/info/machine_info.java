package machine.transmission.info;

public class machine_info {

    public static String BaudRate = "300";
    public static String Parity = "Even";
    public static String DataBits = "7";
    public static String StopBits = "2";
    public static String Timeout = "OFF";
    public static String xonxof = "ON";
    public static String COM  ="COM3";

    public static String getBaudRate() {
        return BaudRate;
    }

    public static String getParity() {
        return Parity;
    }

    public static String getDataBits() {
        return DataBits;
    }

    public static String getStopBits() {
        return StopBits;
    }

    public static String getTimeout() {
        return Timeout;
    }

    public static String getXonxof() {
        return xonxof;
    }

    public static String getCOM() {
        return COM;
    }

    public static void setBaudRate(String baudRate) {
        BaudRate = baudRate;
    }

    public static void setParity(String parity) {
        Parity = parity;
    }

    public static void setDataBits(String dataBits) {
        DataBits = dataBits;
    }

    public static void setStopBits(String stopBits) {
        StopBits = stopBits;
    }

    public static void setTimeout(String timeout) {
        Timeout = timeout;
    }

    public static void setXonxof(String xonxof) {
        machine_info.xonxof = xonxof;
    }

    public static void setCOM(String COM) {
        machine_info.COM = COM;
    }
}

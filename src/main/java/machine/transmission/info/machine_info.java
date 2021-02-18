package machine.transmission.info;

import ComPortSettings.ComPortSetting;

public class machine_info {

    public static String BaudRate = "9200";
    public static ComPortSetting.Parities Parity = ComPortSetting.Parities.EVEN;
    public static String DataBits = "7";
    public static String StopBits = "2";
    public static String Timeout = "OFF";
    public static ComPortSetting.FlowControll xonxof = ComPortSetting.FlowControll.XONXOFF_IN;
    public static String COM  ="COM3";

    public static String getBaudRate() {
        return BaudRate;
    }

    public static ComPortSetting.Parities getParity() {
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

    public static ComPortSetting.FlowControll getXonxof() {
        return xonxof;
    }

    public static String getCOM() {
        return COM;
    }

    public static void setBaudRate(String baudRate) {
        BaudRate = baudRate;
    }

    public static void setParity(ComPortSetting.Parities parity) {
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

    public static void setXonxof(ComPortSetting.FlowControll flow) {
        xonxof = flow;
    }

    public static void setCOM(String COM) {
        machine_info.COM = COM;
    }
}

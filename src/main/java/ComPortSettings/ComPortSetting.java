package ComPortSettings;
import java.util.ArrayList;
import java.util.List;

public class ComPortSetting {

    private List<String> BaudRateList;
    private List<String> DataBitsList;
    private List<String> ParityList;
    private List<String> StopBitsList;
    private List<String> FlowcontrollList;
    
    public ComPortSetting()
    {
        // Initialize Lists
        BaudRateList = new ArrayList<>();
        DataBitsList = new ArrayList<>();
        ParityList = new ArrayList<>();
        StopBitsList = new ArrayList<>();
        FlowcontrollList = new ArrayList<>();
        
        // call basic functionalities;
        getBaud();
        getDataBit();
        getParity();
        getStopBits();
        getFLow();
        
    }

    private void getFLow() {
        FlowcontrollList.add("Xon-Xoff");
        FlowcontrollList.add("None");
    }

    private void getStopBits() {
        StopBitsList.add("1");
        StopBitsList.add("1,5");
        StopBitsList.add("2");

    }

    private void getParity() {

        ParityList.add("Even");
        ParityList.add("Odd");
        ParityList.add("None");

    }

    private void getDataBit() {

        DataBitsList.add("7");
        DataBitsList.add("8");
    }

    private void getBaud() {

        BaudRateList.add("300");
        BaudRateList.add("600");
        BaudRateList.add("1200");
        BaudRateList.add("2400");
        BaudRateList.add("4800");
        BaudRateList.add("7200");
        BaudRateList.add("9600");
        BaudRateList.add("14400");
        BaudRateList.add("19200");
    }


    public List<String> getBaudRateList() {
        return BaudRateList;
    }

    public List<String> getDataBitsList() {
        return DataBitsList;
    }

    public List<String> getParityList() {
        return ParityList;
    }

    public List<String> getStopBitsList() {
        return StopBitsList;
    }

    public List<String> getFlowcontrollList() {
        return FlowcontrollList;
    }
}

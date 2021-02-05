package ConstantFile;
import org.ini4j.Wini;

import java.io.*;
import java.net.URISyntaxException;



public class ConstantFile {

    private static ConstantFile INSTANCE;

    Wini ini;
    String BAUDRATE;
    String PARITY;
    String DATABITS;
    String STOPBITS;
    String TIMEOUT;
    String FLOWCONTROLL;

    public String getPARITY() {
        return PARITY;
    }
    public String getDATABITS() {
        return DATABITS;
    }
    public String getSTOPBITS() {
        return STOPBITS;
    }
    public String getTIMEOUT() {
        return TIMEOUT;
    }
    public String getFLOWCONTROLL() { return FLOWCONTROLL; }
    public String getBAUDRATE() {
        return BAUDRATE;
    }

    private ConstantFile() throws IOException, URISyntaxException {


        // working on testing env. NOT WORKING in deployed .jar!!!!
       // InputStream is = ClassLoader.class.getResourceAsStream("/constants/constants.ini");

        String fileName = "constants/constants.ini";
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource(fileName).getFile());

        try {
            ini = new Wini(file);
        }
        catch (Exception e) {
            System.out.println("Wini Exception -> " + e);
        }
        wini(); // initialize data from file





    }



    public static ConstantFile getInstance() throws IOException, URISyntaxException {
        if(INSTANCE == null){
            INSTANCE = new ConstantFile();
        }
            return INSTANCE;
    }


    public void show() {

        System.out.print("BAUDRATE: " + BAUDRATE + "\n");
        System.out.print("PARITY: " + PARITY + "\n");
        System.out.print("DATABITS: " + DATABITS + "\n");
        System.out.print("TIMEOUT: " + TIMEOUT + "\n");
        System.out.print("TIMEOUT: " + STOPBITS + "\n");
        System.out.print("FLOWCONTROLL: " + FLOWCONTROLL + "\n");

    }


    public enum iniElements
    {
        BAUDRATE,
        PARITY,
        DATABITS,
        STOPBITS,
        TIMEOUT,
        FLOWCONTROLL;
    }


    private void wini() throws IOException {
        try{
             BAUDRATE = ini.get("machine1", "BAUDRATE", String.class);
             PARITY = ini.get("machine1", "PARITY", String.class);
             DATABITS = ini.get("machine1", "DATABITS",String.class);
             TIMEOUT = ini.get("machine1", "NOT IMPLEMENTED",String.class);
             FLOWCONTROLL = ini.get("machine1", "FLOWCONTROLL",String.class);
             STOPBITS = ini.get("machine1","STOPBITS",String.class);

        }catch(Exception e){
            System.err.println(e.getMessage());
        }

    }



    /**
     * @param structureName   structure name, for instance "machine1" without square brackets
     * @param en              enum elements, for instance ConstantFile.iniElements.BAUDRATE
     * @param value           aimed value to change
     * @throws IOException
     */
    public void changeWini(String structureName, iniElements en, String value)   {
        try {
            ini.put(structureName, en.toString(), value);
            ini.store();

            System.out.println("Overriden element: " + en.toString() + " Value -> " + value);
        }
        catch(Exception e)
        {
            System.out.println("Exception in ChangeWIni: " + e);
        }
    }



}


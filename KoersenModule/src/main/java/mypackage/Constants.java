package mypackage;

public class Constants {

    private static String PricefolderMAC = "/Users/aba23913/stockprices/";
    //private static String PricefolderMAC = "~/BeleggingspakketMAC/stockprices/";
    private static String PricefolderPC = "d:\\stockprices\\";

    public static void setPricefolder(String pricefolder) {
        Pricefolder = pricefolder;
    }

    private static String Pricefolder = PricefolderPC;


    public static String getPricefolder() {
        return Pricefolder;
    }

    public static final int startYear = 2014;
    public static final int firstMonth = 1;
    public static final int lastMonth = 12;
}

package nl.beleggingspakket;

public class Constants {

    //private static String PricefolderMAC = "/Users/aba23913/Beleggingspakket-Nieuw/ideaal-beleggen/stockprices/";
    private static String PricefolderMAC = "/Users/abakker/stockprices/";
    //private static String PricefolderMAC = "~/BeleggingspakketMAC/stockprices/";
    //private static String PricefolderPC = "d:\\stockprices\\";
    private static String PricefolderPC = "D:\\Beleggingspakket\\ideaal-beleggen\\stockprices\\";
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

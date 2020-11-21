package nl.ideaalbeleggen;

public class Constants {

    // BKWI laptop
    private static String PricefolderMACBKWI = "/Users/abakker/Pakket/ideaal-beleggen/stockprices/";

    // Ordina laptop
    private static String PricefolderMACOrdina = "/Users/aba23913/Pakket/ideaal-beleggen/stockprices/";


    private static String PricefolderPC = "D:\\Beleggingspakket\\ideaal-beleggen\\stockprices\\";
        //"d:\\stockprices\\";

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

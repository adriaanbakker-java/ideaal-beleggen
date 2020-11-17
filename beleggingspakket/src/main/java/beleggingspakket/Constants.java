package beleggingspakket;

public class Constants {
    private static String folderStockprices = "stockprices"  ;
    private static String folderPortfolio = "portfolio";

    // BKWI laptop
    private static String folderBKWI = "/Users/abakker/Pakket/ideaal-beleggen/";
    private static String PricefolderMACBKWI =  folderBKWI + folderStockprices + "/";
    private static String PFfolderBKWI =  folderBKWI + folderPortfolio + "/";

    // Ordina laptop
    private static String folderOrdina =  "/Users/aba23913/Pakket/ideaal-beleggen/";
    private static String PricefolderMACOrdina = folderOrdina + folderStockprices  + "/";
    private static String PFfolderMacOrdina = folderOrdina  + folderPortfolio  + "/";

    // Windows pc
    private static String folderPC = "D:\\Beleggingspakket\\ideaal-beleggen\\";
    private static String PricefolderPC = folderPC  + folderStockprices + "\\";
    private static String PFfolderPC = folderPC  + folderPortfolio + "\\";

    private static String Pricefolder = PricefolderMACBKWI;
    private static String PFfolder = PFfolderBKWI;

    public static final int startYear = 2014;
    public static final int firstMonth = 1;
    public static final int lastMonth = 12;

    public static String getPricefolder() {
        return Pricefolder;
    }
    public static void setPricefolder(String pricefolder) {
        Pricefolder = pricefolder;
    }
    public static String getPFfolder() {
        return PFfolder;
    }

    public static String getFilenamePF() {
        return "Portefeuille.csv";
    }
}

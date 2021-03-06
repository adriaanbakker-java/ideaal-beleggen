package beleggingspakket;

import java.io.File;

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


    private static final int CEigenPC = 0;
    private static final int COrdinaPC = CEigenPC + 1;
    private static final int CBKWIPC = COrdinaPC + 1;

    // almaar dat wijzigen in de constants voor het bepalen van de pc is niet handig
    private static int geefPCId() {
        String filename = folderPC;
        File myFile = new File(folderPC);
        if (myFile.exists()) {
            return CEigenPC;
        }
        myFile = new File(folderBKWI);
        if (myFile.exists()) {
            return CBKWIPC;
        }
        myFile = new File(folderOrdina);
        if (myFile.exists()) {
            return COrdinaPC;
        }

        return -1;
    }

    // Windows pc
    private static final String folderPC = "D:\\Pakket\\ideaal-beleggen\\";
    private static final String PricefolderPC = folderPC  + folderStockprices + "\\";
    private static final String PFfolderPC = folderPC  + folderPortfolio + "\\";



    public static final int startYear = 2014;
    public static final int firstMonth = 1;
    public static final int lastMonth = 12;

    public static String getPricefolder() throws Exception {
        int pcId = geefPCId();
        switch (pcId) {
            case CEigenPC: return PricefolderPC;
            case COrdinaPC: return PricefolderMACOrdina;
            case CBKWIPC: return PricefolderMACBKWI;
            default:
                throw new IllegalStateException("getPricefolder: kon PC folders niet bepalen");
        }
    }


    public static String getPFfolder() {
        int pcId = geefPCId();
        switch (pcId) {
            case CEigenPC: return PFfolderPC;
            case COrdinaPC: return PFfolderMacOrdina;
            case CBKWIPC: return PFfolderBKWI;
            default:
                throw new IllegalStateException("getPricefolder: kon PC folders niet bepalen");
        }
    }

    public static String getFilenamePF() {
        return "Portefeuille.csv";
    }
}

package mypackage;


import java.util.HashMap;

public class Tickers {
    private HashMap<String, BeleggerLinkRec> bMap = new HashMap<String, BeleggerLinkRec>();

    private void fillLinkMap() {
        bMap.put("AEX", new BeleggerLinkRec("12272", "AEX"));
        bMap.put("AALBERTS", new BeleggerLinkRec("11797", "AALBERTS-NV"));
        bMap.put("ABN", new BeleggerLinkRec("612967", "ABN-AMRO-BANK-NV"));
        bMap.put("ADYEN", new BeleggerLinkRec("613007", "ADYEN-NV"));
        bMap.put("AEGON", new BeleggerLinkRec("11754", "Aegon"));
        bMap.put("DELHAIZE", new BeleggerLinkRec("11755", "Ahold-Delhaize-Koninklijke"));
        bMap.put("AKZO", new BeleggerLinkRec("11756", "Akzo-Nobel"));
        bMap.put("ARCELOR", new BeleggerLinkRec("11895", "ArcelorMittal"));
        bMap.put("ASML", new BeleggerLinkRec("16923", "ASML-Holding"));
        bMap.put("ASR", new BeleggerLinkRec("596718", "ASR-Nederland"));
        bMap.put("DSM", new BeleggerLinkRec("11764", "DSM-Koninklijke"));
        bMap.put("GALAPAGOS", new BeleggerLinkRec("60189120", "Galapagos"));
        bMap.put("HEINEKEN", new BeleggerLinkRec("11770", "Heineken"));
        bMap.put("IMCD", new BeleggerLinkRec("610603", "IMCD"));
        bMap.put("ING", new BeleggerLinkRec("11773", "ING-Groep"));
        bMap.put("KPN", new BeleggerLinkRec("25845", "KPN-Koninklijke"));
        bMap.put("NN", new BeleggerLinkRec("610720", "NN-Group"));
        bMap.put("PHILIPS", new BeleggerLinkRec("11783", "Philips-Koninklijke"));
        bMap.put("RANDSTAD", new BeleggerLinkRec("11785", "RANDSTAD-NV"));
        bMap.put("RELIX", new BeleggerLinkRec("11765", "RELIX"));
        bMap.put("SHELL", new BeleggerLinkRec("210964", "Royal-Dutch-Shell-A"));
        bMap.put("TAKEAWAY", new BeleggerLinkRec("561749", "Takeawaycom"));
        bMap.put("UNIBAIL", new BeleggerLinkRec("360115972", "UNIBAIL-RODAMCO-WESTFIELD"));
        bMap.put("UNILEVER", new BeleggerLinkRec("11962", "UNILEVER"));
        bMap.put("VOPAK", new BeleggerLinkRec("101431", "Vopak-Koninklijke"));
        bMap.put("WOLTERS", new BeleggerLinkRec("11795", "Wolters-Kluwer"));
    }


}

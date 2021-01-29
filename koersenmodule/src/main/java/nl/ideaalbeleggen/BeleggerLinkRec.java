package nl.ideaalbeleggen;

public class BeleggerLinkRec {
    String Stocknr;             // voor ophalen historische koersen vanaf IEX
    String Stockname;           // voor bepalen historische koersen vanaf IEX
    String StocknameBeleggenNL; // van belang voor ophalen intraday koers bij beleggen.nl

    public BeleggerLinkRec(String stocknr, String stockname, String stocknameBeleggenNL) {
        super();
        Stocknr = stocknr;
        Stockname = stockname;
        StocknameBeleggenNL = stocknameBeleggenNL;
    }
}
package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.util.IDate;

import java.util.ArrayList;

// Toppen en dalen in de prijsontwikkeling van het aandeel
// Voorlopig is het aantal vast (laatste 10 toppen en dalen)
public class ToppenEnDalen {
    private final String ticker;
    private final IDate einddatum;
    private  int indexEinddatum;
    private ArrayList<TopOfDal> toppen;
    private ArrayList<TopOfDal> bodems;

    private ArrayList<DayPriceRecord> prices;
    private final int CToppenDalen = 15;


    public ToppenEnDalen(String aTicker, IDate aEinddatum) throws Exception {
        ticker = aTicker;
        toppen = new ArrayList<>();
        bodems = new ArrayList<>();
        einddatum = aEinddatum;
        GetPriceHistory myGPH = new GetPriceHistory();
        prices = myGPH.getHistoricPricesFromFile(ticker);
        indexEinddatum = zoekIndex(einddatum);
    }

    public void addTopOfDal(boolean aIstop, DayPriceRecord aDpr, int aIndex) {
        TopOfDal tod = new TopOfDal(aIstop, aDpr, aIndex);
        toppen.add(tod);
    }

    // Laatste 10 toppen en dalen genereren die voor einddatum liggen
    public void zoekToppenEnDalen() throws Exception {
        ArrayList<TopOfDal> ruweEntries = zoekRuweToppenEnDalen(prices);
        ArrayList<TopOfDal> toppenLokaal = new ArrayList<>();
        ArrayList<TopOfDal> bodemsLokaal = new ArrayList<>();

        // doorloop nu de toppen en de dalen en leg verdere restricties op
        // restrictie: top moet worden omgeven door twee lagere toppen
        // restrictie: bodem moet worden omgeven door twee lagere bodems

        for (int ind = 0; ind<= ruweEntries.size()-1; ind++) {
           TopOfDal td = ruweEntries.get(ind);
           if (td.isTop())
               toppenLokaal.add(td);
           else
               bodemsLokaal.add(td);
        }
        double prev, curr, nxt;
        // stel vast of eerste top ten opzichte van candle op startdatum en de daaropvolgende
        // top een omgekeerde V beschrijft, zo ja dan eerste top ook toevoegen
        prev = prices.get(indexEinddatum).getHigh();
        curr = toppenLokaal.get(0).getDpr().getHigh();
        nxt = toppenLokaal.get(1).getDpr().getHigh();
        if (curr > prev && curr > nxt)
            this.toppen.add(toppenLokaal.get(0));

        prev = prices.get(indexEinddatum).getLow();
        curr = bodemsLokaal.get(0).getDpr().getLow();
        nxt = bodemsLokaal.get(1).getDpr().getLow();
        if (curr < prev && curr < nxt)
            this.bodems.add(bodemsLokaal.get(0));

        for (int ind=1; ind <= toppenLokaal.size()-2; ind++) {
            curr = toppenLokaal.get(ind).getDpr().getHigh();
            prev  = toppenLokaal.get(ind-1).getDpr().getHigh();
            nxt = toppenLokaal.get(ind+1).getDpr().getHigh();
            if (curr > prev && curr > nxt)
                this.toppen.add(toppenLokaal.get(ind));
        }
        for (int ind=1; ind <= bodemsLokaal.size()-2; ind++) {
            curr = bodemsLokaal.get(ind).getDpr().getHigh();
            prev  = bodemsLokaal.get(ind-1).getDpr().getHigh();
            nxt = bodemsLokaal.get(ind+1).getDpr().getHigh();
            if (curr < prev && curr < nxt)
                this.bodems.add(bodemsLokaal.get(ind));
        }
    }

    // Zoek de index van het koersrecord waarvan de datum nog net op of voor einddatum ligt
    private int zoekIndex(IDate aDatum) {
        boolean found = false;
        for (int i=prices.size()-1;(!found) && (i>=0);i++) {
            found = prices.get(i).getIDate().isSmallerEqual(aDatum);
            if (found) return i;
        }
        return -1;
    }

    private ArrayList<TopOfDal> zoekRuweToppenEnDalen(ArrayList<DayPriceRecord> prices) {
        ArrayList<TopOfDal> result = new ArrayList<>();

        // vanaf einddatum in prijsverloop teruglopen en geisoleerde toppen en dalen onderkennen
        int aantalToppen = 0;
        int aantalDalen = 0;

        for (int i = indexEinddatum-1;
             i>0 && aantalDalen < CToppenDalen &&  aantalToppen < CToppenDalen;
             i--) {
            TopOfDal td = checkTopDal(i);
            if (td != null) {
                if (td.isTop() && aantalToppen<= CToppenDalen) {
                    aantalToppen++;
                    result.add(td);
                }
                if (!td.isTop() && aantalDalen<= CToppenDalen) {
                    aantalDalen++;
                    result.add(td);
                }
            }
        }
        return result;
    }

    private TopOfDal checkTopDal(int i) {
        DayPriceRecord prev = prices.get(i-1);
        DayPriceRecord curr = prices.get(i);
        DayPriceRecord nxt = prices.get(i+1);
        if (prev.getLow() > curr.getLow() && curr.getLow() < nxt.getLow()) {
            return new TopOfDal(false, curr, i);
        }
        if (prev.getHigh() < curr.getHigh() && curr.getHigh() > nxt.getHigh()) {
            return new TopOfDal(true, curr, i);
        }
        return null;
    }

    public String toString() {
        String result = "Toppen en dalen:\n";
        for (TopOfDal td: toppen) {
            result += td.toString() + "\n";
        }
        for (TopOfDal td: bodems) {
            result += td.toString() + "\n";
        }
        return result;
    }

}

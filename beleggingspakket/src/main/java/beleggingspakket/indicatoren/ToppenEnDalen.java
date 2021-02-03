package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.util.IDate;
import com.fasterxml.jackson.databind.util.ClassUtil;

import java.util.ArrayList;

// Toppen en dalen in de prijsontwikkeling van het aandeel
// Voorlopig is het aantal vast (laatste 10 toppen en dalen)
public class ToppenEnDalen {
    private final String ticker;
    private final IDate einddatum;
    private  int indexEinddatum;
    private ArrayList<TopOfDal> entries;
    private ArrayList<DayPriceRecord> prices;
    private final int CToppenDalen = 10;


    public ToppenEnDalen(String aTicker, IDate aEinddatum) throws Exception {
        ticker = aTicker;
        entries  = new ArrayList<>();
        einddatum = aEinddatum;
        GetPriceHistory myGPH = new GetPriceHistory();
        prices = myGPH.getHistoricPricesFromFile(ticker);
        indexEinddatum = zoekIndex(einddatum);
    }

    public void addTopOfDal(boolean aIstop, DayPriceRecord aDpr, int aIndex) {
        TopOfDal tod = new TopOfDal(aIstop, aDpr, aIndex);
        entries.add(tod);
    }

    // Laatste 10 toppen en dalen genereren die voor einddatum liggen
    public void zoekToppenEnDalen() throws Exception {
        zoekRuweToppenEnDalen(prices);
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

    private void zoekRuweToppenEnDalen(ArrayList<DayPriceRecord> prices) {
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
                    entries.add(td);
                }
                if (!td.isTop() && aantalDalen<= CToppenDalen) {
                    aantalDalen++;
                    entries.add(td);
                }
            }
        }
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
        for (TopOfDal td: entries) {
            result += td.toString() + "\n";
        }
        return result;
    }

}

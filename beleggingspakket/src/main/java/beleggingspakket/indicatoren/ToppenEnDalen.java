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
        try {
            ticker = aTicker;
            toppen = new ArrayList<>();
            bodems = new ArrayList<>();
            einddatum = aEinddatum;
            GetPriceHistory myGPH = new GetPriceHistory();
            prices = myGPH.getHistoricPricesFromFile(ticker);
            indexEinddatum = zoekIndex(einddatum);
        } catch (Exception e) {
            throw new Exception ("ToppenEnDalen constructor:" + e.getLocalizedMessage());
        }

    }



    // Laatste 10 toppen en dalen genereren die voor einddatum liggen
    // NB code kan waarschijnlijk best een stuk simpeler met wat meer abstractie,
    // gevalsuitsplitsingen hebben het karakter van code duplicatie
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
        TopOfDal top = toppenLokaal.get(0);
        prev = prices.get(indexEinddatum).getHigh();
        curr = top.getDpr().getHigh();
        nxt = toppenLokaal.get(1).getDpr().getHigh();
        if ((curr > prev && curr > nxt) || (top.getOrde() >=2))
            this.toppen.add(top);

        // stel vast of eerste bodem ten opzichte van candle op startdatum en de daaropvolgende
        // top een  V beschrijft, zo ja dan eerste bodem ook toevoegen
        prev = prices.get(indexEinddatum).getLow();
        TopOfDal bodem = bodemsLokaal.get(0);
        curr = bodem.getDpr().getLow();
        nxt = bodemsLokaal.get(1).getDpr().getLow();
        if ((curr < prev && curr < nxt) || (bodem.getOrde() >=2))
            this.bodems.add(bodem);

        for (int ind=1; ind <= toppenLokaal.size()-2; ind++) {
            top = toppenLokaal.get(ind);
            curr = top.getDpr().getHigh();
            prev  = toppenLokaal.get(ind-1).getDpr().getHigh();
            nxt = toppenLokaal.get(ind+1).getDpr().getHigh();
            if ((curr > prev && curr > nxt) || (top.getOrde() >=2))
                this.toppen.add(top);
        }
        for (int ind=1; ind <= bodemsLokaal.size()-2; ind++) {
            bodem = bodemsLokaal.get(ind);
            curr = bodem.getDpr().getHigh();
            prev  = bodemsLokaal.get(ind-1).getDpr().getHigh();
            nxt = bodemsLokaal.get(ind+1).getDpr().getHigh();
            if ((curr < prev && curr < nxt) || (bodem.getOrde() >=2))
                this.bodems.add(bodem);
        }
    }

    // Zoek de index van het koersrecord waarvan de datum nog net op of voor einddatum ligt
    private int zoekIndex(IDate aDatum) throws Exception {
        try {
            boolean found = false;
            for (int i=prices.size()-1;(!found) && (i>=0);i--) {
                found = prices.get(i).getIDate().isSmallerEqual(aDatum);
                if (found) return i;
            }
            return -1;
        } catch (Exception e) {
            throw new Exception ("ToppenEnDalen.zoekIndex():" + e.getLocalizedMessage());
        }

    }

    // Zoek de toppen en de dalen in het koersverloop
    // tevens vermelden wat de orde van het top of dal is
    // orde is het aantal buurcandles dat een lagere hoog heeft (bij top)
    // dan wel het aantal buurcandles dat een hogere laag heeft (bij bodem).
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
        if (prev.getHigh() < curr.getHigh() && curr.getHigh() > nxt.getHigh()) {
            int orde = bepaalOrde(true, i);
            return new TopOfDal(true, curr, i, orde);
        }
        if (prev.getLow() > curr.getLow() && curr.getLow() < nxt.getLow()) {
            int orde = bepaalOrde(false, i);
            return new TopOfDal(false, curr, i, orde);
        }
        return null;
    }

    // bepaal orde van top of dal dwz aantal buren ten opzichte van welke de top dan wel dal
    // nog steeds een hogere top dan wel dal is
    private int bepaalOrde(boolean aIsTop, int aIndex) {
        int orde = 0;
        int nextorde = orde + 1;
        boolean isNogSteedsGoed = true;
        while ((aIndex + nextorde <= indexEinddatum ) && isNogSteedsGoed) {
            isNogSteedsGoed = (aIsTop? (prices.get(aIndex).getHigh() > prices.get(aIndex + nextorde).getHigh())
                    :  (prices.get(aIndex).getLow() < prices.get(aIndex + nextorde).getLow()));
            if (isNogSteedsGoed)
                isNogSteedsGoed = (aIsTop? (prices.get(aIndex).getHigh() > prices.get(aIndex - nextorde).getHigh())
                        :  (prices.get(aIndex).getLow() < prices.get(aIndex - nextorde).getLow()));
            if (isNogSteedsGoed) {
                orde = nextorde;
                nextorde++;
            }
        }
        return orde;
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

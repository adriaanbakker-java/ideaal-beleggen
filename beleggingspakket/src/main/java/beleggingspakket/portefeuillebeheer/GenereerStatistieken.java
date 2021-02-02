package beleggingspakket.portefeuillebeheer;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.indicatoren.IndicatorSignal;
import beleggingspakket.indicatoren.MACD;
import beleggingspakket.indicatoren.Signalen;
import beleggingspakket.util.IDate;

import java.util.ArrayList;

public class GenereerStatistieken {
    String Ticker;
    boolean IsKoopsignaal;
    int AantalDagen;
    IDate Einddatum;
    double Delta;

    public GenereerStatistieken(String aTicker,
                         boolean aIsKoopsignaal,
                         int aAantalDagen,
                         IDate aEinddatum,
                         double aDelta) {
        Ticker = aTicker;
        IsKoopsignaal = aIsKoopsignaal;
        Einddatum = aEinddatum;
        Delta = aDelta;
        AantalDagen = aAantalDagen;
    }

    // Bereken de statistiek bij het gekozen fonds en gekozen parameters voor indicator MACD
    public StatistiekUitkomst berekenStatistiekMACD() throws Exception {
        try {
            StatistiekUitkomst uitk = new StatistiekUitkomst();
            GetPriceHistory myGPH = new GetPriceHistory();
            ArrayList<DayPriceRecord> prices;
            prices = myGPH.getHistoricPricesFromFile(Ticker);
            MACD macd = new MACD(prices);
            ArrayList<IndicatorSignal> signalen = macd.getSignals();

            for (IndicatorSignal s: signalen) {
                int signaalindex = s.getIndexKoersreeks();
                IDate datum = s.getDate();
                IDate checkDate = new IDate(2020, 6, 18);
                if (checkDate.isEqual(datum)) {
                    System.out.println("ref datum 18 jun 2020 gevonden");
                }
                if ((signaalindex + AantalDagen <= prices.size()-1) && (s.getKoopsignaal() == IsKoopsignaal)) {
                    uitk.incAantalGebeurtenissen();
                    double k0 = s.getDpr().getClose();
                    DayPriceRecord dprn = prices.get(signaalindex + AantalDagen);
                    if (dprn.getClose() > k0 * (1 + Delta/100)) {
                        uitk.incHigher(s.getDpr().getIDate());
                    }
                    if (dprn.getClose() < k0 * (1 - Delta/100)) {
                        uitk.incLower(s.getDpr().getIDate());
                    }
                }
            }
            return uitk;
        } catch (Exception e) {
            throw new Exception("berekenStatistiekMACD:" + e.getLocalizedMessage());
        }


    }
}

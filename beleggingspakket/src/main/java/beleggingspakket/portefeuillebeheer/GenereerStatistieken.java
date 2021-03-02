package beleggingspakket.portefeuillebeheer;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.indicatoren.Indicator;
import beleggingspakket.indicatoren.IndicatorSignal;
import beleggingspakket.indicatoren.MACD;
import beleggingspakket.indicatoren.Signalen;
import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;

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

    // negeert Delta en isKoopsignaal, toont laatste 10 signalen voor de einddatum van de portefeuille
    public  String toonLaatsteSignalen() {
        try {
            GetPriceHistory myGPH = new GetPriceHistory();
            ArrayList<DayPriceRecord> prices;
            prices = myGPH.getHistoricPricesFromFile(Ticker);
            MACD macd = new MACD(prices);
            ArrayList<IndicatorSignal> signalen = macd.getSignals();
            String result = "";

            int indexSignalen = signalen.size() - 1;
            int count = 0;
            while ((indexSignalen >=0) && (count < 10)) {
                IndicatorSignal s = signalen.get(indexSignalen);
                if (s.getDate().isSmallerEqual(this.Einddatum)) {
                    count++;
                    String spacer = (s.getKoopsignaal() ? "" : "   ");
                    result += spacer + s.toString() + "\n";
                }
                indexSignalen--;
            }

//            for (int n= signalen.size()-1;n>=signalen.size()-10; n--) {
//                IndicatorSignal s = signalen.get(n);
//                String spacer = (s.getKoopsignaal() ? "" : "   ");
//                result += spacer + s.toString() + "\n";
//            }
            return result;
        } catch(Exception e) {
            return "toonLaatsteSignalen:" + e.getLocalizedMessage();
        }
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

    public int trunk(double value){
        Double result = value - value % 1;
        return result.intValue();
    }

    // Bereken de beleggingsuitkomst bij het gekozen fonds en gekozen parameters voor indicator MACD
    public ArrayList<String> berekenBeleggenMACD() throws Exception {
        ArrayList<String> result = new ArrayList<>();
        try {
            GetPriceHistory myGPH = new GetPriceHistory();
            ArrayList<DayPriceRecord> prices;
            prices = myGPH.getHistoricPricesFromFile(Ticker);
            MACD macd = new MACD(prices);
            ArrayList<IndicatorSignal> signalen = macd.getSignals();
            double vermogen = 1000.0;
            int stuks = 0;

            for (IndicatorSignal s: signalen) {
                // Zodra er een signaal wordt getoond wordt de eerstvolgende handelsdag aan- of verkocht
                // tegen de openingskoers van de volgende handelsdag

                int indexKoersreeks = s.getIndexKoersreeks();
                if ((indexKoersreeks <= prices.size()-2) && (s.getDate().isSmaller(Einddatum))) {
                    DayPriceRecord nextDayDpr = prices.get(indexKoersreeks + 1);
                    String sMsg = s.getDate().toString();
                    sMsg += (s.getKoopsignaal() ? ";Kopen;" : ";Verkopen;") ;
                    double openingsprijs = nextDayDpr.getOpen();
                    if (s.getKoopsignaal()) {
                        stuks = trunk( vermogen/openingsprijs);
                        vermogen  -=   stuks * openingsprijs;
                        sMsg +=   stuks + ";" + Util.toCurrency(openingsprijs);
                    } else {
                        sMsg +=   stuks + ";" + Util.toCurrency(openingsprijs);
                        vermogen += stuks * openingsprijs;
                        stuks = 0;
                    }
                    sMsg += ";" + Util.toCurrency(vermogen);
                    result.add(sMsg);
                }
            }
            return result;
        } catch (Exception e) {
            throw new Exception("berekenBeleggenMACD:" + e.getLocalizedMessage());
        }
    }
}

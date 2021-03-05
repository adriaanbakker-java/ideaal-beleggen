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

    private class AanVerkoopParams {
        boolean koopconditie;
        boolean macdKoopconditie;
        boolean isAangekocht;
        int stuks;
        double aankoopKoers;
        double geldOpRekening;
        double stoplimit; // kleiner dan nul betekent gecanceld
        double stoploss;

        AanVerkoopParams() {
            koopconditie = false;
            macdKoopconditie = false;
            isAangekocht = false;
            stuks = 0;
            aankoopKoers = 0.0;
            geldOpRekening = 1000;
            stoplimit = -1.0;
            stoploss = -1.0;
        }
    }

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


    public ArrayList<String> berekenBeleggenMACDStopLoss() throws Exception {
        ArrayList<String> result = new ArrayList<>();
        try {
            GetPriceHistory myGPH = new GetPriceHistory();
            ArrayList<DayPriceRecord> prices;
            prices = myGPH.getHistoricPricesFromFile(Ticker);
            MACD macd = new MACD(prices);
            AanVerkoopParams aanVerkoopParams  = new AanVerkoopParams();
            int koersindex = 0;
            for (DayPriceRecord price: prices) {
                checkVerwerkAanVerkopen(aanVerkoopParams, price, result);
                checkMACDstatus(aanVerkoopParams, macd, koersindex, result);
                koersindex++;
            }
        } catch (Exception e) {
            throw new Exception("berekenBeleggenMACDStopLoss:" + e.getLocalizedMessage());
        }
        return result;
    }

    private void checkMACDstatus(
            AanVerkoopParams aanVerkoopParams,
            MACD macd,
            int indexKoersreeks,
            ArrayList<String> result) {
            boolean isKopen = macd.getStatus(indexKoersreeks);
            if (isKopen) {
                aanVerkoopParams.koopconditie = true;
                aanVerkoopParams.macdKoopconditie = true;
            } else {
                aanVerkoopParams.koopconditie = false;
                aanVerkoopParams.macdKoopconditie = false;
            }
    }

    private void checkVerwerkAanVerkopen(
            AanVerkoopParams aanVerkoopParams,
            DayPriceRecord price,
            ArrayList<String> result) {
        if (aanVerkoopParams.isAangekocht) {
            // check stoploss
            if (aanVerkoopParams.stoploss >= price.getLow()) {
                verkoopStoploss(aanVerkoopParams, price, result);
            } else {
                // check MACD koopconditie niet meer geldig
                if (!aanVerkoopParams.macdKoopconditie) {
                    verkoopOpeningskoers(aanVerkoopParams, price, result);
                }
            }
        } else { // niet aangekocht
            if (aanVerkoopParams.koopconditie) {
                // kopen zodra er een MACD koopconditie is
                koopOpeningskoers(aanVerkoopParams, price, result);
            } else {
                // onder bepaalde condities bij stoplimit kopen
                if ((aanVerkoopParams.stoplimit > 0) &&
                        (aanVerkoopParams.macdKoopconditie) &&
                        (price.getHigh() > aanVerkoopParams.stoplimit)) {
                    koopStoplimit(aanVerkoopParams, price, result);
                }
            }

        }
    }

    private void koopStoplimit(AanVerkoopParams aanVerkoopParams,
                               DayPriceRecord price,
                               ArrayList<String> result) {
        koopKoers("limit", aanVerkoopParams, price.getIDate(),
                aanVerkoopParams.stoplimit, result);
    }

    private void koopOpeningskoers(AanVerkoopParams aanVerkoopParams,
                                   DayPriceRecord price,
                                   ArrayList<String> result) {
        koopKoers("open", aanVerkoopParams, price.getIDate(),
                price.getOpen(), result);
    }

    private void koopKoers(String msgKoers,
                           AanVerkoopParams aanVerkoopParams,
                           IDate datum,
                           double koers,
                           ArrayList<String> result) {
        aanVerkoopParams.stuks = trunk( aanVerkoopParams.geldOpRekening/koers);
        aanVerkoopParams.aankoopKoers =  koers;
        double aankoopbedrag = aanVerkoopParams.stuks * aanVerkoopParams.aankoopKoers;
        aanVerkoopParams.geldOpRekening  -=  aankoopbedrag;
        String msg = datum.toString() + "\tKopen " + msgKoers;
        msg += aanVerkoopParams.stuks + "\t" + Util.toCurrency(koers) ;
        msg += "\t" + Util.toCurrency(aanVerkoopParams.geldOpRekening + aankoopbedrag);
        aanVerkoopParams.isAangekocht = true;
        aanVerkoopParams.stoploss = 0.95 * koers;
        aanVerkoopParams.stoplimit = -1.0;
        result.add(msg);
    }

    private void verkoopOpeningskoers(AanVerkoopParams aanVerkoopParams,
                                      DayPriceRecord price,
                                      ArrayList<String> result) {
        verkoopKoers("open", aanVerkoopParams, price.getIDate(),
                price.getOpen(), result);
    }

    private void verkoopKoers(
            String msgKoers,
            AanVerkoopParams aanVerkoopParams,
            IDate datum,
            double koers,
            ArrayList<String> result) {

        double verkoopbedrag = aanVerkoopParams.stuks * koers;
        aanVerkoopParams.geldOpRekening  +=  verkoopbedrag;
        String msg = datum.toString() + "\tVerkopen " + msgKoers;
        msg += aanVerkoopParams.stuks + "\t" + Util.toCurrency(koers) ;
        msg += "\t" + Util.toCurrency(aanVerkoopParams.geldOpRekening);
        aanVerkoopParams.isAangekocht = false;
        aanVerkoopParams.stoplimit = 1.05 * koers;
        aanVerkoopParams.stoploss = -1.0;
        result.add(msg);
    }

    private void verkoopStoploss(AanVerkoopParams aanVerkoopParams,
                                 DayPriceRecord price,
                                 ArrayList<String> result) {
        verkoopKoers("stoploss", aanVerkoopParams, price.getIDate(),
                aanVerkoopParams.stoploss, result);
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
            double geldoprekening = 1000.0;
            double waardebelegging = 0.0;
            int stuks = 0;

            double aankoopbedrag = 0;
            double verkoopbedrag = 0;

            int aantalPositief = 0;
            int aantalNegatief = 0;
            int totaalaantal = 0;
            String sExtraOpRegel= "";
            for (IndicatorSignal s: signalen) {
                // Zodra er een signaal wordt getoond wordt de eerstvolgende handelsdag aan- of verkocht
                // tegen de openingskoers van de volgende handelsdag
                int indexKoersreeks = s.getIndexKoersreeks();
                if ((indexKoersreeks <= prices.size()-2) && (s.getDate().isSmaller(Einddatum))) {
                    DayPriceRecord nextDayDpr = prices.get(indexKoersreeks + 1);
                    String sMsg = s.getDate().toString();
                    sMsg += (s.getKoopsignaal() ? "\tKopen\t" : "\tVerkopen\t") ;
                    double openingsprijs = nextDayDpr.getOpen();
                    if (s.getKoopsignaal()) {
                        stuks = trunk( geldoprekening/openingsprijs);
                        aankoopbedrag = stuks * openingsprijs;
                        waardebelegging = aankoopbedrag;
                        geldoprekening  -=  waardebelegging;
                        sMsg +=   stuks + "\t" + Util.toCurrency(openingsprijs) ;
                        sExtraOpRegel = "";
                    } else {
                        totaalaantal++;
                        sMsg +=   stuks + "\t" + Util.toCurrency(openingsprijs);
                        verkoopbedrag = stuks * openingsprijs;
                        geldoprekening += verkoopbedrag;
                        if (verkoopbedrag > aankoopbedrag) {
                            aantalPositief++;
                            sExtraOpRegel =  "\t\thoger";
                        }
                        if (verkoopbedrag < aankoopbedrag) {
                            aantalNegatief++;
                            sExtraOpRegel =  "\t\tlager";
                        }
                        if (verkoopbedrag/aankoopbedrag < 0.95) {
                            sExtraOpRegel = "\t\tlager:" + Util.toCurrency(verkoopbedrag / aankoopbedrag);
                        }
                        if (verkoopbedrag/aankoopbedrag > 1.05) {
                            sExtraOpRegel = "\t\thoger:" + Util.toCurrency(verkoopbedrag / aankoopbedrag);
                        }
                        waardebelegging = 0;
                        stuks = 0;
                    }
                    sMsg += "\t" + Util.toCurrency(geldoprekening);
                    if (geldoprekening < 10.0)
                        sMsg += "\t";
                    sMsg += "\t" + Util.toCurrency(geldoprekening + waardebelegging)  +
                            "\t" + sExtraOpRegel;
                    result.add(sMsg);
                }
            }
            result.add("");
            result.add("totaal aantal verkopen:" + totaalaantal);
            result.add("totaal aantal positief:" + aantalPositief);
            result.add("totaal aantal negatief:" + aantalNegatief);
            return result;
        } catch (Exception e) {
            throw new Exception("berekenBeleggenMACD:" + e.getLocalizedMessage());
        }
    }
}

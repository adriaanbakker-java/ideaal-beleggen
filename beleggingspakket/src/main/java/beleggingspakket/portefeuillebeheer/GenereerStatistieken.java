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
        public int totaalaantal;
        public int aantalPositief;
        public int aantalNegatief;
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
            totaalaantal = 0; // totaal aantal verkooptransacties
            aantalPositief = 0;
            aantalNegatief = 0;
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


    // Bereken beleggingsresultaat bij beleggen met MACD met stoploss
    public ArrayList<String> berekenBeleggenMACDStoploss() throws Exception {
        ArrayList<String> result = new ArrayList<>();
        try {
            GetPriceHistory myGPH = new GetPriceHistory();
            ArrayList<DayPriceRecord> prices;
            prices = myGPH.getHistoricPricesFromFile(Ticker);
            MACD macd = new MACD(prices);
            AanVerkoopParams aanVerkoopParams  = new AanVerkoopParams();
            int koersindex = 0;
            for (DayPriceRecord price: prices) {
                IDate testdate = new IDate(2019, 06, 14);
                if (price.getIDate().isEqual(testdate)) {
                    System.out.println("14 juni 2019");
                }
                if (!price.getIDate().isSmallerEqual(Einddatum))
                    break;
                checkVerwerkAanVerkopen(aanVerkoopParams, price, result);
                checkMACDstatus(aanVerkoopParams, macd, koersindex, result);
                koersindex++;
            }

            result.add("");
            result.add("totaal aantal verkopen:" + aanVerkoopParams.totaalaantal);
            result.add("totaal aantal positief:" + aanVerkoopParams.aantalPositief);
            result.add("totaal aantal negatief:" + aanVerkoopParams.aantalNegatief);
        } catch (Exception e) {
            throw new Exception("berekenBeleggenMACDStopLoss:" + e.getLocalizedMessage());
        }
        return result;
    }

    // Het laatste macd signaal bepaalt of er sprake is van een koopwaardige situatie
    // let op, tussentijds kan er toch verkocht zijn vanwege een stoploss die werd geraakt
    // de macd koopconditie wordt alleen in deze methode geset.
    // de koopconditie wordt op "waar" gezet bij een macd koopsignaal en op "onwaar"
    // bij een macd verkoopsignaal en daarnaast als het laatste signaal verkopen was
    private void checkMACDstatus(
            AanVerkoopParams aanVerkoopParams,
            MACD macd,
            int indexKoersreeks,
            ArrayList<String> result) {
            Boolean isKopen = macd.getStatus(indexKoersreeks);
            if (isKopen != null) {
                if (isKopen == true) {
                    // bij overgang (!) naar macd koopconditie is er een koopconditie ontstaan
                    // dit geldt onafhankelijk van tussentijds verkopen wegens een stoploss
                    if (aanVerkoopParams.macdKoopconditie == false) {
                        aanVerkoopParams.koopconditie = true;
                        aanVerkoopParams.macdKoopconditie = true;
                    }
                } else {
                    aanVerkoopParams.macdKoopconditie = false;
                    aanVerkoopParams.koopconditie = false;
                }
            } else {
                aanVerkoopParams.macdKoopconditie = false;
                aanVerkoopParams.macdKoopconditie = false;
            }
    }

    private void checkVerwerkAanVerkopen(
            AanVerkoopParams aanVerkoopParams,
            DayPriceRecord price,
            ArrayList<String> result) {
        if (aanVerkoopParams.isAangekocht) {
            if (aanVerkoopParams.stoplimit < price.getHigh())
                aanVerkoopParams.stoplimit = price.getHigh();
            if (!aanVerkoopParams.macdKoopconditie) {
                verkoopOpeningskoers(aanVerkoopParams, price, result);
                aanVerkoopParams.koopconditie = false;
                aanVerkoopParams.stoplimit = -1;
                aanVerkoopParams.stoploss = -1;
            } else {
                if (aanVerkoopParams.stoploss >= price.getLow()) {
                    verkoopStoploss(aanVerkoopParams, price, result);
                    aanVerkoopParams.koopconditie = false;
                    aanVerkoopParams.stoplimit = price.getHigh();
                    aanVerkoopParams.stoploss = -1;
                }
            }

        } else { // niet aangekocht
            if (aanVerkoopParams.koopconditie) {
                koopOpeningskoers(aanVerkoopParams, price, result);
                aanVerkoopParams.stoploss = price.getLow();
            } else {
                // onder bepaalde condities bij stoplimit kopen zolang het MACD signaal nog geldig is
                if ((aanVerkoopParams.stoplimit > 0) &&
                        (aanVerkoopParams.macdKoopconditie) &&
                        (price.getHigh() > aanVerkoopParams.stoplimit)) {
                    koopStoplimit(aanVerkoopParams, price, result);
                    aanVerkoopParams.stoploss = price.getLow();
                }
            }
        }
    }

    private void koopStoplimit(AanVerkoopParams aanVerkoopParams,
                               DayPriceRecord price,
                               ArrayList<String> result) {
        koopKoers("limit", aanVerkoopParams, price.getIDate(), price,
                aanVerkoopParams.stoplimit, result);
    }

    private void koopOpeningskoers(AanVerkoopParams aanVerkoopParams,
                                   DayPriceRecord price,
                                   ArrayList<String> result) {
        koopKoers("open", aanVerkoopParams, price.getIDate(), price,
                price.getOpen(), result);
    }

    private void koopKoers(String msgKoers,
                           AanVerkoopParams aanVerkoopParams,
                           IDate datum,
                           DayPriceRecord price,
                           double koers,
                           ArrayList<String> result) {
        aanVerkoopParams.stuks = trunk( aanVerkoopParams.geldOpRekening/koers);
        aanVerkoopParams.aankoopKoers =  koers;
        double aankoopbedrag = aanVerkoopParams.stuks * aanVerkoopParams.aankoopKoers;
        aanVerkoopParams.geldOpRekening  -=  aankoopbedrag;
        String msg = datum.toString() + "\tKopen " + msgKoers;
        msg += "\t" + aanVerkoopParams.stuks + "\t" + Util.toCurrency(koers) ;
        msg += "\t" + Util.toCurrency(aanVerkoopParams.geldOpRekening + aankoopbedrag);
        aanVerkoopParams.isAangekocht = true;
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
        double aankoopbedrag = aanVerkoopParams.stuks * aanVerkoopParams.aankoopKoers;
        aanVerkoopParams.geldOpRekening  +=  verkoopbedrag;
        String msg = datum.toString() + "\tVerkopen " + msgKoers;
        msg += aanVerkoopParams.stuks + "\t" + Util.toCurrency(koers) ;
        msg += "\t" + Util.toCurrency(aanVerkoopParams.geldOpRekening);
        aanVerkoopParams.isAangekocht = false;
        aanVerkoopParams.totaalaantal++;

        if (verkoopbedrag > aankoopbedrag) {
            aanVerkoopParams.aantalPositief++;
            if (verkoopbedrag/aankoopbedrag > 1.05) {
                msg += "\t\thoger:" + Util.toCurrency(verkoopbedrag / aankoopbedrag);
            } else {
                msg +=  "\t\thoger";
            }
        }
        if (verkoopbedrag < aankoopbedrag) {
            aanVerkoopParams.aantalNegatief++;
            if (verkoopbedrag/aankoopbedrag < 0.95) {
                msg += "\t\tlager:" + Util.toCurrency(verkoopbedrag / aankoopbedrag);
            } else {
                msg +=  "\t\tlager";
            }
        }
        result.add(msg);
    }

    private void verkoopStoploss(AanVerkoopParams aanVerkoopParams,
                                 DayPriceRecord price,
                                 ArrayList<String> result) {
        verkoopKoers("stoploss", aanVerkoopParams, price.getIDate(),
                aanVerkoopParams.stoploss, result);
    }

    // Bereken de beleggingsuitkomst bij het gekozen fonds en gekozen parameters voor indicator MACD
    public ArrayList<String> berekenBeleggenMACD1() throws Exception {
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
